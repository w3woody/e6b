//
//  CMFuelForWeight.m
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

#import "CMFuelForWeight.h"
#import "CME6BDataStore.h"

@implementation CMFuelForWeight

/************************************************************************/
/*																		*/
/*	Globals																*/
/*																		*/
/************************************************************************/

static Value GAvGas;
static Value GJP4;
static Value GKerosene;
static Value GOil;

/************************************************************************/
/*																		*/
/*	Input/output														*/
/*																		*/
/************************************************************************/

- (NSString *)calculationName
{
	return @"Fuel by Weight";
}

- (NSString *)calculationDescription
{
	return @"Find the amount of various fuels and oil for a given weight.";
}

- (int)inputFieldCount
{
	return 4;
}

- (NSString *)inputFieldName:(int)index
{
	switch (index) {
		default:
		case 0:		return @"Aviation Gas Weight";
		case 1:		return @"JP-4 Weight";
		case 2:		return @"Kerosene Weight";
		case 3:		return @"Oil Weight";
	}
}

- (id<CMMeasurement>)inputFieldUnit:(int)index
{
	return GWeight;
}

- (int)outputFieldCount
{
	return 4;
}

- (NSString *)outputFieldName:(int)index
{
	switch (index) {
		default:
		case 0:		return @"Aviation Gas Volume";
		case 1:		return @"JP-4 Volume";
		case 2:		return @"Kerosene Volume";
		case 3:		return @"Oil Volume";
	}
}

- (id<CMMeasurement>)outputFieldUnit:(int)index
{
	return GVolume;
}

- (NSArray *)startInputValues
{
	return @[	[[CMValue alloc] initWithValue:GAvGas],
				[[CMValue alloc] initWithValue:GJP4],
				[[CMValue alloc] initWithValue:GKerosene],
				[[CMValue alloc] initWithValue:GOil] ];
}

- (NSArray *)startOutputValues
{
	return @[	[[CMValue alloc] initWithUnit:VOLUME_GALLONS],
				[[CMValue alloc] initWithUnit:VOLUME_GALLONS],
				[[CMValue alloc] initWithUnit:VOLUME_GALLONS],
				[[CMValue alloc] initWithUnit:VOLUME_QUARTS] ];
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
	NSMutableArray *a = [[NSMutableArray alloc] init];

	for (int i = 0; i < 4; ++i) {
		CMValue *iv = [input objectAtIndex:i];

		double val = [iv valueAsUnit:WEIGHT_LBS withMeasurement:GWeight];
		
		switch (i) {
			case 0:
				GAvGas = [iv storeValue];
				val /= 6;
				break;
			case 1:
				GJP4 = [iv storeValue];
				val /= 6.6;
				break;
			case 2:
				GKerosene = [iv storeValue];
				val /= 7.0;
				break;
			case 3:
				GOil = [iv storeValue];
				val /= 7.5;
				break;
		}

		CMValue *v = [[CMValue alloc] initWithValue:val unit:VOLUME_GALLONS];
		[a addObject:v];
	}
	return a;
}


@end
