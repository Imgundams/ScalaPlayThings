package models

object Generator {
  private val clockticker = new java.util.concurrent.atomic.AtomicLong

  def unique: Long = clockticker.getAndIncrement + 1
}

