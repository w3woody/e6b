//
//  CMWBViewController.m
//  E6B
//
//  Created by William Woody on 11/8/14.
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

#import "CMWBViewController.h"
#import "CMInputView.h"
#import "CMOutputView.h"
#import "CMCustomPushSegue.h"
#import "CMUnitPickerTableViewController.h"
#import "CMMenuView.h"
#import "CMTextField.h"
#import "CMLayoutUtils.h"
#import "CMWBData.h"
#import "E6BCalcEngine.h"
#import "CMAircraftTableViewController.h"
#import "CMArmPickerTableViewController.h"
#import "UIView+FirstResponder.h"
#import "CMWBGraph.h"

#define BORDER	10

/************************************************************************/
/*																		*/
/*	Static State														*/
/*																		*/
/************************************************************************/

static BOOL GBuildingCalculation;
static BOOL GCalcRunning;

/************************************************************************/
/*																		*/
/*	Internal Methods													*/
/*																		*/
/************************************************************************/

@interface CMRowRecord : NSObject
@property (strong) UILabel *label;
@property (strong) CMInputView *weight;
@property (strong) CMInputView *arm;
@property (strong) CMOutputView *moment;
@property (strong) UIButton *addDel;

@property (assign) int weightUnit;
@property (assign) int armUnit;
@property (assign) int momentUnit;

@property (assign) double weightValue;
@property (assign) double armValue;
@property (assign) double momentValue;
@end

@implementation CMRowRecord

- (void)calcFuelMomentWithFuel:(int)index
{
	double tmp = self.weight.value;
	tmp = [GVolume toStandardUnit:tmp withUnit:self.weightUnit];
	tmp = [GVolume fromStandardUnit:tmp withUnit:VOLUME_GALLONS];

	switch (index) {
		default:
		case FUELTYPE_UNKNOWN:
		case FUELTYPE_AVGAS:
			tmp *= 6.0;
			break;
		case FUELTYPE_JETA:
			tmp *= 6.6;
			break;
		case FUELTYPE_KEROSENE:
			tmp *= 7.0;
			break;
	}

	// tmp is now in pounds; convert to standard units
	tmp = [GWeight toStandardUnit:tmp withUnit:WEIGHT_LBS];
	self.weightValue = tmp;

	tmp = self.arm.value;
	tmp = [GLength toStandardUnit:tmp withUnit:self.armUnit];
	self.armValue = tmp;

	self.momentValue = self.weightValue * self.armValue;
	tmp = [GMoment fromStandardUnit:self.momentValue withUnit:self.momentUnit];
	[self.moment setOutputValue:tmp];

}

- (void)calcMoment
{
	double tmp = self.weight.value;
	tmp = [GWeight toStandardUnit:tmp withUnit:self.weightUnit];
	self.weightValue = tmp;

	tmp = self.arm.value;
	tmp = [GLength toStandardUnit:tmp withUnit:self.armUnit];
	self.armValue = tmp;

	self.momentValue = self.weightValue * self.armValue;
	tmp = [GMoment fromStandardUnit:self.momentValue withUnit:self.momentUnit];
	[self.moment setOutputValue:tmp];
}

@end

/************************************************************************/
/*																		*/
/*	Class Declaration													*/
/*																		*/
/************************************************************************/

@interface CMWBViewController ()
@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *width;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *height;
@property (weak, nonatomic) IBOutlet UIView *contentView;

@property (nonatomic, assign) BOOL calculationDirty;

/* Controls */
@property (strong) CMMenuView *aircraftPicker;
@property (strong) CMTextField *wbName;

@property (strong) UILabel *armLabel;
@property (strong) UILabel *weightLabel;
@property (strong) UILabel *momentLabel;

/* Aircraft Data */
@property (strong) CMRowRecord *aircraftRow;
@property (strong) UIButton *addRowButton;

/* Fixed rows */
@property (strong) NSMutableArray *fuelRows;

/* Changable rows */
@property (strong) NSMutableArray *adjRows;

