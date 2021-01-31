package controllers

import javax.inject.Inject
import play.api.libs.json.{Format, Json, OFormat, Reads}
import play.api.mvc.{BaseController, ControllerComponents}
import service.models.Genre
import service._
import service.common.InvalidString
import play.api.libs.functional.syntax._

import scala.concurrent.ExecutionContext.Implicits.global

class MovieController @Inject()(val controllerComponents: ControllerComponents,
                                userService: UserService,
                                userLevelsService: UserLevelsService,
                                userLevelMappingService: UserLevelMappingService,
                                movieService: MovieService,
                                movieGenreMappingService: MovieGenreMappingService,
                                reviewService: ReviewService,
                                topRatedMovieService: TopRatedMovieService) extends BaseController{

  final val reviewCountForCriticUpgradation: Int = 3

  def addMovie = Action.async(parse.json[AddMovieRequest]) {
    request =>
      movieService.createMovie(request.body).map(res =>  Ok(Json.obj("result" -> "SUCCESS", "movieId" -> res))) recover {
        case ex: MovieNotFoundException => Ok(Json.obj("result" -> "FAILURE", "error" -> ex.getMessage))
        case e: Exception => InternalServerError(Json.obj("result" -> "FAILURE", "error" -> e.getMessage))

      }
  }

  def addReview = Action.async(parse.json[AddReviewRequest]) {
    request =>
      reviewService.createReview(request.body).map(_ => Ok(Json.obj("result" -> "SUCCESS", "message" -> "Review submitted"))) recover {
        case e: ReviewExistsException => Ok(Json.obj("result" -> "FAILURE", "error" -> e.getMessage))
        case e: Exception => InternalServerError(Json.obj("result" -> "FAILURE", "error" -> e.getMessage))
      }
  }

  def getTopRatedMovieByGenre = Action.async(parse.json[TopRatedMovieByGenreRequest]) {
    request =>
      topRatedMovieService.getTopRatedMovieByGenre(request.body.genre, request.body.limit).map(res => Ok(Json.obj("result" -> res)))
  }

  def getTopRatedMovieByYear = Action.async(parse.json[TopRatedMovieByYearRequest]) {
    request =>
      topRatedMovieService.getTopRatedMovieByYear(request.body.year, request.body.limit).map(res => Ok(Json.obj("result" -> res)))
  }

  def getTopRatedMovieByCriticByYear = Action.async(parse.json[TopRatedMovieByYearRequest]) {
    request =>
      topRatedMovieService.getTopRatedMovieByCriticByYear(request.body.year, request.body.limit).map(res => Ok(Json.obj("result" -> res)))
  }

  def getTopRatedMovieByAverageRatingByYear = Action.async(parse.json[TopRatedMovieByYearRequest]) {
    request =>
      topRatedMovieService.getTopRatedMovieByAverageRatingByYear(request.body.year, request.body.limit).map(res => Ok(Json.obj("result" -> res)))
  }

  def getTopRatedMovie(year: Option[Long], limit: Option[Int], mode: Option[String]) = Action.async{ request =>

    ???
  }

}


case class TopRatedMovieByGenreRequest(genre: Genre, limit: Int)

object TopRatedMovieByGenreRequest {
  implicit val reads: Reads[TopRatedMovieByGenreRequest] = Json.reads[TopRatedMovieByGenreRequest]
}

case class TopRatedMovieByYearRequest(year: Long, limit: Int)

object TopRatedMovieByYearRequest {
  implicit val reads: Reads[TopRatedMovieByYearRequest] = Json.reads[TopRatedMovieByYearRequest]
}

case class AddMovieRequest(movieName: String, year: Long, genreList: List[Genre])

object AddMovieRequest {
  implicit val formats: OFormat[AddMovieRequest] = Json.format
}

case class AddReviewRequest(userName: String, movieName: String, rating: Int)

object AddReviewRequest {
  implicit val formats: OFormat[AddReviewRequest] = Json.format
}

case class UserNotFoundException(userName: String) extends Exception(s"User with name: $userName not found. Note: User Name is case sensitive")

case class MovieNotFoundException(movieName: String) extends Exception(s"Movie with name: $movieName not found. Note: Movie Name is case sensitive")

case class InvalidMovieRatingException(rating: Int) extends Exception(s"Rating for a movie has to be between 1 to 10. Rating give: $rating")

case class LatestUserLevelMappingRecordNotFoundException(userId: Long) extends Exception(s"Latest User Level Mapping record not found for userId: $userId")

case class ReviewExistsException(userName: String, movieName: String) extends Exception(s"Review already submitted for movie $movieName by user $userName")

case class MovieExistsException(movieName: String) extends Exception(s"Movie $movieName already exists")


