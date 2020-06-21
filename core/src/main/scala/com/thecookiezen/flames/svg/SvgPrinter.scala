package com.thecookiezen.flames.svg

import com.thecookiezen.flames.svg.SvgPrinter.TextItem.blackColor
import scalatags.Text.implicits.{raw, _}
import scalatags.Text.svgAttrs._
import scalatags.Text.{TypedTag, svgTags}

import scala.io.Source

object SvgPrinter {

  val svgHeader: GraphConfig => TypedTag[String] = config =>
    svgTags.svg(
      xmlns := "http://www.w3.org/2000/svg",
      width := config.imageWidth,
      height := config.imageHeight,
      attr("onload") := "init(evt)",
      viewBox := s"0 0 ${config.imageWidth} ${config.imageHeight}"
    )

  val description: TypedTag[String] = svgTags.desc("""Flame graph stack visualization.
                 |See https://github.com/brendangregg.FlameGraph for latest version,
                 |and http://www.brendangregg.com/flamegraphs.html for examples.""".stripMargin)

  val definitions: GraphConfig => TypedTag[String] = config =>
    svgTags.defs(
      svgTags.linearGradient(id := "background", y1 := "0", y2 := "1", x1 := "0", x2 := "0")(
        svgTags.stop(stopColor := config.bgColor1, offset := "5%"),
        svgTags.stop(stopColor := config.bgColor2, offset := "95%")
      )
    )

  val style: TypedTag[String] = tag("style")(`type` := "text/css")("func_g:hover {stroke:black; stroke-width:0.5; cursor:pointer; }")

  val rect: GraphConfig => TypedTag[String] = config =>
    svgTags.rect(
      x := 0,
      y := 0,
      width := config.imageWidth,
      height := config.imageHeight,
      fill := "url(#background)"
    )

  val javascript: GraphConfig => TypedTag[String] = config => {
    tag("script")(`type` := "text/ecmascript")(raw(s"""
        |<![CDATA[
        |  var nametype = "Function:";
        |  var fontsize = ${config.fontSize};
        |  var fontwidth = ${config.fontWidth};
        |  var xpad = ${config.padLeftAndRight};
        |  var inverted = false;
        |  var searchcolor = "rgb(230,0,230)";
        |  
        |  ${Source.fromResource("flamegraph.js").mkString}
        |]]>
        |""".stripMargin))(
      )
  }

  def text(item: TextItem): GraphConfig => TypedTag[String] = config => {
    svgTags.text(
      textAnchor := item.location.getOrElse("left"),
      x := config.imageWidth / 2,
      y := config.fontSize * 2,
      fontSize := item.fontSize,
      fontFamily := "Verdana",
      fill := item.color,
      for (a <- item.attributes) yield attr(a.key) := a.value
    )(item.text)
  }

  case class TextItem(
      color: String = blackColor,
      fontSize: Int,
      text: String,
      location: Option[String] = Some("middle"),
      attributes: Seq[Attribute] = Seq.empty
  )

  private[svg] case class Attribute(key: String, value: String)

  object TextItem {
    val blackColor = "rgb(0,0,0)"
  }
}
