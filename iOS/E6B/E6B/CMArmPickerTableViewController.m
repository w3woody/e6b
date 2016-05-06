//
//  CMArmPickerTableViewController.m
//  E6B
//
//  Created by William Woody on 1/27/15.
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

#import "CMArmPickerTableViewController.h"
#import "CMWBData.h"
#import "NSString+FormatDecimal.h"

@interface CMArmPickerTableViewController ()

@end

@implementation CMArmPickerTableViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;
    
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
	CGFloat height = 44 * self.stations.count;
	if (height > 352) height = 352;	/* 8 * 44 */
	self.preferredContentSize = CGSizeMake(320,height);
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of sections.

    return self.stations.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"stationCell" forIndexPath:indexPath];

	CMWBAircraftStation *s = self.stations[indexPath.row];

	NSString *tmp = [NSString stringWithFormat:@"%f",s.arm];
	tmp = [tmp trimTrailingDecimal];
	tmp = [NSString stringWithFormat:@"%@ %@",tmp,self.units];
	cell.textLabel.text = tmp;
	cell.detailTextLabel.text = s.name;

	if (s.arm == self.curStation) {
		cell.accessoryType = UITableViewCellAccessoryCheckmark;
	} else {
		cell.accessoryType = UITableViewCellAccessoryNone;
	}

    return cell;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
	if (self.selectStation) {
		CMWBAircraftStation *s = self.stations[indexPath.row];
		self.selectStation(s.arm);
	}
}


@end
