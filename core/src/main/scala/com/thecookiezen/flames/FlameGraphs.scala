package com.thecookiezen.flames

import com.thecookiezen.flames.FrameParser.ParsedFrame

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.Try

object FlameGraphs {
  type Stack = Seq[String]
  case class ParsingResult(ignored: Long, totalTime: Long, nodes: Seq[TimedStackFrameElement])
  case class StackFrameElement(function: String, depth: Long)
  case class TimedStackFrameElement(function: StackFrameElement, startTime: Long, endTime: Long)

  def parseSamples(stackFrames: Iterable[String])(frameParser: String => Option[ParsedFrame]): ParsingResult = {
    var ignored = 0
    var totalTime: Long = 0
    var previousStack = Seq.empty[String]

    val timers = mutable.HashMap[StackFrameElement, Long]()
    val nodes = ArrayBuffer[TimedStackFrameElement]()

    for (frame <- stackFrames) {
      frameParser(frame) match {
        case Some((currentStack, currentSample)) =>
          nodes ++= flow(timers, previousStack, currentStack, totalTime)
          previousStack = currentStack
          totalTime += currentSample
        case None => ignored += 1
      }
    }

    nodes ++= flow(timers, previousStack, Seq.empty, totalTime)

    ParsingResult(ignored, totalTime, nodes)
  }

  private def flow(
            nodesStartTime: mutable.Map[StackFrameElement, Long],
            lastStack: Stack,
            currentStack: Stack,
            time: Long
          ): Seq[TimedStackFrameElement] = {
    val prev = lastStack.toIterator.buffered
    val current = currentStack.toIterator.buffered
    val nodes = ArrayBuffer[TimedStackFrameElement]()

    var depth = 0
    while (prev.headOption == current.headOption) {
      prev.next()
      current.next()
      depth += 1
    }

    // finishes previous nodes that already ended
    for ((last, i) <- prev.zipWithIndex) {
      val key = StackFrameElement(function = last, depth = depth + i)

      nodesStartTime
        .remove(key)
        .fold(println(s"Did not have start time for $key"))(nodes += TimedStackFrameElement(key, _, time))
    }

    // starts new node
    for ((current, i) <- current.zipWithIndex) {
      val key = StackFrameElement(function = current, depth = depth + i)
      nodesStartTime.put(key, time)
    }

    nodes
  }
}

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
