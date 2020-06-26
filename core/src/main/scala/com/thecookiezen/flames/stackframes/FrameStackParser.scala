package com.thecookiezen.flames.stackframes

import com.thecookiezen.flames.stackframes.FrameParser.ParsedFrame

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object FrameStackParser {
  type Stack = Seq[String]

  def parseSamples(stackFrames: Iterator[String])(frameParser: String => Option[ParsedFrame]): ParsingResult = {
    var ignored = 0
    var totalTime: Long = 0
    var previousStack = Seq.empty[String]

    val timers = mutable.HashMap[Frame, Long]()
    val frames = ArrayBuffer[TimedFrame]()

    for (frame <- stackFrames) {
      frameParser(frame) match {
        case Some((currentStack, currentSample)) =>
          frames ++= flow(timers, previousStack, currentStack, totalTime)
          previousStack = currentStack
          totalTime += currentSample
        case None => ignored += 1
      }
    }

    // need to call flow again with empty current stack to finish all frames from the previous run
    if (previousStack.nonEmpty)
      frames ++= flow(timers, previousStack, Seq.empty, totalTime)

    ParsingResult(ignored, totalTime, frames)
  }

  private def flow(nodesStartTime: mutable.Map[Frame, Long], lastStack: Stack, currentStack: Stack, time: Long): ArrayBuffer[TimedFrame] = {
    val prev = lastStack.toIterator.buffered
    val current = currentStack.toIterator.buffered
    val nodes = ArrayBuffer[TimedFrame]()

    var depth = 1
    while (prev.nonEmpty && prev.headOption == current.headOption) {
      prev.next()
      current.next()
      depth += 1
    }

    // finishes previous nodes that already ended
    for ((last, i) <- prev.zipWithIndex) {
      val key = Frame(function = last, depth = depth + i)

      nodesStartTime
        .remove(key)
        .fold(println(s"Did not have start time for $key"))(nodes += TimedFrame(key, _, time))
    }

    // starts new node
    for ((current, i) <- current.zipWithIndex) {
      val key = Frame(function = current, depth = depth + i)
      nodesStartTime.put(key, time)
    }

    nodes
  }
}
