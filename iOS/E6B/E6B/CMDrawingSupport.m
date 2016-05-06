//
//  CMDrawingSupport.m
//  E6B
//
//  Created by William Woody on 2/7/15.
//  Copyright (c) 2015 William Woody. All rights reserved.
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

#import "CMDrawingSupport.h"

void CMDrawFrame(CGRect r)
{
	[[UIColor colorWithWhite:0.94 alpha:1.0] setFill];
	CGRect tmp = CGRectMake(r.origin.x, r.origin.y, r.size.width, 1);
	UIRectFill(tmp);

	[[UIColor colorWithWhite:0.89 alpha:1.0] setFill];
	tmp = CGRectMake(r.origin.x, r.origin.y, 1, r.size.height);
	UIRectFill(tmp);
	tmp = CGRectMake(r.origin.x + r.size.width - 1, r.origin.y, 1, r.size.height);
	UIRectFill(tmp);

	[[UIColor colorWithWhite:0.85 alpha:1.0] setFill];
	tmp = CGRectMake(r.origin.x, r.origin.y + r.size.height-1, r.size.width, 1);
	UIRectFill(tmp);
}

void CMDrawInset(CGRect r)
{
	[[UIColor colorWithWhite:0.85 alpha:1.0] setFill];
	CGRect tmp = CGRectMake(r.origin.x, r.origin.y, r.size.width, 1);
	UIRectFill(tmp);

	[[UIColor colorWithWhite:0.89 alpha:1.0] setFill];
	tmp = CGRectMake(r.origin.x, r.origin.y, 1, r.size.height);
	UIRectFill(tmp);
	tmp = CGRectMake(r.origin.x + r.size.width - 1, r.origin.y, 1, r.size.height);
	UIRectFill(tmp);

	[[UIColor colorWithWhite:0.94 alpha:1.0] setFill];
	tmp = CGRectMake(r.origin.x, r.origin.y + r.size.height-1, r.size.width, 1);
	UIRectFill(tmp);
}