/* Selected row (for unit picker) */
@property (strong) CMRowRecord *selectedRow;
@property (strong) CMOutputView *selectedOutput;
@property (strong) CMInputView *selectedInput;
@property (assign) int selectedColumn;
@property (assign) int selectedInputUnit;
@property (assign) BOOL selectedIsVolume;

/* Results rows */
@property (strong) CMOutputView *totalWeight;
@property (strong) CMOutputView *totalArm;
@property (strong) CMOutputView *totalMoment;
@property (strong) CMOutputView *totalVA;

@property (strong) CMWBGraph *graph;

@end

@implementation CMWBViewController

- (void)viewDidLoad
{
    [super viewDidLoad];

	// Do any additional setup after loading the view, typically from a nib.
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(runCalculation) name:NOTIFICATION_INPUTUPDATE object:nil];

	[self configureView];

	// Set up scroll insets
	[self resizeInsets];

	// Reset dirty flag
	self.calculationDirty = NO;
}

- (void)viewWillDisappear:(BOOL)animated
{
	[super viewWillDisappear:animated];
	if (self.calculationDirty) {
		[[CMWBCalculationStore shared] saveData];
	}
}

- (void)dealloc
{
	[[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)setCalculation:(CMWBCalculation *)calculation
{
	if (_calculation != calculation) {
		_calculation = calculation;

		if (self.isViewLoaded) {
			[self configureView];
		}
	}
}

- (void)configureView
{
	__weak CMWBViewController *this = self;

	// Create the components
	self.aircraftPicker = [[CMMenuView alloc] initWithFrame:CGRectZero];
	[self.aircraftPicker setLabel:@"Aircraft Name"];
	[self.contentView addSubview:self.aircraftPicker];
	[self.aircraftPicker addTarget:self action:@selector(doAircraftPicker:) forControlEvents:UIControlEventTouchUpInside];

	self.wbName = [[CMTextField alloc] initWithFrame:CGRectZero];
	[self.wbName setLabel:@"Type"];
	self.wbName.updateContents = ^(NSString *newName) {
		[this updateCalculationName:newName];
	};
	[self.contentView addSubview:self.wbName];

	// Create the weight/arm/moment title labels
	self.weightLabel = [[UILabel alloc] initWithFrame:CGRectZero];
	self.weightLabel.text = @"Weight";
	self.weightLabel.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:15];
	self.weightLabel.textAlignment = NSTextAlignmentCenter;
	[self.contentView addSubview:self.weightLabel];

	self.armLabel = [[UILabel alloc] initWithFrame:CGRectZero];
	self.armLabel.text = @"Arm";
	self.armLabel.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:15];
	self.armLabel.textAlignment = NSTextAlignmentCenter;
	[self.contentView addSubview:self.armLabel];

	self.momentLabel = [[UILabel alloc] initWithFrame:CGRectZero];
	self.momentLabel.text = @"Moment";
	self.momentLabel.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:15];
	self.momentLabel.textAlignment = NSTextAlignmentCenter;
	[self.contentView addSubview:self.momentLabel];

	// Set up and build rows for the current aircraft
	self.fuelRows = [[NSMutableArray alloc] init];
	self.adjRows = [[NSMutableArray alloc] init];

	self.addRowButton = [[UIButton alloc] initWithFrame:CGRectZero];
	UIImage *add = [UIImage imageNamed:@"add.png"];
	[self.addRowButton setImage:add forState:UIControlStateNormal];
	[self.addRowButton setContentMode:UIViewContentModeCenter];
	[self.contentView addSubview:self.addRowButton];

	[self.addRowButton addTarget:self action:@selector(doAddRow:) forControlEvents:UIControlEventTouchUpInside];

	// Set up the summary weight, arm, moment, VA
	self.totalWeight = [[CMOutputView alloc] init];
	self.totalWeight.label = @"Total Weight";
	self.totalWeight.displayTime = NO;
	self.totalWeight.unitPicker = ^{
		[this openUnitPickerForOutput:this.totalWeight];
	};
	[self.contentView addSubview:self.totalWeight];

	self.totalArm = [[CMOutputView alloc] init];
	self.totalArm.displayTime = NO;
	self.totalArm.label = @"Total Arm";
	self.totalArm.unitPicker = ^{
		[this openUnitPickerForOutput:this.totalArm];
	};
	[self.contentView addSubview:self.totalArm];

	self.totalMoment = [[CMOutputView alloc] init];
	self.totalMoment.displayTime = NO;
	self.totalMoment.label = @"Total Moment";
	self.totalMoment.unitPicker = ^{
		[this openUnitPickerForOutput:this.totalMoment];
	};
	[self.contentView addSubview:self.totalMoment];

	self.totalVA = [[CMOutputView alloc] init];
	self.totalVA.displayTime = NO;
	self.totalVA.label = @"Va at Total Weight";
	self.totalVA.unitPicker = ^{
		[this openUnitPickerForOutput:this.totalVA];
	};
	[self.contentView addSubview:self.totalVA];

	// Set up graph
	self.graph = [[CMWBGraph alloc] init];
	self.graph.aircraft = self.calculation.aircraftData;
	[self.contentView addSubview:self.graph];

	// TODO

	// Now build the views for the variable rows and populate the values
	[self buildWBForAircraft];

	[self runCalculation];
}

