package com.thecookiezen.flames

import com.thecookiezen.flames.stackframes.TimedFrame.{maxDepthOfFrame, removeTooNarrowFrames}
import com.thecookiezen.flames.stackframes.{FrameParser, FrameStackParser}
import com.thecookiezen.flames.svg.SvgFlameGraph.SvgElement
import com.thecookiezen.flames.svg.SvgPrinter.{TextItem, text}
import com.thecookiezen.flames.svg.{GraphConfig, SvgFlameGraph}

object FlameGraph {
  val invalidInput: GraphConfig => TextItem = config =>
    TextItem
      .fromConfig(config)
      .copy(
        fontSize = config.fontSize + 2,
        text = "ERROR: No valid input provided to flamegraph"
      )

  val title: GraphConfig => TextItem = config =>
    TextItem
      .fromConfig(config)
      .copy(
        fontSize = config.fontSize + 5,
        text = config.title
      )

  def render(stackFrames: Iterator[String], default: GraphConfig = GraphConfig.default): String = {
    val result = FrameStackParser.parseSamples(stackFrames)(FrameParser.parse)

    val frames = removeTooNarrowFrames(result.nodes, default.calculatesMinTime(result.totalTime))
    val maxDepth = maxDepthOfFrame(frames)
    val config = default.copy(imageHeight = default.calculatesImageHeight(maxDepth))

    val msg =
      if (result.totalTime == 0 || result.nodes.isEmpty)
        invalidInput(config)
      else
        title(config)

    SvgFlameGraph(title = _ => text(msg), frames = Seq.empty[SvgElement]).render(config)
  }
}
