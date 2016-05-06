//
//  CMAircraftDatabase.m
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

#import "CMAircraftDatabase.h"
#import "CMXMLParser.h"
#import "CMWBData.h"

/************************************************************************/
/*																		*/
/*	Aircraft group														*/
/*																		*/
/************************************************************************/

@implementation CMAircraftGroup

- (id)initWithName:(NSString *)name list:(NSArray *)list
{
	if (nil != (self = [super init])) {
		self.group = name;
		self.array = list;
	}
	return self;
}

@end

/************************************************************************/
/*																		*/
/*	Aircraft Storage													*/
/*																		*/
/************************************************************************/

static CMAircraftDatabase *GData;

/************************************************************************/
/*																		*/
/*	Aircraft Data														*/
/*																		*/
/************************************************************************/

@interface CMAircraftDatabase ()
@property (strong) NSMutableArray *groups;
@property (strong) NSMutableDictionary *dictionary;
@property (strong) NSMutableArray *userAircraft;
@property (strong) NSMutableDictionary *userDictionary;

- (BOOL)parseData:(NSURL *)d;
@end

@implementation CMAircraftDatabase

/************************************************************************/
/*																		*/
/*	Class Stuff															*/
/*																		*/
/************************************************************************/

+ (CMAircraftDatabase *)shared
{
	if (GData == nil) {
		GData = [[CMAircraftDatabase alloc] init];
	}
	return GData;
}

- (id)init
{
	if (nil != (self = [super init])) {
		self.groups = [[NSMutableArray alloc] initWithCapacity:10];
		self.dictionary = [[NSMutableDictionary alloc] initWithCapacity:10];
		self.userDictionary = [[NSMutableDictionary alloc] initWithCapacity:10];
		self.userAircraft = [[NSMutableArray alloc] initWithCapacity:10];
		
		NSURL *url = [[NSBundle mainBundle] URLForResource:@"aircraft" withExtension:@"xml"];
		[self parseData:url];
	}
	return self;
}

/************************************************************************/
/*																		*/
/*	Data Loading/Saving													*/
/*																		*/
/************************************************************************/

/*	parseData
 *
 *		Internal method to load the internal structures from the XML contents
 *	of the file
 */

- (BOOL)parseData:(NSURL *)url
{
	CMXMLParser *doc = [[CMXMLParser alloc] initWithURL:url];

	NSMutableDictionary *ac = [[NSMutableDictionary alloc] initWithCapacity:10];
	NSMutableArray *ga = [[NSMutableArray alloc] initWithCapacity:10];
	
	CMXMLNode *root = [doc parse];
	if (root == nil) {
		NSLog(@"Parser failure");
		return NO;
	}
	NSArray *list = [root elementsForName:@"group"];
	for (CMXMLNode *gelem in list) {
		NSMutableArray *al = [[NSMutableArray alloc] initWithCapacity:10];
		
		NSArray *alist = [gelem elementsForName:@"aircraft"];
		for (CMXMLNode *aelem in alist) {
			CMWBAircraft *aircraft = [[CMWBAircraft alloc] initFromNode:aelem];
			[al addObject:aircraft];
			[ac setObject:aircraft forKey:aircraft.name];
		}
		
		NSString *tmp = gelem[@"name"];
		CMAircraftGroup *group = [[CMAircraftGroup alloc] initWithName:tmp list:al];
		[ga addObject:group];
	}

	/*
	 *	Now repopulate and send message
	 */
	
	self.groups = ga;
	self.dictionary = ac;
	
	/*
	 *	Load the custom directory
	 */
	 
	[self reload];

	return YES;
}

/************************************************************************/
/*																		*/
/*	Document Aircraft W&B Data											*/
/*																		*/
/************************************************************************/

- (void)reload
{
	NSError *err;
	NSString *rootPath = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];

	NSFileManager *fm = [NSFileManager defaultManager];
	
	[self.userAircraft removeAllObjects];
	[self.userDictionary removeAllObjects];
	
	/* Scan document directory for all files ending .axml */
	NSArray *fileList = [fm contentsOfDirectoryAtPath:rootPath error:&err];
	for (NSString *str in fileList) {
		if ([str hasSuffix:@".axml"]) {
			NSString *pathList = [rootPath stringByAppendingPathComponent:str];
			
			@try {
				NSURL *url = [NSURL fileURLWithPath:pathList];
				CMXMLParser *doc = [[CMXMLParser alloc] initWithURL:url];
				CMXMLNode *node = [doc parse];
				if (node == nil) {
					NSLog(@"Unable to load %@",pathList);
					continue;
				}

				CMWBAircraft *aircraft = [[CMWBAircraft alloc] initFromNode:node];
				
				NSString *name = [str substringToIndex:[str length]-5];
				aircraft.name = name;	/* Name from file */
				
				[self.userAircraft addObject:aircraft];
				[self.userDictionary setObject:aircraft forKey:aircraft.name];
			}
			@catch (id exc) {
				NSLog(@"Unable to load %@",pathList);
			}
		}
	}

	/* Let system know there is a new list */
	[[NSNotificationCenter defaultCenter] postNotificationName:AIRCRAFTLOADED object:self];
}

/*
 *	Save data
 */

