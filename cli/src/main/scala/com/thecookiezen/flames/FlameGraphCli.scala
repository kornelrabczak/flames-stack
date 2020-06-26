package com.thecookiezen.flames

import scala.io.{Codec, Source}

object FlameGraphCli extends App {

  private val sourceFileName: String = args(0)
  private val bufferedSource = Source.fromFile(sourceFileName, Codec.UTF8.name)

  try {
    val lines: Iterator[String] = bufferedSource.getLines()
    Console.print(FlameGraph.render(lines))
  } finally {
    bufferedSource.close()
  }
}
