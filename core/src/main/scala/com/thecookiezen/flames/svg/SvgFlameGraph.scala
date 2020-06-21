package com.thecookiezen.flames.svg

import com.thecookiezen.flames.svg.SvgFlameGraph.SvgElement
import scalatags.Text.{TypedTag, svgTags}

case class SvgFlameGraph(
    header: SvgElement = SvgPrinter.svgHeader,
    description: SvgElement = _ => SvgPrinter.description,
    style: SvgElement = _ => SvgPrinter.style,
    definitions: SvgElement = SvgPrinter.definitions,
    javascript: SvgElement = SvgPrinter.javascript,
    background: SvgElement = SvgPrinter.rect,
    details: SvgElement = SvgPrinter.detailsPlaceholder,
    resetZoom: SvgElement = SvgPrinter.resetZoom,
    search: SvgElement = SvgPrinter.search,
    searchResult: SvgElement = SvgPrinter.searchResult,
    title: SvgElement,
    frames: Seq[SvgElement]
) {
  def render(graphConfig: GraphConfig): String = {
    val framess = frames.map(f => f(graphConfig))

    svgTags.svg(header(graphConfig))(
      description(graphConfig),
      style(graphConfig),
      definitions(graphConfig),
      javascript(graphConfig),
      background(graphConfig),
      title(graphConfig)
    )(framess: _*)
  }.render
}

object SvgFlameGraph {
  type SvgElement = GraphConfig => TypedTag[String]
}
