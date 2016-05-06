/*  ConvexHull.java
 *
 *  Created on Jan 6, 2013 by William Edward Woody
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

package com.glenviewsoftware.e6b.utils;

import java.util.Arrays;

public class ConvexHull
{
    private static final boolean PRINTDEBUG = true;

    public static class CMPoint implements Comparable<CMPoint>
    {
        public final double x;
        public final double y;

        public CMPoint(double xpt, double ypt)
        {
            x = xpt;
            y = ypt;
        }

        @Override
        public int compareTo(CMPoint another)
        {
            CMPoint aa = this;
            CMPoint bb = another;

            int n = IsLeft(GP0,aa,bb);
            if (n != 0) return -n;

            /* In a tie, the closest point sorts first. */
            double da = (aa.x - GP0.x) * (aa.x - GP0.x) + (aa.y - GP0.y) * (aa.y - GP0.y);
            double db = (bb.x - GP0.x) * (bb.x - GP0.x) + (bb.y - GP0.y) * (bb.y - GP0.y);
            if (da < db) return -1;
            if (da > db) return 1;
            return 0;
        }
    }

    public static class CMPolygon
    {
        public final CMPoint[] points;
        
        public CMPolygon(int len, CMPoint[] data)
        {
            points = new CMPoint[len];
            System.arraycopy(data, 0, points, 0, len);
        }
    }


    private static CMPoint GP0;     /* P0 in the list as we sort */

    /************************************************************************/
    /*                                                                      */
    /*  Internal Routines                                                   */
    /*                                                                      */
    /************************************************************************/

    /*  IsLeft
     *
     *      Determine if point 2 is left of the line formed by points 0 and 1
     *  Return 1 if p2 left of p0 and 01, -1 if to the right, and 0 if on the line.
     */

    private static int IsLeft(CMPoint p0, CMPoint p1, CMPoint p2)
    {
        double n = (p1.x - p0.x)*(p2.y - p0.y) - (p2.x - p0.x)*(p1.y - p0.y);
        if (n < 0) return -1;
        if (n > 0) return 1;
        return 0;
    }

    /*  FindMinPoint
     *
     *      Given the list of points, this finds the index of the point with
     *  the lowest y and x coordinates. This is the base point for the Graham Scan
     *  algorithm
     */

    private static int FindMinPoint(CMPoint[] points)
    {
        int minX = 0;
        int i;
        CMPoint pt = points[0];

        for (i = 1; i < points.length; ++i) {
            CMPoint npt = points[i];
            if (npt.y < pt.y) {
                minX = i;
                pt = npt;
            } else if ((npt.y == pt.y) && (npt.x < pt.x)) {
                minX = i;
                pt = npt;
            }
        }
        return minX;
    }

    /************************************************************************/
    /*                                                                      */
    /*  Convex Hull                                                         */
    /*                                                                      */
    /************************************************************************/

    private static void DebugList(int len, CMPoint[] points)
    {
        for (int i = 0; i < len; ++i) {
            CMPoint p = points[i];
            System.out.println("(" + p.x + "," + p.y + ")");
        }
    }

    private static void DebugList(CMPoint[] points)
    {
        DebugList(points.length,points);
    }

    /*  FindConvexHull
     *
     *      Find a convex hull. Note that this is not a re-entrant method
     */

    public static CMPolygon FindConvexHull(CMPoint[] points)
    {
        int x,y,index;
        int plen;
        CMPoint[] scratch = new CMPoint[points.length];
        CMPoint[] stack = new CMPoint[points.length];

        /*
         *  Step 1: Find P0
         */

        if (PRINTDEBUG) {
            DebugList(points);
        }
        index = FindMinPoint(points);
        if (PRINTDEBUG) {
            System.out.println("Min point: " + index);
        }

        /*
         *  Step 2: Compute the sorted points
         */

        // copy points with p0 first
        x = 0;
        scratch[x++] = points[index];
        for (y = 0; y < points.length; ++y) {
            if (y == index) continue;
            scratch[x++] = points[y];
        }

        if (PRINTDEBUG) {
            System.out.println("Start sort:");
            DebugList(scratch);
        }

        // sort around angle
        GP0 = points[index];
        Arrays.sort(scratch,1,scratch.length);

        if (PRINTDEBUG) {
            System.out.println("End Sort:");
            DebugList(scratch);
        }

        // remove duplicates. note that if X and X+1 are the same, we eliminate x.
        plen = points.length;
        x = 1;
        for (y = 2; y < points.length; ++y) {
            if (0 == IsLeft(GP0,scratch[x],scratch[y])) {
                scratch[x] = scratch[y];
                --plen;
            } else {
                scratch[++x] = scratch[y];
            }
        }

        if (PRINTDEBUG) {
            System.out.println("Remove Duplicates");
            DebugList(plen,scratch);
        }

        /*
         *  Step 3: Construct the hull itself
         */

        x = 0;  /* X is stack pointer */
        y = 0;  /* Y points to scratch list */

        while (y < plen) {
            if (x < 2) {
                /* Populates P0, P1, and if something goes haywire */
                stack[x++] = scratch[y++];
            } else {
                int lr = IsLeft(stack[x-2],stack[x-1],scratch[y]);
                if (lr <= 0) {
                    /* To right or on line; pop */
                    --x;
                    if (x < 2) {
                        System.out.println("##ERROR##");
                    }
                } else {
                    /* To left; push */
                    stack[x++] = scratch[y++];
                }
            }
        }

        /*
         *  At this point stack contains the list
         */

        CMPolygon p = new CMPolygon(x,stack);
        return p;
    }
//
//    public static void main(String[] args)
//    {
//        CMPoint[] points = new CMPoint[5];
//
//        points[0] = new CMPoint(0,0);
//        points[1] = new CMPoint(2,2);
//        points[2] = new CMPoint(1,1);
//        points[3] = new CMPoint(0,3);
//        points[4] = new CMPoint(0.5,1);
//        
//        points[0].x = 0;
//        points[0].y = 0;
//        points[1].x = 2;
//        points[1].y = 2;
//        points[2].x = 1;
//        points[2].y = 1;
//        points[3].x = 0;
//        points[3].y = 3;
//        points[4].x = 0.5;
//        points[4].y = 1;
//        
//        CMPolygon poly = FindConvexHull(points);
//
//        System.out.println("Final:");
//        DebugList(poly.points);
//    }
}


