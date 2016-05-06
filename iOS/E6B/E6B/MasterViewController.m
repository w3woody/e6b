//
//  MasterViewController.m
//  E6B
//
//  Created by William Woody on 9/28/14.
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

#import "MasterViewController.h"
#import "DetailViewController.h"
#import "CMHeaderView.h"
#import "E6BCalcEngine.h"
#import "CMWBViewController.h"

@interface MasterViewController ()
@property (strong) IBOutlet UITableView *tableView;
@property (strong) IBOutlet UISegmentedControl *switchControl;

@property (strong) IBOutlet UIToolbar *toolbar;
@end

@implementation MasterViewController

- (void)awakeFromNib
{
	[super awakeFromNib];

	if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPad) {
	    self.preferredContentSize = CGSizeMake(320.0, 600.0);
	}
}

- (void)viewDidLoad
{
	[super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
//	self.navigationItem.leftBarButtonItem = self.editButtonItem;
//
//	UIBarButtonItem *addButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAdd target:self action:@selector(insertNewObject:)];
//	self.navigationItem.rightBarButtonItem = addButton;
	self.detailViewController = (DetailViewController *)[[self.splitViewController.viewControllers lastObject] topViewController];

	/*
	 *	Force selection of first item if this is not collapsed
	 */

//TODO: Open only if collapsed; figure out why this doesn't work...
//	if (![self.splitViewController isCollapsed]) {
//		[self.tableView selectRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0] animated:YES scrollPosition:UITableViewScrollPositionNone];
//		[self performSegueWithIdentifier:@"showDetail" sender:self];
//	}

	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(doUpdateTable:) name:NOTIFICATION_RENAMEWBCALC object:nil];
	[self.switchControl setSelectedSegmentIndex:0];
	[self.navigationItem setRightBarButtonItem:nil];
	[self.toolbar setHidden:YES];
}

- (void)didReceiveMemoryWarning
{
	[super didReceiveMemoryWarning];
	// Dispose of any resources that can be recreated.
}

#pragma mark - Segues

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
	if ([[segue identifier] isEqualToString:@"showDetail"]) {
	    NSIndexPath *indexPath = [self.tableView indexPathForSelectedRow];

		CME6BCalculationStore *store = [CME6BCalculationStore shared];
		id<CME6BCalculation> calc = [store calculationAtIndex:indexPath.row inGroup:indexPath.section];

	    DetailViewController *controller = (DetailViewController *)[[segue destinationViewController] topViewController];
		controller.calculation = calc;
	} else if ([[segue identifier] isEqualToString:@"showWB"]) {
	    NSIndexPath *indexPath = [self.tableView indexPathForSelectedRow];

		CMWBCalculationStore *store = [CMWBCalculationStore shared];
		CMWBCalculation *calc = [store calculationAtIndex:indexPath.row];

		CMWBViewController *controller = (CMWBViewController *)[[segue destinationViewController] topViewController];
		controller.calculation = calc;
	} else if ([[segue identifier] isEqualToString:@"addWB"]) {
		CMWBCalculationStore *store = [CMWBCalculationStore shared];
		CMWBCalculation *calc = [store appendCalculation];
		[self.tableView reloadData];

		CMWBViewController *controller = (CMWBViewController *)[[segue destinationViewController] topViewController];
		controller.calculation = calc;
	}

	/*
	 *	For all controllers we do this; this is how we get the back arrow
	 *	for all of the controllers
	 */
	
	UIViewController *c = [[segue destinationViewController] topViewController];
	c.navigationItem.leftBarButtonItem = self.splitViewController.displayModeButtonItem;
	c.navigationItem.leftItemsSupplementBackButton = YES;
}

#pragma mark - Controls

