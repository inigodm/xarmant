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

  protected void paintCommit(PlotCommit<TLane> commit, int h) {
    int dotSize = computeDotSize(h);
    TLane myLane = commit.getLane();
    int myLaneX = laneC(myLane);
    Color myColor = this.laneColor(myLane);
    int maxCenter = myLaneX;
    PlotLane[] var11 = commit.passingLanes;
    int nParent = var11.length;

    int dotY;
    int n;
    for(dotY = 0; dotY < nParent; ++dotY) {
      TLane passingLane = (TLane) var11[dotY];
      n = laneC(passingLane);
      Color c = this.laneColor(passingLane);
      this.drawLine(c, n, 0, n, h, 2);
      maxCenter = Math.max(maxCenter, n);
    }

    int dotX = myLaneX - dotSize / 2 - 1;
    dotY = (h - dotSize) / 2;
    nParent = commit.getParentCount();
    PlotLane[] var14;
    Color cColor;
    int cx;
    int ix;
    TLane forkingOffLane;
    int i;
    if (nParent > 0) {
      this.drawLine(myColor, myLaneX, h, myLaneX, (h + dotSize) / 2, 2);
      i = (var14 = commit.mergingLanes).length;

      for(n = 0; n < i; ++n) {
        forkingOffLane = (TLane) var14[n];
        cColor = this.laneColor((TLane) forkingOffLane);
        cx = laneC(forkingOffLane);
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

    int textx;
    if (commit.getChildCount() > 0) {
      i = (var14 = commit.forkingOffLanes).length;

      for(n = 0; n < i; ++n) {
        forkingOffLane = (TLane) var14[n];
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

    if (commit.has(RevFlag.UNINTERESTING)) {
      this.drawBoundaryDot(dotX, dotY, dotSize, dotSize);
    } else {
      this.drawCommitDot(dotX, dotY, dotSize, dotSize);
    }

    textx = Math.max(maxCenter + 7, dotX + dotSize) + 8;
    n = commit.refs.length;

    for(i = 0; i < n; ++i) {
      textx += this.drawLabel(textx + dotSize, h / 2, commit.refs[i]);
    }

    String msg = commit.getShortMessage();
    this.drawText(msg, textx + dotSize, h);
  }

  protected abstract int drawLabel(int var1, int var2, Ref var3);

  private static int computeDotSize(int h) {
    int d = (int)((float)Math.min(h, 14) * 0.5F);
    d += d & 1;
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
