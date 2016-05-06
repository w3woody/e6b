//
//  CMFindHeadingCalculation.m
//  E6B
//
//  Created by William Woody on 9/16/12.
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

#import "CMFindHeadingCalculation.h"
#import "CME6BDataStore.h"
#import "CMMath.h"

@implementation CMFindHeadingCalculation

/************************************************************************/
/*																		*/
/*	Input/output														*/
/*																		*/
/************************************************************************/

- (NSString *)calculationName
{
	return @"Heading for True Course";
}

- (NSString *)calculationDescription
{
	return @"Given a wind speed and direction, the true air speed of your aircraft, and a desired (ground) true course, find the true heading you must fly to track the ground course.";
}

- (int)inputFieldCount
{
	return 4;
}

- (NSString *)inputFieldName:(int)index
{
	switch (index) {
		default:
		case 0:	return @"Wind Direction";
		case 1: return @"Wind Speed";
		case 2: return @"(Ground) True Course";
		case 3: return @"True Air Speed";
	}
}

- (id<CMMeasurement>)inputFieldUnit:(int)index
{
	switch (index) {
		default:
		case 0:	return nil;
		case 1: return GSpeed;
		case 2:	return nil;
		case 3: return GSpeed;
	}
}

- (int)outputFieldCount
{
	return 2;
}

- (NSString *)outputFieldName:(int)index
{
	switch (index) {
		default:
		case 0:	return @"(Airplane) True Heading";
		case 1: return @"Ground Speed";
	}
}

- (id<CMMeasurement>)outputFieldUnit:(int)index
{
	switch (index) {
		default:
		case 0:	return nil;
		case 1: return GSpeed;
	}
}

- (NSArray *)startInputValues
{
	return @[	[[CMValue alloc] initWithValue:GWindDirection],
				[[CMValue alloc] initWithValue:GWindSpeed],
				[[CMValue alloc] initWithValue:GGroundCourse],
				[[CMValue alloc] initWithValue:GTrueAirSpeed] ];
}

- (NSArray *)startOutputValues
{
	return @[	[[CMValue alloc] initWithUnit:0],
				[[CMValue alloc] initWithUnit:GGroundSpeed.unit]];
}

- (void)setOutputValue:(Value)value forField:(int)field
{
	switch (field) {
		case 0:
		default:
			GAirplaneCourse = value;
			break;
		case 1:
			GGroundSpeed = value;
			break;
	}
}

- (BOOL)intermixResults
{
	return NO;
}

/************************************************************************/
/*																		*/
/*	Math																*/
/*																		*/
/************************************************************************/

struct FHResult FindHeading(double tc, double tas, double wd, double ws)
{
	struct FHResult ret;
	
	double ca,gs;
	
	tc = tc * M_PI / 180.0;			/* Convert to radians */
	wd = (wd + 180) * M_PI / 180.0;	/* Convert to vector in radians */
	
	double sc = ws * sin(tc - wd) / tas;
	if ((sc < -1) || (sc > 1)) {
		/*
		 *	We are unable to travel fast enough.
		 */
		
		ca = INFINITY;
		gs = INFINITY;
	} else {
		/*
		 *	Compute course corretion angle
		 */
		
		sc = asin(sc);
		ca = tc + sc;
		
		/*
		 *	Step 2: find ground speed given direction
		 */
		
		double ax = sin(wd) * ws + sin(ca) * tas;
		double ay = cos(wd) * ws + cos(ca) * tas;
		
		ca *= 180 / M_PI;
//		double ar = E6BFixAngle(atan2(ax,ay)*180.0/M_PI);	// debugging
//		NSLog(@"%lf - %lf",ca,ar);

		gs = sqrt(ax * ax + ay * ay);
	}
	
	ret.gs = gs;
	ret.th = E6BFixAngle(ca);
	return ret;
}

/*	Calculate density altitude, pressure altitude using equations at
 *
 *		http://wahiduddin.net/calc/density_altitude.htm
 *	and	http://en.wikipedia.org/wiki/Density_altitude
 */

- (NSArray *)calculateWithInput:(NSArray *)input;
{
	CMValue *a = [input objectAtIndex:0];
	CMValue *b = [input objectAtIndex:1];
	CMValue *c = [input objectAtIndex:2];
	CMValue *d = [input objectAtIndex:3];
	
	GWindDirection = [a storeValue];
	GWindSpeed = [b storeValue];
	GGroundCourse = [c storeValue];
	GTrueAirSpeed = [d storeValue];

	double av = [a value];
	double bv = [b valueAsUnit:SPEED_KNOTS withMeasurement:GSpeed];
	double cv = [c value];
	double dv = [d valueAsUnit:SPEED_KNOTS withMeasurement:GSpeed];
	
	/*
	 *	Step 1: Use law of sines to find course correction
	 */
	
	struct FHResult r = FindHeading(cv, dv, av, bv);
	
	/*
	 *	Populate results
	 */

	a = [[CMValue alloc] initWithValue:r.th unit:0];
	b = [[CMValue alloc] initWithValue:r.gs unit:SPEED_KNOTS];
	return @[ a, b ];
}


@end
