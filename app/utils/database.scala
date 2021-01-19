package utils

import java.time.Year

import com.github.tototoshi.slick.{GenericJodaSupport, H2JodaSupport}
import slick.jdbc.H2Profile
import slick.jdbc.H2Profile.api._

object database {
  val dbProfile = H2Profile.api
  val jodaMapping: GenericJodaSupport = H2JodaSupport
  val db = Database.forConfig("default")

  implicit val yearDBMapping: BaseColumnType[Year] = MappedColumnType.base[Year, String](_.toString, Year.parse)
}
