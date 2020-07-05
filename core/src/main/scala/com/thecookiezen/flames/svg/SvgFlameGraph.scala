package com.thecookiezen.flames.svg

import com.thecookiezen.flames.svg.SvgFlameGraph.SvgElement
import scalatags.Text.TypedTag

case class SvgFlameGraph(
    header: SvgElement = SvgPrinter.svgHeader,
    description: SvgElement = _ => SvgPrinter.description,
    style: SvgElement = SvgPrinter.style,
    definitions: SvgElement = SvgPrinter.definitions,
    javascript: SvgElement = SvgPrinter.javascript,
    background: SvgElement = SvgPrinter.rect,
    title: SvgElement = SvgPrinter.title,
    details: SvgElement = SvgPrinter.detailsPlaceholder,
    resetZoom: SvgElement = SvgPrinter.resetZoom,
    search: SvgElement = SvgPrinter.search,
    searchResult: SvgElement = SvgPrinter.searchResult,
    frames: Seq[SvgElement]
) {
  def render(graphConfig: GraphConfig): String = {
    val framess = frames.map(f => f(graphConfig))

    header(graphConfig)(
      description(graphConfig),
      style(graphConfig),
      definitions(graphConfig),
      javascript(graphConfig),
      background(graphConfig),
      title(graphConfig),
      details(graphConfig),
      resetZoom(graphConfig),
      search(graphConfig),
      searchResult(graphConfig)
    )(SvgPrinter.frames(framess))
  }.render
}

object SvgFlameGraph {
  type SvgElement = GraphConfig => TypedTag[String]
}
