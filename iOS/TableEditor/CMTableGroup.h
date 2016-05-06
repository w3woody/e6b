//
//  CMTableGroup.h
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

#import <Foundation/Foundation.h>

/*	CMTableGroup
 *
 *		Group of objects. Inherit from one of the specific items; a
 *	structure or an array
 */

@interface CMTableGroup : NSObject

@property (copy) NSString *groupName;

@end


/*	CMTableGroupStructure
 *
 *		Represents a set of descrete values
 */

@interface CMTableGroupStructure : CMTableGroup

- (id)initWithName:(NSString *)name groupItems:(NSArray *)items;

@property (strong) NSArray *groupItems;

@end

/*	CMTableGroupArray
 *
 *		Represents an array of pages.
 */

@interface CMTableGroupArray : CMTableGroup

- (id)initWithName:(NSString *)name pageClass:(Class)pageClass reorderable:(BOOL)flag;

/*
 *	The CMTablePage class which is used for each item in the array
 */

@property (assign) Class pageClass;

/*
 *	Return true if the items in this array can be reordered
 */

@property (assign) BOOL reorderable;

/*
 *	The page data for each item
 */

@property (strong) NSMutableArray *pageData;

@end
