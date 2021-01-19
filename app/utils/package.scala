import org.joda.time.DateTime
import play.api.Logger
import play.api.libs.json.{Format, JodaReads, JodaWrites}

package object utils {
  lazy val logger = Logger("application")

  implicit val jodaDateTimeFormat: Format[DateTime] = Format[DateTime](JodaReads.JodaDateReads, JodaWrites.JodaDateTimeWrites)
}
