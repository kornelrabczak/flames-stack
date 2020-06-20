package com.thecookiezen.flames

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

  it("removes frames from the list that are too short") {
    TimedFrame.removeTooNarrowFrames(Seq(
      TimedFrame(Frame("A", 2), 0, 5),
      TimedFrame(Frame("B", 15), 0, 15),
      TimedFrame(Frame("C", 8), 3, 12)
    ), 10) should have size 1
  }
}
