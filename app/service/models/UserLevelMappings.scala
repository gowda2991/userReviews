package service.models

import service.Tables.UserLevelMappingsTable
import utils.database.dbProfile._

case class UserLevelMappingRecord(mappingId: Long, userId: Long, levelId: Long, supersededBy: Option[Long])

class UserLevelMappings(tag: Tag) extends Table[UserLevelMappingRecord](tag, UserLevelMappingsTable.name){

  def mappingId = column[Long]("mapping_id", O.PrimaryKey, O.AutoInc)
  def userId = column[Long]("user_id")
  def levelId = column[Long]("level_id")
  def supersededBy = column[Option[Long]]("superseded_by")

  def userIdFK = foreignKey(tableName + "fk_users_userId", userId, Users.tableQuery)(_.userId)
  def movieIdFK = foreignKey(tableName+ "fk_levels_levelId", levelId, UserLevels.tableQuery)(_.levelId)
  def supersededByFK = foreignKey(tableName+ "fk_mappings_supersededBy", supersededBy, UserLevelMappings.tableQuery)(_.mappingId.?)

  def * = (mappingId, userId, levelId, supersededBy)<> ((UserLevelMappingRecord.apply _).tupled, UserLevelMappingRecord.unapply)
}

object UserLevelMappings{
  val tableQuery = TableQuery[UserLevelMappings]
}
