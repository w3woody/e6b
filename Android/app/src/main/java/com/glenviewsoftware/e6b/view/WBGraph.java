/*  WBGraph.java
 *
 *  Created on Jan 5, 2013 by William Edward Woody
 */
/*	E6B: Calculator software for pilots.
 *
 *	Copyright © 2016 by William Edward Woody
 *
 *	This program is free software: you can redistribute it and/or modify it
 *	under the terms of the GNU General Public License as published by the
 *	Free Software Foundation, either version 3 of the License, or (at your
 *	option) any later version.
 *
 *	This program is distributed in the hope that it will be useful, but
 *	WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *	or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *	for more details.
 *
 *	You should have received a copy of the GNU General Public License along
 *	with this program. If not, see <http://www.gnu.org/licenses/>
 */

package com.glenviewsoftware.e6b.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.glenviewsoftware.e6b.calc.wb.aircraft.WBAircraft;
import com.glenviewsoftware.e6b.calc.wb.aircraft.WBAircraftDataPoint;
import com.glenviewsoftware.e6b.calc.wb.aircraft.WBAircraftWBRange;
import com.glenviewsoftware.e6b.units.Units;
import com.glenviewsoftware.e6b.utils.ConvexHull;

/**
 * The graph
 */
public class WBGraph extends View
{
    private Paint fPaint;
    private float fScale;

    private WBAircraft aircraft;
    private double totWeight;
    private double totArm;
    private double emptyWeight;
    private double emptyArm;
    private List<Pair> weights;

    private double minWeight,maxWeight;
    private double minArm,maxArm;

    private Rect graphArea;
    private double scaleWeight;
    private double offsetWeight;
    private double scaleArm;
    private double offsetArm;

    private double startWeight;
    private double stepWeight;
    private int numWeight;
    private double lineWeight;

    private double startArm;
    private double stepArm;
    private int numArm;
    private double lineArm;


    public static class Pair
    {
        double weight;
        double arm;

        public Pair(double w, double a)
        {
            weight = w;
            arm = a;
        }

        public double getWeight()
        {
            return weight;
        }

        public double getArm()
        {
            return arm;
        }
    }

    public WBGraph(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initialize();
    }

    public WBGraph(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize();
    }

    public WBGraph(Context context)
    {
        super(context);
        initialize();
    }

    private void initialize()
    {
        fPaint = new Paint();
        fPaint.setAntiAlias(true);
        fScale = getResources().getDisplayMetrics().density;
        // TODO
        
        weights = new ArrayList<Pair>();
        this.post(new Runnable() {
            @Override
            public void run()
            {
                reloadGraph();
            }
        });
    }


    public void setAircraft(WBAircraft a)
    {
        aircraft = a;
    }

    public void setTotalArm(double v)
    {
        totArm = v;
    }

    public void setTotalWeight(double v)
    {
        totWeight = v;
    }

    public void setWeights(List<Pair> array)
    {
        weights = array;
    }

    /************************************************************************/
    /*                                                                      */
    /*  Va Calculation                                                      */
    /*                                                                      */
    /************************************************************************/

    double vaForWeight(double weight)
    {
        /* Va' = Va * sqrt( weight / gross_weight ) */

        double tmp = weight / aircraft.getWmax();
        return aircraft.getVa() * Math.sqrt(tmp);
    }

    /************************************************************************/
    /*                                                                      */
    /*  Graph layout/drawing                                                */
    /*                                                                      */
    /************************************************************************/

    /*  ValueFromIndex
     *
     *      Returns 1,2,5,10,20,50,etc., for tick mark and value placement
     */

    static double ValueFromIndex(int index)
    {
        int[] a = { 1, 2, 5 };
        int x = index % 3;
        int y = index / 3;

        double v = a[x];
        while (y-- > 0) v *= 10;
        return v;
    }

    /*  StepSpacing
     *
     *      Give the step spacing given the width
     */

    static double StepSpacing(double scale, int pwidth)
    {
        int i = 0;
        scale = Math.abs(scale);
        for (;;) {
            double v = ValueFromIndex(i++);
            if (v * scale >= pwidth) return v;
        }
    }

    /*
     *  Given the scale, find line spacing to give us about 15 to 30 pixel
     *  tickmarks
     */

    static double LineSpacing(double scale)
    {
        return StepSpacing(scale,15);
    }

    /*
     *  Calculate all of the graphing parameters to quickly graph the W&B plot
     */

