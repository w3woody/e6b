//
//  CMAircraftTableViewController.m
//  E6B
//
//  Created by William Woody on 11/28/14.
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

#import "CMAircraftTableViewController.h"
#import "CMAircraftDatabase.h"

@interface CMAircraftTableViewController ()

@end

@implementation CMAircraftTableViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;
    
	self.preferredContentSize = CGSizeMake(320,396);
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
	CMAircraftDatabase *db = [CMAircraftDatabase shared];
	return [db groupCount] + 1;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
	if (section == 0) {
		return NSLocalizedString(@"User Defined", @"User Defined aircraft list header");
	} else {
		CMAircraftDatabase *db = [CMAircraftDatabase shared];
		return [db groupName:(int)section-1];
	}
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
	CMAircraftDatabase *db = [CMAircraftDatabase shared];
	if (section == 0) {
		return [db userAircraftCount];
	} else {
		return [db aircraftCountForGroupIndex:(int)section-1];
	}
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"aircraftCell" forIndexPath:indexPath];

	CMAircraftDatabase *db = [CMAircraftDatabase shared];
	CMWBAircraft *a;
	if (indexPath.section == 0) {
		a = [db aircraftForUserIndex:(int)indexPath.row];
	} else {
		a = [db aircraft:(int)indexPath.row forGroupIndex:(int)indexPath.section-1];
	}
	cell.textLabel.text = a.name;

	if ([a.name isEqualToString:self.curAircraft]) {
		cell.accessoryType = UITableViewCellAccessoryCheckmark;
	} else {
		cell.accessoryType = UITableViewCellAccessoryNone;
	}

    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
	CMAircraftDatabase *db = [CMAircraftDatabase shared];
	CMWBAircraft *a;
	if (indexPath.section == 0) {
		a = [db aircraftForUserIndex:(int)indexPath.row];
	} else {
		a = [db aircraft:(int)indexPath.row forGroupIndex:(int)indexPath.section-1];
	}

	if (self.selectAircraft) {
		self.selectAircraft(a);
	}
}




@end
