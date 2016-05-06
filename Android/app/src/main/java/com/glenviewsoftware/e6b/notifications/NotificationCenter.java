/*  NotificationCenter.java
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Emulates the NSNotificationCenter code from Apple. Allows me to handle sending events
 * to things that may disappear due to Android's lifecycle management without creating
 * memory management issues
 */
public class NotificationCenter
{
    private static NotificationCenter gCenter;
    private LinkedList<NotificationObserver<?>> fGlobal;
    private HashMap<String,LinkedList<NotificationObserver<?>>> fMap;
    private LinkedList<Notification<?>> fNotificationQueue;
    private boolean fSending;
    
    /**
     * Create a new notification center
     */
    public NotificationCenter()
    {
        fGlobal = new LinkedList<NotificationObserver<?>>();
        fMap = new HashMap<String,LinkedList<NotificationObserver<?>>>();
        fNotificationQueue = new LinkedList<Notification<?>>();
    }
    
    /**
     * Get the default notification center
     * @return
     */
    public static NotificationCenter defaultCenter()
    {
        if (gCenter == null) {
            gCenter = new NotificationCenter();
        }
        return gCenter;
    }
    
    /**
     * Add an observer for a given name. If name is null, receives all notifications
     * @param obs
     * @param name
     */
    public void addObserver(NotificationObserver<?> obs, String name)
    {
        if (name == null) {
            fGlobal.add(obs);
        } else {
            LinkedList<NotificationObserver<?>> ll = fMap.get(name);
            if (ll == null) {
                ll = new LinkedList<NotificationObserver<?>>();
                fMap.put(name, ll);
            }
            ll.add(obs);
        }
    }
    
    public void addObserver(NotificationObserver<?> obs)
    {
        fGlobal.add(obs);
    }
    
    /**
     * Remove observer
     * @param obs
     */
    public void removeObserver(NotificationObserver<?> obs)
    {
        if (obs == null) return;
        
        Iterator<NotificationObserver<?>> iter = fGlobal.iterator();
        while (iter.hasNext()) {
            NotificationObserver<?> item = iter.next();
            if (item == obs) {
                iter.remove();
            }
        }
        
        for (LinkedList<NotificationObserver<?>> lv: fMap.values()) {
            iter = lv.iterator();
            while (iter.hasNext()) {
                NotificationObserver<?> item = iter.next();
                if (item == obs) {
                    iter.remove();
                }
            }
        }
    }
    
    /**
     * Internal routine which sends notifications from the queue. If a notification
     * receiver posts a notification, we add it to the queue and send it out
     * afterwards
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void sendNotifications()
    {
        if (fSending) return;
        fSending = true;
        
        Notification<?> n;
        while (fNotificationQueue.size() != 0) {
            n = fNotificationQueue.removeLast();
            
            /*
             * Send notification
             */
            for (NotificationObserver<?> obs: fGlobal) obs.receiveNotification((Notification)n);

            String str = n.getName();
            LinkedList<NotificationObserver<?>> list = fMap.get(str);
            if (list != null) {
                for (NotificationObserver<?> obs: list) obs.receiveNotification((Notification)n);
            }
        }
        
        fSending = false;
    }
    
    /**
     * Post notifications
     */
    
    public void postNotification(final Notification<?> n)
    {
        fNotificationQueue.addFirst(n);
        sendNotifications();
    }
    
    public void postNotification(String name, Object sender)
    {
        postNotification(new Notification<Object>(name,sender));
    }
    
    public <T> void postNotification(String name, Object sender, T userInfo)
    {
        postNotification(new Notification<T>(name,sender,userInfo));
    }
}


