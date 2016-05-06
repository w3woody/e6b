//
//  CMWBCalculation.h
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

#import <Foundation/Foundation.h>
#import "CMWBData.h"

@interface CMWBCalculation : NSObject

- (id)init;
- (id)initWithData:(CMWBData *)data;

- (NSString *)calculationName;
- (NSString *)aircraftName;

- (void)setCalculationName:(NSString *)name;
- (void)setAircraftName:(NSString *)name;

/*
 *	Calculation Data
 */

- (CMWBData *)data;
- (CMWBAircraft *)aircraftData;


@end
