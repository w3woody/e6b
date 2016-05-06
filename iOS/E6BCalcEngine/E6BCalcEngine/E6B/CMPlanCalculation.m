//
//  CMPlanCalculation.m
//  E6B
//
//  Created by William Woody on 10/24/12.
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

#import "CMPlanCalculation.h"
#import "CMMath.h"
#import "CME6BDataStore.h"
#import "CMFindHeadingCalculation.h"

@implementation CMPlanCalculation

/************************************************************************/
/*																		*/
/*	Input/output														*/
/*																		*/
/************************************************************************/

- (NSString *)calculationName
{
	return @"Flight Planner";
}

- (NSString *)calculationDescription
{
	return @"This handles all the calculations necessary to plan a leg of your flight. Given the aircraft's true speed, desired (ground) true course, wind speed and direction, leg distance, magnetic variation and fuel burn rate, calculates the true heading, magnetic course, magnetic heading, leg time, and fuel burn amount for that leg of flight.";
}

- (int)inputFieldCount
{
	return 7;
}

- (NSString *)inputFieldName:(int)index
{
	switch (index) {
		default:
		case 0:	return @"(Ground) True Course";
		case 1: return @"(Ground) Distance";
		case 2: return @"True Air Speed";
		case 3: return @"Wind Direction";
		case 4: return @"Wind Speed";
		case 5: return @"Fuel Burn Rate";
		case 6: return @"Magnetic Variation (+W/-E)";
	}
}

- (id<CMMeasurement>)inputFieldUnit:(int)index
{
	switch (index) {
		default:
		case 0:	return nil;
		case 1: return GDistance;
		case 2: return GSpeed;
		case 3: return nil;
		case 4: return GSpeed;
		case 5: return GVolumeBurn;
		case 6: return nil;
	}
}

- (int)outputFieldCount
{
	return 6;
}

- (NSString *)outputFieldName:(int)index
{
	switch (index) {
		default:
		case 0:	return @"(Aircraft) True Heading";
		case 1: return @"(Ground) Magnetic Course";
		case 2: return @"(Aircraft) Magnetic Heading";
		case 3: return @"Ground Speed";
		case 4: return @"Estimated Time of Arrival";
		case 5: return @"Fuel Required";
	}
}

- (id<CMMeasurement>)outputFieldUnit:(int)index
{
	switch (index) {
		default:
		case 0:	return nil;
		case 1: return nil;
		case 2: return nil;
		case 3: return GSpeed;
		case 4: return GTime;
		case 5: return GVolume;
	}
}

- (NSArray *)startInputValues
{
	return @[	[[CMValue alloc] initWithValue:GGroundCourse],
				[[CMValue alloc] initWithValue:GElapsedDistance],
				[[CMValue alloc] initWithValue:GTrueAirSpeed],
				[[CMValue alloc] initWithValue:GWindDirection],
				[[CMValue alloc] initWithValue:GWindSpeed],
				[[CMValue alloc] initWithValue:GCurrentBurn],
				[[CMValue alloc] initWithValue:GMagVariation] ];
}

- (NSArray *)startOutputValues
{
	return @[	[[CMValue alloc] initWithUnit:0],
				[[CMValue alloc] initWithUnit:0],
				[[CMValue alloc] initWithUnit:0],
				[[CMValue alloc] initWithUnit:GGroundSpeed.unit],
				[[CMValue alloc] initWithUnit:TIME_TIME],
				[[CMValue alloc] initWithUnit:GCurrentVolume.unit] ];
}

- (void)setOutputValue:(Value)value forField:(int)field
{
	switch (field) {
		default:
			break;
		case 3:
			GGroundSpeed = value;
			break;
		case 5:
			GCurrentVolume = value;
			break;
	}
}

- (BOOL)intermixResults
{
	return NO;
}

- (NSArray *)calculateWithInput:(NSArray *)input
{
	CMValue *a = [input objectAtIndex:0];
	CMValue *b = [input objectAtIndex:1];
	CMValue *c = [input objectAtIndex:2];
	CMValue *d = [input objectAtIndex:3];
	CMValue *e = [input objectAtIndex:4];
	CMValue *f = [input objectAtIndex:5];
	CMValue *g = [input objectAtIndex:6];
	
	GGroundCourse = [a storeValue];
	GElapsedDistance = [b storeValue];
	GTrueAirSpeed = [c storeValue];
	GWindDirection = [d storeValue];
	GWindSpeed = [e storeValue];
	GCurrentBurn = [f storeValue];
	GMagVariation = [g storeValue];
	
	double tc = [a value];
	double ed = [b valueAsUnit:DISTANCE_NMILES withMeasurement:GDistance];
	double tas = [c valueAsUnit:SPEED_KNOTS withMeasurement:GSpeed];
	double wd = [d value];
	double ws = [e valueAsUnit:SPEED_KNOTS withMeasurement:GSpeed];
	double fb = [f valueAsUnit:VOLBURN_GALHR withMeasurement:GVolumeBurn];
	double mv = [g value];
	
	/*
	 *	Steal calculations from other calculators.
	 *
	 *	Step 1: Given tc, tac, ws and wd, calculate th and gs
	 */
	
	struct FHResult rs = FindHeading(tc,tas,wd,ws);
	
	/*
	 *	Now calculate the ETA and required fuel
	 */
	
	double eta = ed / rs.gs;		/* eta in hours */
	double fuel = fb * eta;
	
	/*
	 *	Headings
	 */
	
	double mc = E6BFixAngle(tc + mv);
	double mh = E6BFixAngle(rs.th + mv);
	
	/*
	 *	Store results
	 */

	a = [[CMValue alloc] initWithValue:rs.th unit:0];
	b = [[CMValue alloc] initWithValue:mc unit:0];
	c = [[CMValue alloc] initWithValue:mh unit:0];
	d = [[CMValue alloc] initWithValue:rs.gs unit:SPEED_KNOTS];
	e = [[CMValue alloc] initWithValue:eta unit:TIME_TIME];
	f = [[CMValue alloc] initWithValue:fuel unit:VOLUME_GALLONS];

	return @[ a, b, c, d, e, f ];
}


@end
