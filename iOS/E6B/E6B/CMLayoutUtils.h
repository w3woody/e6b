//
//  CMLayoutUtils.h
//  E6B
//
//  Created by William Woody on 12/13/14.
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

#import <UIKit/UIKit.h>

@interface CMLayout : NSObject

- (id)initWithWidth:(int)width yStart:(int)yStart;

- (void)advanceRowPosition:(int)height;
- (void)advanceNextRow;
- (void)setRowHeight:(int)height;

- (CGRect)cell:(int)index columnCount:(int)col;
- (CGRect)cell:(int)index width:(int)width columnCount:(int)col;
- (CGRect)cell:(int)index columnCount:(int)col rightIndent:(int)indent;
- (CGRect)cell:(int)index width:(int)width columnCount:(int)col rightIndent:(int)indent;
- (CGRect)rightCellIndent:(int)indent;

// Call when done; this returns the bottom (after an implicit call to
// nextRow
- (int)yBottom;

@end
