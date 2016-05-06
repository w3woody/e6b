//
//  CMXMLParser.m
//  E6B
//
//  Created by William Woody on 11/14/14.
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

#import "CMXMLParser.h"

/************************************************************************/
/*																		*/
/*	XML Node															*/
/*																		*/
/************************************************************************/

@implementation CMXMLNode

- (void)setObject:(NSString *)value forKeyedSubscript:(NSString *)key
{
	if (value) {
		self.attributes[key] = [value copy];
	}
}

- (NSString *)objectForKeyedSubscript:(NSString *)key
{
	return self.attributes[key];
}

- (NSString *)generateXML
{
	NSMutableString *ret = [[NSMutableString alloc] init];
	[self generateXMLWith:ret];
	return ret;
}

- (void)generateXMLWith:(NSMutableString *)ret
{
	if (self.nodeType == CMNODETYPE_STRING) {
		/* TODO: Escape properly */
		unichar ch = 0;
		NSInteger start = 0;
		NSInteger pos = 0;
		NSInteger len = self.nodeText.length;
		while (pos < len) {
			ch = [self.nodeText characterAtIndex:pos];
			if ((ch == '&') || (ch == '\'') || (ch == '<') || (ch == '>') || (ch == '"')) {
				if (pos > start) {
					[ret appendString:[self.nodeText substringWithRange:NSMakeRange(start, pos-start)]];
				}
				switch (ch) {
					case '&':
						[ret appendString:@"&amp;"];
						break;
					case '<':
						[ret appendString:@"&lt;"];
						break;
					case '>':
						[ret appendString:@"&gt;"];
						break;
					case '\"':
						[ret appendString:@"&quot;"];
						break;
					case '\'':
						[ret appendString:@"&apos;"];
						break;
				}
				start = ++pos;
			} else {
				++pos;
			}
		}
		if (pos > start) {
			[ret appendString:[self.nodeText substringWithRange:NSMakeRange(start, pos-start)]];
		}
	} else {
		/*
		 *	Formulate response
		 */

		[ret appendFormat:@"<%@",self.nodeText];
		[self.attributes enumerateKeysAndObjectsUsingBlock:^(id key, id obj, BOOL *stop) {
			[ret appendFormat:@" %@=\"%@\"",(NSString *)key,(NSString *)obj];
		}];

		if (self.children.count) {
			[ret appendString:@">"];

			for (CMXMLNode *child in self.children) {
				[child generateXMLWith:ret];
			}

			[ret appendFormat:@"</%@>",self.nodeText];
		} else {
			[ret appendString:@"/>"];
		}
	}
}

- (NSArray *)elementsForName:(NSString *)name
{
	NSMutableArray *a = [[NSMutableArray alloc] init];
	for (CMXMLNode *node in self.children) {
		if ((node.nodeType == CMNODETYPE_ELEMENT) && ([node.nodeText isEqualToString:name])) {
			[a addObject:node];
		}
	}
	return a;
}

- (CMXMLNode *)firstElementForName:(NSString *)name
{
	for (CMXMLNode *node in self.children) {
		if ((node.nodeType == CMNODETYPE_ELEMENT) && ([node.nodeText isEqualToString:name])) {
			return node;
		}
	}
	return nil;
}

- (NSString *)stringValue
{
	if (self.nodeType == CMNODETYPE_STRING) {
		return self.nodeText;
	} else {
		NSMutableString *str = [[NSMutableString alloc] init];
		for (CMXMLNode *node in self.children) {
			if (node.nodeType == CMNODETYPE_STRING) {
				[str appendString:node.nodeText];
			}
		}
		return str;
	}
}


+ (CMXMLNode *)nodeWithString:(NSString *)str
{
	CMXMLNode *node = [[CMXMLNode alloc] init];
	node.nodeText = str;
	node.nodeType = CMNODETYPE_STRING;
	return node;
}

+ (CMXMLNode *)elementWithName:(NSString *)str
{
	CMXMLNode *node = [[CMXMLNode alloc] init];
	node.nodeText = str;
	node.nodeType = CMNODETYPE_ELEMENT;
	node.attributes = [[NSMutableDictionary alloc] init];
	node.children = [[NSMutableArray alloc] init];
	return node;
}

+ (CMXMLNode *)elementWithName:(NSString *)str value:(NSString *)value
{
	CMXMLNode *node = [CMXMLNode elementWithName:str];
	[node addNode:[CMXMLNode nodeWithString:value]];
	return node;
}

- (void)addNode:(CMXMLNode *)node
{
	[self.children addObject:node];
}

@end

/************************************************************************/
/*																		*/
/*	XML Parser															*/
/*																		*/
/************************************************************************/

@interface CMXMLParser ()
@property (strong) NSXMLParser *parser;
@property (strong) NSMutableArray *stack;
@property (strong) CMXMLNode *root;
@end

@implementation CMXMLParser

- (id)initWithURL:(NSURL *)url
{
	if (nil != (self = [super init])) {
		self.parser = [[NSXMLParser alloc] initWithContentsOfURL:url];
		self.parser.delegate = self;

		self.stack = [[NSMutableArray alloc] init];
	}
	return self;
}

- (CMXMLNode *)parse
{
	if (![self.parser parse]) return nil;
	return self.root;
}

- (void)pushStack:(CMXMLNode *)node
{
	if (self.root == nil) {
		self.root = node;
	} else {
		CMXMLNode *parent = [self.stack lastObject];
		[parent.children addObject:node];
	}
	[self.stack addObject:node];
}

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName attributes:(NSDictionary *)attributeDict
{
	CMXMLNode *node = [[CMXMLNode alloc] init];
	node.nodeType = CMNODETYPE_ELEMENT;
	node.nodeText = elementName;
	node.attributes = [attributeDict mutableCopy];
	node.children = [[NSMutableArray alloc] init];

	[self pushStack:node];
}

- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName
{
	[self.stack removeLastObject];
}

- (void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string
{
	CMXMLNode *node = [[CMXMLNode alloc] init];
	node.nodeType = CMNODETYPE_STRING;
	node.nodeText = string;

	if (self.root == nil) {
		self.root = node;
	} else {
		CMXMLNode *parent = [self.stack lastObject];
		[parent.children addObject:node];
	}
}

@end
