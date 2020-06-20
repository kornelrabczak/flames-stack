package com.thecookiezen.flames

case class Frame(function: String, depth: Long)
case class TimedFrame(function: Frame, startTime: Long, endTime: Long)

object TimedFrame {
  val maxDepthOfFrame: Seq[TimedFrame] => Long = _.maxBy(_.function.depth).function.depth

  val removeTooNarrowFrames: (Seq[TimedFrame], Double) => Seq[TimedFrame] = (frames, minTime) =>
    frames.filterNot(f => f.endTime - f.startTime < minTime)
}
