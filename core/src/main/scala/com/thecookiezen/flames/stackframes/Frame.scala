package com.thecookiezen.flames.stackframes

case class Frame(function: String, depth: Long)
case class TimedFrame(frame: Frame, startTime: Long, endTime: Long)

object TimedFrame {
  private val numberFormat = java.text.NumberFormat.getIntegerInstance
  private val percentFormat = java.text.NumberFormat.getPercentInstance
  percentFormat.setMaximumFractionDigits(2)

  def empty: TimedFrame = TimedFrame(frame = Frame("", 0), startTime = 0, endTime = 0)

  val isFrameTooNarrow: (TimedFrame, Float) => Boolean = (frame, minTime) =>
    frame.endTime - frame.startTime < minTime

  val info: TimedFrame => Long => String = frame => totalTime => {
    val samples = frame.endTime - frame.startTime
    val samplesText = numberFormat.format(samples)

    if (isEmpty(frame)) {
      s"all ($samplesText, 100%)"
    } else {
      val pct = samples.toFloat / totalTime.toFloat
      s"${stripAnnotations(frame.frame.function)} ($samplesText samples, ${percentFormat.format(pct)})"
    }
  }

  val isEmpty: TimedFrame => Boolean = f => f.frame.function.isEmpty && f.frame.depth == 0

  val stripAnnotations: String => String = functionName => {
    if (functionName.endsWith("]")) {
      val annotationStartIndex = functionName.lastIndexOf("_[")
      if ("kwij".contains(functionName.charAt(annotationStartIndex + 2)))
        functionName.substring(0, annotationStartIndex)
      else {
        functionName
      }
    } else {
      functionName
    }
  }

  val truncateFunctionName: (String, Int) => String = (functionName, fitChars) => {
    if (fitChars >= 3) {
      if (functionName.length < fitChars)
        functionName
      else
        functionName.take(fitChars - 2) + ".."
    } else {
      ""
    }
  }
}
