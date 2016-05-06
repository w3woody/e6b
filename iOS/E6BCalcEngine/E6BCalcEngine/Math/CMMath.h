//
//  CMMath.h
//  E6B
//
//  Created by William Woody on 11/23/12.
//  Copyright (c) 2012 William Woody. All rights reserved.
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

#ifndef E6B_CMMath_h
#define E6B_CMMath_h

/************************************************************************/
/*																		*/
/*	Complex Hull Computation											*/
/*																		*/
/************************************************************************/

/*	CMPoint
 *
 *		A 2D point used in my hull computation
 */

typedef struct CMPoint
{
	double x;
	double y;
} CMPoint;

/*	CMPolygon
 *
 *		A polygon is an array of points, with a length in the header
 */

typedef struct CMPolygon
{
	int length;
	CMPoint points[1];		/* Variable sized */
} CMPolygon;


/*	FindConvexHull
 *
 *		Find the convex hull of the specified points
 */

extern CMPolygon *E6BFindConvexHull(int len, CMPoint *points);

/************************************************************************/
/*																		*/
/*	Angle Support														*/
/*																		*/
/************************************************************************/

extern float E6BFixAngle(float x);

#endif
