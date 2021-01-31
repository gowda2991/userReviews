package service

import com.google.inject.{ImplementedBy, Inject, Singleton}
import controllers.{AddMovieRequest, MovieExistsException, MovieNotFoundException}
import service.models.{MovieRecord, Movies}
import slick.jdbc.H2Profile.api._
import utils.database._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@ImplementedBy(classOf[MovieServiceImpl])
trait MovieService {
  def addMovie(record: MovieRecord): DBIO[Long]
  def getMovie(movieName: String): DBIO[MovieRecord]
  def getMovieOpt(movieName: String): DBIO[Option[MovieRecord]]
  def updateMovieRating(movieId: Long, userRating: Option[Int], criticRating: Option[Int], userRatingCount: Option[Int], criticRatingCount: Option[Int]): DBIO[Int]
  def createMovie(request: AddMovieRequest): Future[Long]

}

@Singleton
class MovieServiceImpl @Inject()(movieGenreMappingService: MovieGenreMappingService) extends MovieService{

  val query = Movies.tableQuery

  override def addMovie(record: MovieRecord): DBIO[Long] =
    query returning query.map(_.movieId) += record


  def getMovie(movieName: String): DBIO[MovieRecord] =
    query.filter(_.name === movieName).result.headOption.map(_.getOrElse(throw MovieNotFoundException(movieName)))


  def getMovieOpt(movieName: String): DBIO[Option[MovieRecord]] =
    query.filter(_.name === movieName).result.headOption


  override def updateMovieRating(movieId: Long, userRating: Option[Int], criticRating: Option[Int], userRatingCount: Option[Int], criticRatingCount: Option[Int]): DBIO[Int] =
    (userRating, userRatingCount, criticRating, criticRatingCount) match {
      case (Some(usrRating), Some(usrRatingCount), None, None) => query.filter(_.movieId === movieId).map(r => (r.userRating, r.userRatingCount)).update(usrRating, usrRatingCount)
      case (None, None, Some(ctcRating), Some(ctcRatingCount)) => query.filter(_.movieId === movieId).map(r => (r.criticRating, r.criticRatingCount)).update(ctcRating, ctcRatingCount)
      case _ => throw InvalidRatingUpdateRequest(movieId, userRating, criticRating, userRatingCount, criticRatingCount)
    }


  /**
    *
    * @param request. Takes in add movie request
    *                 Adds movie record.
    *                 Adds movie-genre mapping records
    * @return   Returns movieId
    */
  override def createMovie(request: AddMovieRequest): Future[Long] = db.run (
    (for {
      movieOpt <- getMovieOpt(request.movieName)
      movieId <- movieOpt match {
        case Some(movie) => throw MovieExistsException(movie.name)
        case None => for {
          movieId <- addMovie(MovieRecord(-1, request.movieName, request.year))
          _ <- DBIO.sequence(request.genreList.map {
            genre => movieGenreMappingService.addMovieGenreMapping(movieId, genre)
          })
        } yield movieId
      }
    } yield movieId).transactionally
  )

}

case class InvalidRatingUpdateRequest(movieId: Long, userRating: Option[Int], criticRating: Option[Int], userRatingCount: Option[Int], criticRatingCount: Option[Int])
  extends Exception(s"Invalid movie rating update request, movieId: $movieId, data: (userRating: $userRating, criticRating: $criticRating, userRatingCount: $userRatingCount, criticRatingCount: $criticRatingCount")
