package com.thecookiezen.flames

import com.thecookiezen.flames.FrameParser.ParsedFrame

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.Try

object FlameGraphs {
  type Stack = Seq[String]

  def parseSamples(stackFrames: Iterable[String])(frameParser: String => Option[ParsedFrame]): ParsingResult = {
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

  private def flow(nodesStartTime: mutable.Map[Frame, Long], lastStack: Stack, currentStack: Stack, time: Long): Seq[TimedFrame] = {
    val prev = lastStack.toIterator.buffered
    val current = currentStack.toIterator.buffered
    val nodes = ArrayBuffer[TimedFrame]()

    var depth = 0
    while (prev.headOption == current.headOption) {
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

  case class ParsingResult(ignored: Long, totalTime: Long, nodes: Seq[TimedFrame])
  case class Frame(function: String, depth: Long)
  case class TimedFrame(function: Frame, startTime: Long, endTime: Long)
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
