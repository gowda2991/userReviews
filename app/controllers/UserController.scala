package controllers

import javax.inject.Inject
import play.api.libs.json.{Json, Reads}
import play.api.mvc.{BaseController, ControllerComponents}
import service.UserService

import scala.concurrent.ExecutionContext.Implicits.global

class UserController  @Inject()(val controllerComponents: ControllerComponents,
                                userService: UserService
                               ) extends BaseController {

  def addUser = Action.async(parse.json[AddUserRequest]) {
    request =>
      userService.createUserAndLevelMapping(request.body).map(res => Ok(Json.obj("result" -> "SUCCESS", "userId" -> res)))
  }

}

case class AddUserRequest(userName: String)

object AddUserRequest {
  implicit val reads: Reads[AddUserRequest] = Json.reads[AddUserRequest]
}