- (CMRowRecord *)createEmptyRow
{
	__weak CMWBViewController *this = self;

	CMRowRecord *r = [[CMRowRecord alloc] init];
	__weak CMRowRecord *rweak = r;

	r.weight = [[CMInputView alloc] initWithFrame:CGRectZero];
	r.weight.timeEditor = NO;
	r.weight.showMenu = NO;
	r.weightUnit = self.calculation.data.weightUnit;	// to default for now
	[self.contentView addSubview:r.weight];
	r.weight.unitPicker = ^{
		[this openUnitPickerForRow:rweak column:0];
	};

	r.arm = [[CMInputView alloc] initWithFrame:CGRectZero];
	r.arm.timeEditor = NO;
	r.arm.showMenu = NO;
	r.armUnit = self.calculation.data.armUnit;
	[self.contentView addSubview:r.arm];
	r.arm.unitPicker = ^{
		[this openUnitPickerForRow:rweak column:1];
	};

	r.moment = [[CMOutputView alloc] initWithFrame:CGRectZero];
	r.moment.displayTime = NO;
	r.momentUnit = self.calculation.data.momentUnit;
	[self.contentView addSubview:r.moment];
	r.moment.unitPicker = ^{
		[this openUnitPickerForRow:rweak column:2];
	};

	return r;
}

- (CMRowRecord *)createEmptyRowWithLabel:(NSString *)label
{
	CMRowRecord *r = [self createEmptyRow];

	r.label = [[UILabel alloc] initWithFrame:CGRectZero];
	r.label.text = label;
	r.label.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:12];
	r.label.textAlignment = NSTextAlignmentLeft;
	[self.contentView addSubview:r.label];

	return r;
}

- (void)adjustUnitLabels:(CMRowRecord *)r
{
	NSString *unit;

	unit = [GWeight abbrMeasure][r.weightUnit];
	[r.weight setUnit:unit];

	unit = [GLength abbrMeasure][r.armUnit];
	[r.arm setUnit:unit];

	unit = [GMoment abbrMeasure][r.momentUnit];
	[r.moment setUnit:unit];
}

- (void)adjustVolumeLabels:(CMRowRecord *)r
{
	NSString *unit;

	unit = [GVolume abbrMeasure][r.weightUnit];
	[r.weight setUnit:unit];

	unit = [GLength abbrMeasure][r.armUnit];
	[r.arm setUnit:unit];

	unit = [GMoment abbrMeasure][r.momentUnit];
	[r.moment setUnit:unit];
}

/*
 *	Populates the contents of the fixed and adjustable rows in the W&B
 *	calculation database.
 *
 *	This both creates the variable view rows and populates the data for
 *	each of the rows with the stored values.
 */

