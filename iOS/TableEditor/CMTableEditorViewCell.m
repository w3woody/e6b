//
//  CMTableEditorViewCell.m
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

#import "CMTableEditorViewCell.h"

@interface CMTableEditorViewCell ()
@property (weak, nonatomic) IBOutlet UILabel *label;
@property (weak, nonatomic) IBOutlet UITextField *editorField;
@property (copy) void (^callback)(NSString *value);
@end

@implementation CMTableEditorViewCell


- (void)awakeFromNib
{
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)setLabel:(NSString *)label value:(NSString *)value callback:(void (^)(NSString *newValue))callback
{
	self.label.text = label;
	self.editorField.text = value;
	self.callback = callback;
}

- (IBAction)doUpdateValue:(id)sender
{
	NSString *str = self.editorField.text;
	self.callback(str);
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
	[textField resignFirstResponder];
	return NO;
}

- (void)startEditing
{
	[self.editorField becomeFirstResponder];
}

- (void)setEditorType:(CMTableItemType)type
{
	self.editorField.autocapitalizationType = UITextAutocapitalizationTypeNone;
	self.editorField.autocorrectionType = UITextAutocorrectionTypeNo;
	if (type == CMTableItemTypeString) {
		self.editorField.keyboardType = UIKeyboardTypeAlphabet;
	} else if (type == CMTableItemTypeInteger) {
		self.editorField.keyboardType = UIKeyboardTypeNumberPad;
	} else {
		self.editorField.keyboardType = UIKeyboardTypeDecimalPad;
	}
}

@end
