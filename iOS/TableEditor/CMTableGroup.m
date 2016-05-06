//
//  CMTableGroup.m
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

#import "CMTablePage.h"
#import "CMTableGroup.h"

@implementation CMTableGroup
@end

@implementation CMTableGroupStructure

- (id)initWithName:(NSString *)name groupItems:(NSArray *)items
{
	if (nil != (self = [super init])) {
		self.groupName = name;
		self.groupItems = items;
	}
	return self;
}

@end

@implementation CMTableGroupArray

- (id)initWithName:(NSString *)name pageClass:(Class)pageClass reorderable:(BOOL)flag
{
	if (nil != (self = [super init])) {
		self.groupName = name;
		self.pageClass = pageClass;
		self.reorderable = flag;
		self.pageData = [[NSMutableArray alloc] init];
	}
	return self;
}

@end
