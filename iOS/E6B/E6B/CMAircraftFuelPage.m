//
//  CMAircraftFuelPage.m
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

#import "CMAircraftFuelPage.h"
#import "CMTableGroup.h"
#import "CMTableItem.h"
#import "CMWBData.h"
#import "E6BCalcEngine.h"

@interface CMAircraftFuelPage ()
@property (strong) CMTableItem *name;
@property (strong) CMTableItem *fuelType;
@property (strong) CMTableItem *fuelUnit;
@property (strong) CMTableItem *volume;
@property (strong) CMTableItem *arm;
@end

@implementation CMAircraftFuelPage

+ (NSArray *)fuelItems
{
	static NSArray *fuels;
	static dispatch_once_t onceToken;
	dispatch_once(&onceToken, ^{
		fuels = @[ @"Unknown", @"Av Gas", @"Kerosene", @"Jet A" ];
	});
	return fuels;
}

- (id)init
{
	if (nil != (self = [super init])) {
		/*
		 *	Construct fields
		 */

		self.name = [[CMTableItem alloc] initWithName:@"Name" type:CMTableItemTypeString];

		CMTableGroupStructure *s = [[CMTableGroupStructure alloc] initWithName:@"Fuel Tank" groupItems:@[ self.name ]];


		/*
		 *	Characteristics
		 */

		self.fuelType = [[CMTableItem alloc] initWithName:@"Fuel Type" array:[CMAircraftFuelPage fuelItems]];
		self.fuelUnit = [[CMTableItem alloc] initWithName:@"Volume Unit" array:GVolume.measurements];
		self.volume = [[CMTableItem alloc] initWithName:@"Volume" type:CMTableItemTypeFloat];
		self.arm = [[CMTableItem alloc] initWithName:@"Arm" type:CMTableItemTypeFloat];

		CMTableGroupStructure *d = [[CMTableGroupStructure alloc] initWithName:@"Fuel Data" groupItems:@[ self.fuelType, self.fuelUnit, self.volume, self.arm ]];

		self.groups = @[ s, d ];
	}
	return self;
}

- (id)initWithFuelData:(CMWBFuelTank *)fuel
{
	if (nil != (self = [self init])) {
		/*
		 *	Construct fields
		 */

		self.name.data = fuel.name;

		self.fuelType.data = @( fuel.fuelType );
		self.fuelUnit.data = @( fuel.fuelUnit );
		self.volume.data = @( fuel.volume );
		self.arm.data = @( fuel.arm );
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

- (CMWBFuelTank *)fuelTankForData
{
	CMWBFuelTank *fuelTank = [[CMWBFuelTank alloc] init];

	fuelTank.name = (NSString *)self.name.data;
	fuelTank.fuelType = [(NSNumber *)self.fuelType.data intValue];
	fuelTank.fuelUnit = [(NSNumber *)self.fuelUnit.data intValue];
	fuelTank.volume = [(NSNumber *)self.volume.data doubleValue];
	fuelTank.arm = [(NSNumber *)self.arm.data doubleValue];

	return fuelTank;
}

@end
