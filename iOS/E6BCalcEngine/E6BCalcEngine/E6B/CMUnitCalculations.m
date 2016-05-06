//
//  CMUnitCalculations.m
//  E6B
//
//  Created by William Woody on 9/27/12.
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

#import "CMUnitCalculations.h"
#import "CME6BDataStore.h"

@implementation CMUnitCalculations

/************************************************************************/
/*																		*/
/*	Globals																*/
/*																		*/
/************************************************************************/

static Value GCurWeight;

/************************************************************************/
/*																		*/
/*	Input/output														*/
/*																		*/
/************************************************************************/

- (NSString *)calculationName
{
	return @"Unit Conversions";
}

- (NSString *)calculationDescription
{
	return @"Various built-in unit conversions for distance, speed, weight, temperature, volume, and pressure.";
}

- (int)inputFieldCount
{
	return 6;
}

- (NSString *)inputFieldName:(int)index
{
	switch (index) {
		default:
		case 0:		return @"Input Distance";
		case 1:		return @"Input Speed";
		case 2:		return @"Input Weight";
		case 3:		return @"Input Temperature";
		case 4:		return @"Input Volume";
		case 5:		return @"Input Pressure";
	}
}

- (id<CMMeasurement>)inputFieldUnit:(int)index
{
	switch (index) {
		default:
		case 0:		return GDistance;
		case 1:		return GSpeed;
		case 2:		return GWeight;
		case 3:		return GTemperature;
		case 4:		return GVolume;
		case 5:		return GPressure;
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
		case 0:		return @"Output Distance";
		case 1:		return @"Output Speed";
		case 2:		return @"Output Weight";
		case 3:		return @"Output Temperature";
		case 4:		return @"Output Volume";
		case 5:		return @"Output Pressure";
	}
}

- (id<CMMeasurement>)outputFieldUnit:(int)index
{
	switch (index) {
		default:
		case 0:		return GDistance;
		case 1:		return GSpeed;
		case 2:		return GWeight;
		case 3:		return GTemperature;
		case 4:		return GVolume;
		case 5:		return GPressure;
	}
}

- (NSArray *)startInputValues
{
	return @[	[[CMValue alloc] initWithValue:GElapsedDistance],
				[[CMValue alloc] initWithValue:GCurrentSpeed],
				[[CMValue alloc] initWithValue:GCurWeight],
				[[CMValue alloc] initWithValue:GGroundTemperature],
				[[CMValue alloc] initWithValue:GCurrentVolume],
				[[CMValue alloc] initWithValue:GBarometerSetting] ];
}

- (NSArray *)startOutputValues
{
	return @[	[[CMValue alloc] initWithUnit:[GDistance standardUnit]],
				[[CMValue alloc] initWithUnit:[GSpeed standardUnit]],
				[[CMValue alloc] initWithUnit:[GWeight standardUnit]],
				[[CMValue alloc] initWithUnit:[GTemperature standardUnit]],
				[[CMValue alloc] initWithUnit:[GVolume standardUnit]],
				[[CMValue alloc] initWithUnit:[GPressure standardUnit]]  ];
}

- (void)setOutputValue:(Value)value forField:(int)field
{
	switch (field) {
		default:
			break;
	}
}

- (BOOL)intermixResults
{
	return YES;
}

- (NSArray *)calculateWithInput:(NSArray *)input
{
	for (int i = 0; i < 6; ++i) {
		CMValue *iv = [input objectAtIndex:i];

		switch (i) {
			case 0:	GElapsedDistance = [iv storeValue];	break;
			case 1:	GCurrentSpeed = [iv storeValue];	break;
			case 2:	GCurWeight = [iv storeValue];	break;
			case 3:	GGroundTemperature = [iv storeValue];	break;
			case 4:	GCurrentVolume = [iv storeValue];	break;
			case 5:	GBarometerSetting = [iv storeValue];	break;
		}
	}
	return input;
}


@end
