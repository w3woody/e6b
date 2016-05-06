//
//  CMComplexHull.c
//  E6B
//
//  Created by William Woody on 11/23/12.
//  Copyright (c) 2012 William Woody. All rights reserved.
//
//	Algorithm source: http://softsurfer.com/Archive/algorithm_0109/algorithm_0109.htm
//

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

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "CMMath.h"

#define PRINTDEBUG		1

/************************************************************************/
/*																		*/
/*	Internal Routines													*/
/*																		*/
/************************************************************************/

/*	IsLeft
 *
 *		Determine if point 2 is left of the line formed by points 0 and 1
 *	Return 1 if p2 left of p0 and 01, -1 if to the right, and 0 if on the line.
 */

static int IsLeft(CMPoint p0, CMPoint p1, CMPoint p2)
{
	double n = (p1.x - p0.x)*(p2.y - p0.y) - (p2.x - p0.x)*(p1.y - p0.y);
	if (n < 0) return -1;
	if (n > 0) return 1;
	return 0;
}

/*	FindMinPoint
 *
 *		Given the list of points, this finds the index of the point with
 *	the lowest y and x coordinates. This is the base point for the Graham Scan
 *	algorithm
 */

static int FindMinPoint(int len, CMPoint *points)
{
	int minX = 0;
	int i;
	CMPoint *pt = points;
	
	for (i = 1; i < len; ++i) {
		CMPoint *npt = points + i;
		if (npt->y < pt->y) {
			minX = i;
			pt = npt;
		} else if ((npt->y == pt->y) && (npt->x < pt->x)) {
			minX = i;
			pt = npt;
		}
	}
	return minX;
}

static CMPoint GP0;		/* P0 in the list as we sort */

/*	GCompare
 *
 *		Compare to determine the sort order of the points.
 */

static int GCompare(const void *a, const void *b)
{
	CMPoint *aa = (CMPoint *)a;
	CMPoint *bb = (CMPoint *)b;
	
	int n = IsLeft(GP0,*aa,*bb);
	if (n) return -n;
	
	/* In a tie, the closest point sorts first. */
	double da = (aa->x - GP0.x) * (aa->x - GP0.x) + (aa->y - GP0.y) * (aa->y - GP0.y);
	double db = (bb->x - GP0.x) * (bb->x - GP0.x) + (bb->y - GP0.y) * (bb->y - GP0.y);
	if (da < db) return -1;
	if (da > db) return 1;
	return 0;
}

/************************************************************************/
/*																		*/
/*	Convex Hull															*/
/*																		*/
/************************************************************************/

#ifdef PRINTDEBUG
static void DebugList(int len, CMPoint *points)
{
	for (int i = 0; i < len; ++i) {
		printf("(%f, %f)\n",points[i].x,points[i].y);
	}
}
#endif

/*	E6BFindConvexHull
 *
 *		Find a convex hull. Note that this is not a re-entrant method
 */

CMPolygon *E6BFindConvexHull(int len, CMPoint *points)
{
	int x,y,index;
	int plen;
	CMPoint *scratch = (CMPoint *)malloc(sizeof(CMPoint) * len);
	CMPoint *stack = (CMPoint *)malloc(sizeof(CMPoint) * len);
	
	/*
	 *	Step 1: Find P0
	 */
	
	DebugList(len,points);
	
	index = FindMinPoint(len, points);
#ifdef PRINTDEBUG
	printf("Min point: %d\n",index);
#endif

	/*
	 *	Step 2: Compute the sorted points
	 */
	
	// copy points with p0 first
	x = 0;
	scratch[x++] = points[index];
	for (y = 0; y < len; ++y) {
		if (y == index) continue;
		scratch[x++] = points[y];
	}
	
#ifdef PRINTDEBUG
	printf("Start Sort:\n");
	DebugList(len, scratch);
#endif

	// sort around angle
	GP0 = points[index];
	qsort(scratch+1, len-1, sizeof(CMPoint), GCompare);

#ifdef PRINTDEBUG
	printf("End Sort:\n");
	DebugList(len, scratch);
#endif

	// remove duplicates. note that if X and X+1 are the same, we eliminate x.
	plen = len;
	x = 1;
	for (y = 2; y < len; ++y) {
		if (0 == IsLeft(GP0,scratch[x],scratch[y])) {
			scratch[x] = scratch[y];
			--plen;
		} else {
			scratch[++x] = scratch[y];
		}
	}
	
#ifdef PRINTDEBUG
	printf("Remove Duplicates\n");
	DebugList(plen,scratch);
#endif

	/*
	 *	Step 3: Construct the hull itself
	 */
	
	x = 0;	/* X is stack pointer */
	y = 0;	/* Y points to scratch list */
	
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
					printf("##ERROR\n");
				}
			} else {
				/* To left; push */
				stack[x++] = scratch[y++];
			}
		}
	}
	
	/*
	 *	At this point stack contains the list
	 */
	
	CMPolygon *p = (CMPolygon *)malloc(sizeof(CMPolygon) + sizeof(CMPoint) * (x - 1));
	p->length = x;
	memcpy(p->points,stack,x * sizeof(CMPoint));
	
	free(stack);
	free(scratch);
	return p;
}

void Test()
{
#ifdef PRINTDEBUG
	CMPoint points[5];
	
	points[0].x = 0;
	points[0].y = 0;
	points[1].x = 2;
	points[1].y = 2;
	points[2].x = 1;
	points[2].y = 1;
	points[3].x = 0;
	points[3].y = 3;
	points[4].x = 0.5;
	points[4].y = 1;
	
	CMPolygon *poly = E6BFindConvexHull(5, points);
	printf("Final:\n");
	DebugList(poly->length,poly->points);
	free(poly);
#endif
}

