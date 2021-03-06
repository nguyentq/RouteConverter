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

import slash.common.io.CompactCalendar;
import slash.navigation.gopal.GoPalTrackFormat;
import slash.navigation.itn.TomTom5RouteFormat;
import slash.navigation.itn.TomTomRoute;
import slash.navigation.nmea.NmeaFormat;
import slash.navigation.nmea.NmeaRoute;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

public class StartDateIT extends NavigationTestCase {

    private void checkPosition(BaseNavigationPosition position, CompactCalendar expectedDate) {
        CompactCalendar actual = position.getTime();
        DateFormat format = DateFormat.getDateTimeInstance();
        format.setTimeZone(CompactCalendar.UTC);
        String cal1 = format.format(actual.getTime());
        String cal2 = format.format(expectedDate.getTime());
        assertEquals(cal2, cal1);
        assertEquals(expectedDate.getTimeInMillis(), actual.getTimeInMillis());
        assertEquals(expectedDate.getTime(), actual.getTime());
    }

    private void checkPositions(List<? extends BaseRoute> routes) {
        Calendar expectedDate = Calendar.getInstance();
        expectedDate.set(2007, 6, 21);
        checkPositionsWithDate(routes, expectedDate);
    }

    @SuppressWarnings({"unchecked"})
    private void checkPositionsWithDate(List<? extends BaseRoute> routes, Calendar expectedDate) {
        int year = expectedDate.get(Calendar.YEAR);
        int month = expectedDate.get(Calendar.MONTH) + 1;
        int day = expectedDate.get(Calendar.DAY_OF_MONTH);

        assertNotNull(routes);
        assertEquals(1, routes.size());
        BaseRoute<BaseNavigationPosition, BaseNavigationFormat> route = routes.get(0);
        assertEquals(3, route.getPositionCount());
        BaseNavigationPosition position1 = route.getPositions().get(0);
        checkPosition(position1, calendar(year, month, day, 18, 51, 36));
        BaseNavigationPosition position2 = route.getPositions().get(1);
        checkPosition(position2, calendar(year, month, day, 18, 51, 45));
        BaseNavigationPosition position3 = route.getPositions().get(2);
        checkPosition(position3, calendar(year, month, day, 18, 51, 59));
    }


    public void testFileStartDateForTomTomRouteWithDate() throws IOException {
        List<TomTomRoute> routes = readSampleTomTomRouteFile("startdate-with-date.itn", true);
        checkPositions(routes);
    }

    public void testCurrentStartDateForTomTomRouteWithDate() throws IOException {
        FileInputStream source = new FileInputStream(new File(SAMPLE_PATH + "startdate-with-date.itn"));
        Calendar startDate = Calendar.getInstance();
        List<TomTomRoute> routes = new TomTom5RouteFormat().read(source, CompactCalendar.fromCalendar(startDate));
        checkPositions(routes);
    }

    public void testNullStartDateForTomTomRouteWithDate() throws IOException {
        List<TomTomRoute> routes = readSampleTomTomRouteFile("startdate-with-date.itn", false);
        checkPositions(routes);
    }


    public void testFileStartDateForTomTomRouteWithoutDate() throws IOException {
        File source = new File(SAMPLE_PATH + "startdate-without-date.itn");
        Calendar startDate = Calendar.getInstance();
        startDate.setTimeInMillis(source.lastModified());
        List<TomTomRoute> routes = new TomTom5RouteFormat().read(new FileInputStream(source), CompactCalendar.fromCalendar(startDate));
        checkPositionsWithDate(routes, startDate);
    }

    public void testCurrentStartDateForTomTomRouteWithoutDate() throws IOException {
        FileInputStream source = new FileInputStream(new File(SAMPLE_PATH + "startdate-without-date.itn"));
        Calendar startDate = Calendar.getInstance();
        List<TomTomRoute> routes = new TomTom5RouteFormat().read(source, CompactCalendar.fromCalendar(startDate));
        checkPositionsWithDate(routes, startDate);
    }

    public void testNullStartDateForTomTomRouteWithoutDate() throws IOException {
        List<TomTomRoute> routes = readSampleTomTomRouteFile("startdate-without-date.itn", false);
        Calendar startDate = Calendar.getInstance();
        startDate.set(1970, 0, 1);
        checkPositionsWithDate(routes, startDate);
    }


    public void testFileStartDateForNmeaWithDate() throws IOException {
        List<NmeaRoute> routes = readSampleNmeaFile("startdate-with-date.nmea", true);
        checkPositions(routes);
    }

    public void testCurrentStartDateForNmeaWithDate() throws IOException {
        FileInputStream source = new FileInputStream(new File(SAMPLE_PATH + "startdate-with-date.nmea"));
        Calendar startDate = Calendar.getInstance();
        List<NmeaRoute> routes = new NmeaFormat().read(source, CompactCalendar.fromCalendar(startDate));
        checkPositions(routes);
    }

    public void testNullStartDateForNmeaWithDate() throws IOException {
        List<NmeaRoute> routes = readSampleNmeaFile("startdate-with-date.nmea", false);
        checkPositions(routes);
    }


    public void testFileStartDateForNmeaWithoutDate() throws IOException {
        File source = new File(SAMPLE_PATH + "startdate-without-date.nmea");
        Calendar startDate = Calendar.getInstance();
        startDate.setTimeInMillis(source.lastModified());
        List<NmeaRoute> routes = new NmeaFormat().read(new FileInputStream(source), CompactCalendar.fromCalendar(startDate));
        checkPositionsWithDate(routes, startDate);
    }

    public void testCurrentStartDateForNmeaWithoutDate() throws IOException {
        FileInputStream source = new FileInputStream(new File(SAMPLE_PATH + "startdate-without-date.nmea"));
        Calendar startDate = Calendar.getInstance();
        List<NmeaRoute> routes = new NmeaFormat().read(source, CompactCalendar.fromCalendar(startDate));
        checkPositionsWithDate(routes, startDate);
    }

    public void testNullStartDateForNmeaWithoutDate() throws IOException {
        List<NmeaRoute> routes = readSampleNmeaFile("startdate-without-date.nmea", false);
        Calendar startDate = Calendar.getInstance();
        startDate.set(1970, 0, 1);
        checkPositionsWithDate(routes, startDate);
    }


    public void testFileStartDateForGopalTrackWithoutDate() throws IOException {
        File source = new File(SAMPLE_PATH + "startdate-without-date.trk");
        Calendar startDate = Calendar.getInstance();
        startDate.setTimeInMillis(source.lastModified());
        List<SimpleRoute> routes = new GoPalTrackFormat().read(new FileInputStream(source), CompactCalendar.fromCalendar(startDate));
        checkPositionsWithDate(routes, startDate);
    }

    public void testCurrentStartDateForGopalTrackWithoutDate() throws IOException {
        FileInputStream source = new FileInputStream(new File(SAMPLE_PATH + "startdate-without-date.trk"));
        Calendar startDate = Calendar.getInstance();
        List<SimpleRoute> routes = new GoPalTrackFormat().read(source, CompactCalendar.fromCalendar(startDate));
        checkPositionsWithDate(routes, startDate);
    }

    public void testNullStartDateForGopalTrackWithoutDate() throws IOException {
        List<SimpleRoute> routes = readSampleGopalTrackFile("startdate-without-date.trk", false);
        Calendar startDate = Calendar.getInstance();
        startDate.set(1970, 0, 1);
        checkPositionsWithDate(routes, startDate);
    }
}
