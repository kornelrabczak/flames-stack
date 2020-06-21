package com.thecookiezen.flames

import com.thecookiezen.flames.stackframes.{FrameStackParser, FrameParser}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.io.Source

class FrameStackParserTest extends AnyFunSpec with Matchers {

  describe("A stack frames parser") {
    it("should parse a frames to get stack with sample time") {
      val stackFrame = "function1;function2;function3 1"

      FrameParser.parse(stackFrame) shouldBe Some((Seq("function1", "function2", "function3"), 1))
    }

    it("should ignore fractional part of sample in a frame") {
      val stackFrame = "function1;function2;function3 1.34ow"

      FrameParser.parse(stackFrame) shouldBe Some((Seq("function1", "function2", "function3"), 1))
    }

    it("should return None for line with only white spaces") {
      FrameParser.parse("  ") shouldBe None
    }

    it("should return None for frame without sample data") {
      FrameParser.parse("test1;test2;test3 ") shouldBe None
    }

    it("should return None for frame without stack") {
      FrameParser.parse(" 1") shouldBe None
    }

    it("should return number of ignored lines because of parsing error") {
      FrameStackParser.parseSamples(Seq("f1;f2 ", "f1,f2,f3 3", ""))(FrameParser.parse).ignored shouldBe 2
    }

    it("should build a list of all of the nodes from stack frames") {
      val stackFrames = Seq(
        "f1 1",
        "f1;f2 1",
        "f1;f2;f3 1",
        "f1; 1",
        "f1;f4 1",
        "f1;f5 1",
        "a1;b1;c1 1"
      )

      val result = FrameStackParser.parseSamples(stackFrames)(FrameParser.parse)
      result.ignored shouldBe 0
      result.totalTime shouldBe 7
      result.nodes.size shouldBe 8
    }

    it("should build a list of all of the nodes from real life stack frames") {
      val stackFrames = Source.fromResource("java-stack.txt").getLines()

      val result = FrameStackParser.parseSamples(stackFrames.toSeq)(FrameParser.parse)
      println(result.nodes)
      result.ignored shouldBe 0
      result.totalTime shouldBe 7
      result.nodes.size shouldBe 8
    }
  }
}
