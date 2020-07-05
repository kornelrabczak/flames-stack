package com.thecookiezen.flames.svg

case class GraphConfig(
    title: String,
    imageWidth: Long,
    imageHeight: Long,
    frameHeight: Long,
    fontSize: Int,
    fontFamily: String,
    fontWidth: Float,
    minFunctionWidth: Float,
    widthPerTime: Float,
    padTopWithTitle: Int,
    padBottomWithLabels: Int,
    padTopWithSubtitle: Int,
    padVertical: Int,
    framePad: Int,
    maxDepth: Int,
    bgColor1: Color,
    bgColor2: Color
) {
  def calculatesImageHeight(maxDepth: Long): Long = ((maxDepth + 1) * frameHeight) + padTopWithTitle + padBottomWithLabels
  def calculatesWidthPerTime(totalTime: Long): Float = {
    (imageWidth - 2 * padVertical).toFloat / totalTime.toFloat
  }
}

object GraphConfig {
  val defaultFontSize = 12

  val default: GraphConfig = GraphConfig(
    title = "Flame Graph",
    imageWidth = 1200,
    imageHeight = defaultFontSize * 5,
    frameHeight = 16,
    fontSize = defaultFontSize,
    fontFamily = "Verdana",
    fontWidth = 0.59f,
    minFunctionWidth = 0.1f,
    widthPerTime = 0f,
    padTopWithTitle = defaultFontSize * 3,
    padBottomWithLabels = defaultFontSize * 2 + 10,
    padTopWithSubtitle = defaultFontSize * 2,
    padVertical = 10,
    framePad = 1,
    maxDepth = 0,
    bgColor1 = Color(238,238,238),
    bgColor2 = Color(238,238,176)
  )
}
