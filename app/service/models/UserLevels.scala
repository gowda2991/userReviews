package service.models

import play.api.libs.json.Format
import service.common.InvalidString
import play.api.libs.functional.syntax._
import service.Tables.UserLevelsTable
import utils.database.dbProfile._

case class UserLevelRecord(levelId: Long, userLevel: UserLevel, ratingMultiplier: Int)

class UserLevels(tag: Tag) extends Table[UserLevelRecord](tag, UserLevelsTable.name){
  def levelId = column[Long]("level_id", O.PrimaryKey, O.AutoInc)
  def userLevel = column[UserLevel]("user_level")
  def ratingMultiplier = column[Int]("rating_multiplier")

  def * = (levelId, userLevel, ratingMultiplier)<> ((UserLevelRecord.apply _).tupled, UserLevelRecord.unapply)

}

object UserLevels{
  val tableQuery = TableQuery[UserLevels]
}

sealed trait UserLevel{
  def asString: String
  override def toString = asString
}

object UserLevel{
  case object User extends UserLevel{
    override def asString: String = "USER"
  }

  case object Critic extends UserLevel{
    override def asString: String = "CRITIC"
  }

  def all: List[UserLevel] = List(User, Critic)

  def fromString(str: String): UserLevel = all.collectFirst {
    case level if level.asString == str => level
  }.getOrElse(throw InvalidString(str, UserLevel.getClass))

  implicit val format: Format[UserLevel] = Format.of[String].inmap(fromString, _.asString)

  implicit val dbMapping: BaseColumnType[UserLevel] = MappedColumnType.base[UserLevel, String](_.asString, fromString)
}



