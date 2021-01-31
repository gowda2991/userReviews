package service.models

import service.Tables.MovieTable
import utils.database.dbProfile._

case class MovieRecord(movieId: Long, name: String, year: Long, userRating: Int = 0, criticRating: Int = 0, userRatingCount: Int =0, criticRatingCount: Int = 0)

class Movies(tag: Tag)extends Table[MovieRecord](tag, MovieTable.name){

  def movieId = column[Long]("movie_id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def year = column[Long]("year")
  def userRating = column[Int]("user_rating")
  def criticRating = column[Int]("critic_rating")
  def userRatingCount = column[Int]("user_rating_count")
  def criticRatingCount = column[Int]("critic_rating_count")

  override def * = (movieId, name, year, userRating, criticRating, userRatingCount, criticRatingCount) <> ((MovieRecord.apply _).tupled, MovieRecord.unapply)

}

object Movies{
  val tableQuery = TableQuery[Movies]
}