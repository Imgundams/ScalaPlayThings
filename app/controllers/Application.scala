package controllers

import java.io.{ByteArrayOutputStream, File}
import javax.imageio.ImageIO
import javax.inject.Inject
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64
import akka.stream.scaladsl.Source
import akka.stream.{Materializer, scaladsl}
import play.api.mvc._
import play.api.Play._
//import play.api.i18n.Messages.Implicits._
import play.api.i18n.{I18nSupport, MessagesApi}
import models.Item
import play.api.http.ContentTypes
import play.api.libs.Comet
import play.api.libs.json._
import scala.concurrent.duration._

class Application @Inject()(val messagesApi: MessagesApi, val materializer: Materializer) extends Controller with I18nSupport {


  def index = Action {
    Ok(
      views.html.index("Hello World.")).withCookies(Cookie("Cookie", "Not_tasty", Option(10800)))
  }


  def help = Action {
    Ok(views.html.playframework("Play Framework Help"))
  }

  // -- Page not found

  def brokenLink = Action { implicit request =>
    NotFound(views.html.pageNotFound()).withCookies(Cookie("Cookie", "Kind_of_tasty", Option(10800)))
  }

  val echo = Action { implicit request =>
    Ok("Got request [" + request + "]")
  }

  def map() = Action {
    Redirect("https://www.google.co.uk/maps")
  }

  def boot() = Action {
    Ok("Here is a static page")
  }

  // -- hello
  def hello(name: String) =
    Action {
      if (name.isEmpty) {
        Ok(views.html.hello("Stranger")).withCookies(Cookie("Cookie", "Yummy", Option(10800)))
      }
      else
        Ok(views.html.hello(name)).withCookies(Cookie("Cookie", "Yummy", Option(10800)))
    }

  def static() = Action {
    Ok(views.html.static("Here is a static page")).withCookies(Cookie("Cookie", "Very_tasty", Option(10800)))
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
    Ok(views.html.cookieForYou("Eat your cookies!")).withCookies(Cookie("Cookie", "Nasty", Option(10800)))
  }

  def showMeTheCookie() = Action {
    implicit request =>
      val taste = request.cookies.get("Cookie")
      val tastes = if (taste.isEmpty) {
        "Tasteless"
      } else {
        taste.get.value
      }
      Ok(views.html.cookies(tastes))
  }

  def eatCookie() = Action { implicit request =>
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
    Ok(views.html.welcome("Logged in")).withSession("connected" -> "Imgundams@gmail.com")
  }

  def session() = Action { implicit request =>
    request.session.get("connected").map { user =>
      Ok(views.html.welcome("You are Logged in!"))
    }.getOrElse {
      Unauthorized(views.html.leave("Get out of here!"))
    }
  }

  def leaveSession() = Action {
    Ok(views.html.welcome("You are logged out")).withNewSession
  }

  // flash

  def flashSession() = Action {
    Redirect("/static").flashing("set" -> "true", "success" -> "good job")
  }


  // Items

  def listItems() = Action {implicit request =>
//    request.session.get("connected").map { user =>
      Ok(views.html.listItemsAdmin(Item.Items, Item.createItemForm))
//    }.getOrElse {
//      Unauthorized(views.html.listItems(Item.Items, Item.createItemForm))
//    }
  }

  def createItem = Action {implicit request =>

    val formValidationResult = Item.createItemForm.bindFromRequest
    formValidationResult.fold({ formWithErrors =>
      BadRequest(views.html.listItemsAdmin(Item.Items, formWithErrors))
    }, { item =>
      Item.Items.append(item)
      Redirect(routes.Application.listItems())
    })
  }


  def edit(index: Int) = Action { implicit request =>

    if (index > Item.Items.length) {
      Redirect(routes.Application.brokenLink())
    } else {
      request.session.get("connected").map { user =>
        Ok(views.html.edit(Item.Items, Item.createItemForm.fill(Item.Items(index)), index))
      }.getOrElse {
        Unauthorized(views.html.listItems(Item.Items, Item.createItemForm))
      }
    }
  }

  def editItem(index: Int) = Action { implicit request =>
    //    val indexToUse = index.getOrElse(-1)
    val formResult = Item.createItemForm.bindFromRequest
    formResult.fold({ formWithErrors =>
      BadRequest(views.html.listItemsAdmin(Item.Items, formWithErrors))
    }, { item =>

      Item.Items.update(index, item)
      Redirect(routes.Application.listItems())
    }
    )
  }

  def delete(index: Int) = Action { implicit request =>
    Item.Items.remove(index)
    Redirect(routes.Application.listItems())
  }


  //Comet

  def clocker = Action{Ok(views.html.comet())}

  def cometString = Action {
    implicit val m = materializer
    def stringSource: Source[String, _] = Source(List("kiki", "foo", "bar"))
    Ok.chunked(stringSource via Comet.string("parent.cometMessage")).as(ContentTypes.HTML)
  }

  def cometJson = Action {
    implicit val m = materializer
    def jsonSource: Source[JsValue, _] = Source(List(JsString("jsonString")))
    Ok.chunked(jsonSource via Comet.json("parent.cometMessage")).as(ContentTypes.HTML)
  }

  def streamClock() = Action {
    Ok.chunked(stringSource via Comet.string("parent.clockChanged")).as(ContentTypes.HTML)
  }

  def stringSource: Source[String, _] = {
    val df: DateTimeFormatter = DateTimeFormatter.ofPattern("HH mm ss")
    val tickSource = Source.tick(0 second, 1 second, "TICK")
    val s = tickSource.map((tick) => df.format(ZonedDateTime.now()))
    s
  }

  //Upload

  def upload = Action(parse.multipartFormData) { implicit request =>
    val uploadService: UploadService = UploadService
    val result = uploadService.uploadFile(request)
    Redirect(routes.Application.index)
  }


  def returnBytedImage: String = {
    try {
      val image = ImageIO.read(new File("C:/Users/Public/Pictures/Sample Pictures/epsom.png"))
      val baos = new ByteArrayOutputStream
      ImageIO.write(image, "png", baos)
      val res = baos.toByteArray
      val encodedImage = Base64.encode(baos.toByteArray)
      System.out.println("encoded image " + encodedImage)
      return encodedImage
    } catch {
      case e: Exception =>
        e.printStackTrace()
        System.out.println("Error occured")
    }
    "Byting failed"
  }

}