package service

import com.google.inject.{ImplementedBy, Inject, Singleton}
import controllers.{AddReviewRequest, ReviewExistsException}
import service.models.UserLevel.{Critic, User}
import service.models.{ReviewRecord, Reviews, UserLevel}
import slick.jdbc.H2Profile.api._
import utils.database._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@ImplementedBy(classOf[ReviewServiceImpl])
trait ReviewService{
  def addReview(record: ReviewRecord): DBIO[Int]
  def getReview(movieId: Long, userId: Long): DBIO[Option[ReviewRecord]]
  def getReviewsByUserId(userId: Long): DBIO[Seq[ReviewRecord]]
  def createReview(request: AddReviewRequest): Future[Unit]
}

@Singleton
class ReviewServiceImpl @Inject()(userService: UserService,
                                  userLevelMappingService: UserLevelMappingService,
                                  movieService: MovieService
                                 ) extends ReviewService {
  val query = Reviews.tableQuery

  override def addReview(record: ReviewRecord): DBIO[Int] =
    query += record


  override def getReview(movieId: Long, userId: Long): DBIO[Option[ReviewRecord]] =
    query.filter(r => r.movieId === movieId && r.userId === userId).result.headOption


  override def getReviewsByUserId(userId: Long): DBIO[Seq[ReviewRecord]] =
    query.filter(_.userId === userId).result

  /**
    *
    * @param request: Takes in add review request
    *                 Gets user review count. If count is 3, user will be upgraded as critic.
    *                 Inserts review record.
    *                 Updates movie record with the rating values and count.
    * @return
    */
  override def createReview(request: AddReviewRequest): Future[Unit] = db.run (
    for {
      user <- userService.getUserInformation(request.userName)
      userReviewCount <- getReviewsByUserId(user.userId).map(_.size)
      _ <- userService.checkAndUpdateUserLevel(userReviewCount, user.userId)
      abc <- userLevelMappingService.getUserRatingMultiplier(user.userId)
      movie <- movieService.getMovie(request.movieName)
      revOpt <- getReview(movie.movieId, user.userId)
      _ <- revOpt match {
        case None => for {
          res <- addReview(ReviewRecord(-1, user.userId, movie.movieId, request.rating * abc._2))
          _ <- abc._1 match {
            case User => movieService.updateMovieRating(movie.movieId, Some(movie.userRating + request.rating), None, Some(movie.userRatingCount + 1), None)
            case Critic => movieService.updateMovieRating(movie.movieId, None, Some(movie.criticRating + request.rating * abc._2), None, Some(movie.criticRatingCount+1))
          }
        } yield res
        case Some(_) => throw ReviewExistsException(user.userName, movie.name)
      }
    } yield Unit
  )
}
