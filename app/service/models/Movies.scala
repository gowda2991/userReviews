package service.models

import play.api.libs.functional.syntax._
import play.api.libs.json.Format
import service.Tables.MovieTable
import service.common.InvalidString
import utils.database.dbProfile._

case class MovieRecord(movieId: Long, name: String, year: Long, genre: Genre)

class Movies(tag: Tag)extends Table[MovieRecord](tag, MovieTable.name){

  def movieId = column[Long]("movie_id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def year = column[Long]("year")
  def genre = column[Genre]("genre")

  override def * = (movieId, name, year, genre) <> ((MovieRecord.apply _).tupled, MovieRecord.unapply)

}

object Movies{
  val tableQuery = TableQuery[Movies]
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
    override def asString: String = "Action"
  }

  case object Romance extends Genre{
    override def asString: String = "Romance"
  }

  def all: List[Genre] = List(Comedy, Drama, Fiction, SciFi, Action, Romance)

  def fromString(str: String): Genre = all.collectFirst {
    case level if level.asString == str => level
  }.getOrElse(throw InvalidString(str, UserLevel.getClass))

  implicit val format: Format[Genre] = Format.of[String].inmap(fromString, _.asString)

  implicit val dbMapping: BaseColumnType[Genre] = MappedColumnType.base[Genre, String](_.asString, fromString)
}