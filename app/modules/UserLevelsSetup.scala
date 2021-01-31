package modules

import com.google.inject.{AbstractModule, Inject, Singleton}
import play.api.Logger
import play.api.libs.json.{Json, Reads}
import service.UserLevelsService
import service.models.{UserLevel, UserLevelRecord, UserLevels}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import utils.database._
import slick.jdbc.H2Profile.api._



@Singleton
class UserLevelsSetup @Inject()() extends AbstractModule {

  val logger: Logger = utils.logger

  val userLevelsJsonPath = "app/utils/user-levels.json"

  val userLevelQuery = UserLevels.tableQuery

  override def configure(): Unit = {
    logger.info("User Level Setup Begins")
    val source = scala.io.Source.fromFile(userLevelsJsonPath, "utf-8")
    val string = try source.mkString finally source.close()

    val jsonData = Json.parse(string).as[List[UserLevelDTO]]
    for{
      _ <- Future.sequence(jsonData .map {
        level =>
          for{
            levelOpt <- db.run( userLevelQuery.filter(_.userLevel === level.levelName).result.headOption)
            _ <- levelOpt match {
              case Some(l) => db.run( userLevelQuery.filter(_.userLevel === l.userLevel).map(_.ratingMultiplier).update(level.ratingMultiplier).map(_ => Unit))
              case None => db.run((userLevelQuery += UserLevelRecord(-1, level.levelName, level.ratingMultiplier)).map(_ => Unit))
            }
          }yield Unit
      })
    }yield {
      logger.info("User Level Setup Ends")
      Unit
    }
  }
}

case class UserLevelDTO(levelName: UserLevel, ratingMultiplier: Int)
object UserLevelDTO{
  implicit val reads: Reads[UserLevelDTO] = Json.reads
}
