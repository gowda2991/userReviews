package service

import com.google.inject.{ImplementedBy, Singleton}
import service.models.{UserLevel, UserLevelRecord, UserLevels}
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits.global

@ImplementedBy(classOf[UserLevelServiceImpl])
trait UserLevelsService {
  def getUserLevelOptByUserLevelName(userLevel: UserLevel): DBIO[Option[UserLevelRecord]]
  def getUserLevelByUserLevelName(userLevel: UserLevel): DBIO[UserLevelRecord]
  def updateUserLevelRecordWithMultiplier(userLevel: UserLevel, ratingMultiplier: Int): DBIO[Unit]
  def insertUserLevel(userLevelRecord: UserLevelRecord): DBIO[Unit]
}

@Singleton
class UserLevelServiceImpl extends UserLevelsService {
  val userLevelQuery = UserLevels.tableQuery
  override def getUserLevelOptByUserLevelName(userLevel: UserLevel): DBIO[Option[UserLevelRecord]] =
    userLevelQuery.filter(_.userLevel === userLevel).result.headOption


  override def getUserLevelByUserLevelName(userLevel: UserLevel): DBIO[UserLevelRecord] =
    getUserLevelOptByUserLevelName(userLevel).map(_.getOrElse(throw new Exception(s"User Level record not found for $userLevel")))


  override def updateUserLevelRecordWithMultiplier(userLevel: UserLevel, ratingMultiplier: Int): DBIO[Unit] =
    userLevelQuery.filter(_.userLevel === userLevel).map(_.ratingMultiplier).update(ratingMultiplier).map(_ => Unit)


  override def insertUserLevel(userLevelRecord: UserLevelRecord): DBIO[Unit] =
    (userLevelQuery += userLevelRecord).map(_ => Unit)

}
