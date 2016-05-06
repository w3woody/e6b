/*  AircraftDatabase.java
 *
 *  Created on Dec 25, 2012 by William Edward Woody
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

package com.glenviewsoftware.e6b.calc.wb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.glenviewsoftware.e6b.E6BApplication;
import com.glenviewsoftware.e6b.calc.wb.aircraft.WBAircraft;
import com.glenviewsoftware.e6b.notifications.NotificationCenter;
import com.glenviewsoftware.e6b.xml.XMLWriter;

public class AircraftDatabase
{
    public static final String AIRCRAFTLOADED = "CMAircraftDatabaseLoaded";
    public static final String AIRCRAFTUPDATED = "CMAircraftDatabaseUpdated";
    public static final String AIRCRAFTRENAME = "CMAircraftRename";
    
    
    /**
     * A group of aircraft
     */
    public static class Group
    {
        private String group;
        private ArrayList<WBAircraft> array;

        private Group(String g)
        {
            group = g;
            array = new ArrayList<WBAircraft>();
        }

        public String getGroupName()
        {
            return group;
        }

        public List<WBAircraft> getArray()
        {
            return array;
        }
    }

    private ArrayList<Group> fGroups;
    private HashMap<String,WBAircraft> fDictionary;
    private ArrayList<WBAircraft> fUserAircraft;
    private HashMap<String,WBAircraft> fUserDictionary;
    private boolean fCanRead;
    private boolean fCanWrite;

    private static AircraftDatabase gAircraft;

    private AircraftDatabase()
    {
        fGroups = new ArrayList<Group>();
        fDictionary = new HashMap<String,WBAircraft>();
        fUserAircraft = new ArrayList<WBAircraft>();
        fUserDictionary = new HashMap<String,WBAircraft>();

        try {
            getExternalDirectoryState();
            loadDatabase();
            reload();
        }
        catch (Exception e) {
            Log.e("E6B", "Internal load database error", e);
        }
    }

    /**
     * Get the shared database of aircraft, including user defined aircraft
     * @return
     */
    public static AircraftDatabase shared()
    {
        if (gAircraft == null) {
            gAircraft = new AircraftDatabase();
        }
        return gAircraft;
    }

    /**
     * Load the internal d
     * @throws IOException atabase
     * @throws ParserConfigurationException 
     * @throws SAXException 
     */
    private void loadDatabase() throws IOException, ParserConfigurationException, SAXException
    {
//        Uri url = Uri.parse("file:///android_asset/aircraft/aircraft.xml");
//        E6BApplication app = E6BApplication.shared();
//        InputStream is = app.getContentResolver().openInputStream(url);
        InputStream is = getClass().getResourceAsStream("aircraft.xml");
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder parser = dbf.newDocumentBuilder();
        Document doc = parser.parse(is);
        is.close();
        
        /* Populate group database */
        Element e = doc.getDocumentElement();
        NodeList nl = e.getElementsByTagName("group");
        int i,len = nl.getLength();
        for (i = 0; i < len; ++i) {
            Element ge = (Element)nl.item(i);
            
            String name = ge.getAttribute("name");
            Group g = new Group(name);
            fGroups.add(g);
            
            NodeList anl = ge.getElementsByTagName("aircraft");
            int ai,alen = anl.getLength();
            for (ai = 0; ai < alen; ai++) {
            	Element ac = (Element)anl.item(ai);
            	WBAircraft a = new WBAircraft(ac);
            	g.array.add(a);
            	
            	fDictionary.put(a.getName(), a);
            }
        }
    }
    
    private void getExternalDirectoryState()
    {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            fCanRead = true;
            fCanWrite = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            fCanRead = true;
            fCanWrite = false;
        } else {
            fCanRead = false;
            fCanWrite = false;
        }
    }
    
    /**
     * Reload database
     * @throws IOException 
     * @throws ParserConfigurationException 
     * @throws SAXException 
     */
    public void reload() throws IOException, ParserConfigurationException, SAXException
    {
        if (fCanRead) {
            fUserAircraft = new ArrayList<WBAircraft>();
            fUserDictionary = new HashMap<String,WBAircraft>();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = dbf.newDocumentBuilder();

            File path = Environment.getExternalStorageDirectory();
            path = new File(path,"E6B");
            if (path.exists() && path.isDirectory()) {
                /*
                 * Load contents of E6B directory
                 */
                
                File[] list = path.listFiles();
                for (File f: list) {
                    String name = f.getName();
                    if (name.endsWith(".axml")) {
                        /*
                         * read the aircraft
                         */
                        
                        try {
                            FileInputStream fis = new FileInputStream(f);
                            Document doc = parser.parse(fis);
                            fis.close();

                            WBAircraft a = new WBAircraft(doc.getDocumentElement());

                            fUserAircraft.add(a);
                            fUserDictionary.put(a.getName(), a);
                        }
                        catch (Exception ex) {
                            Log.d("E6B","Aircraft file " + name + " failed to load. Removing");
                            f.delete();
                        }
                    }
                }
            }
            
            NotificationCenter.defaultCenter().postNotification(AIRCRAFTLOADED, this);
        }
    }
    
    /**
     * Capable of writing to the aircraft directory
     * @return
     */
    public boolean canEditAircraft()
    {
        return fCanWrite;
    }
    
    /**
     * Trigger scan of file so it shows up
     * @param f
     */
    private static void scanFile(File f)
    {
        MediaScannerConnection.scanFile(E6BApplication.shared(), 
                new String[]{ f.getAbsolutePath() }, 
                null, 
                new MediaScannerConnection.OnScanCompletedListener() {

            @Override
            public void onScanCompleted(String path, Uri uri)
            {
                // done.
            }
        });
    }

    /************************************************************************/
    /*                                                                      */
    /*  Data Access                                                         */
    /*                                                                      */
    /************************************************************************/
    
    /**
     * Get the aircraft record for the given name
     * @param name
     * @return
     */
    public WBAircraft aircraftForName(String name)
    {
        WBAircraft a = fUserDictionary.get(name);
        if (a == null) {
            a = fDictionary.get(name);
        }
        return a;
    }
    
    /**
     * Get the number of groups of aircraft
     * @return
     */
    public int groupCount()
    {
        return fGroups.size();
    }

    public String groupName(int index)
    {
        return fGroups.get(index).getGroupName();
    }
    
    public int aircraftCountForGroupIndex(int gIndex)
    {
        return fGroups.get(gIndex).getArray().size();
    }
    
    public String aircraftNameInGroup(int index, int gIndex)
    {
        return fGroups.get(gIndex).getArray().get(index).getName();
    }
    
    public int userAircraftCount()
    {
        return fUserAircraft.size();
    }
   
    public String aircraftNameForUserIndex(int index)
    {
        return fUserAircraft.get(index).getName();
    }
    
    public String aircraftIdentForUserIndex(int index)
    {
        WBAircraft a = fUserAircraft.get(index);
        if (a.getMaker() == null) {
            return a.getModel();
        } else if (a.getModel() == null) {
            return a.getMaker();
        } else {
            return a.getMaker() + " " + a.getModel();
        }
    }

    /************************************************************************/
    /*                                                                      */
    /*  Edit Support                                                        */
    /*                                                                      */
    /************************************************************************/
    
    public WBAircraft userDefinedAircraft(int index)
    {
        return fUserAircraft.get(index);
    }
    
    private File getDirectory()
    {
        File path = Environment.getExternalStorageDirectory();
        path = new File(path,"E6B");
        if (!path.exists()) {
            if (!path.mkdirs()) return null;
            scanFile(path);
        }
        return path;
    }
    
    public boolean renameUserAircraft(int index, String name) throws IOException
    {
        File rootPath = getDirectory();
        File path = new File(rootPath,name + ".axml");
        if (path.exists()) return false;
        
        WBAircraft a = fUserAircraft.get(index);
        File oldPath = new File(rootPath,a.getName() + ".axml");
        
        boolean result = oldPath.renameTo(path);
        if (result) {
            fUserDictionary.remove(a.getName());
            a.setName(name);                        // TODO: Should I save this???
            fUserDictionary.put(a.getName(), a);
            save(a,true);
            
            scanFile(rootPath);
            scanFile(path);
        }
        
        NotificationCenter.defaultCenter().postNotification(AIRCRAFTRENAME, this, Integer.valueOf(index));
        
        return result;
    }
    
    public void deleteUserAircraft(int index)
    {
        File rootPath = getDirectory();
        
        WBAircraft a = fUserAircraft.get(index);
        File path = new File(rootPath,a.getName() + ".axml");
        if (path.delete()) {
            fUserDictionary.remove(a.getName());
            fUserAircraft.remove(index);
            scanFile(rootPath);
        }
    }
    
    public boolean save(WBAircraft a, boolean repl) throws IOException
    {
        File rootPath = getDirectory();
        
        File path = new File(rootPath,a.getName() + ".axml");
        if (!repl && path.exists()) return false;
        
        FileOutputStream fos = new FileOutputStream(path);
        OutputStreamWriter w = new OutputStreamWriter(fos,"UTF-8");
        XMLWriter writer = new XMLWriter(w);
        a.toXML(writer);
        writer.close();
        
        scanFile(path);
        
        return true;
    }
    
    private int internalSave(WBAircraft a) throws IOException
    {
        if (!save(a,false)) return -1;
        
        int index = fUserAircraft.size();
        fUserAircraft.add(a);
        fUserDictionary.put(a.getName(), a);
        
        NotificationCenter.defaultCenter().postNotification(AIRCRAFTUPDATED, this, Integer.valueOf(index));

        return index;
    }
    
    public int duplicateUserAircraft(int index, String name) throws IOException
    {
        WBAircraft a = fUserAircraft.get(index);
        a = new WBAircraft(a);
        a.setName(name);
        
        return internalSave(a);
    }

    public int addUserAircraft(String name) throws IOException
    {
        WBAircraft a = new WBAircraft(name);
        return internalSave(a);
    }
    
    public boolean saveAircraft(int index, WBAircraft aircraft) throws IOException
    {
        if (!save(aircraft, true)) return false;
        
        fUserAircraft.set(index, aircraft); // TODO: Don't I replace fUserDictionary?
        
        NotificationCenter.defaultCenter().postNotification(AIRCRAFTUPDATED, this, Integer.valueOf(index));
        
        return true;
    }

    public int saveNewAircraft(WBAircraft aircraft) throws IOException
    {
        return internalSave(aircraft);
    }
}


