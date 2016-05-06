//
//  CMAircraftWBDataPage.m
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

#import "CMAircraftWBDataPage.h"
#import "CMTableGroup.h"
#import "CMTableItem.h"
#import "CMWBData.h"
#import "E6BCalcEngine.h"
#import "CMAircraftWBPointPage.h"

@interface CMAircraftWBDataPage ()
@property (strong) CMTableItem *name;
@property (strong) CMTableGroupArray *wbdata;
@end

@implementation CMAircraftWBDataPage

- (id)init
{
	if (nil != (self = [super init])) {
		self.name = [[CMTableItem alloc] initWithName:@"Name" type:CMTableItemTypeString];
		CMTableGroupStructure *s = [[CMTableGroupStructure alloc] initWithName:@"W&B" groupItems:@[ self.name ]];

		/*
		 *	Weight and balance
		 */

		self.wbdata = [[CMTableGroupArray alloc] initWithName:@"W&B Data" pageClass:CMAircraftWBPointPage.class reorderable:YES];

		self.groups = @[ s, self.wbdata ];
	}
	return self;
}

- (id)initWithData:(CMWBAircraftWBRange *)data
{
	if (nil != (self = [self init])) {
		/*
		 *	Construct fields
		 */

		self.name.data = data.name;
		for (CMWBAircraftDataPoint *dp in data.data) {
			[self.wbdata.pageData addObject:[[CMAircraftWBPointPage alloc] initWithData:dp]];
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
	if (self.name == item) {
		[self notifyPageNameUpdated];
	}
}

- (CMWBAircraftWBRange *)wbRangeForData
{
	CMWBAircraftWBRange *range = [[CMWBAircraftWBRange alloc] init];
	range.name = (NSString *)self.name.data;
	for (CMAircraftWBPointPage *pt in self.wbdata.pageData) {
		[range.data addObject:[pt wbPointForData]];
	}
	return range;
}

@end
