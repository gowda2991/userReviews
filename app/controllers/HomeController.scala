package controllers

import javax.inject._
import play.api._
import play.api.libs.json.{Json, OFormat, Reads}
import play.api.mvc._
import service.models.UserLevel.{Critic, User}
import service.models.{Genre, MovieRecord, ReviewRecord, UserRecord}
import service.{MovieGenreMappingService, MovieService, ReviewService, TopRatedMovieService, UserLevelMappingService, UserLevelsService, UserService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents,
                               userService: UserService,
                               userLevelsService: UserLevelsService,
                               userLevelMappingService: UserLevelMappingService,
                               movieService: MovieService,
                               movieGenreMappingService: MovieGenreMappingService,
                               reviewService: ReviewService,
                               topRatedMovieService: TopRatedMovieService) extends BaseController {


  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }
}
