package com.thecookiezen.flames.svg

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SvgFlameGraphTest extends AnyFunSpec with Matchers {

  it("should render an empty SVG") {
    val svg = SvgFlameGraph(frames = Seq.empty)

    svg.render(GraphConfig.default) should contain allElementsOf Seq(
      "<svg",
      "<desc>",
      "<defs>",
      "<style",
      "<rect"
    )
  }

}
