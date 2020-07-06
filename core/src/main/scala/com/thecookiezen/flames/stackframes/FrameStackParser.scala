package com.thecookiezen.flames.stackframes

import com.thecookiezen.flames.stackframes.FrameParser.ParsedFrame

object FrameStackParser {
  type Stack = Seq[String]

  def parseSamples(stackFrames: Iterator[String])(frameParser: String => Option[ParsedFrame]): ParsingResult = {
    val state = calculateState(stackFrames)(frameParser)
    ParsingResult(ignored = state.ignored, totalTime = state.totalTime, nodes = state.nodes.toSeq)
  }

  private def calculateState(stackFrames: Iterator[String])(frameParser: String => Option[ParsedFrame]) = {
    val state = stackFrames.foldLeft(StackState.initial) {
      case (state, frames) =>
        frameParser(frames) match {
          case Some((currentStack, currentSample)) =>
            flow(state, currentStack)
              .copy(
                totalTime = state.totalTime + currentSample,
                previousStack = currentStack
              )
          case None => state.copy(ignored = state.ignored + 1)
        }
    }

    // calculates state for the last frame
    val endState = flow(state, Seq.empty)

    // adds base node that covers whole time range
    endState.nodes.prepend(TimedFrame.empty.copy(endTime = state.totalTime))

    endState
  }

  private def flow(state: StackState, currentStack: Stack) = {
    val prev = state.previousStack.iterator.buffered
    val current = currentStack.iterator.buffered

    var depth = 1
    while (prev.nonEmpty && prev.headOption == current.headOption) {
      prev.next()
      current.next()
      depth += 1
    }

    // finishes previous nodes that already ended
    for ((last, i) <- prev.zipWithIndex) {
      val key = Frame(function = last, depth = depth + i)

      state.timers
        .remove(key)
        .fold(println(s"Did not have start time for $key"))(state.nodes += TimedFrame(key, _, state.totalTime))
    }

    // starts new node
    for ((current, i) <- current.zipWithIndex) {
      val key = Frame(function = current, depth = depth + i)
      state.timers.put(key, state.totalTime)
    }

    state
  }
}
