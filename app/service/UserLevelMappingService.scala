package service

import com.google.inject.{ImplementedBy, Inject, Singleton}
import controllers.LatestUserLevelMappingRecordNotFoundException
import service.models.{UserLevel, UserLevelMappingRecord, UserLevelMappings, UserLevels}
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits.global

@ImplementedBy(classOf[UserLevelMappingServiceImpl])
trait UserLevelMappingService {
  def addUserLevelMappings(userId: Long, levelId: Long): DBIO[Long]
  def getLatestUserLevel(userId: Long): DBIO[UserLevelMappingRecord]
  def getUserRatingMultiplier(userId: Long) : DBIO[(UserLevel, Int)]
}

@Singleton
class UserLevelMappingServiceImpl @Inject()() extends UserLevelMappingService {
  val query = UserLevelMappings.tableQuery
  val userLevelsQuery = UserLevels.tableQuery

  override def addUserLevelMappings(userId: Long, levelId: Long): DBIO[Long] =
    for{
      userLevelMappingOpt <- getUserLevelMappingByUserIdDBIO(userId)
      result <- userLevelMappingOpt match {
        case Some(rec) => for{
          newMappingId <- query returning query.map(_.mappingId) += UserLevelMappingRecord(-1, userId, levelId, None)
          _ <- updateUserMappingSupersededBy(newMappingId, rec.mappingId)
        }yield newMappingId
        case None => query returning query.map(_.mappingId) += UserLevelMappingRecord(-1, userId, levelId, None)
      }
    }yield result


  def getUserLevelMappingByUserIdDBIO(userId: Long): DBIO[Option[UserLevelMappingRecord]]  =
    query.filter(_.userId === userId).result.headOption

  def updateUserMappingSupersededBy(newMappingId: Long, oldMappingId: Long): DBIO[Int] =
    query.filter(_.mappingId === oldMappingId).map(_.supersededBy).update(Some(newMappingId))

  override def getLatestUserLevel(userId: Long): DBIO[UserLevelMappingRecord] =
    latestUserLevelMappingQuery(userId).result.headOption.map(_.getOrElse(throw LatestUserLevelMappingRecordNotFoundException(userId)))


  override def getUserRatingMultiplier(userId: Long): DBIO[(UserLevel, Int)] =
    (for{
      userLvlMapping <- latestUserLevelMappingQuery(userId)
      userLevelDetail <- userLevelsQuery if userLvlMapping.levelId === userLevelDetail.levelId
    }yield (userLevelDetail.userLevel, userLevelDetail.ratingMultiplier)).result.headOption.map(_.getOrElse(throw new Exception(s"Rating Multiplier details not found for userId: $userId")))


  private def latestUserLevelMappingQuery(userId: Long) = query.filter(r => r.userId === userId && r.supersededBy.isEmpty)
}
