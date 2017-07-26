package models

import play.api.data._
import play.api.data.Forms._

import scala.collection.mutable.ArrayBuffer

case class Item(name: String, price: Int)

object Item {

  val createItemForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "price" -> number(min = 0, max = 400)
    )(Item.apply)(Item.unapply)
  )

  val Items = ArrayBuffer(
    Item("Super Nintendo Entertainment System", 199),
    Item("Playstation", 299),
    Item("Sega Saturn", 399)
  )

}