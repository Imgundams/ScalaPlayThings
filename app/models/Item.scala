package models

import play.api.data._
import play.api.data.Forms._

import scala.collection.mutable.ArrayBuffer
import controllers.Application
import views.html.helper.inputFile
case class Item(itemName: String, description: String, maker: String,
                warranty: String, price: Int, discount: String, seller: String, picture: String)


object Item {

  val createItemForm =
    Form(
      mapping(
        "itemName" -> nonEmptyText,
        "description" -> default(text(maxLength = 64), "No description given"),
        "maker" -> nonEmptyText,
        "warranty" -> default(text(maxLength = 64),"no Warranty"),
        "price" -> number(min = 0, max = 400),
        "discount" -> default(text(maxLength = 64),"no discount"),
        "seller" -> nonEmptyText,
        "picture" -> default(text(maxLength = 128),"no picture")

      )
      (Item.apply)(Item.unapply)
    )

  val Items = ArrayBuffer(
    Item("SNES","Super Nintendo Entertainment System","Nintendo","1 year",199,"no discount","Amazon","http://icons.iconarchive.com/icons/sykonist/console/128/Snes-icon.png"),
    Item("NES","Nintendo Entertainment System","Nintendo","1 year",189,"25% off","Amazon","http://www.iconeasy.com/icon/ico/Game/Console/Nintendo%20mix.ico")
    //    Item("Sega Saturn", 399, "soo much lol")
  )


}

