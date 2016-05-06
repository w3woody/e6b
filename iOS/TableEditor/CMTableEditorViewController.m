//
//  CMTableEditorViewController.m
//  E6B
//
//  Created by William Woody on 12/16/14.
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

#import "CMTableEditorViewController.h"
#import "CMTableEditorMenuViewController.h"
#import "CMTablePage.h"
#import "CMTableGroup.h"
#import "CMTableItem.h"
#import "CMTableEditorViewCell.h"
#import "CMTableEditorBooleanViewCell.h"

@interface CMTableEditorViewController ()
@property (strong) CMTablePage *pageData;
@property (copy) BOOL (^saveCallback)(CMTablePage *pageData);
@end

@implementation CMTableEditorViewController

/************************************************************************/
/*																		*/
/*	Class Stuff															*/
/*																		*/
/************************************************************************/
#pragma mark - Class Headers

+ (void)present:(UIViewController *)ctl withPage:(CMTablePage *)pageData tint:(UIColor *)color callback:(BOOL (^)(CMTablePage *data))callback
{
	/*
	 *	Create editor
	 */

	UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"CMTableEditorStoryboard" bundle:nil];
	CMTableEditorViewController *editor = storyboard.instantiateInitialViewController;
	editor.pageData = pageData;
	editor.saveCallback = callback;

	/*
	 *	Create as popup
	 */

	UINavigationController *popover = [[UINavigationController alloc] initWithRootViewController:editor];
	popover.view.tintColor = color;
	[ctl presentViewController:popover animated:YES completion:nil];
}

- (void)viewDidLoad
{
	[super viewDidLoad];

	/*
	 *	Set the name of this page in the title bar
	 */

	[self.navigationItem setTitle:self.pageData.pageName];
	[self.tableView setEditing:YES];		/* Always editing */
	[self.tableView setAllowsSelectionDuringEditing:YES];

	/*
	 *	If we have a save callback, wire up.
	 */

	if (self.saveCallback) {
		UIBarButtonItem *save = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemSave target:self action:@selector(doSave:)];
		self.navigationItem.rightBarButtonItem = save;

		UIBarButtonItem *cancel = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(doCancel:)];
		self.navigationItem.leftBarButtonItem = cancel;
	}

	/*
	 *	Set up for callback
	 */

	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(doUpdatePageName:) name:CMTABLEFORMATNOTIFICATION_PAGENAME object:self.pageData];

	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardShowHide:) name:UIKeyboardWillHideNotification object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardShowHide:) name:UIKeyboardWillShowNotification object:nil];
}

