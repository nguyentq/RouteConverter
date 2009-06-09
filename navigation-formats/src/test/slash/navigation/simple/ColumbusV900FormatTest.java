/*
    This file is part of RouteConverter.

    RouteConverter is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    RouteConverter is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with RouteConverter; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

    Copyright (C) 2007 Christian Pesch. All Rights Reserved.
*/

package slash.navigation.simple;

import slash.navigation.NavigationTestCase;
import slash.navigation.Wgs84Position;
import slash.navigation.util.CompactCalendar;

import java.text.DateFormat;

public class ColumbusV900FormatTest extends NavigationTestCase {
    ColumbusV900Format format = new ColumbusV900Format();

    public void testIsValidLine() {
        assertTrue(format.isValidLine("INDEX,TAG,DATE,TIME,LATITUDE N/S,LONGITUDE E/W,HEIGHT,SPEED,HEADING,VOX"));
        assertTrue(format.isValidLine("INDEX,TAG,DATE,TIME,LATITUDE N/S,LONGITUDE E/W,HEIGHT,SPEED,HEADING,FIX MODE,VALID,PDOP,HDOP,VDOP,VOX"));
        assertTrue(format.isValidLine("4     ,T,090421,061054,47.797283N,013.049748E,519  ,5   ,206,         "));
        assertTrue(format.isValidLine("1150  ,T,090522,150532,48.206931N,016.372713E,-5   ,0   ,0  ,3D,SPS ,2.3  ,2.1  ,1.0  ,"));
        assertTrue(format.isValidLine("7\u0000\u0000\u0000\u0000\u0000,V,090421,061109,47.797191N,013.049593E,500\u0000\u0000,0\u0000\u0000\u0000,206,VOX00014 "));
    }

    public void testIsPosition() {
        assertTrue(format.isPosition("5     ,T,090421,061057,47.797281N,013.049743E,504  ,0   ,206,         "));

        assertFalse(format.isPosition("INDEX,TAG,DATE,TIME,LATITUDE N/S,LONGITUDE E/W,HEIGHT,SPEED,HEADING,VOX"));
        assertFalse(format.isPosition("INDEX,TAG,DATE,TIME,LATITUDE N/S,LONGITUDE E/W,HEIGHT,SPEED,HEADING,FIX MODE,VALID,PDOP,HDOP,VDOP,VOX"));
    }

    public void testParsePosition() {
        Wgs84Position position = format.parsePosition("6     ,T,090421,061058,47.797278N,013.049739E,502  ,0 8 ,206,VOX00006 ", null);
        assertEquals(13.049739, position.getLongitude());
        assertEquals(47.797278, position.getLatitude());
        assertEquals(502.0, position.getElevation());
        String actual = DateFormat.getDateTimeInstance().format(position.getTime().getTime());
        CompactCalendar expectedCal = calendar(2009, 4, 21, 6, 10, 58);
        String expected = DateFormat.getDateTimeInstance().format(expectedCal.getTime());
        assertEquals(expected, actual);
        assertEquals(8.0, position.getSpeed());
        assertEquals(expectedCal, position.getTime());
        assertEquals("VOX00006", position.getComment());
    }

    public void testParseSouthWestPosition() {
        Wgs84Position position = format.parsePosition("6     ,V,090421,061058,47.797278S,013.049739W,-102  ,8   ,206,", null);
        assertEquals(-13.049739, position.getLongitude());
        assertEquals(-47.797278, position.getLatitude());
        assertEquals(-102.0, position.getElevation());
        assertNull(position.getComment());
    }

    public void testParsePOIPosition() {
        Wgs84Position position = format.parsePosition("6     ,C,090421,061058,47.797278S,013.049739W,502  ,8   ,206,", null);
        assertEquals("POI 6", position.getComment());
    }

    public void testParseProfessionalModePosition() {
        Wgs84Position position = format.parsePosition("2971  ,V,090508,084815,48.132451N,016.321871E,319  ,12  ,207,3D,SPS ,1.6  ,1.3  ,0.9  ,VOX02971", null);
        assertEquals(16.321871, position.getLongitude());
        assertEquals(48.132451, position.getLatitude());
        assertEquals(319.0, position.getElevation());
        String actual = DateFormat.getDateTimeInstance().format(position.getTime().getTime());
        CompactCalendar expectedCal = calendar(2009, 5, 8, 8, 48, 15);
        String expected = DateFormat.getDateTimeInstance().format(expectedCal.getTime());
        assertEquals(expected, actual);
        assertEquals(12.0, position.getSpeed());
        assertEquals(expectedCal, position.getTime());
        assertEquals("VOX02971", position.getComment());
    }
}