- (void)buildWBForAircraft
{
	__weak CMWBViewController *this = self;

	GBuildingCalculation = YES;

	[self.aircraftRow.label removeFromSuperview];
	[self.aircraftRow.weight removeFromSuperview];
	[self.aircraftRow.arm removeFromSuperview];
	[self.aircraftRow.moment removeFromSuperview];
	self.aircraftRow = nil;

	for (CMRowRecord *r in self.fuelRows) {
		[r.label removeFromSuperview];
		[r.weight removeFromSuperview];
		[r.arm removeFromSuperview];
		[r.moment removeFromSuperview];
		[r.addDel removeFromSuperview];
	}
	[self.fuelRows removeAllObjects];

	for (CMRowRecord *r in self.adjRows) {
		[r.label removeFromSuperview];
		[r.weight removeFromSuperview];
		[r.arm removeFromSuperview];
		[r.moment removeFromSuperview];
		[r.addDel removeFromSuperview];
	}
	[self.adjRows removeAllObjects];

	/*
	 *	Add fixed rows. This is the aircraft's W&B and the fuel lines
	 *	(TODO: (a) Update CMWBCalculation. (b) Add rows using CMRowRecord.
	 *	(c) layout in viewDidLayoutSubviews
	 */

	CMWBData *data = self.calculation.data;

	// Aircraft name, calculation title
	self.aircraftPicker.value = self.calculation.aircraftName;
	self.wbName.text = self.calculation.data.name;

	// Airplane W&B
	CMRowRecord *r;

	r = [self createEmptyRowWithLabel:@"Aircraft"];
	r.weightUnit = data.aircraftWeight.unit;
	r.armUnit = data.aircraftArm.unit;
	r.momentUnit = data.aircraftMomentUnit;
	[r.weight setValue:data.aircraftWeight.value];
	[r.arm setValue:data.aircraftArm.value];
	[self adjustUnitLabels:r];

	self.aircraftRow = r;

	// Fuel W&B
	for (CMWBFRow *fuel in data.fuel) {
		r = [self createEmptyRowWithLabel:fuel.name];
		r.weightUnit = fuel.volume.unit;
		r.armUnit = fuel.arm.unit;
		r.momentUnit = fuel.momentUnit;
		[r.weight setValue:fuel.volume.value];
		[r.arm setValue:fuel.arm.value];
		[self adjustVolumeLabels:r];

		[self.fuelRows addObject:r];
	}

	// Stations W&B
	BOOL first = YES;
	int index = 0;
	for (CMWBRow *row in data.list) {
		if (first) {
			first = NO;
			r = [self createEmptyRowWithLabel:@"Stations"];
		} else {
			r = [self createEmptyRow];
		}
		r.weightUnit = row.weight.unit;
		r.armUnit = row.arm.unit;
		r.momentUnit = row.momentUnit;
		[r.weight setValue:row.weight.value];
		[r.arm setValue:row.arm.value];
		[self adjustUnitLabels:r];

		r.arm.showMenu = YES;
		__weak CMRowRecord *pRow = r;
		r.arm.menuPicker = ^{
			[this openStationPickerForInput:pRow.arm withUnit:pRow.armUnit];
		};

		UIButton *btn = [[UIButton alloc] initWithFrame:CGRectZero];
		UIImage *del = [UIImage imageNamed:@"delete.png"];
		[btn setImage:del forState:UIControlStateNormal];
		[btn setContentMode:UIViewContentModeCenter];
		r.addDel = btn;
		[self.contentView addSubview:r.addDel];

		[btn setTag:index++];
		[btn addTarget:self action:@selector(doDeleteRow:) forControlEvents:UIControlEventTouchUpInside];

		[self.adjRows addObject:r];
	}

	// Totals
	self.totalWeight.unit = [GWeight abbrMeasure][data.weightUnit];
	self.totalArm.unit = [GLength abbrMeasure][data.armUnit];
	self.totalMoment.unit = [GMoment abbrMeasure][data.momentUnit];
	self.totalVA.unit = [GSpeed abbrMeasure][data.speedUnit];

	GBuildingCalculation = NO;
}