- (void)dealloc
{
	[[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)viewWillAppear:(BOOL)animated
{
	[super viewWillAppear:animated];

	/*
	 *	Reload page names
	 */

	int section = 0;
	for (CMTableGroup *g in self.pageData.groups) {
		if ([g isKindOfClass:[CMTableGroupArray class]]) {
			CMTableGroupArray *ga = (CMTableGroupArray *)g;
			int row = 0;
			for (CMTablePage *pg in ga.pageData) {
				NSIndexPath *path = [NSIndexPath indexPathForRow:row inSection:section];
				UITableViewCell *cell = [self.tableView cellForRowAtIndexPath:path];
				cell.textLabel.text = pg.pageName;
				++row;
			}
		}
		++section;
	}
}

/*
 *	Update page name
 */

- (void)doUpdatePageName:(NSNotification *)notification
{
	if (notification.object == self.pageData) {
		/*
		 *	This is my name.
		 */

		[self.navigationItem setTitle:self.pageData.pageName];
	} else {
		/*
		 *	See if I have an item with this page. If so, then rename the
		 *	cell
		 */

		int section = 0;
		for (CMTableGroup *g in self.pageData.groups) {
			if ([g isKindOfClass:[CMTableGroupArray class]]) {
				CMTableGroupArray *ga = (CMTableGroupArray *)g;
				int row = 0;
				for (CMTablePage *pg in ga.pageData) {
					if (pg == notification.object) {
						NSIndexPath *path = [NSIndexPath indexPathForRow:row inSection:section];
						UITableViewCell *cell = [self.tableView cellForRowAtIndexPath:path];
						cell.textLabel.text = pg.pageName;
						break;
					}
					++row;
				}
			}
			++section;
		}
	}
}

/*
 *	Save
 */

- (void)doSave:(id)sender
{
	if (self.saveCallback(self.pageData)) {
		[self dismissViewControllerAnimated:YES completion:nil];
	}
}

- (void)doCancel:(id)sender
{
	[self dismissViewControllerAnimated:YES completion:nil];
}

- (void)keyboardShowHide:(NSNotification *)n
{
	CGRect krect;

	/* Extract the size of the keyboard when the animation stops */
	krect = [n.userInfo[UIKeyboardFrameEndUserInfoKey] CGRectValue];

	/* Convert that to the rectangle in our primary view. Note the raw
	 * keyboard size from above is in the window's frame, which could be
	 * turned on its side.
	 */
	krect = [self.view convertRect:krect fromView:nil];

	/* Get the animation duration, and animation curve */
	NSTimeInterval duration = [[n.userInfo objectForKey:UIKeyboardAnimationDurationUserInfoKey] doubleValue];
	UIViewAnimationCurve curve = [[n.userInfo objectForKey:UIKeyboardAnimationCurveUserInfoKey] intValue];

	/* Kick off the animation. What you do with the keyboard size is up to you */
	[UIView animateWithDuration:0 delay:duration options:UIViewAnimationOptionBeginFromCurrentState | curve animations:^{
			/* Set up the destination rectangle sizes given the keyboard size */

			CGRect r = self.tableView.bounds;
			UIEdgeInsets insets = [self.tableView contentInset];
			insets.bottom = r.size.height + r.origin.y - krect.origin.y;
			if (insets.bottom < 0) insets.bottom = 0;
			[self.tableView setContentInset:insets];
		} completion:^(BOOL finished) {
			/* Finish up here */
		}];
}

/************************************************************************/
/*																		*/
/*	Class Stuff															*/
/*																		*/
/************************************************************************/
#pragma mark - Table Delegate

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
	return self.pageData.groups.count;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
	CMTableGroup *g = self.pageData.groups[section];
	return g.groupName;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
	CMTableGroup *g = self.pageData.groups[section];

	if ([g isKindOfClass:[CMTableGroupArray class]]) {
		CMTableGroupArray *ga = (CMTableGroupArray *)g;
		return ga.pageData.count + 1;
	} else {
		CMTableGroupStructure *gs = (CMTableGroupStructure *)g;
		return gs.groupItems.count;
	}
}

- (NSIndexPath *)tableView:(UITableView *)tableView targetIndexPathForMoveFromRowAtIndexPath:(NSIndexPath *)sourceIndexPath toProposedIndexPath:(NSIndexPath *)proposedDestinationIndexPath
{
	// Keep moves within single sections. Do not move 'add' row at bottom
	if (sourceIndexPath.section != proposedDestinationIndexPath.section) {
		NSInteger row;
		if (sourceIndexPath.section < proposedDestinationIndexPath.section) {
			row = [tableView numberOfRowsInSection:sourceIndexPath.section]-2;
		} else {
			row = 0;
		}
		return [NSIndexPath indexPathForRow:row inSection:sourceIndexPath.section];
	} else {
		// If drag destination is past 'add', bump to before
		NSInteger maxRow = [tableView numberOfRowsInSection:sourceIndexPath.section];
		if (proposedDestinationIndexPath.row >= maxRow-1) {
			return [NSIndexPath indexPathForRow:maxRow-2 inSection:sourceIndexPath.section];
		} else {
			return proposedDestinationIndexPath;
		}
	}
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
	CMTableGroup *g = self.pageData.groups[indexPath.section];

	if ([g isKindOfClass:[CMTableGroupArray class]]) {
		return YES;
	} else {
		return NO;
	}
}

- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath
{
	CMTableGroup *g = self.pageData.groups[indexPath.section];

	if ([g isKindOfClass:[CMTableGroupArray class]]) {
		CMTableGroupArray *ga = (CMTableGroupArray *)g;
		NSInteger nrows = [ga.pageData count];
		return ga.reorderable && (indexPath.row < nrows);
	} else {
		return NO;
	}
}

- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath
{
	CMTableGroup *g = self.pageData.groups[indexPath.section];

	if ([g isKindOfClass:[CMTableGroupArray class]]) {
		CMTableGroupArray *ga = (CMTableGroupArray *)g;

		NSInteger maxrow = ga.pageData.count;
		if (indexPath.row < maxrow) {
			return UITableViewCellEditingStyleDelete;
		} else {
			return UITableViewCellEditingStyleInsert;
		}
	} else {
		return UITableViewCellEditingStyleNone;
	}
}

// TODO: Cell, cell editor, add, delete, move calls.

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
	[tableView deselectRowAtIndexPath:indexPath animated:YES];

	CMTableGroup *g = self.pageData.groups[indexPath.section];
	if ([g isKindOfClass:[CMTableGroupArray class]]) {
		CMTableGroupArray *ga = (CMTableGroupArray *)g;

		/*
		 *	Select row
		 */

		NSInteger maxrow = ga.pageData.count;
		if (indexPath.row < maxrow) {
			/*
			 *	Open page editor for edit.
			 */

			UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"CMTableEditorStoryboard" bundle:nil];
			CMTableEditorViewController *editor = storyboard.instantiateInitialViewController;
			editor.pageData = ga.pageData[indexPath.row];
			editor.saveCallback = nil;
			[self.navigationController pushViewController:editor animated:YES];
		}

	} else {
		CMTableGroupStructure *gs = (CMTableGroupStructure *)g;

		/*
		 *	Select the row to edit
		 */

		CMTableItem *item = gs.groupItems[indexPath.row];
		if (item.itemType == CMTableItemTypeArray) {
			/*
			 *	Select array
			 */

			NSInteger index = [((NSNumber *)item.data) integerValue];
			CMTableEditorMenuViewController *vc = [[CMTableEditorMenuViewController alloc] initWithArray:item.arrayValues selected:index withCallback:^(NSInteger index) {
				item.data = @( index );

				// Refresh
				UITableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
				cell.detailTextLabel.text = item.arrayValues[index];

				// Update item
				[self.pageData updateItem:item];
			}];
			[self.navigationController pushViewController:vc animated:YES];

		} else if (item.itemType != CMTableItemTypeBoolean) {
			/*
			 *	Edit item
			 */

			CMTableEditorViewCell *cell = (CMTableEditorViewCell *)[tableView cellForRowAtIndexPath:indexPath];
			[cell startEditing];
		}
	}
}

