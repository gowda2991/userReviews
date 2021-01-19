package modules

import com.google.inject.{AbstractModule, Singleton}
import play.api.Logger
import slick.jdbc.meta.MTable
import utils.database
import utils.database.dbProfile._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

@Singleton
class DBSetup extends AbstractModule {

  val logger: Logger = utils.logger

  override def configure(): Unit = {
    logger.info("DBSetup begins")
    service.Tables.all.map { tableName =>
      Await.result(database.db.run {
        MTable.getTables(tableName.name).headOption.flatMap {
          case Some(_) => DBIO.successful(false)
          case None =>
            tableName.createStatement
        }
      }, Duration.Inf)

    }
    logger.info("DBSetup ended")
  }
}