//
//  CMAircraftDatabase.h
//  E6B
//
//  Created by William Woody on 10/2/12.
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
#import "CMWBData.h"

/*
 *	Notification sent via notification center when database is loaded or
 *	updated in the background
 */

#define AIRCRAFTLOADED	@"CMAircraftDatabaseLoaded"
#define AIRCRAFTUPDATED	@"CMAircraftDatabaseUpdated"

@interface CMAircraftGroup : NSObject
@property (copy) NSString *group;
@property (copy) NSArray *array;

- (id)initWithName:(NSString *)name list:(NSArray *)list;
@end

@interface CMAircraftDatabase : NSObject <NSURLConnectionDelegate>

+ (CMAircraftDatabase *)shared;

- (void)reload;

- (CMWBAircraft *)aircraftForName:(NSString *)name;

- (int)groupCount;
- (NSString *)groupName:(int)index;
- (int)aircraftCountForGroupIndex:(int)gindex;
- (CMWBAircraft *)aircraft:(int)index forGroupIndex:(int)gindex;

- (int)userAircraftCount;
- (CMWBAircraft *)aircraftForUserIndex:(int)index;

/*
 *	Edit support
 */

// Adds aircraft to database with generated name
- (void)addNewAircraft;

// Duplicates the aircraft provided, giving a new name
- (void)duplicateAircraft:(CMWBAircraft *)oldCopy;

// Deletes the aircraft at the given index
- (void)deleteAircraft:(int)index;

// Saves the aircraft at the given index. Returns false if unable.
- (BOOL)saveAircraft:(CMWBAircraft *)aircraft atIndex:(int)index;

@end
