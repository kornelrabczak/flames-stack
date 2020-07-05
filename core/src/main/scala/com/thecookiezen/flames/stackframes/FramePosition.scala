package com.thecookiezen.flames.stackframes

import com.thecookiezen.flames.svg.GraphConfig

case class FramePosition(x1: Float, x2: Float, y1: Float, y2: Float) {
  def width: Float = x2 - x1
  def height: Float = y2 - y1
}

object FramePosition {
  def apply(config: GraphConfig, frame: TimedFrame): FramePosition = new FramePosition(
    x1 = config.padVertical + (frame.startTime * config.widthPerTime),
    x2 = config.padVertical + (frame.endTime * config.widthPerTime),
    y1 = config.imageHeight - config.padBottomWithLabels - (frame.frame.depth + 1) * config.frameHeight + config.framePad,
    y2 = config.imageHeight - config.padBottomWithLabels - frame.frame.depth * config.frameHeight
  )
}