/*
 *	Move rows
 */

- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)sourceIndexPath toIndexPath:(NSIndexPath *)destinationIndexPath
{
	CMTableGroup *g = self.pageData.groups[sourceIndexPath.section];

	if ([g isKindOfClass:[CMTableGroupArray class]]) {
		CMTableGroupArray *ga = (CMTableGroupArray *)g;
		CMTablePage *gp = ga.pageData[sourceIndexPath.row];
		[ga.pageData removeObjectAtIndex:sourceIndexPath.row];

		[ga.pageData insertObject:gp atIndex:destinationIndexPath.row];
	}
}

/*
 *	Delete/add
 */

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
	CMTableGroup *g = self.pageData.groups[indexPath.section];

	if ([g isKindOfClass:[CMTableGroupArray class]]) {
		CMTableGroupArray *ga = (CMTableGroupArray *)g;
		if (editingStyle == UITableViewCellEditingStyleDelete) {
			[ga.pageData removeObjectAtIndex:indexPath.row];
			[tableView deleteRowsAtIndexPaths:@[ indexPath ] withRowAnimation:UITableViewRowAnimationAutomatic];
		} else {
			CMTablePage *insertPage = [[ga.pageClass alloc] init];
			[ga.pageData addObject:insertPage];
			[tableView insertRowsAtIndexPaths:@[ indexPath ] withRowAnimation:UITableViewRowAnimationAutomatic];
		}
	}
}

