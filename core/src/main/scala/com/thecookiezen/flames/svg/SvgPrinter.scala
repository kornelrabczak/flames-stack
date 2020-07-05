package com.thecookiezen.flames.svg

import com.thecookiezen.flames.stackframes.TimedFrame.{info, stripAnnotations, truncateFunctionName}
import com.thecookiezen.flames.stackframes.{FramePosition, TimedFrame}
import scalatags.Text.implicits._
import scalatags.Text.svgAttrs._
import scalatags.Text.{TypedTag, svgTags}

import scala.io.Source

object SvgPrinter {

  val svgHeader: GraphConfig => TypedTag[String] = config =>
    svgTags.svg(
      xmlns := "http://www.w3.org/2000/svg",
      xmlnsXlink := "http://www.w3.org/1999/xlink",
      width := config.imageWidth,
      height := config.imageHeight,
      attr("onload") := "init(evt)",
      viewBox := s"0 0 ${config.imageWidth} ${config.imageHeight}"
    )

  val invalidInput: GraphConfig => TypedTag[String] = config =>
    text(
      TextItem
        .fromConfig(config)
        .copy(
          text = "ERROR: No valid input provided to flamegraph",
          attributes = Seq(Attribute("id", "title"))
        )
    )

  val title: GraphConfig => TypedTag[String] = config =>
    text(
      TextItem
        .fromConfig(config)
        .copy(
          text = config.title,
          attributes = Seq(Attribute("id", "title"))
        )
    )

  val description: TypedTag[String] = svgTags.desc("""Flame graph stack visualization.
                 |See https://github.com/brendangregg.FlameGraph for latest version,
                 |and http://www.brendangregg.com/flamegraphs.html for examples.""".stripMargin)

  val definitions: GraphConfig => TypedTag[String] = config =>
    svgTags.defs(
      svgTags.linearGradient(id := "background", y1 := "0", y2 := "1", x1 := "0", x2 := "0")(
        svgTags.stop(stopColor := config.bgColor1.toString, offset := "5%"),
        svgTags.stop(stopColor := config.bgColor2.toString, offset := "95%")
      )
    )

  val style: GraphConfig => TypedTag[String] = config => tag("style")(`type` := "text/css")(s"""
      |	text { font-family:${config.fontFamily}; font-size:${config.fontSize}px; fill:${Color.blackColor}; }
      |	#search { opacity:0.1; cursor:pointer; }
      |	#search:hover, #search.show { opacity:1; }
      |	#subtitle { text-anchor:middle; font-color:rgb(160,160,160); }
      |	#title { text-anchor:middle; font-size:17px}
      |	#unzoom { cursor:pointer; }
      |	#frames > *:hover { stroke:black; stroke-width:0.5; cursor:pointer; }
      |	.hide { display:none; }
      |	.parent { opacity:0.5; }
      |""".stripMargin)

  val rect: GraphConfig => TypedTag[String] = config =>
    svgTags.rect(
      x := 0,
      y := 0,
      width := config.imageWidth,
      height := config.imageHeight,
      fill := "url(#background)"
    )

  val javascript: GraphConfig => TypedTag[String] = config => {
    val searchColor = Color(230, 0, 230)

    tag("script")(`type` := "text/ecmascript")(raw(s"""
        |<![CDATA[
        |  var nametype = "Function:";
        |  var fontsize = ${config.fontSize};
        |  var fontwidth = ${config.fontWidth};
        |  var xpad = ${config.padVertical};
        |  var inverted = ${false};
        |  var searchcolor = "${searchColor.toString}";
        |  
        |  ${Source.fromResource("flamegraph.js").mkString}
        |]]>
        |""".stripMargin))(
      )
  }

  val detailsPlaceholder: GraphConfig => TypedTag[String] = config =>
    text(
      TextItem(
        x = config.padVertical,
        y = config.imageHeight - (config.padBottomWithLabels.toFloat / 2),
        attributes = Seq(Attribute("id", "details")),
        text = " "
      )
    )

  val resetZoom: GraphConfig => TypedTag[String] = config =>
    text(
      TextItem
        .fromConfig(config)
        .copy(
          x = config.padVertical,
          text = "Reset Zoom",
          attributes = Seq(
            Attribute("id", "unzoom"),
            Attribute("class", "hide")
          )
        )
    )

  val search: GraphConfig => TypedTag[String] = config =>
    text(
      TextItem
        .fromConfig(config)
        .copy(
          x = config.imageWidth - config.padVertical - 100,
          text = "Search",
          attributes = Seq(
            Attribute("id", "search")
          )
        )
    )

  val searchResult: GraphConfig => TypedTag[String] = config =>
    text(
      TextItem(
        x = config.imageWidth - config.padVertical - 100,
        y = config.imageHeight - (config.padBottomWithLabels.toFloat / 2),
        attributes = Seq(Attribute("id", "matched"))
      )
    )

  val frames: Seq[TypedTag[String]] => TypedTag[String] = frames => {
    svgTags.g(
      id := "frames"
    )(frames: _*)
  }

  val frame: (TimedFrame, Long) => GraphConfig => TypedTag[String] = (frame, totalTime) =>
    config => {
      val framePosition = FramePosition(config, frame)
      val fitchars = ((framePosition.x2 - framePosition.x1) / (config.fontSize * config.fontWidth)).toInt

      svgTags.g(
        tag("title")(info(frame)(totalTime)),
        svgTags.rect(
          x := framePosition.x1,
          y := framePosition.y1,
          width := framePosition.width,
          height := framePosition.height,
          fill := Color(242, 10, 32).toString
        ),
        text(
          TextItem(
            text = truncateFunctionName(stripAnnotations(frame.frame.function), fitchars),
            x = framePosition.x1 + 3,
            y = 3 + (framePosition.y1 + framePosition.y2) / 2
          )
        )
      )
    }

  def text(item: TextItem): TypedTag[String] =
    svgTags.text(
      x := item.x,
      y := item.y,
      for (a <- item.attributes) yield attr(a.key) := a.value
    )(item.text)

  case class TextItem(
      x: Double,
      y: Double,
      text: String = "",
      attributes: Seq[Attribute] = Seq.empty
  )

  private[svg] case class Attribute(key: String, value: String)

  object TextItem {
    def fromConfig(config: GraphConfig): TextItem = TextItem(
      x = config.imageWidth.toFloat / 2,
      y = config.fontSize * 2
    )
  }
}
