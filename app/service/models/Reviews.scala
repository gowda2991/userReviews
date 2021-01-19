package service.models

import service.Tables.ReviewTable
import utils.database.dbProfile._

case class ReviewRecord(reviewId: Long, userId: Long, movieId: Long, rating: Long)

class Reviews(tag: Tag)extends Table[ReviewRecord](tag, ReviewTable.name) {
  def reviewId = column[Long]("review_id", O.PrimaryKey, O.AutoInc)
  def userId = column[Long]("user_id")
  def movieId = column[Long]("movie_id")
  def rating = column[Long]("rating")

  def userIdFK = foreignKey("fk_users_userId", userId, Users.tableQuery)(_.userId)
  def movieIdFK = foreignKey("fk_movies_moviedId", movieId, Movies.tableQuery)(_.movieId)

  override def * = (reviewId, userId, movieId, rating) <> ((ReviewRecord.apply _).tupled, ReviewRecord.unapply)
}

object Reviews{
  val tableQuery = TableQuery[Reviews]
}
