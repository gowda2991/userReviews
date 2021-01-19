package modules

import com.google.inject.{AbstractModule, Inject, Singleton}
import play.api.Logger
import play.api.libs.json.{Json, Reads}
import service.UserLevelsService
import service.models.{UserLevel, UserLevelRecord}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


@Singleton
class UserLevelsSetup @Inject()(userLevelsService: UserLevelsService) extends AbstractModule {

  val logger: Logger = utils.logger

  val userLevelsJsonPath = "app/utils/user-levels.json"

  override def configure(): Unit = {
    logger.info("User Level Setup Begins")
    val source = scala.io.Source.fromFile(userLevelsJsonPath, "utf-8")
    val string = try source.mkString finally source.close()

    val jsonData = Json.parse(string).as[List[UserLevelDTO]]
    for{
      _ <- Future.sequence(jsonData .map {
        level =>
          for{
            levelOpt <- userLevelsService.getUserLevelByUserLevelName(level.levelName)
            _ <- levelOpt match {
              case Some(l) => userLevelsService.updateUserLevelRecordWithMultiplier(l.userLevel, level.ratingMultiplier)
              case None => userLevelsService.insertUserLevel(UserLevelRecord(-1, level.levelName, level.ratingMultiplier))
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
