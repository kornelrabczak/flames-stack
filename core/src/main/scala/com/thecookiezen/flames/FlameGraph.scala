package com.thecookiezen.flames

import com.thecookiezen.flames.stackframes.TimedFrame.{maxDepthOfFrame, removeTooNarrowFrames}
import com.thecookiezen.flames.stackframes.{Frame, ParsingResult, TimedFrame}
import com.thecookiezen.flames.svg.SvgFlameGraph.SvgElement
import com.thecookiezen.flames.svg.SvgPrinter.{TextItem, text}
import com.thecookiezen.flames.svg.{GraphConfig, SvgFlameGraph}
import scalatags.Text.TypedTag

object FlameGraph {
  val invalidInput: GraphConfig => TextItem = config =>
    TextItem(
      fontSize = config.fontSize + 2,
      text = "ERROR: No valid input provided to flamegraph"
    )

  val title: GraphConfig => TextItem = config =>
    TextItem(
      fontSize = config.fontSize + 5,
      text = config.title
    )

  def render(stackFrames: Iterable[String], default: GraphConfig = GraphConfig.default): String = {
    val result = ParsingResult(0, 100, Seq(TimedFrame(Frame("f1", 5), 0, 15)))
//    val result = FrameStackParser.parseSamples(stackFrames)

    val frames = removeTooNarrowFrames(result.nodes, default.calculatesMinTime(result.totalTime))
    val maxDepth = maxDepthOfFrame(frames)
    val config = default.copy(imageHeight = default.calculatesImageHeight(maxDepth))

    val (titleSvg: SvgElement, framesToDraw) =
      if (result.totalTime == 0 || result.nodes.isEmpty)
        (invalidInput(config), Seq.empty[SvgElement])
      else
        (text(title(config)), Seq.empty[SvgElement])

    SvgFlameGraph(title = titleSvg, frames = framesToDraw).render(config)
  }
}
