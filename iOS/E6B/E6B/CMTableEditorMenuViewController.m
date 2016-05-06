//
//  CMTableEditorMenuViewController.m
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

#import "CMTableEditorMenuViewController.h"

@interface CMTableEditorMenuViewController ()
@property (strong) NSArray *array;
@property (assign) NSInteger selected;
@property (copy) void (^callback)(NSInteger index);
@end

@implementation CMTableEditorMenuViewController

- (id)initWithArray:(NSArray *)array selected:(NSInteger)index withCallback:(void (^)(NSInteger index))callback
{
	if (nil != (self = [super initWithStyle:UITableViewStylePlain])) {
		self.array = array;
		self.selected = index;
		self.callback = callback;
	}
	return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
}

#pragma mark - Table view data source

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
	return self.array.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
	static NSString *cellString = @"TableCell";

    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:cellString];
	if (cell == nil) {
		cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellString];
	}

	cell.textLabel.text = self.array[indexPath.row];
	cell.accessoryType = (indexPath.row == self.selected) ? UITableViewCellAccessoryCheckmark : UITableViewCellAccessoryNone;
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
	[tableView deselectRowAtIndexPath:indexPath animated:YES];

	UITableViewCell *cell;
	cell = [tableView cellForRowAtIndexPath:[NSIndexPath indexPathForRow:self.selected inSection:0]];
	cell.accessoryType = UITableViewCellAccessoryNone;

	cell = [tableView cellForRowAtIndexPath:indexPath];
	cell.accessoryType = UITableViewCellAccessoryCheckmark;

	self.callback(indexPath.row);

	[self.navigationController popViewControllerAnimated:YES];
}


@end
