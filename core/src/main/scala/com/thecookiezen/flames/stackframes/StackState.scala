package com.thecookiezen.flames.stackframes

import com.thecookiezen.flames.stackframes.FrameStackParser.Stack

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

case class StackState(ignored: Long, totalTime: Long, previousStack: Stack, timers: mutable.Map[Frame, Long], nodes: mutable.Buffer[TimedFrame])

object StackState {
  def initial: StackState = StackState(
    ignored = 0,
    totalTime = 0,
    previousStack = Seq.empty,
    timers = mutable.HashMap[Frame, Long](),
    nodes = ArrayBuffer[TimedFrame]()
  )
}
