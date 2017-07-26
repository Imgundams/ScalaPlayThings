package controllers


import play.api.mvc._
import play.api.Play._
import play.api.i18n.Messages.Implicits._
import models.Item

class Application extends Controller {

  def index = Action {
    Ok(views.html.index("Hello World.")).withCookies(Cookie("Cookie", "Not_tasty"))
  }

  // -- Page not found

  def brokenLink = Action { implicit request =>
    NotFound(views.html.pageNotFound()).withCookies(Cookie("Cookie", "Kind_of_tasty"))
  }

  val echo = Action { request =>
    Ok("Got request [" + request + "]")
  }

  def map() = Action {
    Redirect("https://www.google.co.uk/maps")
  }

  def boot() = Action {
    Ok("Here is a static page")
  }

  // -- hello
  def hello(name: Option[String]) = Action {
    Ok(views.html.hello(name)).withCookies(Cookie("Cookie", "Yummy"))
  }

  def static() = Action {
    Ok(views.html.static("Here is a static page")).withCookies(Cookie("Cookie", "Very_tasty"))
  }

  def dynamic(string: String) = Action {
    Ok(views.html.dynamic("Here is a dynamic page", string))

  }

  def option(string: Option[String]) = Action {
    Ok(views.html.option("Here is an option page, write \"?string=something\" in the url", string))
  }

  def reverseRoute() = Action {
    Redirect(routes.Application.index())
  }


  def foreverTodo() = TODO

  //  Cookies
  def cookie() = Action {
    //        Ok(views.html.cookies("Eat your cookies!")).withCookies(Cookie("Chocolate Chip","Not tasty"))
    Ok(views.html.cookieForYou("Eat your cookies!")).withCookies(Cookie("Cookie", "Nasty"))
  }

  def showMeTheCookie() = Action {
    request =>
      val taste = request.cookies.get("Cookie")
      val tastes = if (taste.isEmpty) {
        "Tasteless"
      } else {
        taste.get.value
      }
      Ok(views.html.cookies(tastes))
  }

  def eatCookie() = Action { request =>
    val cookieTaste = request.cookies.get("Cookie")
    val tastes = if (cookieTaste.isEmpty) {
      "Tasteless"
    } else {
      cookieTaste.get.value
    }
    Ok(views.html.eatCookie(s"Cookie was $tastes. Your cookie has been destroyed")).discardingCookies(DiscardingCookie("Cookie"))
  }

  // Session

  def sessionPlease() = Action {
    Ok("You are Welcome!").withSession("connected" -> "Imgundams@gmail.com")
  }

  def session() = Action { request =>
    request.session.get("connected").map { user =>
      Ok("Welcome " + user)
    }.getOrElse {
      Unauthorized(views.html.leave("Get out of here!"))
    }
  }

  def leaveSession() = Action {
    Ok("You are logged out").withNewSession
  }

  // flash

  def flashSession() = Action {
    Redirect("/static").flashing("set" -> "true", "success" -> "good job")
  }

  def listItems() = Action {implicit request =>
    Ok(views.html.listItems(Item.Items, Item.createItemForm))
  }

  // Items

  def createItem = Action { implicit request =>

    val formValidationResult = Item.createItemForm.bindFromRequest
    formValidationResult.fold({ formWithErrors =>
      BadRequest(views.html.listItems(Item.Items, formWithErrors))
    }, { item =>
      Item.Items.append(item)
      Redirect(routes.Application.listItems())
    })
  }


  // TODO Functions
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