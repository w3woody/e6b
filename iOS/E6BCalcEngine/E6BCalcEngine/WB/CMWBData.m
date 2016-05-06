//
//  CMWBData.m
//  E6B
//
//  Created by William Woody on 9/22/12.
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

#import "CMWBData.h"


/************************************************************************/
/*																		*/
/*	Fuel weight															*/
/*																		*/
/************************************************************************/

double WeightForFuel(int fuelType)
{
	switch (fuelType) {
		case FUELTYPE_AVGAS:	return 6;
		case FUELTYPE_KEROSENE:	return 7;
		case FUELTYPE_JETA:		return 6.6;
		default:				return 1;	/* ??? */
	}
}

/************************************************************************/
/*																		*/
/*	CMWBAircraftDataPoint data											*/
/*																		*/
/************************************************************************/

@implementation CMWBAircraftDataPoint
@end

/************************************************************************/
/*																		*/
/*	CMWBFuelTank data													*/
/*																		*/
/************************************************************************/

@implementation CMWBFuelTank
@end

/************************************************************************/
/*																		*/
/*	CMWBAircraftDataPoint data											*/
/*																		*/
/************************************************************************/

@implementation CMWBAircraftStation
@end

/************************************************************************/
/*																		*/
/*	CMWBAircraftWBRange data											*/
/*																		*/
/************************************************************************/

@implementation CMWBAircraftWBRange

- (id)init
{
	if (nil != (self = [super init])) {
		self.data = [[NSMutableArray alloc] initWithCapacity:10];
	}
	return self;
}
@end

/************************************************************************/
/*																		*/
/*	CMWBAircraft data													*/
/*																		*/
/************************************************************************/

@implementation CMWBAircraft

- (id)init
{
	if (nil != (self = [super init])) {
		self.data = [[NSMutableArray alloc] initWithCapacity:10];
		self.station = [[NSMutableArray alloc] initWithCapacity:10];
		self.fuel = [[NSMutableArray alloc] initWithCapacity:10];
	}
	return self;
}

- (id)initFromNode:(CMXMLNode *)node
{
	if (nil != (self = [self init])) {
		/*
		 *	The XML for the WBAircraft object is:
		 *
		 *		<aircraft name="name" wunit="0" aunit="0" munit="0" sunit="0">
		 *			<va>155</va>
		 *			<wmax>3000</va>
		 *			<weight>1450</weight>
		 *			<arm>38</arm>
		 *			<fuel>
		 *				<tank name="name" fueltype="1">
		 *					<volume unit="0">56</volume>
		 *					<arm>38.5</arm>
		 *				</tank>
		 *			</fuel>
		 *			<station>
		 *				<arm name="Location">37</arm>
		 *				<arm name="Location 2">39</arm>
		 *			</station>
		 *			<data>
		 *				<range name="utility">     <!-- Multiple sets -->
		 *					<point>				 <!-- Multiple points -->
		 *						<weight>2050</weight>
		 *						<arm>38.5</arm>
		 *					</point>
		 *					<point>
		 *						<weight>2950</weight>
		 *						<arm>42.5</arm>
		 *					</point>
		 *				</range>
		 *			</data>
		 *		</aircraft>
		 */

		self.name = node[@"name"];
		self.maker = node[@"maker"];
		self.model = node[@"model"];

		self.weightUnit = [node[@"wunit"] intValue];
		self.armUnit = [node[@"aunit"] intValue];
		self.momentUnit = [node[@"munit"] intValue];
		self.speedUnit = [node[@"sunit"] intValue];

		self.va = [[[node firstElementForName:@"va"] stringValue] doubleValue];
		self.wmax = [[[node firstElementForName:@"wmax"] stringValue] doubleValue];
		self.weight = [[[node firstElementForName:@"weight"] stringValue] doubleValue];
		self.arm = [[[node firstElementForName:@"arm"] stringValue] doubleValue];

		/*
		 *	Run the fuel tanks
		 */

		CMXMLNode *telem = [node firstElementForName:@"fuel"];
		NSArray *tarray = [telem elementsForName:@"tank"];

		for (CMXMLNode *e in tarray) {
			CMWBFuelTank *tank = [[CMWBFuelTank alloc] init];
			
			/*
			 *	Get the name of the tank
			 */

			tank.name = e[@"name"];
			tank.fuelType = [e[@"fueltype"] intValue];

			/*
			 *	Get the fields and their values
			 */

			CMXMLNode *celem = [e firstElementForName:@"volume"];
			tank.volume = [[celem stringValue] doubleValue];
			tank.fuelUnit = [celem[@"unit"] intValue];
			tank.arm = [[[e firstElementForName:@"arm"] stringValue] doubleValue];

			[self.fuel addObject:tank];
		}

		/*
		 *	Run the ARMs
		 */

		telem = [node firstElementForName:@"station"];
		tarray = [telem elementsForName:@"arm"];

		for (CMXMLNode *e in tarray) {
			CMWBAircraftStation *st = [[CMWBAircraftStation alloc] init];
			
			/*
			 *	Get the name of the station
			 */

			st.name = e[@"name"];
			st.arm = [[e stringValue] doubleValue];

			/*
			 *	Get the value
			 */
			
			st.arm = [[e stringValue] doubleValue];
			
			[self.station addObject:st];
		}

		/*
		 *	Run the data objects
		 */
		
		telem = [node firstElementForName:@"data"];
		tarray = [telem elementsForName:@"range"];

		for (CMXMLNode *e in tarray) {
			CMWBAircraftWBRange *range = [[CMWBAircraftWBRange alloc] init];
			[self.data addObject:range];
			
			/*
			 *	Each item is 'range' object
			 */

			range.name = e[@"name"];

			/*
			 *	Get the data points for the range.
			 */
			
			NSArray *parray = [e elementsForName:@"point"];
			for (CMXMLNode *de in parray) {
				CMWBAircraftDataPoint *dp = [[CMWBAircraftDataPoint alloc] init];
				[range.data addObject:dp];

				dp.weight = [[[de firstElementForName:@"weight"] stringValue] doubleValue];
				dp.arm = [[[de firstElementForName:@"arm"] stringValue] doubleValue];
			}
		}
	}
	return self;
}

