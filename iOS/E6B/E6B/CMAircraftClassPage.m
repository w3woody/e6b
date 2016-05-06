//
//  CMAircraftClassPage.m
//  E6B
//
//  Created by William Woody on 12/16/14.
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

#import "CMAircraftClassPage.h"
#import "CMTableGroup.h"
#import "CMTableItem.h"
#import "CMWBData.h"
#import "E6BCalcEngine.h"
#import "CMAircraftFuelPage.h"
#import "CMAircraftStationPage.h"
#import "CMAircraftWBDataPage.h"

@interface CMAircraftClassPage ()
@property (strong) CMTableItem *maker;
@property (strong) CMTableItem *model;
@property (strong) CMTableItem *name;

@property (strong) CMTableItem *weightUnit;
@property (strong) CMTableItem *armUnit;
@property (strong) CMTableItem *momentUnit;
@property (strong) CMTableItem *speedUnit;

@property (strong) CMTableItem *va;
@property (strong) CMTableItem *wmax;
@property (strong) CMTableItem *weight;
@property (strong) CMTableItem *arm;

@property (strong) CMTableGroupArray *fuel;
@property (strong) CMTableGroupArray *stations;
@property (strong) CMTableGroupArray *wbdata;
@end

@implementation CMAircraftClassPage

- (id)initWithAircraft:(CMWBAircraft *)aircraft
{
	if (nil != (self = [super init])) {

		/*
		 *	Construct fields
		 */

		self.maker = [[CMTableItem alloc] initWithName:@"Maker" type:CMTableItemTypeString];
		self.model = [[CMTableItem alloc] initWithName:@"Model" type:CMTableItemTypeString];
		self.name = [[CMTableItem alloc] initWithName:@"Name" type:CMTableItemTypeString];

		CMTableGroupStructure *s = [[CMTableGroupStructure alloc] initWithName:@"Aircraft Summary" groupItems:@[ self.maker, self.model, self.name ]];

		/*
		 *	Units section
		 */

		self.weightUnit = [[CMTableItem alloc] initWithName:@"Weight Unit" array:GWeight.measurements];
		self.armUnit = [[CMTableItem alloc] initWithName:@"Arm Unit" array:GLength.measurements];
		self.momentUnit = [[CMTableItem alloc] initWithName:@"Moment Unit" array:GMoment.measurements];
		self.speedUnit = [[CMTableItem alloc] initWithName:@"Speed Unit" array:GSpeed.measurements];

		CMTableGroupStructure *u = [[CMTableGroupStructure alloc] initWithName:@"Measurement Units" groupItems:@[ self.weightUnit, self.armUnit, self.momentUnit, self.speedUnit ]];

		/*
		 *	Airraft speed/moment
		 */

		self.va = [[CMTableItem alloc] initWithName:@"Va (max)" type:CMTableItemTypeFloat];
		self.wmax = [[CMTableItem alloc] initWithName:@"Max Weight" type:CMTableItemTypeFloat];
		self.weight = [[CMTableItem alloc] initWithName:@"Empty Weight" type:CMTableItemTypeFloat];
		self.arm = [[CMTableItem alloc] initWithName:@"Empty Arm" type:CMTableItemTypeFloat];

		CMTableGroupStructure *d = [[CMTableGroupStructure alloc] initWithName:@"Aircraft Data" groupItems:@[ self.va, self.wmax, self.weight, self.arm ]];

		/*
		 *	Fuel tanks
		 */

		self.fuel = [[CMTableGroupArray alloc] initWithName:@"Fuel Tanks" pageClass:CMAircraftFuelPage.class reorderable:YES];

		/*
		 *	Stations
		 */

		self.stations = [[CMTableGroupArray alloc] initWithName:@"Stations" pageClass:CMAircraftStationPage.class reorderable:YES];

		/*
		 *	Weight and balance
		 */

		self.wbdata = [[CMTableGroupArray alloc] initWithName:@"WB Limits" pageClass:CMAircraftWBDataPage.class reorderable:YES];
		/*
		 *	Wire up groups
		 */

		self.groups = @[ s, u, d, self.fuel, self.stations, self.wbdata ];

		/*
		 *	Set data
		 */

		self.maker.data = aircraft.maker;
		self.model.data = aircraft.model;
		self.name.data = aircraft.name;

		self.weightUnit.data = @( aircraft.weightUnit );
		self.armUnit.data = @( aircraft.armUnit );
		self.momentUnit.data = @( aircraft.momentUnit );
		self.speedUnit.data = @( aircraft.speedUnit );

		self.va.data = @( aircraft.va );
		self.wmax.data = @( aircraft.wmax );
		self.weight.data = @( aircraft.weight );
		self.arm.data = @( aircraft.arm );

		for (CMWBFuelTank *ft in aircraft.fuel) {
			[self.fuel.pageData addObject:[[CMAircraftFuelPage alloc] initWithFuelData:ft]];
		}

		for (CMWBAircraftStation *station in aircraft.station) {
			[self.stations.pageData addObject:[[CMAircraftStationPage alloc] initWithStation:station]];
		}

		for (CMWBAircraftWBRange *wb in aircraft.data) {
			[self.wbdata.pageData addObject:[[CMAircraftWBDataPage alloc] initWithData:wb]];
		}

	}
	return self;
}

- (NSString *)pageName
{
	return (NSString *)self.name.data;
}

- (void)updateItem:(CMTableItem *)item
{
	if (item == self.name) {
		[self notifyPageNameUpdated];
	}
}

- (CMWBAircraft *)aircraftForData
{
	CMWBAircraft *a = [[CMWBAircraft alloc] init];

	a.maker = (NSString *)self.maker.data;
	a.model = (NSString *)self.model.data;
	a.name = (NSString *)self.name.data;

	a.weightUnit = [(NSNumber *)self.weightUnit.data intValue];
	a.armUnit = [(NSNumber *)self.armUnit.data intValue];
	a.momentUnit = [(NSNumber *)self.momentUnit.data intValue];
	a.speedUnit = [(NSNumber *)self.speedUnit.data intValue];

	a.va = [(NSNumber *)self.va.data doubleValue];
	a.wmax = [(NSNumber *)self.wmax.data doubleValue];
	a.weight = [(NSNumber *)self.weight.data doubleValue];
	a.arm = [(NSNumber *)self.arm.data doubleValue];

	for (CMAircraftFuelPage *f in self.fuel.pageData) {
		[a.fuel addObject:[f fuelTankForData]];
	}
	for (CMAircraftStationPage *s in self.stations.pageData) {
		[a.station addObject:[s stationForData]];
	}
	for (CMAircraftWBDataPage *d in self.wbdata.pageData) {
		[a.data addObject:[d wbRangeForData]];
	}

	return a;
}

@end
