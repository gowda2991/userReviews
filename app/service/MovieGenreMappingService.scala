package service

import com.google.inject.{ImplementedBy, Singleton}
import service.models.{Genre, MovieGenreMappingRecord, MovieGenreMappings}
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits.global

@ImplementedBy(classOf[MovieGenreMappingServiceImpl])
trait MovieGenreMappingService{
  def addMovieGenreMapping(movieId: Long, genre: Genre): DBIO[Int]
  def getGenreByMovieId(movieId: Long): DBIO[List[MovieGenreMappingRecord]]
  def getMoviesIdsByGenre(genre: Genre): DBIO[List[MovieGenreMappingRecord]]
}

@Singleton
class MovieGenreMappingServiceImpl extends MovieGenreMappingService {
  val query = MovieGenreMappings.tableQuery

  override def addMovieGenreMapping(movieId: Long, genre: Genre): DBIO[Int] =
    query += MovieGenreMappingRecord(-1, movieId, genre)


  override def getGenreByMovieId(movieId: Long): DBIO[List[MovieGenreMappingRecord]] =
    query.filter(_.movieId === movieId).result.map(_.toList)


  override def getMoviesIdsByGenre(genre: Genre): DBIO[List[MovieGenreMappingRecord]] =
    query.filter(_.genre === genre).result.map(_.toList)

}
