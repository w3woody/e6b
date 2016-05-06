//
//  CMReqCalibratedAirspeed.m
//  E6B
//
//  Created by William Woody on 9/17/12.
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

#import "CMReqCalibratedAirspeed.h"
#import "CME6BDataStore.h"

@implementation CMReqCalibratedAirspeed

/************************************************************************/
/*																		*/
/*	Input/output														*/
/*																		*/
/************************************************************************/

- (NSString *)calculationName
{
	return @"Required Calibrated Airspeed";
}

- (NSString *)calculationDescription
{
	return @"Given your desired true airspeed, current altitude and the outside observed temperature, find the calibrated airspeed and current density altitude for your aircraft.";
}

- (int)inputFieldCount
{
	return 3;
}

- (NSString *)inputFieldName:(int)index
{
	switch (index) {
		default:
		case 0:	return @"Pressure Altitude";
		case 1: return @"Outside Temperature";
		case 2: return @"True Air Speed";
	}
}

- (id<CMMeasurement>)inputFieldUnit:(int)index
{
	switch (index) {
		default:
		case 0:	return GDistance;
		case 1: return GTemperature;
		case 2: return GSpeed;
	}
}

- (int)outputFieldCount
{
	return 3;
}

- (NSString *)outputFieldName:(int)index
{
	switch (index) {
		default:
		case 0: return @"Calibrated Air Speed";
		case 1: return @"Mach Number";
		case 2:	return @"Density Altitude";
	}
}

- (id<CMMeasurement>)outputFieldUnit:(int)index
{
	switch (index) {
		default:
		case 0:	return GSpeed;
		case 1: return nil;
		case 2: return GDistance;
	}
}

- (NSArray *)startInputValues
{
	return @[	[[CMValue alloc] initWithValue:GPressureAltitude],
				[[CMValue alloc] initWithValue:GOutsideTemperature],
				[[CMValue alloc] initWithValue:GTrueAirSpeed] ];
}

- (NSArray *)startOutputValues
{
	return @[	[[CMValue alloc] initWithUnit:GCalibratedAirSpeed.unit],
				[[CMValue alloc] initWithUnit:0],
				[[CMValue alloc] initWithUnit:GDensityAltitude] ];
}

- (void)setOutputValue:(Value)value forField:(int)field
{
	switch (field) {
		default:
			break;
		case 0:
			GCalibratedAirSpeed = value;
			break;
		case 2:
			GDensityAltitude = value.unit;
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


/*	Calculate Density Altitude, Mach # and Calibrated Airspeed.
 *
 *	Source: http://en.wikipedia.org/wiki/Airspeed#Calibrated_airspeed
 */

- (NSArray *)calculateWithInput:(NSArray *)input
{
	CMValue *a = [input objectAtIndex:0];
	CMValue *b = [input objectAtIndex:1];
	CMValue *c = [input objectAtIndex:2];
	
	GPressureAltitude = [a storeValue];
	GOutsideTemperature = [b storeValue];
	GTrueAirSpeed = [c storeValue];
	
	double av = [a valueAsUnit:DISTANCE_FEET withMeasurement:GDistance];	/* Pressure altitude */
	double bv = [b valueAsUnit:TEMP_KELVIN withMeasurement:GTemperature];	/* Temperature */
	double cv = [c valueAsUnit:SPEED_KNOTS withMeasurement:GSpeed];			/* Speed in knots */
	
	/*
	 *	Step 1: Figure out station pressure given pressure altitude.
	 *
	 *	http://www.nwstc.noaa.gov/DATAACQ/d.ALGOR/d.PRES/PRESalgoProcessW8.html
	 */
	
	double tmp = pow(29.92,0.1903);		/* Pressure altitude with constant sea level */
	tmp = tmp - 1.313e-5 * av;
	double p = pow(tmp, 5.255);

	/*
	 *	Step 2: Calculate density altitude
	 *
	 *		http://en.wikipedia.org/wiki/Density_altitude
	 */
	
	double da = (p/29.92)/(bv/288.15);
	da = 1-pow(da,0.234969);
	da *= 145442.156;

	/*
	 *	Step 3: Calculate the mach number from true airspeed. This is
	 *	related to the equivalent airspeed.
	 */
	
	double tr = sqrt(bv/288.15);	/* sqrt(T/T0) */
	double es = cv / tr;			/* equivalent airspeed */
	double m = es / 661.4788;		/* Mach number */
	
	/*
	 *	Step 4: Calculate impact pressure on the pitot tube. Derived from
	 *	the mach number
	 */
	
	tmp = (m * m)/5 + 1;
	tmp = pow(tmp,7.0/2.0);
	double qc = (tmp - 1) * p;
	
	/*
	 *	Step 5: From the impact pressure on the pitot tube, calculate calibrated
	 *	airspeed
	 */
	
	double cs = pow(qc/29.92126 + 1,2.0/7.0);
	cs = 661.4788 * sqrt(5*(cs - 1));

	/*
	 *	Populate outputs
	 */

	a = [[CMValue alloc] initWithValue:cs unit:SPEED_KNOTS];
	b = [[CMValue alloc] initWithValue:m unit:0];
	c = [[CMValue alloc] initWithValue:da unit:DISTANCE_FEET];

	return @[ a, b, c ];
}


@end
