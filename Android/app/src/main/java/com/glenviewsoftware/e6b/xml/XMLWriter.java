/*  XMLWriter.java
 *
 *  Created on Nov 30, 2005 by William Edward Woody
 */
/*
 * Copyright 2007 William Woody, All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list 
 * of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this 
 * list of conditions and the following disclaimer in the documentation and/or other 
 * materials provided with the distribution.
 * 
 * 3. Neither the name of Chaos In Motion nor the names of its contributors may be used 
 * to endorse or promote products derived from this software without specific prior 
 * written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES 
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED 
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR 
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH 
 * DAMAGE.
 * 
 * Contact William Woody at woody@alumni.caltech.edu or at woody@chaosinmotion.com. 
 * Chaos In Motion is at http://www.chaosinmotion.com
 */

package com.glenviewsoftware.e6b.xml;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;

/**
 * XMLWriter class extends the Writer class to handle creating start and end tags,
 * as well as escaping SGML strings
 */
public class XMLWriter extends Writer
{
    private static final String   gNewLine = System.getProperty("line.separator");
    
    private Writer      fWriter;
    private LinkedList<String>  fTagStack;
    private boolean     fOpenTag;
    private boolean     fCloseTag;
    private boolean     fComplete;
    private int         fDepth;
    
    private int         fNewLine;
    
    public static final int FULLNEWLINE = 0;
    public static final int NEWLINEONCLOSE = 1;
    public static final int NONEWLINE = 2;

    /**
     * Construct a new XML writer to write data
     * @param writer
     */
    public XMLWriter(Writer writer)
    {
        fWriter = writer;
        fTagStack = new LinkedList<String>();
    }
    
    /**
     * Create a writer but that doesn't have a writer output to write to
     */
    protected XMLWriter()
    {
        fWriter = null;
        fTagStack = new LinkedList<String>();
    }
    
    /**
     * Set the current writer.
     * @param writer
     */
    protected void setWriter(Writer writer)
    {
        fWriter = writer;
    }
    
    /**
     * Set the newline policy to one of FULLNEWLINE, NEWLINEONCLOSE or NONEWLINE
     * @param policy
     */
    public int setNewLinePolicy(int policy)
    {
        int old = fNewLine;
        fNewLine = policy;
        return old;
    }
    
    /**
     * Internal routine writes a newline
     * @throws IOException 
     *
     */
    private void writeNewLine() throws IOException
    {
        int i;
        
        if (fNewLine != NONEWLINE) {
            fWriter.write(gNewLine);
            for (i = 0; i < fDepth; ++i) fWriter.write("  ");
        }
    }
    
    /**
     * Start a new XML tag
     * @param name the name of the tag to start
     * @throws IOException 
     */
    public void startTag(String name) throws IOException
    {
        if (fComplete) closeTag(false);
        if (fCloseTag || (fOpenTag && (fNewLine != NEWLINEONCLOSE))) {
            // If either an open or a close tag was written, write EOL
            writeNewLine();
        }
        
        fOpenTag = true;
        fCloseTag = false;
        
        // Write the label
        fWriter.write("<" + escapeString(name));
        fTagStack.addLast(name);
        fComplete = true;
    }
    
    /**
     * Add an attribute to an open tag
     * @param name
     * @param value
     * @throws IOException
     */
    
    public void addAttribute(String name, String value) throws IOException
    {
        if ((name == null) || (value == null)) return;
        
        if (!fOpenTag) throw new XMLWriterException("Tag not open");
        fWriter.write(" " + escapeString(name) + "=\"" + escapeString(value) + "\"");
    }
    
    /**
     * Add an attribute to an open tag
     * @param name
     * @param value
     * @throws IOException
     */
    public void addAttribute(String name, long value) throws IOException
    {
        addAttribute(name,Long.toString(value));
    }
    