    public void reloadGraph()
    {
        double w,a,m;

        /*
         *  Find the min weight, max weight on the graph
         */

        minWeight = 99999999;
        maxWeight = 0;
        minArm = 99999999;
        maxArm = 0;
        if (aircraft != null) {
            for (WBAircraftWBRange wg: aircraft.getData()) {
                for (WBAircraftDataPoint dp: wg.getData()) {
                    w = dp.getWeight();
                    a = dp.getArm();

                    if (minWeight > w) minWeight = w;
                    if (maxWeight < w) maxWeight = w;
                    if (minArm > a) minArm = a;
                    if (maxArm < a) maxArm = a;
                }
            }
        }

        /*
         *  Factor in total weight/arm
         */

        if (minWeight > totWeight) minWeight = totWeight;
        if (maxWeight < totWeight) maxWeight = totWeight;
        if (minArm > totArm) minArm = totArm;
        if (maxArm < totArm) maxArm = totArm;

        /*
         *  Factor in fuel ranges
         */

        m = totWeight * totArm;
        w = totWeight;
        for (Pair pair: weights) {
            w -= pair.weight;
            m -= pair.weight * pair.arm;
        }
        if (w > 0) {
            a = m / w;
        } else {
            a = 0;
        }
        if (minWeight > w) minWeight = w;
        if (maxWeight < w) maxWeight = w;
        if (minArm > a) minArm = a;
        if (maxArm < a) maxArm = a;
        emptyWeight = w;
        emptyArm = a;

        /*
         *  If this is zeroish, create a default range
         */

        if ((maxWeight < minWeight) || (maxArm < minArm)) {
            minWeight = 500;
            maxWeight = 1500;
            minArm = 10;
            maxArm = 30;
        }

        /*
         *  Guarantee minimum range
         */

        maxWeight += 100;       /* Buffer (with min at zero) */
        minArm -= 2;
        maxArm += 2;

        /*
         *  Calculate the tick marks and graph drawing area
         */

        String slabel = Integer.toString((int)maxWeight);

        fPaint.setTextSize(12 * fScale);
        float width = fPaint.measureText(slabel);
        float height = fPaint.descent() - fPaint.ascent();

        graphArea = new Rect((int)(width + 10),0,(int)(getWidth() - width - 10),(int)(getHeight() - height - 5));

        /*
         *  Calculate the scale and offset to draw in graph area
         */

        double minp = graphArea.left;
        double maxp = graphArea.right;
        scaleArm = (maxp - minp)/(maxArm - minArm);
        offsetArm = minp - scaleArm * minArm;       /* arm * scaleArm + offsetArm = pos */

        maxp = graphArea.top;
        minp = graphArea.bottom;
        scaleWeight = (maxp - minp)/(maxWeight - minWeight);
        offsetWeight = minp - scaleWeight * minWeight;

        /*
         *  Calculate the tick mark locations
         */

        lineWeight = LineSpacing(scaleWeight);
        lineArm = LineSpacing(scaleArm);

        /*
         *  Calculate the label locations
         */

        stepWeight = StepSpacing(scaleWeight, 50);
        stepArm = StepSpacing(scaleArm, 80);

        double tmpb = Math.ceil(minWeight / stepWeight) + 1;
        double tmpt = Math.floor(maxWeight / stepWeight);
        startWeight = tmpb * stepWeight;
        numWeight = (int)(tmpt - tmpb + 1);

        tmpb = Math.ceil(minArm / stepArm);
        tmpt = Math.floor(maxArm / stepArm);
        startArm = tmpb * stepArm;
        numArm = (int)(tmpt - tmpb + 1);

        invalidate();
    }

    void drawWeight(Canvas canvas, double weight)
    {
        fPaint.setTextSize(12 * fScale);
        fPaint.setColor(0xFFFFFFFF);

        int ypos = (int)(offsetWeight + scaleWeight * weight);
        // TODO: Bump from bottom?
        String str = Integer.toString((int)weight);
        canvas.drawText(str, 0, ypos, fPaint);
    }

    void drawArm(Canvas canvas, double arm)
    {
        fPaint.setTextSize(12 * fScale);
        fPaint.setColor(0xFFFFFFFF);

        int xpos = (int)(offsetArm + scaleArm * arm);
        String str = Integer.toString((int)(arm));
        xpos -= fPaint.measureText(str)/2;

        // TODO: Bump left/right
        canvas.drawText(str,xpos,getHeight() - fPaint.descent() - 1,fPaint);
    }

