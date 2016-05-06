//
//  CMWBCalculation.m
//  E6B
//
//  Created by William Woody on 11/8/14.
//  Copyright (c) 2014 William Woody. All rights reserved.
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

#import "CMWBCalculation.h"
#import "CMAircraftDatabase.h"
#import "CMWBData.h"

@interface CMWBCalculation ()
@property (strong) CMWBData *wbData;
@property (strong) CMWBAircraft *aircraft;
@end

@implementation CMWBCalculation

- (id)init
{
	if (nil != (self = [super init])) {
		self.wbData = [[CMWBData alloc] init];
		self.aircraft = nil;
	}
	return self;
}

- (id)initWithData:(CMWBData *)data
{
	if (nil != (self = [super init])) {
		self.wbData = data;
		self.aircraft = [[CMAircraftDatabase shared] aircraftForName:data.aircraft];
	}
	return self;
}

- (NSString *)calculationName
{
	return self.wbData.name;
}

- (NSString *)aircraftName
{
	return self.wbData.aircraft;
}

- (void)setCalculationName:(NSString *)name
{
	self.wbData.name = name;
}

- (void)setAircraftName:(NSString *)name
{
	self.wbData.aircraft = name;
	self.aircraft = [[CMAircraftDatabase shared] aircraftForName:name];
		
	Value v;
	v.unit = self.aircraft.weightUnit;
	v.value = self.aircraft.weight;
	self.wbData.aircraftWeight = v;
	
	v.unit = self.aircraft.armUnit;
	v.value = self.aircraft.arm;
	self.wbData.aircraftArm = v;
	
	[self.wbData.fuel removeAllObjects];
	for (CMWBFuelTank *tank in self.aircraft.fuel) {
		CMWBFRow *row = [[CMWBFRow alloc] init];
		
		row.name = tank.name;
		row.fuelType = tank.fuelType;

		v.unit = tank.fuelUnit;
		v.value = tank.volume;
		row.volume = v;

		v.unit = self.aircraft.armUnit;
		v.value = tank.arm;
		row.arm = v;
		
		row.momentUnit = self.aircraft.momentUnit;
		[self.wbData.fuel addObject:row];
	}

	/*
	 *	Wipe out the stations and repopulate, assuming a one-to-one
	 *	match
	 */

	NSArray *oldStations = [NSArray arrayWithArray:self.wbData.list];
	[self.wbData.list removeAllObjects];
	int index = 0;
	for (CMWBAircraftStation *s in self.aircraft.station) {
		CMWBRow *row = [[CMWBRow alloc] init];

		if (index < oldStations.count) {
			row.weight = ((CMWBRow *)oldStations[index]).weight;
			++index;
		} else {
			v.unit = self.aircraft.weightUnit;
			v.value = 0;
			row.weight = v;
		}

		v.unit = self.aircraft.armUnit;
		v.value = s.arm;
		row.arm = v;

		row.momentUnit = self.aircraft.momentUnit;
		[self.wbData.list addObject:row];
	}
}

- (CMWBData *)data
{
	return self.wbData;
}

- (CMWBAircraft *)aircraftData
{
	return self.aircraft;
}

@end
