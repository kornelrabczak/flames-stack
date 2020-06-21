package com.thecookiezen.flames.svg

case class GraphConfig(
    title: String,
    imageWidth: Long,
    imageHeight: Long,
    frameHeight: Long,
    fontSize: Int,
    fontWidth: Double,
    minFunctionWidth: Double,
    padTop: Int,
    padBottom: Int,
    padTop2: Int,
    padLeftAndRight: Int,
    framePad: Int,
    maxDepth: Int,
    bgColor1: String,
    bgColor2: String
) {
  def calculatesImageHeight(maxDepth: Long): Long = ((maxDepth + 1) * frameHeight) + padTop + padTop2
  def calculatesMinTime(totalTime: Long): Double = {
    val widthPerTime = (imageWidth - 2 * padLeftAndRight).toFloat / totalTime.toFloat
    minFunctionWidth / widthPerTime
  }
}

object GraphConfig {
  val defaultFontSize = 12

  val default = GraphConfig(
    title = "Flame graph",
    imageWidth = 1200,
    imageHeight = defaultFontSize * 5,
    frameHeight = 16,
    fontSize = defaultFontSize,
    fontWidth = 0.59,
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