- (IBAction)doAddRow:(id)sender
{
	/*
	 *	Create blank row
	 */

	CMWBRow *row = [[CMWBRow alloc] init];

	Value v;
	v.unit = self.calculation.aircraftData.weightUnit;
	v.value = 0;
	row.weight = v;
	v.unit = self.calculation.aircraftData.armUnit;
	v.value = 0;
	row.arm = v;
	row.momentUnit = self.calculation.aircraftData.momentUnit;

	[self.calculation.data.list addObject:row];
	[self buildWBForAircraft];
	[self runCalculation];
}

- (IBAction)doDeleteRow:(id)sender
{
	// Delete row, reformulate
	NSInteger index = [sender tag];
	[self.calculation.data.list removeObjectAtIndex:index];
	[self buildWBForAircraft];
	[self runCalculation];
}

- (void)layoutRow:(CMRowRecord *)r withLayout:(CMLayout *)l colWidth:(int)ncol
{
	if (r.label) {
		[l setRowHeight:22];
		r.label.frame = [l cell:0 columnCount:1 rightIndent:44];
		[l advanceNextRow];
	}
	[l setRowHeight:44];

	r.weight.frame = [l cell:0 columnCount:ncol rightIndent:44];
	r.arm.frame = [l cell:1 columnCount:ncol rightIndent:44];
	if (ncol >= 3) {
		r.moment.frame = [l cell:2 columnCount:ncol rightIndent:44];
		r.moment.hidden = NO;
	} else {
		r.moment.hidden = YES;
	}

	if (r.addDel) {
		r.addDel.frame = [l rightCellIndent:44];
	}

	[l advanceNextRow];
}

- (void)viewDidLayoutSubviews
{
	CGRect r = self.scrollView.frame;
	CGFloat width = r.size.width;

	int ncol = (width > 480) ? 3 : 2;

	CMLayout *layout = [[CMLayout alloc] initWithWidth:width yStart:0];

	/*
	 *	Title lines, aircraft lines
	 */

	if (ncol == 3) {
		self.aircraftPicker.frame = [layout cell:0 columnCount:2];
		self.wbName.frame = [layout cell:1 columnCount:2];
		[layout advanceNextRow];
	} else {
		self.aircraftPicker.frame = [layout cell:0 columnCount:1];
		[layout advanceNextRow];
		self.wbName.frame = [layout cell:0 columnCount:1];
		[layout advanceNextRow];
	}
	[layout advanceRowPosition:11];

	/*
	 *	Add labels
	 */

	[layout setRowHeight:22];

	self.weightLabel.frame = [layout cell:0 columnCount:ncol rightIndent:44];
	self.armLabel.frame = [layout cell:1 columnCount:ncol rightIndent:44];
	if (ncol >= 3) {
		self.momentLabel.hidden = NO;
		self.momentLabel.frame = [layout cell:2 columnCount:ncol rightIndent:44];
	} else {
		self.momentLabel.hidden = YES;
	}
	[layout advanceNextRow];

	/*
	 *	Layout W&B rows
	 */

	[self layoutRow:self.aircraftRow withLayout:layout colWidth:ncol];
	for (CMRowRecord *r in self.fuelRows) {
		[self layoutRow:r withLayout:layout colWidth:ncol];
	}
	for (CMRowRecord *r in self.adjRows) {
		[self layoutRow:r withLayout:layout colWidth:ncol];
	}

	self.addRowButton.frame = [layout rightCellIndent:44];
	[layout advanceNextRow];

	/*
	 *	Layout totals
	 */

	[layout setRowHeight:50];
	if (ncol == 3) {
		self.totalWeight.frame = [layout cell:0 columnCount:2 rightIndent:44];
		self.totalArm.frame = [layout cell:1 columnCount:2 rightIndent:44];
		[layout advanceNextRow];
		self.totalVA.frame = [layout cell:0 columnCount:2 rightIndent:44];
		self.totalMoment.frame = [layout cell:1 columnCount:2 rightIndent:44];
		[layout advanceNextRow];
	} else {
		self.totalWeight.frame = [layout cell:0 columnCount:1 rightIndent:44];
		[layout advanceNextRow];
		self.totalArm.frame = [layout cell:0 columnCount:1 rightIndent:44];
		[layout advanceNextRow];
		self.totalMoment.frame = [layout cell:0 columnCount:1 rightIndent:44];
		[layout advanceNextRow];
		self.totalVA.frame = [layout cell:0 columnCount:1 rightIndent:44];
		[layout advanceNextRow];
	}

	/*
	 *	Layout graph.
	 */

	[layout advanceRowPosition:22];
	[layout setRowHeight:(width*3)/5];
	self.graph.frame = [layout cell:0 columnCount:1];
	[layout advanceNextRow];
	[layout advanceRowPosition:22];

	/*
	 *	Height
	 */

	int height = layout.yBottom;
	[self.height setConstant:height];
	[self.width setConstant:width];
	[self.scrollView setContentSize:CGSizeMake(width, height)];

	/*
	 *	Start laying out the controls
	 */

	[self resizeInsets];
}

