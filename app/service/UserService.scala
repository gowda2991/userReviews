package service

import com.google.inject.Singleton
import service.models.{UserRecord, Users}
import slick.jdbc.H2Profile.api._
import utils.database._

import scala.concurrent.Future

trait UserService {

}

@Singleton
class UserServiceImpl extends UserService{
  val userQuery = Users.tableQuery
  def getUserInformation(name: String): Future[Option[UserRecord]] = db.run(
    userQuery.filter(_.userName ===name).result.headOption
  )

  def addUser(userRecord: UserRecord): Future[Long] = db.run(
    userQuery returning userQuery.map(_.userId) += userRecord
  )
}
