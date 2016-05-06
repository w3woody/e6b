//
//  CMPDAltCalculation.m
//  E6B
//
//  Created by William Woody on 9/15/12.
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

#import "CMPDAltCalculation.h"
#import "CME6BDataStore.h"

@implementation CMPDAltCalculation

/************************************************************************/
/*																		*/
/*	Input/output														*/
/*																		*/
/************************************************************************/

- (NSString *)calculationName
{
	return @"Pressure/Density Altitude";
}

- (NSString *)calculationDescription
{
	return @"Given your altimeter settings and the observed outside temperature, find the density altitude and pressure altitude.";
}

- (int)inputFieldCount
{
	return 3;
}

- (NSString *)inputFieldName:(int)index
{
	switch (index) {
		default:
		case 0:	return @"Indicated Altitude";
		case 1: return @"Barometer Setting";
		case 2: return @"Outside Temperature";
	}
}

- (id<CMMeasurement>)inputFieldUnit:(int)index
{
	switch (index) {
		default:
		case 0:	return GDistance;
		case 1: return GPressure;
		case 2: return GTemperature;
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
		case 0:	return @"Density Altitude";
		case 1: return @"Pressure Altitude";
	}
}

- (id<CMMeasurement>)outputFieldUnit:(int)index
{
	return GDistance;
}

- (NSArray *)startInputValues
{
	return @[	[[CMValue alloc] initWithValue:GIndicatedAltitude],
				[[CMValue alloc] initWithValue:GBarometerSetting],
				[[CMValue alloc] initWithValue:GOutsideTemperature] ];
}

- (NSArray *)startOutputValues
{
	return @[	[[CMValue alloc] initWithUnit:GDensityAltitude],
				[[CMValue alloc] initWithUnit:GPressureAltitude.unit] ];
}

- (void)setOutputValue:(Value)value forField:(int)field
{
	switch (field) {
		case 0:
		default:
			GDensityAltitude = value.unit;
			break;
		case 1:
			GPressureAltitude = value;
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


/*	Calculate density altitude, pressure altitude using equations at
 *
 *		http://wahiduddin.net/calc/density_altitude.htm
 *	and	http://en.wikipedia.org/wiki/Density_altitude
 */

- (NSArray *)calculateWithInput:(NSArray *)input
{
	CMValue *a = [input objectAtIndex:0];
	CMValue *b = [input objectAtIndex:1];
	CMValue *c = [input objectAtIndex:2];
	
	double av = [a valueAsUnit:DISTANCE_FEET withMeasurement:GDistance];
	double bv = [b valueAsUnit:PRESSURE_INHG withMeasurement:GPressure];
	double cv = [c valueAsUnit:TEMP_KELVIN withMeasurement:GTemperature];
	
	GIndicatedAltitude = [a storeValue];
	GBarometerSetting = [b storeValue];
	GOutsideTemperature = [c storeValue];
	
	/*
	 *	Step 1: derive station pressure based on altimeter settings.
	 *	Equation source: 
	 *
	 *	http://www.nwstc.noaa.gov/DATAACQ/d.ALGOR/d.PRES/PRESalgoProcessW8.html
	 */
	
	double ea = pow(bv,0.1903);	/* bv: altimeter setting */
	ea -= 1.313e-5 * av;		/* av: station elevation */
	double pr = pow(ea,5.255);	/* pa: actual station pressure */
	
	/*
	 *	Step 2: derive density altitude in feet
	 */
	
	double da = (pr/29.92) / (cv/288.15);	/* cv: temperature */
	da = 1 - pow(da,0.234969);
	da = 145442.156 * da;
	
	/*
	 *	Step 3: derive pressure altitude from station pressure. Basically
	 *	work step 1 backwards but with 29.92 imHG for pressure. Note we
	 *	still have 'ea' as an intermediate value.
	 */
	
	double pa = pow(29.92,0.1903) - ea;	/* pa = ea ^ 5.255 */
	pa /= 1.313e-5;
	
	/*
	 *	Slam values in
	 */

	a = [[CMValue alloc] initWithValue:da unit:DISTANCE_FEET];
	b = [[CMValue alloc] initWithValue:pa unit:DISTANCE_FEET];

	return @[ a, b ];
}

@end
