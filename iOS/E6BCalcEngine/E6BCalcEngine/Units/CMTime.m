//
//  CMTime.m
//  E6B
//
//  Created by William Woody on 9/15/12.
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

#import "CMTime.h"

@implementation CMTime

- (NSArray *)measurements
{
	return @[ @"Time (H:M:S)" ];
}

- (NSArray *)abbrMeasure
{
	return @[ @"time" ];
}

- (int)standardUnit
{
	return TIME_TIME;		/* Using MKS internally */
}

- (double)toStandardUnit:(double)value withUnit:(int)index
{
	return value;
}

- (double)fromStandardUnit:(double)value withUnit:(int)index
{
	return value;
}

@end
