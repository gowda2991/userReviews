package service

import com.google.inject.{ImplementedBy, Inject, Singleton}
import controllers.{AddUserRequest, UserNotFoundException}
import service.models.UserLevel.{Critic, User}
import service.models.{UserRecord, Users}
import slick.jdbc.H2Profile.api._
import utils.database._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@ImplementedBy(classOf[UserServiceImpl])
trait UserService {
  def getUserInformation(name: String): DBIO[UserRecord]
  def createUserAndLevelMapping(request: AddUserRequest): Future[Long]
  def checkAndUpdateUserLevel(userReviewCount: Int, userId: Long): DBIO[Unit]
}

@Singleton
class UserServiceImpl @Inject()(userLevelsService: UserLevelsService,
                                userLevelMappingService: UserLevelMappingService) extends  UserService{
  val userQuery = Users.tableQuery

  override def getUserInformation(name: String): DBIO[UserRecord] =
    userQuery.filter(_.userName ===name).result.headOption.map(_.getOrElse(throw UserNotFoundException(name)))


  private def addUser(userRecord: UserRecord): DBIO[Long] =
    userQuery returning userQuery.map(_.userId) += userRecord

  /**
    *
    * @param request: Takes in add user request.
    *                 Creates a user record.
    *                 Adds User-Level mapping record.
    *                 Returns userId.
    * @return
    */
  override def createUserAndLevelMapping(request: AddUserRequest): Future[Long] = db.run(
    (for {
      userId <- addUser(UserRecord(-1, request.userName))
      levelId <- userLevelsService.getUserLevelByUserLevelName(User).map(_.levelId)
      _ <- userLevelMappingService.addUserLevelMappings(userId, levelId)
    }yield userId).transactionally
  )

  override def checkAndUpdateUserLevel(userReviewCount: Int, userId: Long): DBIO[Unit] = {

    if (userReviewCount == 3) {
      for {
        levelId <- userLevelsService.getUserLevelByUserLevelName(Critic).map(_.levelId)
        _ <- userLevelMappingService.addUserLevelMappings(userId, levelId)
      } yield Unit
    }
    else
      DBIO.successful(Unit)
  }
}
