package controllers


import play.api.mvc._

class Application extends Controller {

  def index = Action {
    Ok(views.html.index("Hello World."))
  }

  // -- Page not found

  def brokenLink = Action { implicit request =>
    NotFound(views.html.pageNotFound())
  }

  val echo = Action { request =>
    Ok("Got request ["+request+"]")
  }

  // -- hello
  def hello(name: String) = Action {
    Ok(views.html.hello("Hello "+ name))
  }

  def someEdit(id: String) = Action {
    Ok(s"The id given here is: $id")
  }
  def someCreate() = Action {
    Ok("The id given here is nothing")
  }
  def someUpdate(id: String) = Action {
    Ok(s"The id given here is: $id")
  }
  def someDelete(id: String) = Action {
    Ok(s"The id given here is: $id")
  }
}