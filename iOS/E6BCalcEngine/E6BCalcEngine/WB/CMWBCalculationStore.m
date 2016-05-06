//
//  CMWBCalculationStore.m
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

#import "CMWBCalculationStore.h"
#import "CMXMLParser.h"

@interface CMWBCalculationStore ()
@property (strong) NSMutableArray *array;
@end

@implementation CMWBCalculationStore

+ (CMWBCalculationStore *)shared
{
	static CMWBCalculationStore *instance;
	static dispatch_once_t onceToken;
	dispatch_once(&onceToken, ^{
		instance = [[CMWBCalculationStore alloc] init];
	});
	return instance;
}

- (id)init
{
	if (nil != (self = [super init])) {
		/*
		 *	If anything should happen while loading the data, that means
		 *	that somehow the data was corrupted. We need to delete the
		 *	data that was stored; this is better than an application that
		 *	constantly crashes when the user starts it up
		 */

		@try {
			[self loadData];
		}
		@catch (NSException *exception) {
			[self deleteData];
		}
	}
	return self;
}

- (void)loadData
{
	self.array = [[NSMutableArray alloc] init];

	NSString *rootPath = [NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, YES) objectAtIndex:0];
	NSString *plistPath = [rootPath stringByAppendingPathComponent:@"save.xml"];

	CMXMLParser *parser = [[CMXMLParser alloc] initWithURL:[NSURL fileURLWithPath:plistPath]];
	CMXMLNode *doc = [parser parse];

	if (doc == nil) {
		CMWBCalculation *tmp = [[CMWBCalculation alloc] init];
		[self.array addObject:tmp];
	} else {
		NSArray *a = [doc elementsForName:@"aircraft"];
		for (CMXMLNode *e in a) {
			CMWBData *tdata = [[CMWBData alloc] initFromNode:e];
			CMWBCalculation *tmp = [[CMWBCalculation alloc] initWithData:tdata];
			[self.array addObject:tmp];
		}
	}
}

- (void)saveData
{
	CMXMLNode *elem = [CMXMLNode elementWithName:@"data"];
	
	for (CMWBCalculation *c in self.array) {
		CMXMLNode *ec = [c.data toNode];
		[elem addNode:ec];
	}

	NSString *xmlData = [elem generateXML];
	NSData *data = [xmlData dataUsingEncoding:NSUTF8StringEncoding];

	NSString *rootPath = [NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, YES) objectAtIndex:0];
	NSFileManager *fm = [NSFileManager defaultManager];
	[fm createDirectoryAtPath:rootPath withIntermediateDirectories:YES attributes:nil error:nil];
	
	NSString *plistPath = [rootPath stringByAppendingPathComponent:@"save.xml"];
	[data writeToFile:plistPath atomically:YES];
}

- (void)deleteData
{
	/*
	 *	Deletes the save.xml file
	 */

	NSError *err;
	NSString *rootPath = [NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, YES) objectAtIndex:0];
	NSString *plistPath = [rootPath stringByAppendingPathComponent:@"save.xml"];

	NSFileManager *fm = [NSFileManager defaultManager];
	[fm removeItemAtPath:plistPath error:&err];

	/*
	 *	Zero internal store
	 */

	self.array = [[NSMutableArray alloc] init];
}


- (NSInteger)numberCalculations
{
	return [self.array count];
}

- (CMWBCalculation *)calculationAtIndex:(NSInteger)index
{
	return self.array[index];
}

- (CMWBCalculation *)appendCalculation
{
	CMWBCalculation *c = [[CMWBCalculation alloc] init];
	[self.array addObject:c];
	[self saveData];
	return c;
}

- (void)deleteCalculationAtIndex:(NSInteger)index
{
	[self.array removeObjectAtIndex:index];
	[self saveData];
}

@end
