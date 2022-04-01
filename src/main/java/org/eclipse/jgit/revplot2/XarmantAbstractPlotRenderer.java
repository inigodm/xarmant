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
  private static final int LANE_WIDTH = 14;
  private static final int LINE_WIDTH = 2;
  private static final int LEFT_PAD = 2;

  protected void paintWorking(PlotCommit<TLane> working, PlotCommit<TLane> parentCommit, int h) {
    int dotSize = computeDotSize(h) * 2;
    int myLaneX = laneC(parentCommit.getLane());
    int dotX = myLaneX - dotSize / 2 - 1;
    int dotY = (h - dotSize) / 2;

    myLaneX = drawLanes(parentCommit, h, dotSize, working.getLane(), myLaneX, dotY);
    drawPassingLanes(parentCommit, h, myLaneX);
    Color myColor = this.laneColor(parentCommit.getLane());
    this.drawBoundaryDot(dotX, dotY, dotSize, dotSize);
    int textx = Math.max(myLaneX + 7, dotX + dotSize) + 8;
    this.drawText("Working", textx + dotSize, h);
  }

  protected void paintCommit(PlotCommit<TLane> commit, int h) {
    int dotSize = computeDotSize(h);
    int myLaneX = laneC(commit.getLane());
    int dotX = myLaneX - dotSize / 2 - 1;
    int dotY = (h - dotSize) / 2;

    myLaneX = drawLanes(commit, h, dotSize, commit.getLane(), myLaneX, dotY);
    drawCommitPointInPossition(commit, dotSize, dotX, dotY);
    drawTextReferences(commit, h, dotSize, myLaneX, dotX);
  }

  private int drawLanes(final PlotCommit<TLane> commit, final int h, final int dotSize, final TLane myLane, int myLaneX, final int dotY) {
    drawPassingLanes(commit, h, myLaneX);
    Color myColor = this.laneColor(myLane);
    drawMergingLanes(commit, h, dotSize, myLaneX, myColor);
    return drawForkingOffLines(commit, h, myLaneX, myColor, dotY);
  }

  private int drawPassingLanes(final PlotCommit<TLane> commit, final int h, int maxCenter) {
    PlotLane[] passingLanes = commit.passingLanes;
    int nPassingLanes = passingLanes.length;
    for(int n = 0; n < nPassingLanes; ++n) {
      TLane passingLane = (TLane) passingLanes[n];
      int laneC = laneC(passingLane);
      Color color = this.laneColor(passingLane);
      this.drawLine(color, laneC, 0, laneC, h, 2);
      maxCenter = Math.max(maxCenter, laneC);
    }
    return maxCenter;
  }

  private int drawForkingOffLines(final PlotCommit<TLane> commit, final int h, final int myLaneX, final Color myColor, final int dotY) {
    TLane forkingOffLane;
    Color cColor;
    int textx;
    int maxCenter = myLaneX;
    if (commit.getChildCount() > 0) {
      PlotLane[] forkingOffLanes;
      int forkingOffLanesNumber = (forkingOffLanes = commit.forkingOffLanes).length;
      int cx;
      int ix;

      for(int n = 0; n < forkingOffLanesNumber; ++n) {
        forkingOffLane = (TLane) forkingOffLanes[n];
        cColor = this.laneColor(forkingOffLane);
        cx = laneC(forkingOffLane);
        if (Math.abs(myLaneX - cx) > 14) {
          if (myLaneX < cx) {
            ix = cx - 7;
          } else {
            ix = cx + 7;
          }

          this.drawLine(cColor, myLaneX, h / 2, ix, h / 2, 2);
          this.drawLine(cColor, ix, h / 2, cx, 0, 2);
        } else {
          this.drawLine(cColor, myLaneX, h / 2, cx, 0, 2);
        }

        maxCenter = Math.max(maxCenter, cx);
      }

      textx = commit.getChildCount() - commit.forkingOffLanes.length;
      if (textx > 0) {
        this.drawLine(myColor, myLaneX, 0, myLaneX, dotY, 2);
      }
    }
    return maxCenter;
  }

  private int drawMergingLanes(final PlotCommit<TLane> commit, final int h, final int dotSize, final int myLaneX, final Color myColor) {
    int nParents = commit.getParentCount();
    Color cColor;
    TLane mergingLane;
    int maxCenter = myLaneX;
    if (nParents > 0) {
      PlotLane[] mergingLanes;
      this.drawLine(myColor, myLaneX, h, myLaneX, (h + dotSize) / 2, 2);
      int mergingLanesNumber = (mergingLanes = commit.mergingLanes).length;
      int cx;
      int ix;

      for(int n = 0; n < mergingLanesNumber; ++n) {
        mergingLane = (TLane) mergingLanes[n];
        cColor = this.laneColor((TLane) mergingLane);
        cx = laneC(mergingLane);
        if (Math.abs(myLaneX - cx) > 14) {
          if (myLaneX < cx) {
            ix = cx - 7;
          } else {
            ix = cx + 7;
          }

          this.drawLine(cColor, myLaneX, h / 2, ix, h / 2, 2);
          this.drawLine(cColor, ix, h / 2, cx, h, 2);
        } else {
          this.drawLine(cColor, myLaneX, h / 2, cx, h, 2);
        }

        maxCenter = Math.max(maxCenter, cx);
      }
    }
    return maxCenter;
  }

  private void drawTextReferences(final PlotCommit<TLane> commit, final int h, final int dotSize, final int maxCenter, final int dotX) {
    int textx;
    int i;
    textx = Math.max(maxCenter + 7, dotX + dotSize) + 8;
    int numberOfRefs = commit.refs.length;

    for(i = 0; i < numberOfRefs; ++i) {
      textx += this.drawLabel(textx + dotSize, h / 2, commit.refs[i]);
    }

    this.drawText(commit.getShortMessage(), textx + dotSize, h);
  }

  private void drawCommitPointInPossition(final PlotCommit<TLane> commit, final int dotSize, final int dotX, final int dotY) {
    if (commit.has(RevFlag.UNINTERESTING)) {
      this.drawBoundaryDot(dotX, dotY, dotSize, dotSize);
    } else {
      this.drawCommitDot(dotX, dotY, dotSize, dotSize);
    }
  }

  protected abstract int drawLabel(int var1, int var2, Ref var3);

  private static int computeDotSize(int h) {
    int d = (int)((float)Math.min(h, 14) * 0.5F);
    d += d & 1;
    System.out.println(h + " -> " +d);
    return d;
  }

  protected abstract Color laneColor(TLane var1);

  protected abstract void drawLine(Color var1, int var2, int var3, int var4, int var5, int var6);

  protected abstract void drawCommitDot(int var1, int var2, int var3, int var4);

  protected abstract void drawBoundaryDot(int var1, int var2, int var3, int var4);

  protected abstract void drawText(String var1, int var2, int var3);

  private static int laneX(PlotLane myLane) {
    int p = myLane != null ? myLane.getPosition() : 0;
    return 2 + 14 * p;
  }

  private static int laneC(PlotLane myLane) {
    return laneX(myLane) + 7;
  }
}
