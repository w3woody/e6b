//
//  CMTableItem.h
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

typedef enum CMTableItemType
{
	CMTableItemTypeString,
	CMTableItemTypeInteger,
	CMTableItemTypeFloat,
	CMTableItemTypeBoolean,
	CMTableItemTypeArray			/* Array of items, stored as index */
} CMTableItemType;


/*	CMTableItem
 *
 *		A single item in a group
 */

@interface CMTableItem : NSObject

/*
 *	Simple initializers
 */

- (id)initWithName:(NSString *)name type:(CMTableItemType)itemType;
- (id)initWithName:(NSString *)name array:(NSArray *)array;

/*
 *	Return type type of this item
 */

@property (assign) CMTableItemType itemType;

/*
 *	Return the name of this row
 */

@property (copy) NSString *itemName;

/*
 *	Return the array of possible values for this table item. Ideally this
 *	should be stored in a series of globals
 */

@property (strong) NSArray *arrayValues;

/*
 *	Used to store the value of this item. Type depends on the type
 *	that is being stored here. For String, this is NSString. For the others
 *	this is NSNumber. (Including the array type, which stores an integer
 *	index of the selected item)
 */

@property (copy) id<NSObject> data;

@end