/*
 *	Deep mutable copy
 */

- (CMWBAircraft *)copyWithZone:(NSZone *)zone
{
	CMWBAircraft *ret = [[CMWBAircraft allocWithZone:zone] init];

	ret.maker = self.maker;
	ret.model = self.model;
	ret.name = self.name;
	ret.weightUnit = self.weightUnit;
	ret.armUnit = self.armUnit;
	ret.momentUnit = self.momentUnit;
	ret.speedUnit = self.speedUnit;
	ret.va = self.va;
	ret.wmax = self.wmax;
	ret.weight = self.weight;
	ret.arm = self.arm;

	for (CMWBFuelTank *t in self.fuel) {
		CMWBFuelTank *tcopy = [[CMWBFuelTank allocWithZone:zone] init];
		tcopy.name = t.name;
		tcopy.volume = t.volume;
		tcopy.arm = t.arm;
		tcopy.fuelType = t.fuelType;
		tcopy.fuelUnit = t.fuelUnit;
		[ret.fuel addObject:tcopy];
	}

	for (CMWBAircraftWBRange *r in self.data) {
		CMWBAircraftWBRange *rcopy = [[CMWBAircraftWBRange allocWithZone:zone] init];
		rcopy.name = r.name;
		for (CMWBAircraftDataPoint *d in r.data) {
			CMWBAircraftDataPoint *dcopy = [[CMWBAircraftDataPoint allocWithZone:zone] init];
			dcopy.weight = d.weight;
			dcopy.arm = d.arm;
			[rcopy.data addObject:dcopy];
		}
		[ret.data addObject:rcopy];
	}

	for (CMWBAircraftStation *s in self.station) {
		CMWBAircraftStation *scopy = [[CMWBAircraftStation allocWithZone:zone] init];
		scopy.name = s.name;
		scopy.arm = s.arm;
		[ret.station addObject:s];
	}

	return ret;
}

