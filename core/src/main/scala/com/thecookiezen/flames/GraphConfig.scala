package com.thecookiezen.flames

case class GraphConfig(
    imageMaxWidth: Int,
    frameHeight: Int,
    fontSize: Int,
    fontWith: Double,
    minFunctionWidth: Double,
    padTop: Int,
    padBottom: Int,
    padTop2: Int,
    padLeftAndRight: Int,
    framePad: Int,
    maxDepth: Int,
    bgColor1: String,
    bgColor2: String
)

object GraphConfig {
  val defaultFontSize = 12

  val default = GraphConfig(
    imageMaxWidth = 1200,
    frameHeight = 16,
    fontSize = defaultFontSize,
    fontWith = 0.59,
    minFunctionWidth = 0.1,
    padTop = defaultFontSize * 3,
    padBottom = defaultFontSize * 2 + 10,
    padTop2 = defaultFontSize * 2,
    padLeftAndRight = 10,
    framePad = 1,
    maxDepth = 0,
    bgColor1 = "#f8f8f8",
    bgColor2 = "#e8e8e8"
  )
}
