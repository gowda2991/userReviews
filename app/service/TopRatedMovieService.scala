package service

import com.google.inject.{ImplementedBy, Singleton}
import service.models.{Genre, MovieGenreMappings, MovieRecord, Movies}
import slick.jdbc.H2Profile.api._
import utils.database._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@ImplementedBy(classOf[TopRatedMovieServiceImpl])
trait TopRatedMovieService {
  def getTopRatedMovieByGenre(genre: Genre, limit: Int): Future[Seq[(String, Int)]]

  def getTopRatedMovieByYear(year: Long, limit: Int): Future[Seq[(String, Int)]]

  def getTopRatedMovieByCriticByYear(year: Long, limit: Int): Future[Seq[(String, Int)]]

  def getTopRatedMovieByAverageRatingByYear(year: Long, limit: Int): Future[Seq[(String, Int)]]

}

@Singleton
class TopRatedMovieServiceImpl extends TopRatedMovieService {
  val genreQuery = MovieGenreMappings.tableQuery
  val movieQuery: Query[Movies, MovieRecord, Seq] = Movies.tableQuery

  private type MovieQuery = Query[Movies, MovieRecord, Seq]

  def getMovieGenreQuery(genre: Genre) = for{
    genreMovies <- genreQuery.filter(_.genre === genre)
    movieInfo <- movieQuery if genreMovies.movieId === movieInfo.movieId
  } yield movieInfo

  override def getTopRatedMovieByGenre(genre: Genre, limit: Int): Future[Seq[(String, Int)]] = db.run{
    for{
      moviesForGenre <- getMovieGenreQuery(genre).result
    }yield {
      val movieRatingResult = moviesForGenre.map(r => (r.name, r.userRating + r.criticRating)).sortWith(_._2 > _._2).take(limit)
      movieRatingResult
    }
  }

  override def getTopRatedMovieByYear(year: Long, limit: Int): Future[Seq[(String, Int)]] = db.run(
    for{
      movies <- movieQuery.filter(_.year === year).result
    }yield {
      val result = movies.map(r => (r.name, r.userRating + r.criticRating)).sortWith(_._2 > _._2).take(limit)
      result
    }
  )

  override def getTopRatedMovieByCriticByYear(year: Long, limit: Int): Future[Seq[(String, Int)]] = db.run(
    for{
      movies <- movieQuery.filter(_.year === year).result
    }yield {
      val result = movies.map(r => (r.name, r.criticRating)).sortWith(_._2 > _._2).take(limit)
      result
    }
  )

  override def getTopRatedMovieByAverageRatingByYear(year: Long, limit: Int): Future[Seq[(String, Int)]] = db.run(
    for{
      movies <- movieQuery.filter(_.year === year).result
    }yield {
      val result = movies.map{
        r =>
          val totalReviews = if(r.userRatingCount == 0 && r.criticRatingCount == 0) 1 else r.userRatingCount + r.criticRatingCount
          (r.name, (r.userRating + r.criticRating)/totalReviews)
      }.sortWith(_._2 > _._2).take(limit)
      result
    }
  )
}
