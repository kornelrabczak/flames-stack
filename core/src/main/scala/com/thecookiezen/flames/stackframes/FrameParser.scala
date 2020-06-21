package com.thecookiezen.flames.stackframes

import scala.util.Try

object FrameParser {
  type ParsedFrame = (Seq[String], Long)

  def parse(stackFrame: String): Option[ParsedFrame] = {
    val trimmedFrame = stackFrame.trim
    if (trimmedFrame.isEmpty) {
      Option.empty
    }

    val lastIndexOfSpace = trimmedFrame.lastIndexOf(' ') match {
      case -1 => Option.empty
      case i  => Some(i)
    }

    lastIndexOfSpace.flatMap { index =>
      val (stack, sample) = trimmedFrame.splitAt(index)
      val trimmedSample = sample.trim
      if (trimmedSample.isEmpty)
        None
      else {
        val sampleWithoutFraction = trimmedSample.split('.').head
        Try((stack.trim.split(';').toSeq, sampleWithoutFraction.toLong)).toOption
      }
    }
  }
}
