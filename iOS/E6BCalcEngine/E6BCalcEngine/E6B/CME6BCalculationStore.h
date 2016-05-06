//
//  CME6BCalculationStore.h
//  E6BCalcEngine
//
//  Created by William Woody on 10/20/14.
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
#import "CME6BCalculation.h"

@interface CME6BCalculationStore : NSObject

+ (CME6BCalculationStore *)shared;

+ (void)saveDefaults;
+ (void)deleteDefaults;

- (NSInteger)numberGroups;
- (NSString *)groupNameForIndex:(NSInteger)index;
- (NSInteger)numberItemsInGroup:(NSInteger)group;
- (id<CME6BCalculation>)calculationAtIndex:(NSInteger)index inGroup:(NSInteger)group;

@end
