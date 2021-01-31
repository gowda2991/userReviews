package service.models

import service.Tables.UserTable
import utils.database.dbProfile._

case class UserRecord(userId: Long, userName: String)

class Users(tag: Tag)  extends Table[UserRecord](tag, UserTable.name){
  def userId = column[Long]("user_id", O.PrimaryKey, O.AutoInc)
  def userName = column[String]("user_name")

  override def * = (userId, userName) <> ((UserRecord.apply _).tupled, UserRecord.unapply)
}

object Users{
  val tableQuery = TableQuery[Users]
}
