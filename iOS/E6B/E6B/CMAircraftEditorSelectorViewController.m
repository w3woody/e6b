//
//  CMAircraftEditorSelectorViewController.m
//  E6B
//
//  Created by William Woody on 11/29/14.
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

#import "CMAircraftEditorSelectorViewController.h"
#import "CMAircraftTableViewController.h"
#import "CMAircraftDatabase.h"
#import "CMCustomPushSegue.h"
#import "CMTableEditorViewController.h"
#import "CMTablePage.h"
#import "CMTableGroup.h"
#import "CMTableItem.h"
#import "CMAircraftClassPage.h"

@interface CMAircraftEditorSelectorViewController ()
@property (strong, nonatomic) IBOutlet UITableView *tableView;
@property (strong, nonatomic) IBOutlet UIBarButtonItem *duplicateButton;
@end

@implementation CMAircraftEditorSelectorViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
	return [[CMAircraftDatabase shared] userAircraftCount];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
	UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"aircraftCell" forIndexPath:indexPath];

	CMWBAircraft *a = [[CMAircraftDatabase shared] aircraftForUserIndex:(int)indexPath.row];

	cell.textLabel.text = a.name;
	cell.detailTextLabel.text = a.maker;
	return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
	CMWBAircraft *a = [[CMAircraftDatabase shared] aircraftForUserIndex:(int)indexPath.row];

	UIViewController *ev = self.navigationController;
	CMAircraftClassPage *data = [[CMAircraftClassPage alloc] initWithAircraft:a];
	[CMTableEditorViewController present:ev withPage:data tint:self.view.tintColor callback:^(CMTablePage *data) {

		/*
		 *	Deserialize and store the results
		 */

		int saveIndex = (int)indexPath.row;
		CMAircraftClassPage *pg = (CMAircraftClassPage *)data;
		CMWBAircraft *a = [pg aircraftForData];
		if (![[CMAircraftDatabase shared] saveAircraft:a atIndex:saveIndex]) {
			UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Unable to save" message:@"An aircraft with the same name has already been saved; please use a different name." delegate:nil cancelButtonTitle:@"OK" otherButtonTitles: nil];
			[alert show];
			return NO;
		} else {
			[self.tableView reloadData];
			return YES;
		}
	}];
}

- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath
{
	return UITableViewCellEditingStyleDelete;
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
	if (editingStyle == UITableViewCellEditingStyleDelete) {
		[[CMAircraftDatabase shared] deleteAircraft:(int)indexPath.row];
		[tableView deleteRowsAtIndexPaths:@[ indexPath ] withRowAnimation:YES];
	}
}

- (IBAction)doEdit:(id)sender
{
	UIBarButtonItem *btn = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(doDone:)];
	[self.navigationItem setRightBarButtonItem:btn];
	[self.tableView setEditing:YES animated:YES];
}

- (IBAction)doDone:(id)sender
{
	UIBarButtonItem *btn = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemEdit target:self action:@selector(doEdit:)];
	[self.navigationItem setRightBarButtonItem:btn];
	[self.tableView setEditing:NO animated:YES];
}

- (IBAction)doAdd:(id)sender
{
	[[CMAircraftDatabase shared] addNewAircraft];
	[self.tableView reloadData];
}

- (IBAction)doDuplicate:(id)sender
{
	[self performSegueWithIdentifier:@"presentAirplaneSelector" sender:self];
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
	/*
	 *	Perform a segue to present the selector, and when we get a
	 *	result, perform the duplicate operation
	 */

	if ([segue.identifier isEqualToString:@"presentAirplaneSelector"]) {
		CMCustomPushSegue *push = (CMCustomPushSegue *)segue;
		CMAircraftTableViewController *vc = (CMAircraftTableViewController *)segue.destinationViewController;

		push.presentButton = self.duplicateButton;

		vc.selectAircraft = ^(CMWBAircraft *aircraft) {
			[push closePopup];

			if (aircraft) {
				[[CMAircraftDatabase shared] duplicateAircraft:aircraft];
				[self.tableView reloadData];
			}
		};
	}
}

@end
