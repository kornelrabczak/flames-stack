package com.thecookiezen.flames

import com.thecookiezen.flames.stackframes.TimedFrame.{isFrameTooNarrow, maxDepthOfFrame}
import com.thecookiezen.flames.stackframes.{FrameParser, FrameStackParser}
import com.thecookiezen.flames.svg.{GraphConfig, SvgFlameGraph}
import com.thecookiezen.flames.svg.SvgPrinter._

object FlameGraph {
  def render(stackFrames: Iterator[String], default: GraphConfig = GraphConfig.default): String = {
    val result = FrameStackParser.parseSamples(stackFrames)(FrameParser.parse)

    val widthPerTime = default.calculatesWidthPerTime(result.totalTime)
    val minTime = default.minFunctionWidth / widthPerTime
    val frames = result
      .nodes
      .filterNot(isFrameTooNarrow(_, minTime))

    val maxDepth = maxDepthOfFrame(frames)
    val imageHeight = default.calculatesImageHeight(maxDepth)

    val config = default.copy(
      imageHeight = imageHeight,
      widthPerTime = widthPerTime
    )

    SvgFlameGraph(
      title = _ => if (result.totalTime == 0 || result.nodes.isEmpty) invalidInput(config) else title(config),
      frames = frames.map(frame(_, result.totalTime))
    ).render(config)
  }
}
