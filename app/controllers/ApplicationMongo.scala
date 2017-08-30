package controllers
// http://reactivemongo.org/releases/0.12/documentation/tutorial/play.html
import javax.inject.Inject

import scala.concurrent.Future
import play.api.Logger
import play.api.mvc.{Action, AnyContent, Controller}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._

// Reactive Mongo imports
import reactivemongo.api.Cursor

import play.modules.reactivemongo.{ // ReactiveMongo Play2 plugin
MongoController,
ReactiveMongoApi,
ReactiveMongoComponents
}
import play.api.data.Form
import models._
import models.JsonFormats._
import reactivemongo.play.json._, collection._


class ApplicationMongo @Inject()(
                                                    val reactiveMongoApi: ReactiveMongoApi) extends Controller
  with MongoController with ReactiveMongoComponents {
  def collection: Future[JSONCollection] = database.map(_.collection[JSONCollection]("persons"))


  def create: Action[AnyContent] = Action.async {
    //case class User(age: Int, firstName: String, lastName: String, feeds: Seq[Feed])
    //case class Feed(name: String, url: String)
    val user = User(29, "FirstName", "Lastname", List(Feed("BBC news", "http://www.bbc.co.uk")))
    val futureResult = collection.flatMap(_.insert(user))
    futureResult.map(_ => Ok("Added user " + user.firstName + " " + user.lastName))
  }

  def findByName: Action[AnyContent] = Action.async {
    val cursor: Future[Cursor[User]] = collection.map {
      _.find(Json.obj("lastName" -> "Lastname"))
        .sort(Json.obj("created" -> -1))
        .cursor[User]
    }
    val futureUsersList: Future[List[User]] = cursor.flatMap(_.collect[List]())
    futureUsersList.map { persons =>
      Ok(persons.head.toString)
    }
  }
}