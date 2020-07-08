package com.thecookiezen.flames.svg

import scala.annotation.tailrec

case class Color (r:Int, g:Int, b:Int) {
  override def toString: String = s"rgb($r,$g,$b)"
}

object Color {
  val blackColor: Color = Color(0,0,0)

  def byFunction(functionName: String): Color = {
    val f1 = hashFunctionName(functionName)
    val f2, f3 = hashFunctionName(functionName.reverse)

    Color(
      r = (205 + 50 * f3).toInt,
      g = (0 + 230 * f1).toInt,
      b = (0 + 55 * f2).toInt
    )
  }

  def hashFunctionName(function: String): Float = {
    val splits = function.split("`", 1).lastOption.getOrElse("")

    val (vector, _, max, _) = foldWhile((0f, 1f, 1f, 10))(splits)(_._4 <= 12) {
      case ((vec, weight, max, mod), char) =>
        val i = char % mod
        (vec + (i.toFloat / (mod - 1)) * weight, weight * 0.7f, max + 1 * weight, mod + 1)
    }
    1 - vector / max
  }

  def foldWhile[A, T](zero: A)(items: Iterable[T])(until: A => Boolean)(op: (A, T) => A): A = {
    @tailrec
    def loop(acc: A, remaining: Iterable[T]): A = {
      if (remaining.isEmpty || !until(acc)) acc else loop(op(acc, remaining.head), remaining.tail)
    }

    loop(zero, items)
  }
}