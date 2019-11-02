//
//  CMHelpViewController.m
//  E6B
//
//  Created by William Woody on 11/1/14.
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

#import "CMHelpViewController.h"

@interface CMHelpViewController ()
@property (strong) IBOutlet WKWebView *webView;
@property (strong) IBOutlet UIBarButtonItem *prevButton;
@property (strong) IBOutlet UIBarButtonItem *nextButton;
@property (strong) IBOutlet UIBarButtonItem *refreshButton;
@property (strong) IBOutlet UIBarButtonItem *stopButton;
@end

@implementation CMHelpViewController

- (void)viewDidLoad
{
    [super viewDidLoad];

	NSURL *url = [[NSBundle mainBundle] URLForResource:@"index" withExtension:@"html"];
	NSURLRequest *req = [NSURLRequest requestWithURL:url];
	[self.webView loadRequest:req];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

/************************************************************************/
/*																		*/
/*	Web Management														*/
/*																		*/
/************************************************************************/



//- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType
//{
//	NSURL *url = [request URL];
//	if ([url isFileURL]) {
//		[self.stopButton setEnabled:YES];
//		return YES;
//	} else {
//        [[UIApplication sharedApplication] openURL:options:completionHandler:url];
//		return NO;
//	}
//}

//- (void)webViewDidFinishLoad:(UIWebView *)webView
//{
//	[self.prevButton setEnabled:[webView canGoBack]];
//	[self.nextButton setEnabled:[webView canGoForward]];
//	[self.refreshButton setEnabled:YES];
//	[self.stopButton setEnabled:NO];
//}

- (IBAction)doPrev:(id)sender
{
	[self.webView goBack];
}

- (IBAction)doNext:(id)sender
{
	[self.webView goForward];
}

- (IBAction)doReload:(id)sender
{
	[self.webView reload];
}

- (IBAction)doStop:(id)sender
{
	[self.webView stopLoading];
}

/************************************************************************/
/*                                                                        */
/*    WKWebView Web Management                                            */
/*                                                                        */
/************************************************************************/

-(void)webView:(WKWebView *)webView decidePolicyForNavigationAction:(WKNavigationAction *)navigationAction decisionHandler:(void (^)(WKNavigationActionPolicy))decisionHandler {
    NSURL *url = navigationAction.request.URL;
    if ([url isFileURL]) {
        [self.stopButton setEnabled:YES];
        decisionHandler(WKNavigationActionPolicyAllow);
    } else {
        [[UIApplication sharedApplication] openURL:url options:@{} completionHandler:nil];
        decisionHandler(WKNavigationActionPolicyCancel);
    }
}

-(void)webView:(WKWebView *)webView didFinishNavigation:(WKNavigation *)navigation {
    [self.prevButton setEnabled:[webView canGoBack]];
    [self.nextButton setEnabled:[webView canGoForward]];
    [self.refreshButton setEnabled:YES];
    [self.stopButton setEnabled:NO];
}

@end