/*
 *	Run the calculation for this. This builds an input array with the
 *	current value of all of the inputs, then generates and output
 */

- (void)runCalculation
{
	double totWeight,totMoment;

	if (GBuildingCalculation) return;
	if (GCalcRunning) return;
	GCalcRunning = YES;

	CMWBData *data = self.calculation.data;

	/*
	 *	Step 1: run through the list of weights and arms and compute
	 *	moments for all pairs
	 */

	[self.aircraftRow calcMoment];

	data.aircraftWeight = CMMakeValue(self.aircraftRow.weight.value, self.aircraftRow.weightUnit);
	data.aircraftArm = CMMakeValue(self.aircraftRow.arm.value, self.aircraftRow.armUnit);
	data.aircraftMomentUnit = self.aircraftRow.momentUnit;

	totWeight = self.aircraftRow.weightValue;
	totMoment = self.aircraftRow.momentValue;

	NSInteger i,len = [data.fuel count];
	for (i = 0; i < len; ++i) {
		CMRowRecord *r = self.fuelRows[i];
		CMWBFRow *fr = data.fuel[i];

		[r calcFuelMomentWithFuel:fr.fuelType];

		fr.volume = CMMakeValue(r.weight.value, r.weightUnit);
		fr.arm = CMMakeValue(r.arm.value, r.armUnit);
		fr.momentUnit = r.momentUnit;

		totWeight += r.weightValue;
		totMoment += r.momentValue;
	}

	[data.list removeAllObjects];
	len = [self.adjRows count];
	for (i = 0; i < len; ++i) {
		CMRowRecord *r = self.adjRows[i];
		CMWBRow *fr = [[CMWBRow alloc] init];

		[r calcMoment];

		fr.weight = CMMakeValue(r.weight.value, r.weightUnit);
		fr.arm = CMMakeValue(r.arm.value, r.armUnit);
		fr.momentUnit = r.momentUnit;

		[data.list addObject:fr];

		totWeight += r.weightValue;
		totMoment += r.momentValue;
	}

	/*
	 *	Now set the output values, calculating arm and Va on the fly
	 */

	double totArm;
	if (totWeight <= 0) totArm = 0;
	else totArm = totMoment / totWeight;

	double totVA = self.calculation.aircraftData.va;
	totVA = [GSpeed toStandardUnit:totVA withUnit:self.calculation.aircraftData.speedUnit];
	double maxWeight = self.calculation.aircraftData.wmax;
	maxWeight = [GWeight toStandardUnit:maxWeight withUnit:self.calculation.aircraftData.weightUnit];
	totVA *= sqrt(totWeight / maxWeight);

	[self.totalWeight setOutputValue:[GWeight fromStandardUnit:totWeight withUnit:data.weightUnit]];
	[self.totalArm setOutputValue:[GLength fromStandardUnit:totArm withUnit:data.armUnit]];
	[self.totalMoment setOutputValue:[GMoment fromStandardUnit:totMoment withUnit:data.momentUnit]];
	[self.totalVA setOutputValue:[GSpeed fromStandardUnit:totVA withUnit:data.speedUnit]];

	/*
	 *	Set data to graph
	 */

	self.graph.aircraft = self.calculation.aircraftData;
	self.graph.totWeight = [GWeight fromStandardUnit:totWeight withUnit:self.calculation.aircraftData.weightUnit];
	self.graph.totArm = [GLength fromStandardUnit:totArm withUnit:self.calculation.aircraftData.armUnit];

	NSMutableArray *a = [[NSMutableArray alloc] init];
	len = [data.fuel count];
	for (i = 0; i < len; ++i) {
		CMRowRecord *r = self.fuelRows[i];
		CMWBPair *pair = [[CMWBPair alloc] init];
		pair.weight = [GWeight fromStandardUnit:r.weightValue withUnit:self.calculation.aircraftData.weightUnit];
		pair.arm = [GLength fromStandardUnit:r.armValue withUnit:self.calculation.aircraftData.armUnit];
		[a addObject:pair];
	}

	self.graph.weights = a;
	[self.graph reloadGraph];

	/*
	 *	Mark this as dirty for save
	 */

	self.calculationDirty = YES;

	/*
	 *	Done
	 */

	GCalcRunning = NO;
}

