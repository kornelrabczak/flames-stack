package com.thecookiezen.flames.stackframes

case class ParsingResult(ignored: Long, totalTime: Long, nodes: Seq[TimedFrame])