/*
 *	Row (TODO)
 */

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
	CMTableGroup *g = self.pageData.groups[indexPath.section];
	if ([g isKindOfClass:[CMTableGroupArray class]]) {
		/*
		 *	Array list. Each item is a pop-down to edit the page
		 *	data
		 */

		CMTableGroupArray *ga = (CMTableGroupArray *)g;
		CMTablePage *page;
		if (indexPath.row >= ga.pageData.count) {
			/*
			 *	Add row
			 */

			UITableViewCell *cell;
			cell = [tableView dequeueReusableCellWithIdentifier:@"AddCell" forIndexPath:indexPath];
			cell.textLabel.text = @"Add...";
			return cell;
		} else {
			/*
			 *	Row
			 */

			page = ga.pageData[indexPath.row];

			UITableViewCell *cell;
			cell = [tableView dequeueReusableCellWithIdentifier:@"PageCell" forIndexPath:indexPath];
			cell.textLabel.text = page.pageName;
			return cell;
		}

	} else {
		/*
		 *	Each row is an item: a string editor, a button or an array
		 *	selector
		 */

		CMTableGroupStructure *gs = (CMTableGroupStructure *)g;
		CMTableItem *item = gs.groupItems[indexPath.row];

		if (item.itemType == CMTableItemTypeArray) {
			/*
			 *	Array selector
			 */

			UITableViewCell *cell;
			cell = [tableView dequeueReusableCellWithIdentifier:@"ArrayCell" forIndexPath:indexPath];

			NSArray *data = item.arrayValues;
			cell.textLabel.text = item.itemName;
			NSInteger index = [(NSNumber *)item.data integerValue];
			cell.detailTextLabel.text = data[index];

			return cell;

		} else if (item.itemType == CMTableItemTypeBoolean) {
			/*
			 *	Boolean value
			 */

			CMTableEditorBooleanViewCell *c;
			c = [tableView dequeueReusableCellWithIdentifier:@"BooleanCell"];

			BOOL val = [(NSNumber *)item.data boolValue];
			[c setLabel:item.itemName value:val callback:^(BOOL newValue) {
				item.data = @( newValue );

				// Update item
				[self.pageData updateItem:item];
			}];
			return c;

		} else if (item.itemType == CMTableItemTypeString) {
			/*
			 *	Text editor
			 */

			CMTableEditorViewCell *cell;
			cell = [tableView dequeueReusableCellWithIdentifier:@"EditorCell" forIndexPath:indexPath];
			[cell setEditorType:item.itemType];

			[cell setLabel:item.itemName value:(NSString *)item.data callback:^(NSString *newValue) {
				item.data = [newValue copy];

				// Update item
				[self.pageData updateItem:item];
			}];

			return cell;
		} else {
			/*
			 *	Text editor (TODO: Set type and convert)
			 */

			CMTableEditorViewCell *cell;
			cell = [tableView dequeueReusableCellWithIdentifier:@"EditorCell" forIndexPath:indexPath];
			[cell setEditorType:item.itemType];

			NSString *value = [(NSNumber *)item.data stringValue];
			[cell setLabel:item.itemName value:value callback:^(NSString *newValue) {
				if (item.itemType == CMTableItemTypeFloat) {
					item.data = [NSNumber numberWithDouble:[newValue doubleValue]];
				} else {
					item.data = [NSNumber numberWithInteger:[newValue integerValue]];
				}

				// Update item
				[self.pageData updateItem:item];
			}];

			return cell;
		}
	}
}

@end
