package com.thecookiezen.flames.svg

case class Color (r:Int, g:Int, b:Int) {
  override def toString: String = s"rgb($r,$g,$b)"
}

object Color {
  val blackColor: Color = Color(0,0,0)
}