- (void)resizeInsets
{
	CGRect r = self.navigationController.navigationBar.frame;
	CGRect s = [[UIApplication sharedApplication] statusBarFrame];
	CGFloat h = s.size.height;
	if (h > s.size.width) h = s.size.width;

	self.scrollView.contentInset = UIEdgeInsetsMake(r.size.height + h, 0, 0, 0);
}

- (void)openStationPickerForInput:(CMInputView *)input withUnit:(int)unit
{
	self.selectedInput = input;
	self.selectedInputUnit = unit;
	[self performSegueWithIdentifier:@"presentArmSelector" sender:self];
}

- (void)openUnitPickerForOutput:(CMOutputView *)output
{
	self.selectedOutput = output;
	self.selectedRow = nil;
	self.selectedColumn = 0;
	self.selectedIsVolume = NO;
	[self performSegueWithIdentifier:@"presentUnitSelector" sender:self];
}

- (void)openUnitPickerForRow:(CMRowRecord *)r column:(int)col
{
	BOOL volFlag = NO;
	if (col == 0) {
		/* Determine if fuel row; that uses volumen, not weight */
		for (CMRowRecord *tmp in self.fuelRows) {
			if (tmp == r) {
				volFlag = YES;
				break;
			}
		}
	}

	self.selectedRow = r;
	self.selectedOutput = nil;
	self.selectedColumn = col;
	self.selectedIsVolume = volFlag;
	[self performSegueWithIdentifier:@"presentUnitSelector" sender:self];
}

