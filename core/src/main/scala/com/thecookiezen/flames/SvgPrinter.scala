package com.thecookiezen.flames

import scalatags.Text.implicits._
import scalatags.Text.svgAttrs._
import scalatags.Text.{TypedTag, svgTags}

object SvgPrinter {

  val svg: GraphConfig => TypedTag[String] = config =>
    svgTags.svg(
      xmlns := "http://www.w3.org/2000/svg",
      width := config.imageMaxWidth,
      height := config.fontSize * 5,
      attr("onload") := "init(evt)",
      viewBox := s"0 0 ${config.imageMaxWidth} ${config.fontSize * 5}"
    )

  val description: String = """Flame graph stack visualization.
                 |See https://github.com/brendangregg.FlameGraph for latest version,
                 |and http://www.brendangregg.com/flamegraphs.html for examples.""".stripMargin

  val invalidInput: GraphConfig => TextItem = config =>
    TextItem(
      color = "black",
      fontSize = config.fontSize + 2,
      x = config.imageMaxWidth / 2,
      y = config.fontSize * 2,
      text = "ERROR: No valid input provided to flamegraph",
      location = Some("middle")
    )

  def main(args: Array[String]): Unit = {
    val config = GraphConfig.default

    val r = svg(config)(
      svgTags.desc(description),
      printTextElement(invalidInput(config))
    )

    println(r.render)
  }

  def printTextElement(item: TextItem): TypedTag[String] = {
    svgTags.text(
      textAnchor := item.location.getOrElse("left"),
      x := item.x,
      y := item.y,
      fontSize := item.fontSize,
      fontFamily := "Verdana",
      fill := item.color,
      for (a <- item.attributes) yield attr(a.key) := a.value
    )(item.text)
  }

  case class Attribute(key: String, value: String)

  case class TextItem(
      color: String,
      fontSize: Int,
      x: Long,
      y: Long,
      text: String,
      location: Option[String],
      attributes: Seq[Attribute] = Seq.empty
  )
}
