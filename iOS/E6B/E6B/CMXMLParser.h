//
//  CMXMLParser.h
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

#import <Foundation/Foundation.h>

#define CMNODETYPE_STRING		1
#define CMNODETYPE_ELEMENT		2

/*
 *	Parser node for in-memory representation of XML data
 */

@interface CMXMLNode : NSObject
@property (assign) int nodeType;
@property (copy) NSString *nodeText;
@property (strong) NSMutableDictionary *attributes;
@property (strong) NSMutableArray *children;

+ (CMXMLNode *)nodeWithString:(NSString *)str;
+ (CMXMLNode *)elementWithName:(NSString *)str;
+ (CMXMLNode *)elementWithName:(NSString *)str value:(NSString *)value;

- (void)addNode:(CMXMLNode *)node;

- (NSString *)objectForKeyedSubscript:(NSString *)key;
- (void)setObject:(NSString *)value forKeyedSubscript:(NSString *)key;

- (NSString *)generateXML;

- (NSArray *)elementsForName:(NSString *)name;
- (CMXMLNode *)firstElementForName:(NSString *)name;
- (NSString *)stringValue;
@end

/*
 *	Parser engine for parsing XML
 */


@interface CMXMLParser : NSObject <NSXMLParserDelegate>
- (id)initWithURL:(NSURL *)url;
- (CMXMLNode *)parse;
@end
