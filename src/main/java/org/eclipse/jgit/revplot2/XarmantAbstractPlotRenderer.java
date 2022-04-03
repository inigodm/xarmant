//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.eclipse.jgit.revplot2;

import javafx.scene.paint.Color;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revplot.PlotCommit;
import org.eclipse.jgit.revplot.PlotLane;
import org.eclipse.jgit.revwalk.RevFlag;

public abstract class XarmantAbstractPlotRenderer<TLane extends PlotLane> {
  private static final int LINE_PAD = 7;
  private static final int LINE_WIDTH = 2;
  private static final int POINT_SIZE = 8;

  protected void paintWorking(PlotCommit<TLane> working, PlotCommit<TLane> parentCommit, int h) {
    int dotSize = POINT_SIZE * 2;
    int myLaneX = laneX(parentCommit.getLane());
    int dotX = myLaneX - dotSize / 2 - 1;
    int dotY = (h - dotSize) / 2;

    drawPassingLanes(parentCommit, h);
    this.drawBoundaryDot(dotX, dotY, dotSize, dotSize);
    int textx = Math.max(myLaneX + LINE_PAD, dotX + dotSize) + 8;
    this.drawText("Working", textx + dotSize, h);
  }

  protected void paintCommit(PlotCommit<TLane> commit, int h) {
    int dotSize = POINT_SIZE;
    int myLaneX = laneX(commit.getLane());
    int dotX = myLaneX - dotSize / 2;
    int dotY = (h - dotSize) / 2;

    drawLines(commit, h, commit.getLane(), myLaneX, dotY);
    drawCommitPointInPosition(commit, dotSize, dotX, dotY);
    drawTextReferences(commit, h, myLaneX, dotX);
  }

  private void drawLines(final PlotCommit<TLane> commit, final int h, final TLane myLane, int myLaneX, final int dotY) {
    drawPassingLanes(commit, h);
    Color myColor = this.laneColor(myLane);
    drawMergingLanes(commit, h, myLaneX, myColor);
    drawForkingOffLines(commit, h, myLaneX, myColor, dotY);
  }

  private void drawPassingLanes(final PlotCommit<TLane> commit, final int h) {
    for(PlotLane passingLane: commit.passingLanes) {
      int laneX = laneX((TLane)passingLane);
      Color color = this.laneColor((TLane)passingLane);
      this.drawLine(color, laneX, 0, laneX, h, LINE_WIDTH);
    }
  }

  private void drawForkingOffLines(final PlotCommit<TLane> commit, final int h, final int myLaneX, final Color myColor, final int dotY) {
    if (commit.getChildCount() > 0) {
      int ix;
      for(PlotLane forkingOffLane : commit.forkingOffLanes) {
        Color cColor = this.laneColor((TLane) forkingOffLane);
        int cx = laneX(forkingOffLane);
        if (Math.abs(myLaneX - cx) > 14) {
          if (myLaneX < cx) {
            ix = cx - LINE_PAD;
          } else {
            ix = cx + LINE_PAD;
          }
          this.drawLine(cColor, myLaneX, h / 2, ix, h / 2, LINE_WIDTH);
          this.drawLine(cColor, ix, h / 2, cx, 0, LINE_WIDTH);
        } else {
          this.drawLine(cColor, myLaneX, h / 2, cx, 0, LINE_WIDTH);
        }
      }
      int textx = commit.getChildCount() - commit.forkingOffLanes.length;
      if (textx > 0) {
        this.drawLine(myColor, myLaneX, 0, myLaneX, dotY, LINE_WIDTH);
      }
    }
  }

  private void drawMergingLanes(final PlotCommit<TLane> commit, final int h, final int myLaneX, final Color myColor) {
    if (commit.getParentCount() > 0) {
      this.drawLine(myColor, myLaneX, h, myLaneX, h / 2, LINE_WIDTH);
      int ix;
      for(PlotLane mergingLane: commit.mergingLanes) {
        Color cColor = this.laneColor((TLane) mergingLane);
        int cx = laneX(mergingLane);
        if (Math.abs(myLaneX - cx) > 14) {
          if (myLaneX < cx) {
            ix = cx - LINE_PAD;
          } else {
            ix = cx + LINE_PAD;
          }
          this.drawLine(cColor, myLaneX, h / 2, ix, h / 2, LINE_WIDTH);
          this.drawLine(cColor, ix, h / 2, cx, h, LINE_WIDTH);
        } else {
          this.drawLine(cColor, myLaneX, h / 2, cx, h, LINE_WIDTH);
        }
      }
    }
  }

  private void drawTextReferences(final PlotCommit<TLane> commit, final int h, final int maxCenter, final int dotX) {
    int textx = Math.max(maxCenter + LINE_PAD, dotX) + 8;
    for(Ref ref : commit.refs) {
      textx += this.drawLabel(textx, h / 2, ref);
    }
    this.drawText(commit.getShortMessage(), textx, h);
  }

  private void drawCommitPointInPosition(final PlotCommit<TLane> commit, final int dotSize, final int dotX, final int dotY) {
    if (commit.has(RevFlag.UNINTERESTING)) {
      this.drawBoundaryDot(dotX, dotY, dotSize, dotSize);
    } else {
      this.drawCommitDot(dotX, dotY, dotSize, dotSize);
    }
  }

  protected abstract int drawLabel(int x, int y, Ref ref);

  protected abstract Color laneColor(TLane lane);

  protected abstract void drawLine(Color color, int x1, int y1, int x2, int y2, int width);

  protected abstract void drawCommitDot(int x, int y, int w, int h);

  protected abstract void drawBoundaryDot(int x, int y, int w, int h);

  protected abstract void drawText(String msg, int x, int y);

  private static int laneX(PlotLane myLane) {
    int p = myLane != null ? myLane.getPosition() : 0;
    return 2 + 14 * p + LINE_PAD;
  }
}
