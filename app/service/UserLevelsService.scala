package service

import com.google.inject.{ImplementedBy, Inject, Singleton}
import service.models.{UserLevel, UserLevelRecord, UserLevels}
import utils.database._
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@ImplementedBy(classOf[UserLevelServiceImpl])
trait UserLevelsService {
  def getUserLevelByUserLevelName(userLevel: UserLevel): Future[Option[UserLevelRecord]]
  def updateUserLevelRecordWithMultiplier(userLevel: UserLevel, ratingMultiplier: Int): Future[Unit]
  def insertUserLevel(userLevelRecord: UserLevelRecord): Future[Unit]
}

@Singleton
class UserLevelServiceImpl @Inject()() extends UserLevelsService {
  val userLevelQuery = UserLevels.tableQuery
  override def getUserLevelByUserLevelName(userLevel: UserLevel): Future[Option[UserLevelRecord]] = db.run(
    userLevelQuery.filter(_.userLevel === userLevel).result.headOption
  )

  override def updateUserLevelRecordWithMultiplier(userLevel: UserLevel, ratingMultiplier: Int): Future[Unit] = db.run(
    userLevelQuery.filter(_.userLevel === userLevel).map(_.ratingMultiplier).update(ratingMultiplier).map(_ => Unit)
  )

  override def insertUserLevel(userLevelRecord: UserLevelRecord): Future[Unit] = db.run(
    (userLevelQuery += userLevelRecord).map(_ => Unit)
  )
}
