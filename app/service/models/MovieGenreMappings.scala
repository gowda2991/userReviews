package service.models

import play.api.libs.json.Format
import service.common.InvalidString
import play.api.libs.functional.syntax._
import service.Tables.MovieGenreMappingsTable
import utils.database.dbProfile._


case class MovieGenreMappingRecord(id: Long, movieId: Long, genre: Genre)

class MovieGenreMappings(tag: Tag) extends Table[MovieGenreMappingRecord](tag, MovieGenreMappingsTable.name){
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def movieId = column[Long]("movie_id")
  def genre = column[Genre]("genre")

  def movieIdFK = foreignKey(tableName + "fk_movies_moviedId", movieId, Movies.tableQuery)(_.movieId)

  def * = (id, movieId, genre)<>((MovieGenreMappingRecord.apply _).tupled, MovieGenreMappingRecord.unapply)
}

object MovieGenreMappings{
  val tableQuery = TableQuery[MovieGenreMappings]
}

sealed trait Genre{
  def asString: String
  override def toString = asString
}

object Genre{
  case object Comedy extends Genre{
    override def asString: String = "COMEDY"
  }

  case object Drama extends Genre{
    override def asString: String = "DRAMA"
  }

  case object Fiction extends Genre{
    override def asString: String = "FICTION"
  }

  case object SciFi extends Genre{
    override def asString: String = "SCI_FI"
  }

  case object Action extends Genre{
    override def asString: String = "ACTION"
  }

  case object Romance extends Genre{
    override def asString: String = "ROMANCE"
  }

  def all: List[Genre] = List(Comedy, Drama, Fiction, SciFi, Action, Romance)

  def fromString(str: String): Genre = all.collectFirst {
    case level if level.asString == str => level
  }.getOrElse(throw InvalidString(str, UserLevel.getClass))

  implicit val format: Format[Genre] = Format.of[String].inmap(fromString, _.asString)

  implicit val dbMapping: BaseColumnType[Genre] = MappedColumnType.base[Genre, String](_.asString, fromString)
}