    /**
     * Add an attribute to an open tag
     * @param name
     * @param value
     * @throws IOException
     */
    public void addAttribute(String name, double value) throws IOException
    {
        addAttribute(name,Double.toString(value));
    }
    
    /**
     * Add an attribute to an open tag
     * @param name
     * @param value
     * @throws IOException
     */
    public void addAttribute(String name, int value) throws IOException
    {
        addAttribute(name,Integer.toString(value));
    }
    
    /**
     * Internal routine to close the start tag, if it's open
     * @param close True if this should not do a short-circuit close
     * @throws IOException 
     */
    void closeTag(boolean noclose) throws IOException
    {
        if (noclose) fWriter.write("/");
        else fDepth++;
        fWriter.write(">");
        fComplete = false;
    }
    
    /**
     * Close the tag that was started with startTag.
     * @throws IOException 
     */
    public void endTag() throws IOException
    {
        String tag = (String)fTagStack.removeLast();
        
        if (fComplete) {
            // no content since open tag. Short circuit close
            closeTag(true);
            fOpenTag = true;
            fCloseTag = true;
        } else {
            --fDepth;

            // Content was written.
            if (fCloseTag) {
                // And a close tag was the last thing out. Bump to next line
                writeNewLine();
            }
            fOpenTag = false;
            fCloseTag = true;
            
            fWriter.write("</" + escapeString(tag) + ">");
        }
    }
    
    /**
     * Close all open tags
     * @throws IOException
     */
    public void endAllTags() throws IOException
    {
        while (!fTagStack.isEmpty()) endTag();
    }
    
    /**
     * Prepend close of open tag if needed
     * @throws IOException
     */
    private void writePrepend() throws IOException
    {
        if (fComplete) closeTag(false);
        fOpenTag = false;
        fCloseTag = false;
    }
    

    public void write(String str) throws IOException
    {
        writePrepend();
        if (str == null) str = "";
        fWriter.write(escapeString(str));
    }

    /**
     * Internal escapes characters
     * @param str
     * @return
     */
    private String escapeString(CharSequence str)
    {
        StringBuffer b = new StringBuffer();
        int i,len = str.length();
        for (i = 0; i < len; ++i) {
            b.append(escapeChar(str.charAt(i)));
        }
        return b.toString();
    }

    /**
     * Internal escape characters
     * @param c
     * @return
     */
    private String escapeChar(int c)
    {
        if (c == '"') return "&quot;";
        if (c == '&') return "&amp;";
        if (c == '\'') return "&apos;";
        if (c == '<') return "&lt;";
        if (c == '>') return "&gt;";
        return Character.toString((char)c);
    }

    public void write(char[] cbuf, int off, int len) throws IOException
    {
        writePrepend();
        write(new String(cbuf,off,len));
    }

    public void write(int c) throws IOException
    {
        writePrepend();
        String str = escapeChar(c);
        if (str == null) {
            fWriter.write(c);
        } else {
            fWriter.write(str);
        }
    }

    public void write(String str, int off, int len) throws IOException
    {
        writePrepend();
        write(str.substring(off,off+len));
    }
    
    public void writeRaw(String str) throws IOException
    {
        writePrepend();
        fWriter.write(str);
    }
    
    public void writeRaw(int c) throws IOException
    {
        writePrepend();
        fWriter.write(c);
    }
    
    public void writeRaw(char[] cbuf, int off, int len) throws IOException
    {
        writePrepend();
        fWriter.write(cbuf,off,len);
    }
    
    public void writeRaw(char[] cbuf) throws IOException
    {
        writeRaw(cbuf,0,cbuf.length);
    }

    public void newLine() throws IOException
    {
        writePrepend();
        fWriter.write(gNewLine);
    }
    

    public void flush() throws IOException
    {
        fWriter.flush();
    }

    public void close() throws IOException
    {
        if (!fTagStack.isEmpty()) throw new XMLWriterException("Tag not closed");
        fWriter.close();
    }
}