- (CMXMLNode *)toNode
{
	CMXMLNode *elem = [CMXMLNode elementWithName:@"aircraft"];
	
	/*
	 *	Format:
	 *
	 *		<aircraft name="name" wunit="0" aunit="0" munit="0" sunit="0">
	 *			<va>155</va>
	 *			<wmax>3000</va>
	 *			<weight>1450</weight>
	 *			<arm>38</arm>
	 *			<fuel>
	 *				<tank name="name" fueltype="1">
	 *					<volume unit="0">56</volume>
	 *					<arm>38.5</arm>
	 *				</tank>
	 *			</fuel>
	 *			<fuel_volume>56</fuel_volume>
	 *			<fuel_arm>36</fuel_arm>
	 *			<tip_fuel_volume>30</tip_fuel_volume>
	 *			<tip_fuel_arm>40</tip_fuel_arm>
	 *			<station>
	 *				<arm name="Location">37</arm>
	 *				<arm name="Location 2">39</arm>
	 *			</station>
	 *			<data>
	 *				<range name="utility">     <!-- Multiple sets -->
	 *					<point>				 <!-- Multiple points -->
	 *						<weight>2050</weight>
	 *						<arm>38.5</arm>
	 *					</point>
	 *					<point>
	 *						<weight>2950</weight>
	 *						<arm>42.5</arm>
	 *					</point>
	 *				</range>
	 *			</data>
	 *		</aircraft>
	 */

	elem[@"name"] = self.name;
	elem[@"maker"] = self.maker;
	elem[@"model"] = self.model;
	elem[@"wunit"] = [NSString stringWithFormat:@"%d",self.weightUnit];
	elem[@"aunit"] = [NSString stringWithFormat:@"%d",self.armUnit];
	elem[@"munit"] = [NSString stringWithFormat:@"%d",self.momentUnit];
	elem[@"sunit"] = [NSString stringWithFormat:@"%d",self.speedUnit];

	[elem addNode:[CMXMLNode elementWithName:@"va" value:[NSString stringWithFormat:@"%f",self.va]]];
	[elem addNode:[CMXMLNode elementWithName:@"wmax" value:[NSString stringWithFormat:@"%f",self.wmax]]];
	[elem addNode:[CMXMLNode elementWithName:@"weight" value:[NSString stringWithFormat:@"%f",self.weight]]];
	[elem addNode:[CMXMLNode elementWithName:@"arm" value:[NSString stringWithFormat:@"%f",self.arm]]];

	/* Fuel tag */
	CMXMLNode *f = [CMXMLNode elementWithName:@"fuel"];
	for (CMWBFuelTank *ft in self.fuel) {
		CMXMLNode *txml = [CMXMLNode elementWithName:@"tank"];
		txml[@"name"] = ft.name;
		txml[@"fueltype"] = [NSString stringWithFormat:@"%d",ft.fuelType];


		CMXMLNode *val = [CMXMLNode elementWithName:@"volume" value:[NSString stringWithFormat:@"%f",ft.volume]];
		val[@"unit"] = [NSString stringWithFormat:@"%d",ft.fuelUnit];
		[txml addNode:val];
		
		val = [CMXMLNode elementWithName:@"arm" value:[NSString stringWithFormat:@"%f",ft.arm]];
		[txml addNode:val];

		[f addNode:txml];
	}
	[elem addNode:f];
	
	/* Station tag */
	CMXMLNode *s = [CMXMLNode elementWithName:@"station"];
	for (CMWBAircraftStation *st in self.station) {
		CMXMLNode *sxml = [CMXMLNode elementWithName:@"arm" value:[NSString stringWithFormat:@"%f",st.arm]];
		sxml[@"name"] = st.name;
		[s addNode:sxml];
	}
	[elem addNode:s];
	
	/* Data tag */
	CMXMLNode *d = [CMXMLNode elementWithName:@"data"];
	for (CMWBAircraftWBRange *range in self.data) {
		CMXMLNode *rxml = [CMXMLNode elementWithName:@"range"];

		rxml[@"name"] = range.name;
		for (CMWBAircraftDataPoint *dp in range.data) {
			CMXMLNode *e = [CMXMLNode elementWithName:@"point"];
			
			[e addNode:[CMXMLNode elementWithName:@"weight" value:[NSString stringWithFormat:@"%f",dp.weight]]];
			[e addNode:[CMXMLNode elementWithName:@"arm" value:[NSString stringWithFormat:@"%f",dp.arm]]];

			[rxml addNode:e];
		}
		[d addNode:rxml];
	}
	[elem addNode:d];
	
	return elem;
}