- (IBAction)doUpdateSelector:(id)sender
{
	UIEdgeInsets insets = [self.tableView contentInset];

	NSInteger selIndex = [self.switchControl selectedSegmentIndex];

	if (self.tableView.isEditing) {
		[self.tableView setEditing:NO animated:NO];
	}

	if (selIndex == 0) {
		[self.navigationItem setRightBarButtonItem:nil];
		[self.toolbar setHidden:YES];

		insets.bottom = 0;
		[self.tableView setContentInset:insets];
	} else {
		UIBarButtonItem *btn = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemEdit target:self action:@selector(doEdit:)];
		[self.navigationItem setRightBarButtonItem:btn];
		[self.toolbar setHidden:NO];

		insets.bottom = self.toolbar.frame.size.height;
		[self.tableView setContentInset:insets];
	}
	[self.tableView reloadData];
}

- (void)doEdit:(id)sender
{
	UIBarButtonItem *btn = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(doDone:)];
	[self.navigationItem setRightBarButtonItem:btn];
	[self.tableView setEditing:YES animated:YES];

	// blank if detail is showing
	if (!self.splitViewController.collapsed) {
		[self performSegueWithIdentifier:@"blankWB" sender:self];
	}
}

- (void)doDone:(id)sender
{
	UIBarButtonItem *btn = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemEdit target:self action:@selector(doEdit:)];
	[self.navigationItem setRightBarButtonItem:btn];
	[self.tableView setEditing:NO animated:YES];
}

- (void)doUpdateTable:(NSNotification *)n
{
	[self.tableView reloadData];
}

#pragma mark - Table View

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
	NSInteger selIndex = [self.switchControl selectedSegmentIndex];

	if (selIndex == 0) {
		CME6BCalculationStore *store = [CME6BCalculationStore shared];
		return [store numberGroups];
	} else {
		return 1;
	}
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
	NSInteger selIndex = [self.switchControl selectedSegmentIndex];

	if (selIndex == 0) {
		CMHeaderView *v = [[CMHeaderView alloc] initWithFrame:CGRectZero];
		CME6BCalculationStore *store = [CME6BCalculationStore shared];
		v.label = [store groupNameForIndex:section];
		return v;
	} else {
		CMHeaderView *v = [[CMHeaderView alloc] initWithFrame:CGRectZero];
		v.label = NSLocalizedString(@"Weight & Balance", @"Weight & Balance");
		return v;
	}
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
	NSInteger selIndex = [self.switchControl selectedSegmentIndex];

	if (selIndex == 0) {
		CME6BCalculationStore *store = [CME6BCalculationStore shared];
		return [store numberItemsInGroup:section];
	} else {
		CMWBCalculationStore *store = [CMWBCalculationStore shared];
		return [store numberCalculations];
	}
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
	UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"Cell" forIndexPath:indexPath];

	NSInteger selIndex = [self.switchControl selectedSegmentIndex];

	if (selIndex == 0) {
		CME6BCalculationStore *store = [CME6BCalculationStore shared];
		id<CME6BCalculation> calc = [store calculationAtIndex:indexPath.row inGroup:indexPath.section];

		cell.textLabel.text = [calc calculationName];
	} else {
		CMWBCalculationStore *store = [CMWBCalculationStore shared];
		CMWBCalculation *calc = [store calculationAtIndex:indexPath.row];

		cell.textLabel.text = [calc calculationName];
	}

	return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
	NSInteger selIndex = [self.switchControl selectedSegmentIndex];

	if (selIndex == 0) {
		[self performSegueWithIdentifier:@"showDetail" sender:self];
	} else {
		[self performSegueWithIdentifier:@"showWB" sender:self];
	}
}


- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
	NSInteger selIndex = [self.switchControl selectedSegmentIndex];
	if (selIndex == 1) return YES;
	return NO;
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
	if (editingStyle == UITableViewCellEditingStyleDelete) {
		CMWBCalculationStore *store = [CMWBCalculationStore shared];

		[store deleteCalculationAtIndex:indexPath.row];
	    [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
	}
}

@end
