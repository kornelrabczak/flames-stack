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

  val detailsPlaceholder: GraphConfig => TypedTag[String] = config =>
    text(
      TextItem
        .fromConfig(config)
        .copy(
          x = config.padLeftAndRight,
          y = config.imageHeight - (config.padBottom / 2),
          attributes = Seq(Attribute("id", "details")),
          location = None
        )
    )

  val resetZoom: GraphConfig => TypedTag[String] = config =>
    text(
      TextItem
        .fromConfig(config)
        .copy(
          x = config.padLeftAndRight,
          text = "Reset Zoom",
          attributes = Seq(
            Attribute("id", "unzoom"),
            Attribute("onclick", "unzoom()"),
            Attribute("style", "opacity:0.0;cursor:pointer")
          ),
          location = None
        )
    )

  val search: GraphConfig => TypedTag[String] = config =>
    text(
      TextItem
        .fromConfig(config)
        .copy(
          x = config.imageWidth - config.padLeftAndRight - 100,
          text = "Search",
          attributes = Seq(
            Attribute("id", "search"),
            Attribute("onclick", "search_prompt()"),
            Attribute("onmouseover", "searchover()"),
            Attribute("onmouseout", "searchout()"),
            Attribute("style", "opacity:0.1;cursor:pointer")
          ),
          location = None
        )
    )

  val searchResult: GraphConfig => TypedTag[String] = config =>
    text(
      TextItem
        .fromConfig(config)
        .copy(
          x = config.imageWidth - config.padLeftAndRight - 100,
          y = config.imageHeight - (config.padBottom / 2),
          attributes = Seq(Attribute("id", "matched")),
          location = None
        )
    )

  def text(item: TextItem): TypedTag[String] =
    svgTags.text(
      textAnchor := item.location.getOrElse("left"),
      x := item.x,
      y := item.y,
      fontSize := item.fontSize,
      fontFamily := "Verdana",
      fill := item.color,
      for (a <- item.attributes) yield attr(a.key) := a.value
    )(item.text)

  case class TextItem(
      color: String = blackColor,
      fontSize: Int,
      x: Double,
      y: Double,
      text: String = "",
      location: Option[String] = Some("middle"),
      attributes: Seq[Attribute] = Seq.empty
  )

  private[svg] case class Attribute(key: String, value: String)

  object TextItem {
    val blackColor = "rgb(0,0,0)"

    def fromConfig(config: GraphConfig): TextItem = TextItem(
      fontSize = config.fontSize,
      x = config.imageWidth / 2,
      y = config.fontSize * 2
    )
  }
}