@end

/************************************************************************/
/*																		*/
/*	CMWBRow data														*/
/*																		*/
/************************************************************************/

@implementation CMWBRow
@end

/************************************************************************/
/*																		*/
/*	CMWBFRow data														*/
/*																		*/
/************************************************************************/

@implementation CMWBFRow
@end

/************************************************************************/
/*																		*/
/*	CMWBData data														*/
/*																		*/
/************************************************************************/

@implementation CMWBData

- (id)init
{
	if (nil != (self = [super init])) {
		self.name = @"Unnamed";
		self.list = [[NSMutableArray alloc] initWithCapacity:10];
		self.fuel = [[NSMutableArray alloc] initWithCapacity:10];

		/*
		 *	Dummy fuel row
		 */
		
		CMWBFRow *frow = [[CMWBFRow alloc] init];
		frow.name = @"Fuel Tank";
		[self.fuel addObject:frow];
	}
	return self;
}

- (id)initWithData:(CMWBData *)data
{
	if (nil != (self = [self init])) {
		self.name = data.name;
		self.aircraft = data.aircraft;
		self.aircraftWeight = data.aircraftWeight;
		self.aircraftArm = data.aircraftArm;
		self.aircraftMomentUnit = data.aircraftMomentUnit;
		self.weightUnit = data.weightUnit;
		self.armUnit = data.armUnit;
		self.momentUnit = data.momentUnit;
		self.speedUnit = data.speedUnit;
		
		[self.list removeAllObjects];
		for (CMWBRow *row in data.list) {
			CMWBRow *dupRow = [[CMWBRow alloc] init];
			dupRow.weight = row.weight;
			dupRow.arm = row.arm;
			dupRow.momentUnit = row.momentUnit;
			[self.list addObject:dupRow];
		}
		
		[self.fuel removeAllObjects];
		for (CMWBFRow *frow in data.fuel) {
			CMWBFRow *dupRow = [[CMWBFRow alloc] init];
			dupRow.volume = frow.volume;
			dupRow.arm = frow.arm;
			dupRow.momentUnit = frow.momentUnit;
			dupRow.fuelType = frow.fuelType;
			[self.fuel addObject:dupRow];
		}
	}
	return self;
}

/*	initFromNode:
 *
 *		Initialize node. Note that some of the fields are calculated
 *
 *	<weightbalance name="name" aircraft="aircraft" wunit="unit" aunit="unit" munit="unit">
 *		<datum unit="unit" [item="aircraft/fuel"] [name="fueltank"]>			-- row
 *			<weight unit="unit">weight</weight>
 *			<volume unit="unit" type="type">volume</volume>	-- for fuel types.
 *			<arm unit="unit">arm</weight>
 *		</datum>
 *	</weightbalance>
 */

- (id)initFromNode:(CMXMLNode *)node
{
	if (nil != (self = [self init])) {
		self.name = node[@"name"];
		self.aircraft = node[@"aircraft"];
		self.weightUnit = [node[@"wunit"] intValue];
		self.armUnit = [node[@"aunit"] intValue];
		self.momentUnit = [node[@"munit"] intValue];
		self.speedUnit = [node[@"sunit"] intValue];

		[self.list removeAllObjects];
		[self.fuel removeAllObjects];
		
		NSArray *l = [node elementsForName:@"datum"];
		for (CMXMLNode *e in l) {
			Value wv,av,vv;
			int vtype = 0;
			int munit = [e[@"unit"] intValue];

			CMXMLNode *telem = [e firstElementForName:@"weight"];
			wv.value = [[telem stringValue] doubleValue];
			wv.unit = [telem[@"unit"] intValue];

			telem = [e firstElementForName:@"volume"];
			vv.value = [[telem stringValue] doubleValue];
			vv.unit = [telem[@"unit"] intValue];
			vtype = [telem[@"fueltype"] intValue];

			telem = [e firstElementForName:@"arm"];
			av.value = [[telem stringValue] doubleValue];
			av.unit = [telem[@"item"] intValue];

			NSString *item = e[@"item"];
			if ([item isEqualToString:@"aircraft"]) {
				self.aircraftMomentUnit = munit;
				self.aircraftWeight = wv;
				self.aircraftArm = av;
			} else if ([item isEqualToString:@"fuel"]) {
				CMWBFRow *frow = [[CMWBFRow alloc] init];

				frow.name = e[@"name"];
				if (frow.name == nil) frow.name = @"Fuel Tank";

				frow.volume = vv;
				frow.arm = av;
				frow.momentUnit = munit;
				frow.fuelType = vtype;
				[self.fuel addObject:frow];
			} else {
				CMWBRow *row = [[CMWBRow alloc] init];
				row.weight = wv;
				row.arm = av;
				row.momentUnit = munit;
				[self.list addObject:row];
			}
		}
	}
	return self;
}

