package service

import slick.jdbc.H2Profile.api._
import service.models._

object Tables {
  sealed abstract class TableName(val name: String) {
    def tableQuery: TableQuery[_ <: Table[_]]

    final def createStatement: DBIOAction[Unit, NoStream, Effect.Schema] = tableQuery.schema.create
  }

  case object UserTable extends TableName("users"){
    override def tableQuery = Users.tableQuery
  }

  case object MovieTable extends TableName("movies"){
    override def tableQuery = Movies.tableQuery
  }

  case object ReviewTable extends TableName("reviews"){
    override def tableQuery = Reviews.tableQuery
  }

  case object UserLevelsTable extends TableName("user_levels"){
    override def tableQuery = UserLevels.tableQuery
  }

  case object UserLevelMappingsTable extends TableName("user_level_mappings"){
    override def tableQuery = UserLevelMappings.tableQuery
  }

  case object MovieGenreMappingsTable extends TableName("movie_genre_mappings"){
    override def tableQuery = MovieGenreMappings.tableQuery
  }

  def all: List[TableName] = List(UserTable, MovieTable, ReviewTable, UserLevelsTable, UserLevelMappingsTable, MovieGenreMappingsTable)
}