- (void)saveData:(CMWBAircraft *)aircraft withOldName:(NSString *)name
{
	NSError *err;

	/*
	 *	Rename and save the file
	 */

	NSFileManager *fm = [NSFileManager defaultManager];
	NSString *rootPath = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
	NSString *toName = [rootPath stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.axml",aircraft.name]];
	if (name && ![name isEqual:aircraft.name]) {
		NSString *fromName = [rootPath stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.axml",name]];
		[fm moveItemAtPath:fromName toPath:toName error:&err];
	}
	CMXMLNode *data = [aircraft toNode];
	NSString *xml = [data generateXML];
	NSData *d = [xml dataUsingEncoding:NSUTF8StringEncoding];
	[d writeToFile:toName options:NSDataWritingAtomic error:&err];
}

/************************************************************************/
/*																		*/
/*	Data Access															*/
/*																		*/
/************************************************************************/

- (CMWBAircraft *)aircraftForName:(NSString *)name
{
	CMWBAircraft *a = [self.userDictionary objectForKey:name];
	if (a == nil) {
		a = [self.dictionary objectForKey:name];
	}
	return a;
}

- (int)groupCount
{
	return (int)[self.groups count];
}

- (NSString *)groupName:(int)index
{
	CMAircraftGroup *g = [self.groups objectAtIndex:index];
	return g.group;
}

- (int)aircraftCountForGroupIndex:(int)gindex
{
	CMAircraftGroup *g = [self.groups objectAtIndex:gindex];
	return (int)[g.array count];
}

- (CMWBAircraft *)aircraft:(int)index forGroupIndex:(int)gindex
{
	CMAircraftGroup *g = [self.groups objectAtIndex:gindex];
	CMWBAircraft *a = [g.array objectAtIndex:index];
	return a;
}

- (int)userAircraftCount
{
	return (int)[self.userAircraft count];
}

- (CMWBAircraft *)aircraftForUserIndex:(int)index
{
	CMWBAircraft *a = [self.userAircraft objectAtIndex:index];
	return a;
}

/************************************************************************/
/*																		*/
/*	Edit Support														*/
/*																		*/
/************************************************************************/

- (NSString *)uniqueName
{
	for (NSInteger index = 1; index < 999999; ++index) {
		NSString *str = (index == 1) ? @"Untitled" : [NSString stringWithFormat:@"Untitled %d",(int)index];
		if (nil == [self aircraftForName:str]) return str;
	}

	/* Punt */
	NSUUID *uuid = [NSUUID UUID];
	return [uuid UUIDString];
}

// Adds aircraft to database with generated name
- (void)addNewAircraft
{
	CMWBAircraft *a = [[CMWBAircraft alloc] init];
	a.name = [self uniqueName];

	CMWBFuelTank *tank = [[CMWBFuelTank alloc] init];
	tank.name = @"Fuel Tank";
	[a.fuel addObject:tank];

	[self.userAircraft addObject:a];
	[self.userDictionary setObject:a forKey:a.name];

	[self saveData:a withOldName:nil];

	[[NSNotificationCenter defaultCenter] postNotificationName:AIRCRAFTUPDATED object:self userInfo:@{ @"name" : a.name }];
}

// Duplicates the aircraft provided, giving a new name
- (void)duplicateAircraft:(CMWBAircraft *)oldCopy
{
	// Duplicate provided aircraft with unique name
	CMWBAircraft *a = [oldCopy copy];
	a.name = [self uniqueName];

	[self.userAircraft addObject:a];
	[self.userDictionary setObject:a forKey:a.name];

	[self saveData:a withOldName:nil];

	[[NSNotificationCenter defaultCenter] postNotificationName:AIRCRAFTUPDATED object:self userInfo:@{ @"name" : a.name }];
}

// Deletes the aircraft at the given index
- (void)deleteAircraft:(int)index
{
	NSError *err;

	CMWBAircraft *a = self.userAircraft[index];
	[self.userAircraft removeObjectAtIndex:index];
	[self.userDictionary removeObjectForKey:a.name];

	/*
	 *	Delete by name
	 */

	NSFileManager *fm = [NSFileManager defaultManager];
	NSString *rootPath = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
	NSString *toName = [rootPath stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.axml",a.name]];
	[fm removeItemAtPath:toName error:&err];

	[[NSNotificationCenter defaultCenter] postNotificationName:AIRCRAFTUPDATED object:self userInfo:@{ @"name" : a.name }];
}

// Saves the aircraft at the given index. Returns false if unable.
- (BOOL)saveAircraft:(CMWBAircraft *)aircraft atIndex:(int)index
{
	/*
	 *	Verify that the name doesn't match something already in the system
	 */

	CMWBAircraft *atIndex = self.userAircraft[index];
	CMWBAircraft *dup = [self aircraftForName:aircraft.name];
	if ((dup != nil) && (dup != atIndex)) return NO;	/* Name already taken */

	[self.userDictionary removeObjectForKey:atIndex.name];
	[self.userDictionary setObject:aircraft forKey:aircraft.name];
	self.userAircraft[index] = aircraft;

	/*
	 *	Save with rename if needed
	 */

	[self saveData:aircraft withOldName:atIndex.name];

	/*
	 *	Notify aircraft name updated
	 */

	[[NSNotificationCenter defaultCenter] postNotificationName:AIRCRAFTUPDATED object:self userInfo:@{ @"name" : aircraft.name }];

	return YES;
}



@end