- (void)updateCalculationName:(NSString *)name
{
	self.calculation.data.name = name;

	NSDictionary *args = @{ @"Calculation": self.calculation };
	[[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_RENAMEWBCALC object:self userInfo:args];
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
	[[self.view findFirstResponder] resignFirstResponder];

	if ([[segue identifier] isEqualToString:@"presentAirplaneSelector"]) {
		// TODO: Properly populate to handle aircraft selection
		UIView *v = sender;
		CGRect r = v.bounds;
		r.origin.x += r.size.width - 44;
		r.size.width = 44;

		CMCustomPushSegue *s = (CMCustomPushSegue *)segue;
		s.presentRect = r;
		s.presentView = v;

		CMAircraftTableViewController *vc = (CMAircraftTableViewController *)segue.destinationViewController;

		vc.curAircraft = self.calculation.aircraftName;
		vc.selectAircraft = ^(CMWBAircraft *aircraft) {
			[s closePopup];

			if (aircraft) {
				[self.aircraftPicker setValue:aircraft.name];
				[self.calculation setAircraftName:aircraft.name];
				[self buildWBForAircraft];
				[self runCalculation];
			}
		};
	}

	if ([[segue identifier] isEqualToString:@"presentArmSelector"]) {
		CGRect r = self.selectedInput.bounds;
		r.size.width = 44;

		CMCustomPushSegue *push = (CMCustomPushSegue *)segue;
		push.presentView = self.selectedInput;
		push.presentRect = r;

		CMArmPickerTableViewController *vc = segue.destinationViewController;
		vc.units = [GLength abbrMeasure][self.calculation.aircraftData.armUnit];
		vc.stations = self.calculation.aircraftData.station;

		double value = self.selectedInput.value;
		value = [GLength toStandardUnit:value withUnit:self.selectedInputUnit];
		value = [GLength fromStandardUnit:value withUnit:self.calculation.aircraftData.armUnit];
		vc.curStation = value;
		vc.selectStation = ^(double arm) {
			[push closePopup];
			arm = [GLength toStandardUnit:arm withUnit:self.calculation.aircraftData.armUnit];
			arm = [GLength fromStandardUnit:arm withUnit:self.selectedInputUnit];
			[self.selectedInput setValue:arm];
		};
	}

	if ([[segue identifier] isEqualToString:@"presentUnitSelector"]) {
		/*
		 *	Determine the field we're sending from, and populate the
		 *	unit dropdown as needed.
		 */

		UIView *v;
		id<CMMeasurement> units;
		int selUnit;

		CMRowRecord *rr = self.selectedRow;
		UIView *ioView;
		if (self.selectedOutput) {
			/*
			 *	one of the sum totals
			 */

			ioView = self.selectedOutput;
			if (self.selectedOutput == self.totalWeight) {
				units = GWeight;
				selUnit = self.calculation.data.weightUnit;
			} else if (self.selectedOutput == self.totalArm) {
				units = GLength;
				selUnit = self.calculation.data.armUnit;
			} else if (self.selectedOutput == self.totalMoment) {
				units = GMoment;
				selUnit = self.calculation.data.momentUnit;
			} else if (self.selectedOutput == self.totalVA) {
				units = GSpeed;
				selUnit = self.calculation.data.speedUnit;
			} else {
				NSLog(@"???");
				units = GTemperature;
				selUnit = 0;
			}

		} else {
			switch (self.selectedColumn) {
				case 0:
				default:
					ioView = rr.weight;
					units = self.selectedIsVolume ? GVolume : GWeight;
					selUnit = rr.weightUnit;
					break;
				case 1:
					ioView = rr.arm;
					units = GLength;
					selUnit = rr.armUnit;
					break;
				case 2:
					ioView = rr.moment;
					units = GMoment;
					selUnit = rr.momentUnit;
					break;
			}
		}
		CGRect r = ioView.bounds;
		r.origin.x += r.size.width - 44;
		r.size.width = 44;

		CMCustomPushSegue *push = (CMCustomPushSegue *)segue;
		push.presentView = v;
		push.presentRect = r;

		CMUnitPickerTableViewController *vc = segue.destinationViewController;
		vc.curUnit = selUnit;
		vc.units = units;

		vc.selectUnit = ^(int index) {
			if (self.selectedOutput) {
				/*
				 *	one of the sum totals
				 */

				self.selectedOutput.unit = [units abbrMeasure][index];
				if (self.selectedOutput == self.totalWeight) {
					self.calculation.data.weightUnit = index;
				} else if (self.selectedOutput == self.totalArm) {
					self.calculation.data.armUnit = index;
				} else if (self.selectedOutput == self.totalMoment) {
					self.calculation.data.momentUnit = index;
				} else if (self.selectedOutput == self.totalVA) {
					self.calculation.data.speedUnit = index;
				}

			} else {
				switch (self.selectedColumn) {
					case 0:
					default:
						rr.weight.unit = [units abbrMeasure][index];
						rr.weightUnit = index;
						break;
					case 1:
						rr.arm.unit = [units abbrMeasure][index];
						rr.armUnit = index;
						break;
					case 2:
						rr.moment.unit = [units abbrMeasure][index];
						rr.momentUnit = index;
						break;
				}
			}
			[push closePopup];
			[self runCalculation];
		};
	}
}

- (IBAction)doAircraftPicker:(id)sender
{
	[self performSegueWithIdentifier:@"presentAirplaneSelector" sender:self.aircraftPicker];
}

@end
