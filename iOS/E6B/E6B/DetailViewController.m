//
//  DetailViewController.m
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

#import "DetailViewController.h"
#import "CMInputView.h"
#import "CMOutputView.h"
#import "CMCustomPushSegue.h"
#import "CMUnitPickerTableViewController.h"
#import "UIView+FirstResponder.h"

#define BORDER	10

@interface DetailViewController ()
@property (nonatomic, strong) IBOutlet UIScrollView *scrollView;
@property (nonatomic, strong) IBOutlet UIView *contentView;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *contentViewWidth;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *contentViewHeight;

@property (nonatomic, strong) UILabel *descLabel;

@property (nonatomic, strong) NSMutableArray *inputViews;
@property (nonatomic, strong) NSMutableArray *outputViews;

@property (nonatomic, strong) NSMutableArray *inputUnits;
@property (nonatomic, strong) NSMutableArray *outputUnits;

@property (nonatomic, assign) BOOL calcRunning;

@property (nonatomic, assign) int selUnit;
@property (nonatomic, assign) BOOL selUnitFlag;
@end

@implementation DetailViewController

#pragma mark - Managing the detail item

- (void)viewDidLoad
{
	[super viewDidLoad];

	// Do any additional setup after loading the view, typically from a nib.
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(runCalculation) name:NOTIFICATION_INPUTUPDATE object:nil];

	if (self.calculation == nil) {
		// starting up iPad; start first calculation
		self.calculation = [[CME6BCalculationStore shared] calculationAtIndex:0 inGroup:0];
	}

	[self configureView];

	// Set up scroll insets
	[self resizeInsets];
}

- (void)dealloc
{
	[[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)setCalculation:(id<CME6BCalculation>)calculation
{
	if (_calculation != calculation) {
	    _calculation = calculation;
	        
	    // Update the view.
	    [self configureView];
	}
}

- (void)configureView
{
	self.calcRunning = NO;

	// Update the user interface for the detail item.
	if (self.calculation) {
		self.navigationItem.title = [self.calculation calculationName];
	} else {
		self.navigationItem.title = @"No Calculation Selected";
	}

	/*
	 *	Tear down the old views
	 */

	NSArray *a = [NSArray arrayWithArray:self.contentView.subviews];
	for (UIView *v in a) [v removeFromSuperview];

	/*
	 *	Construct new views
	 */

	self.descLabel = [[UILabel alloc] initWithFrame:CGRectZero];
	self.descLabel.numberOfLines = 0;
	self.descLabel.text = [self.calculation calculationDescription];
	if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad) {
		self.descLabel.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:17];
	} else {
		self.descLabel.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:14];
	}
	[self.contentView addSubview:self.descLabel];

	/*
	 *	Construct inputs, outputs. This sets the default values and units
	 *	for the inputs and outputs
	 */

	self.inputViews = [[NSMutableArray alloc] init];
	self.outputViews = [[NSMutableArray alloc] init];
	self.inputUnits = [[NSMutableArray alloc] init];
	self.outputUnits = [[NSMutableArray alloc] init];

	NSArray *startInputValues = [self.calculation startInputValues];
	NSArray *startOutputValues = [self.calculation startOutputValues];

	int i,len;
	len = [self.calculation inputFieldCount];
	for (i = 0; i < len; ++i) {
		CMInputView *input = [[CMInputView alloc] initWithFrame:CGRectZero];
		[self.inputViews addObject:input];
		[self.contentView addSubview:input];

		id<CMMeasurement> m = [self.calculation inputFieldUnit:i];

		input.label = [self.calculation inputFieldName:i];
		CMValue *value = startInputValues[i];
		input.unit = m.abbrMeasure[value.unit];
		input.value = value.value;
		input.timeEditor = (m == GTime);
		input.showMenu = NO;

		[self.inputUnits addObject:@( value.unit )];

		input.unitPicker = ^{
			// Unit picker
			[self presentUnitEditorFor:i inputFlag:YES];
		};
	}

	len = [self.calculation outputFieldCount];
	for (i = 0; i < len; ++i) {
		CMOutputView *output = [[CMOutputView alloc] initWithFrame:CGRectZero];
		[self.outputViews addObject:output];
		[self.contentView addSubview:output];

		id<CMMeasurement> m = [self.calculation outputFieldUnit:i];

		output.label = [self.calculation outputFieldName:i];
		CMValue *value = startOutputValues[i];
		output.unit = m.abbrMeasure[value.unit];
		output.displayTime = (m == GTime);
		output.outputValue = value.value;

		[self.outputUnits addObject:@( value.unit )];

		output.unitPicker = ^{
			// Unit picker
			[self presentUnitEditorFor:i inputFlag:NO];
		};
	}

	self.calcRunning = YES;
	[self runCalculation];
}

- (void)presentUnitEditorFor:(int)index inputFlag:(BOOL)inputFlag
{
	id<CMMeasurement> units;

	if (inputFlag) {
		units = [self.calculation inputFieldUnit:index];
	} else {
		units = [self.calculation outputFieldUnit:index];
	}
	if ([[units abbrMeasure] count] <= 1) return;	/* No units to pick */

	self.selUnit = index;
	self.selUnitFlag = inputFlag;
	[self performSegueWithIdentifier:@"presentUnitSelector" sender:self];
}