- (CMXMLNode *)toNode
{
	CMXMLNode *elem = [CMXMLNode elementWithName:@"aircraft"];
	
	/*
	 *	Format:
	 *
	 *	<weightbalance name="name" aircraft="aircraft">
	 *		<datum unit="unit">			-- row
	 *			<weight unit="unit">weight</weight>
	 *			<arm unit="unit">arm</weight>
	 *		</datum>
	 *	</weightbalance>
	 */

	elem[@"name"] = self.name;
	elem[@"aircraft"] = self.aircraft;
	elem[@"wunit"] = [NSString stringWithFormat:@"%d",self.weightUnit];
	elem[@"aunit"] = [NSString stringWithFormat:@"%d",self.armUnit];
	elem[@"munit"] = [NSString stringWithFormat:@"%d",self.momentUnit];
	elem[@"sunit"] = [NSString stringWithFormat:@"%d",self.speedUnit];

	/* Aircraft */
	CMXMLNode *d = [CMXMLNode elementWithName:@"datum"];
	d[@"item"] = @"aircraft";
	d[@"unit"] = [NSString stringWithFormat:@"%d",self.aircraftMomentUnit];


	CMXMLNode *el = [CMXMLNode elementWithName:@"weight" value:[NSString stringWithFormat:@"%f",self.aircraftWeight.value]];
	el[@"unit"] = [NSString stringWithFormat:@"%d",self.aircraftWeight.unit];
	[d addNode:el];
	
	el = [CMXMLNode elementWithName:@"arm" value:[NSString stringWithFormat:@"%f",self.aircraftArm.value]];
	el[@"unit"] = [NSString stringWithFormat:@"%d",self.aircraftArm.unit];
	[d addNode:el];
	[elem addNode:d];

	/* Fuel */
	for (CMWBFRow *frow in self.fuel) {
		d = [CMXMLNode elementWithName:@"datum"];
		d[@"item"] = @"fuel";
		d[@"unit"] = [NSString stringWithFormat:@"%d",frow.momentUnit];
		d[@"name"] = frow.name;

		el = [CMXMLNode elementWithName:@"volume" value:[NSString stringWithFormat:@"%f",frow.volume.value]];
		el[@"unit"] = [NSString stringWithFormat:@"%d",frow.volume.unit];
		[d addNode:el];
		
		el = [CMXMLNode elementWithName:@"arm" value:[NSString stringWithFormat:@"%f",frow.arm.value]];
		el[@"unit"] = [NSString stringWithFormat:@"%d",frow.arm.unit];
		[d addNode:el];
		[elem addNode:d];
	}

	/* Rest */
	for (CMWBRow *range in self.list) {
		d = [CMXMLNode elementWithName:@"datum"];

		d[@"unit"] = [NSString stringWithFormat:@"%d",range.momentUnit];
		
		el = [CMXMLNode elementWithName:@"weight" value:[NSString stringWithFormat:@"%f",range.weight.value]];
		el[@"unit"] = [NSString stringWithFormat:@"%d",range.weight.unit];
		[d addNode:el];
		
		el = [CMXMLNode elementWithName:@"arm" value:[NSString stringWithFormat:@"%f",range.arm.value]];
		el[@"unit"] = [NSString stringWithFormat:@"%d",range.arm.unit];
		[d addNode:el];

		[elem addNode:d];
	}
	
	return elem;
}


@end
