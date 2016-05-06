//
//  CMAircraftStationPage.m
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

#import "CMAircraftStationPage.h"
#import "CMTableGroup.h"
#import "CMTableItem.h"
#import "CMWBData.h"
#import "E6BCalcEngine.h"

@interface CMAircraftStationPage ()
@property (strong) CMTableItem *name;
@property (strong) CMTableItem *arm;
@end

@implementation CMAircraftStationPage

- (id)init
{
	if (nil != (self = [super init])) {
		/*
		 *	Construct fields
		 */

		self.name = [[CMTableItem alloc] initWithName:@"Name" type:CMTableItemTypeString];

		CMTableGroupStructure *s = [[CMTableGroupStructure alloc] initWithName:@"Station" groupItems:@[ self.name ]];


		/*
		 *	Characteristics
		 */

		self.arm = [[CMTableItem alloc] initWithName:@"Arm" type:CMTableItemTypeFloat];

		CMTableGroupStructure *d = [[CMTableGroupStructure alloc] initWithName:@"Station Data" groupItems:@[ self.arm ]];

		self.groups = @[ s, d ];
	}
	return self;
}

- (id)initWithStation:(CMWBAircraftStation *)station
{
	if (nil != (self = [self init])) {
		/*
		 *	Construct fields
		 */

		self.name.data = station.name;
		self.arm.data = @( station.arm );
	}
	return self;
}

- (NSString *)pageName
{
	return (NSString *)self.name.data;
}

- (void)updateItem:(CMTableItem *)item
{
	if (self.name == item) {
		[self notifyPageNameUpdated];
	}
}

- (CMWBAircraftStation *)stationForData
{
	CMWBAircraftStation *s = [[CMWBAircraftStation alloc] init];
	s.name = (NSString *)self.name.data;
	s.arm = [(NSNumber *)self.arm.data doubleValue];
	return s;
}

@end
