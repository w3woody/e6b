//
//  CMWBData.h
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

#import <Foundation/Foundation.h>
#import "CMXMLParser.h"
#import "CMMath.h"
#import "CMValue.h"

/************************************************************************/
/*																		*/
/*	Fuel Type															*/
/*																		*/
/************************************************************************/

#define FUELTYPE_UNKNOWN	0		/* Unknown fuel type */
#define FUELTYPE_AVGAS		1		/* 6 pounds/gallon */
#define FUELTYPE_KEROSENE	2		/* 7 pounds/gallon */
#define FUELTYPE_JETA		3		/* 6.6 pounds/gallon */

extern double WeightForFuel(int fuelType);

/************************************************************************/
/*																		*/
/*	Aircraft Data														*/
/*																		*/
/************************************************************************/

/*	CMWBAircraftDataPoint
 *
 *		Aircraft W&B information
 */

@interface CMWBAircraftDataPoint : NSObject
@property (assign) double weight;
@property (assign) double arm;
@end

/*	CMWBAircraftWBRange
 *
 *		Weight and balance range polygon.
 */

@interface CMWBAircraftWBRange : NSObject
@property (copy) NSString *name;
@property (strong) NSMutableArray *data;
@end

/*	CMWBAircraftStation
 *
 *		Station; this is a name and a distance or ARM
 */

@interface CMWBAircraftStation : NSObject
@property (copy) NSString *name;
@property (assign) double arm;
@end

/*	CMWBFuelTank
 *
 *		Fuel tank; this gives the maximum volume, arm and fuel type for
 *	a fuel tank
 */

@interface CMWBFuelTank : NSObject
@property (copy) NSString *name;
@property (assign) double volume;
@property (assign) double arm;
@property (assign) int fuelType;
@property (assign) int fuelUnit;
@end

/*	CMWBAircraft
 *
 *		The aircraft data. Each data object is a polygon which defines the
 *	boundaries for a particular WB range. Also contains information for 
 *	manuvering speed, which is given by Vn = Va * sqrt(W/wmax).
 */

@interface CMWBAircraft : NSObject <NSCopying>
@property (copy) NSString *maker;
@property (copy) NSString *model;
@property (copy) NSString *name;

@property (assign) int weightUnit;
@property (assign) int armUnit;
@property (assign) int momentUnit;
@property (assign) int speedUnit;

@property (assign) double va;
@property (assign) double wmax;
@property (assign) double weight;
@property (assign) double arm;

@property (strong) NSMutableArray *fuel;
@property (strong) NSMutableArray *data;
@property (strong) NSMutableArray *station;

- (id)init;
- (id)initFromNode:(CMXMLNode *)node;
- (CMXMLNode *)toNode;

@end


/************************************************************************/
/*																		*/
/*	Weight and balance data												*/
/*																		*/
/************************************************************************/

/*	CMWBRow
 *
 *		Represents a single object; a single weight/balance point. This is with
 *	the internal units used for handling measurements
 */

@interface CMWBRow : NSObject
@property (assign) Value weight;
@property (assign) Value arm;
@property (assign) int momentUnit;
@end

/*	CMWBFRow
 *
 *		Represents a fuel weight/balance point. This corresponds to the actual
 *	fuel items in the list, and is regenerated as an aircraft is assigned or
 *	replaces this row
 */

@interface CMWBFRow : NSObject
@property (copy) NSString *name;
@property (assign) Value volume;
@property (assign) Value arm;
@property (assign) int momentUnit;
@property (assign) int fuelType;
@end

/*	CMWBData
 *
 *		Weight and balance record
 */

@interface CMWBData : NSObject
@property (copy) NSString *name;
@property (copy) NSString *aircraft;
@property (strong) NSMutableArray *list;
@property (strong) NSMutableArray *fuel;

@property (assign) int weightUnit;
@property (assign) int armUnit;
@property (assign) int momentUnit;
@property (assign) int speedUnit;

@property (assign) Value aircraftWeight;
@property (assign) Value aircraftArm;
@property (assign) int aircraftMomentUnit;

- (CMXMLNode *)toNode;
- (id)initFromNode:(CMXMLNode *)node;
- (id)initWithData:(CMWBData *)data;
@end