    void drawVALabel(Canvas canvas, Point loc, double s)
    {
        fPaint.setTextSize(14 * fScale);
        
        String str = "Va: " + ((int)(s + 0.5)) + " " + Units.speed.getAbbrMeasure(aircraft.getSpeedUnit());

        boolean rhs = true;
        int sizeWidth = (int)fPaint.measureText(str);

        if (loc.x + sizeWidth + 40 > getWidth()) rhs = false;

        if (rhs) loc.x += 5;
        else loc.x -= 5;

        /*
         *  Build the flag
         */

        int h2 = (int)(fPaint.descent() - fPaint.ascent())/2 + 4;
        int dx = rhs ? h2 : -h2;
        int dw = rhs ? sizeWidth + 10 : -(sizeWidth + 10);
        
        Path path = new Path();
        path.moveTo(loc.x, loc.y);
        path.lineTo(loc.x + dx, loc.y + h2);
        path.lineTo(loc.x + dx + dw, loc.y + h2);
        path.lineTo(loc.x + dx + dw, loc.y - h2);
        path.lineTo(loc.x + dx, loc.y - h2);
        path.lineTo(loc.x, loc.y);
        
        fPaint.setColor(0xFF000000);
        fPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, fPaint);
        
        fPaint.setColor(0xFF999999);
        fPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, fPaint);
        
        fPaint.setColor(0xFFFFFFFF);
        fPaint.setStyle(Paint.Style.FILL);
        
        int left = loc.x + dx + 5;
        if (!rhs) left += dw;
        int bl = (int)(loc.y + h2 - fPaint.descent() - 2);
        
        canvas.drawText(str, left, bl, fPaint);
    }

    /*
     *  Given weight/arm, find point
     */

    Point findPoint(double w, double a)
    {
        return new Point((int)(offsetArm + scaleArm * a), (int)(offsetWeight + scaleWeight * w));
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        /*
         *  Step 1: Draw the graph grid
         */
        
        if (isInEditMode() || (graphArea == null)) {
            canvas.drawColor(0xFF004400);
            return;
        }

        /* Border */
        fPaint.setColor(0xFF008000);
        fPaint.setStyle(Paint.Style.STROKE);

        canvas.drawRect(graphArea, fPaint);
        
        /* Clip and grid lines */
        canvas.save();
        canvas.clipRect(graphArea);

        int min = (int)Math.ceil(minArm / lineArm);
        int max = (int)Math.floor(maxArm / lineArm);
        for (int i = min; i <= max; ++i) {
            float pos = (float)Math.floor(scaleArm * lineArm * i + offsetArm) + 0.5f;
            canvas.drawLine(pos, graphArea.top, pos, graphArea.bottom, fPaint);
        }

        min = (int)Math.ceil(minWeight / lineWeight);
        max = (int)Math.floor(maxWeight / lineWeight);
        for (int i = min; i <= max; ++i) {
            float pos = (float)Math.floor(scaleWeight * lineWeight * i + offsetWeight) + 0.5f;
            canvas.drawLine(graphArea.left, pos, graphArea.right, pos, fPaint);
        }

        canvas.restore();

        /* Draw labels */
        drawWeight(canvas,minWeight);
        for (int i = 0; i < numWeight; ++i) {
            drawWeight(canvas,startWeight + stepWeight * i);
        }
        for (int i = 0; i < numArm; ++i) {
            drawArm(canvas,startArm + stepArm * i);
        }
        
        /* Draw the contents */
        if (aircraft != null) {
            /*
             *  Draw the graph
             */

//            [[UIColor colorWithRed:0 green:1 blue:0 alpha:1.0] setStroke];
//            [[UIColor colorWithRed:0 green:1 blue:0 alpha:0.1] setFill];
            
            for (WBAircraftWBRange r: aircraft.getData()) {
                int i,len = r.getData().size();
                if (len == 0) continue;

                WBAircraftDataPoint pt = r.getData().get(len-1);
//                CMWBAircraftDataPoint *pt = [r.data objectAtIndex:len-1];
                double y = offsetWeight + scaleWeight * pt.getWeight();
                double x = offsetArm + scaleArm * pt.getArm();
                
                Path path = new Path();
                path.moveTo((int)x, (int)y);

                for (i = 0; i < len; ++i) {
                    pt = r.getData().get(i);
                    y = offsetWeight + scaleWeight * pt.getWeight();
                    x = offsetArm + scaleArm * pt.getArm();
                    path.lineTo((int)x,(int)y);
                }
                
                fPaint.setColor(0x1100FF00);
                fPaint.setStyle(Paint.Style.FILL);
                canvas.drawPath(path, fPaint);
                
                fPaint.setColor(0xFF00FF00);
                fPaint.setStyle(Paint.Style.STROKE);
                canvas.drawPath(path, fPaint);
                
                fPaint.setStyle(Paint.Style.FILL);
            }
        }

        /* Draw the total, total less fuel points */
        if ((totWeight != 0) && (totArm != 0)) {
            Point twhere;
            Point where;
            double w,a,m;
            
            twhere = findPoint(totWeight,totArm);
            fPaint.setStyle(Paint.Style.FILL);
            fPaint.setColor(0x11FFFFFF);
            canvas.drawCircle(twhere.x, twhere.y, 4, fPaint);
            fPaint.setStyle(Paint.Style.STROKE);
            fPaint.setColor(0xFFFFFFFF);
            canvas.drawCircle(twhere.x, twhere.y, 4, fPaint);

            if (weights.size() > 0) {
                if (weights.size() == 1) {
                    Pair p = weights.get(0);

                    /*
                     *  This is a line. Draw it
                     */

                    Path path = new Path();
                    path.moveTo(twhere.x, twhere.y);

                    for (double fw = 10; fw < p.weight; fw += 10) {
                        w = totWeight - fw;
                        m = totWeight * totArm - fw * p.arm;
                        a = m / w;
                        where = findPoint(w,a);
                        path.lineTo(where.x, where.y);
                    }

                    w = totWeight - p.weight;
                    m = totWeight * totArm - p.weight * p.arm;
                    a = m / w;
                    where = findPoint(w,a);
                    path.lineTo(where.x,where.y);
                    
                    canvas.drawPath(path, fPaint);
                } else {
                    /*
                     *  This is a matrix of values. We need to find the bounding
                     *  box of this. We do this by (a) finding all the min/max
                     *  points (meaning the number of possible bounding points is
                     *  2 ** (# weights), and (b) finding all of the weight/moment
                     *  points. We then find the bounding polygon then draw back
                     *  in weight/arm space
                     */

                    int nw = weights.size();
                    int num = 1 << nw;
                    ConvexHull.CMPoint[] p = new ConvexHull.CMPoint[num];

                    for (int index = 0; index < num; ++index) {
                        w = totWeight;
                        m = totWeight * totArm;
                        for (int n = 0; n < nw; ++n) {
                            if (0 != (index & (1 << n))) {
                                Pair pa = weights.get(n);
                                w -= pa.weight;
                                m -= pa.weight * pa.arm;
                            }
                        }
                        p[index] = new ConvexHull.CMPoint(m, w);
                    }

                    /*
                     *  At this point plist is all possible min/max bounds on the
                     *  weight/moment space. Find the bounding polygon
                     */

                    ConvexHull.CMPolygon poly = ConvexHull.FindConvexHull(p);

                    /*
                     *  Draw the polygon in w/a space
                     */

                    Path path = new Path();

                    // Note: min w will be first item
                    m = poly.points[0].x;
                    w = poly.points[0].y;
                    a = m / w;
                    where = findPoint(w,a);
//                    where = [self findPointWithWeight:w arm:a];
                    path.moveTo(where.x, where.y);
//                    CGContextMoveToPoint(ref, where.x, where.y);

                    for (int index = poly.points.length-1; index >= 0; --index) {
                        double mm = poly.points[index].x;
                        double ww = poly.points[index].y;
                        double aa = mm / ww;

                        for (double alpha = 0.1; alpha < 1.0; alpha += 0.1) {
                            double lm = m * (1 - alpha) + mm * alpha;
                            double lw = w * (1 - alpha) + ww * alpha;
                            double la = lm / lw;
                            Point lpt = findPoint(lw,la);
//                            CGPoint lpt = [self findPointWithWeight:lw arm:la];
                            path.lineTo(lpt.x, lpt.y);
//                            CGContextAddLineToPoint(ref, lpt.x, lpt.y);
                        }

                        Point lpt = findPoint(ww,aa);
                        path.lineTo(lpt.x,lpt.y);
                        
                        m = mm;
                        w = ww;
                    }

                    fPaint.setColor(0x11FFFFFF);
                    fPaint.setStyle(Paint.Style.FILL);
                    canvas.drawPath(path,fPaint);
                    fPaint.setColor(0xFFFFFFFF);
                    fPaint.setStyle(Paint.Style.STROKE);
                    canvas.drawPath(path, fPaint);
                }
            }

            /*
             *  Draw VA markers
             */

            drawVALabel(canvas,twhere,vaForWeight(totWeight));
            if (weights.size() > 0) {
                where = findPoint(emptyWeight,emptyArm);
                drawVALabel(canvas,where,vaForWeight(emptyWeight));
            }
        }
    }

}


