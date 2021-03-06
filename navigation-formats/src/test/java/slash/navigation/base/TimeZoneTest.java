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

package slash.navigation.base;

import org.junit.Test;
import slash.navigation.gpx.GpxPosition;
import slash.common.io.CompactCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static slash.common.TestCase.*;

public class TimeZoneTest {

    @Test
    public void testGMTAndLocalTimeZone() {
        long now = System.currentTimeMillis();
        Calendar local = localCalendar(now).getCalendar();
        Calendar utc = utcCalendar(now).getCalendar();

        DateFormat format = DateFormat.getInstance();
        format.setTimeZone(CompactCalendar.UTC);
        String localTime = format.format(local.getTime().getTime());
        String utcTime = format.format(utc.getTime().getTime());
        assertEquals(localTime, utcTime);

        local.setTimeZone(CompactCalendar.UTC);
        assertCalendarEquals(local, utc);
    }

    @Test
    public void testXMLGregorianCalendarViaDatatypeFactory() throws DatatypeConfigurationException {
        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        XMLGregorianCalendar xml = datatypeFactory.newXMLGregorianCalendar("2007-06-07T14:04:42Z");
        GregorianCalendar java = xml.toGregorianCalendar();
        String javaTime = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.ENGLISH).format(java.getTime().getTime());
        assertEquals("6/7/07 4:04 PM", javaTime);
        Calendar parsed = XmlNavigationFormat.parseTime(xml).getCalendar();
        assertCalendarEquals(parsed, java);
    }

    @Test
    public void testXMLGregorianCalendarWithZasTimeZone() throws DatatypeConfigurationException {
        String xmlString = "2007-06-07T14:04:42Z";
        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        XMLGregorianCalendar xml = datatypeFactory.newXMLGregorianCalendar(xmlString);
        assertEquals("2007-06-07T14:04:42Z", xml.toXMLFormat());
        GregorianCalendar java = xml.toGregorianCalendar(TimeZone.getDefault(), null, null);
        XMLGregorianCalendar formatted = XmlNavigationFormat.formatTime(CompactCalendar.fromCalendar(java));
        assertEquals("2007-06-07T14:04:42.000Z", formatted.toXMLFormat());
    }

    @Test
    public void testTimeZone() {
        long now = System.currentTimeMillis();
        Calendar local = localCalendar(now).getCalendar();
        CompactCalendar compactLocal = CompactCalendar.fromCalendar(local);
        Calendar utc = utcCalendar(now).getCalendar();
        CompactCalendar compactUtc = CompactCalendar.fromCalendar(utc);

        GpxPosition gpxPosition = new GpxPosition(3.0, 2.0, 1.0, null, compactLocal, "gpx");
        assertCalendarEquals(compactUtc, gpxPosition.getTime());
    }
}
