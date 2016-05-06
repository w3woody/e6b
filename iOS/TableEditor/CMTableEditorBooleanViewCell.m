//
//  CMTableEditorBooleanViewCell.m
//  E6B
//
//  Created by William Woody on 12/20/14.
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

#import "CMTableEditorBooleanViewCell.h"

@interface CMTableEditorBooleanViewCell ()
@property (weak, nonatomic) IBOutlet UILabel *label;
@property (weak, nonatomic) IBOutlet UISwitch *button;
@property (copy) void (^callback)(BOOL flag);
@end

@implementation CMTableEditorBooleanViewCell

- (void)awakeFromNib {
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)setLabel:(NSString *)label value:(BOOL)value callback:(void (^)(BOOL newValue))callback
{
	self.label.text = label;
	self.button.on = value;
	self.callback = callback;
}

- (IBAction)doUpdateValue:(id)sender
{
	self.callback(self.button.isOn);
}

@end
