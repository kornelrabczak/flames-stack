package com.thecookiezen.flames

case class Frame(function: String, depth: Long)
case class TimedFrame(function: Frame, startTime: Long, endTime: Long)

object TimedFrame {
  val maxDepthOfFrame: Seq[TimedFrame] => Long = _.maxBy(_.function.depth).function.depth

  def removeTooNarrowFrames(minTime: Int): Seq[TimedFrame] => Seq[TimedFrame] = _.filter(f => f.endTime - f.startTime < minTime)
}
