package com.thecookiezen.flames

import com.thecookiezen.flames.stackframes.{Frame, TimedFrame}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class TimedFrameTest extends AnyFunSpec with Matchers {
  it("calculates max depth of all the frames") {
    TimedFrame.maxDepthOfFrame(Seq(
      TimedFrame(Frame("A", 2), 0, 1),
      TimedFrame(Frame("B", 15), 0, 1),
      TimedFrame(Frame("C", 8), 0, 1)
    )) shouldBe 15
  }

  it("check if frames are too narrow") {
    TimedFrame.isFrameTooNarrow(TimedFrame(Frame("A", 2), 0, 5), 10) shouldBe true
    TimedFrame.isFrameTooNarrow(TimedFrame(Frame("B", 15), 0, 15), 10) shouldBe false
    TimedFrame.isFrameTooNarrow(TimedFrame(Frame("C", 8), 3, 12), 10) shouldBe true
  }
}
