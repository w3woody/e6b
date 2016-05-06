//
//  CMAircraftWBPointPage.m
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

#import "CMAircraftWBPointPage.h"
#import "CMTableGroup.h"
#import "CMTableItem.h"
#import "CMWBData.h"
#import "E6BCalcEngine.h"

@interface CMAircraftWBPointPage ()
@property (strong) CMTableItem *weight;
@property (strong) CMTableItem *arm;
@end

@implementation CMAircraftWBPointPage

- (id)init
{
	if (nil != (self = [super init])) {
		/*
		 *	Characteristics
		 */

		self.weight = [[CMTableItem alloc] initWithName:@"Weight" type:CMTableItemTypeFloat];
		self.arm = [[CMTableItem alloc] initWithName:@"Arm" type:CMTableItemTypeFloat];

		CMTableGroupStructure *d = [[CMTableGroupStructure alloc] initWithName:@"Point Data" groupItems:@[ self.weight, self.arm ]];

		self.groups = @[ d ];
	}
	return self;
}

- (id)initWithData:(CMWBAircraftDataPoint *)dp
{
	if (nil != (self = [self init])) {
		/*
		 *	Characteristics
		 */

		self.weight.data = @( dp.weight );
		self.arm.data = @( dp.arm );
	}
	return self;
}

- (NSString *)pageName
{
	return [NSString stringWithFormat:@"%@-%@",[(NSNumber *)self.weight.data stringValue],[(NSNumber *)self.arm.data stringValue]];
}

- (void)updateItem:(CMTableItem *)item
{
	[self notifyPageNameUpdated];
}


- (CMWBAircraftDataPoint *)wbPointForData
{
	CMWBAircraftDataPoint *retVal = [[CMWBAircraftDataPoint alloc] init];
	retVal.weight = [(NSNumber *)self.weight.data doubleValue];
	retVal.arm = [(NSNumber *)self.arm.data doubleValue];
	return retVal;
}

@end
