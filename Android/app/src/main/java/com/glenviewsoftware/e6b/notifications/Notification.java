/*  Notification.java
 *
 *  Created on Nov 19, 2012 by William Edward Woody
 */
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

package com.glenviewsoftware.e6b.notifications;


/**
 * The notification record sent during a notification
 */
public class Notification<T>
{
    private String fName;
    private Object fSender;
    private T fUserInfo;
    
    public Notification(String name, Object sender)
    {
        fSender = sender;
        fName = name;
    }
    
    public Notification(String name, Object sender, T userInfo)
    {
        fSender = sender;
        fName = name;
        fUserInfo = userInfo;
    }

    public String getName()
    {
        return fName;
    }

    public Object getSender()
    {
        return fSender;
    }

    public T getUserInfo()
    {
        return fUserInfo;
    }
}