- (void)viewDidLayoutSubviews
{
	/*
	 *	Start laying out the controls
	 */

	CGRect r = self.scrollView.frame;
	CGRect s = CGRectInset(r, BORDER, BORDER);

	CGSize size = [self.descLabel sizeThatFits:s.size];
	s.size.height = size.height;
	self.descLabel.frame = s;

	s.origin.y += s.size.height + BORDER;

	BOOL twoWide = s.size.width > 360;

	// Place inputs, outputs
	if (twoWide) {
		CGRect left = s;
		left.size.width = floorf((s.size.width - BORDER)/2);
		CGRect right = s;
		right.origin.x += left.size.width + BORDER;
		right.size.width -= left.size.width + BORDER;

		int i,len;
		len = [self.calculation inputFieldCount];
		for (i = 0; i < len; ++i) {
			left.size.height = 50;
			[self.inputViews[i] setFrame:left];
			left.origin.y += left.size.height;
		}

		len = [self.calculation outputFieldCount];
		for (i = 0; i < len; ++i) {
			right.size.height = 50;
			[self.outputViews[i] setFrame:right];
			right.origin.y += right.size.height;
		}

		s.origin.y = MAX(left.origin.y, right.origin.y);
	} else if ([self.calculation intermixResults]) {
		int i,len;
		len = [self.calculation inputFieldCount];
		for (i = 0; i < len; ++i) {
			s.size.height = 50;
			[self.inputViews[i] setFrame:s];
			s.origin.y += s.size.height;

			s.size.height = 50;
			[self.outputViews[i] setFrame:s];
			s.origin.y += s.size.height;

			s.origin.y += BORDER;
		}
	} else {
		int i,len;
		len = [self.calculation inputFieldCount];
		for (i = 0; i < len; ++i) {
			s.size.height = 50;
			[self.inputViews[i] setFrame:s];
			s.origin.y += s.size.height;
		}

		s.origin.y += BORDER;

		len = [self.calculation outputFieldCount];
		for (i = 0; i < len; ++i) {
			s.size.height = 50;
			[self.outputViews[i] setFrame:s];
			s.origin.y += s.size.height;
		}
	}

	s.origin.y += BORDER;

	r.size.height = s.origin.y - r.origin.y;
	self.contentView.frame = r;
	[self.contentViewWidth setConstant:r.size.width];
	[self.contentViewHeight setConstant:r.size.height];
	self.scrollView.contentSize = r.size;

	[self resizeInsets];
}

/*
 *	Run the calculation for this. This builds an input array with the
 *	current value of all of the inputs, then generates and output
 */

- (void)runCalculation
{
	int i,len;

	if (!self.calcRunning) return;	/* Calc is being built, not running */

	/*
	 *	Step 1: sweep through all of the inputs and construct the value
	 *	objects for each
	 */

	NSMutableArray *inputData = [[NSMutableArray alloc] init];
	len = [self.calculation inputFieldCount];
	for (i = 0; i < len; ++i) {
		CMInputView *input = self.inputViews[i];

		int unit = [self.inputUnits[i] intValue];
		CMValue *value = [[CMValue alloc] initWithValue:input.value unit:unit];

		[inputData addObject:value];
	}

	NSArray *outputData = [self.calculation calculateWithInput:inputData];

	/*
	 *	Step 2: store the outputs, converting according to the proper units
	 */

	len = [self.calculation outputFieldCount];
	for (i = 0; i < len; ++i) {
		CMOutputView *output = self.outputViews[i];
		CMValue *value = outputData[i];

		id<CMMeasurement> outUnit = [self.calculation outputFieldUnit:i];
		if (outUnit) {
			int unit = [self.outputUnits[i] intValue];
			value.value = [outUnit toStandardUnit:value.value withUnit:value.unit];
			value.value = [outUnit fromStandardUnit:value.value withUnit:unit];
			value.unit = unit;
			[output setUnit:outUnit.abbrMeasure[unit]];
		}

		[output setOutputValue:value.value];

		// Save the value
		[self.calculation setOutputValue:[value storeValue] forField:i];
	}
}

- (void)resizeInsets
{
	CGRect r = self.navigationController.navigationBar.frame;
	CGRect s = [[UIApplication sharedApplication] statusBarFrame];
	CGFloat h = s.size.height;
	if (h > s.size.width) h = s.size.width;

	self.scrollView.contentInset = UIEdgeInsetsMake(r.size.height + h, 0, 0, 0);
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
	[[self.view findFirstResponder] resignFirstResponder];

	if ([[segue identifier] isEqualToString:@"presentUnitSelector"]) {
		/*
		 *	Determine the field we're sending from
		 */

		UIView *v;
		id<CMMeasurement> units;
		int selUnit;

		if (self.selUnitFlag) {
			v = (CMInputView *)self.inputViews[self.selUnit];
			units = [self.calculation inputFieldUnit:self.selUnit];
			selUnit = [self.inputUnits[self.selUnit] intValue];
		} else {
			v = (CMOutputView *)self.outputViews[self.selUnit];
			units = [self.calculation outputFieldUnit:self.selUnit];
			selUnit = [self.outputUnits[self.selUnit] intValue];
		}

		/* Wire up appropriately for menu/unit */
		CGRect r = v.bounds;
		r.origin.x += r.size.width - 44;
		r.size.width = 44;

		CMCustomPushSegue *push = (CMCustomPushSegue *)segue;
		push.presentView = v;
		push.presentRect = r;

		CMUnitPickerTableViewController *vc = segue.destinationViewController;
		vc.curUnit = selUnit;
		vc.units = units;

		vc.selectUnit = ^(int index) {
			if (self.selUnitFlag) {
				[(CMInputView *)v setUnit:units.abbrMeasure[index]];
				self.inputUnits[self.selUnit] = @( index );
			} else {
				[(CMOutputView *)v setUnit:units.abbrMeasure[index]];
				self.outputUnits[self.selUnit] = @( index );
			}

			[push closePopup];
			[self runCalculation];
		};

		// Determine input
	}
}

- (void)didReceiveMemoryWarning
{
	[super didReceiveMemoryWarning];
	// Dispose of any resources that can be recreated.
}

@end
