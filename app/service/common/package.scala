package service

package object common {
  case class InvalidString(str: String, `trait`: Class[_]) extends Exception(s"Invalid ${`trait`.getName} $str specified")
}
