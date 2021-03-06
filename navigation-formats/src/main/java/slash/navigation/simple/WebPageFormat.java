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

import slash.common.io.CompactCalendar;
import slash.common.io.InputOutput;
import slash.navigation.base.*;
import slash.navigation.util.Positions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Writes a Web Page (*.html).
 *
 * @author Christian Pesch
 */

public class WebPageFormat extends SimpleFormat<Wgs84Route> {
 
    public String getExtension() {
        return ".html";
    }

    public String getName() {
        return "Web Page (*" + getExtension() + ")";
    }

    public boolean isSupportsReading() {
        return false;
    }

    public boolean isWritingRouteCharacteristics() {
        return true; 
    }

    public int getMaximumPositionCount() {
        return UNLIMITED_MAXIMUM_POSITION_COUNT;
    }

    @SuppressWarnings({"unchecked"})
    public <P extends BaseNavigationPosition> Wgs84Route createRoute(RouteCharacteristics characteristics, String name, List<P> positions) {
        return new Wgs84Route(this, characteristics, (List<Wgs84Position>) positions);
    }

    public List<Wgs84Route> read(BufferedReader reader, CompactCalendar startDate, String encoding) throws IOException {
        throw new UnsupportedOperationException();
    }

    public void write(Wgs84Route route, PrintWriter writer, int startIndex, int endIndex) throws IOException {
        String template = new String(InputOutput.readBytes(getClass().getResourceAsStream("webpage.html")));
        List<Wgs84Position> positions = route.getPositions();

        StringBuffer routeBuffer = new StringBuffer();
        if(route.getCharacteristics() == RouteCharacteristics.Route) {
            // TODO segment and limit position count
            for (Wgs84Position position : positions) {
                routeBuffer.append("new GLatLng(").append(position.getLatitude()).append(",").
                        append(position.getLongitude()).append("),");
            }
        }

        StringBuffer trackBuffer = new StringBuffer();
        if(route.getCharacteristics() == RouteCharacteristics.Track) {
            // TODO segment and limit position count
            for (Wgs84Position position : positions) {
                trackBuffer.append("new GLatLng(").append(position.getLatitude()).append(",").
                        append(position.getLongitude()).append("),");
            }
        }

        StringBuffer waypointsBuffer = new StringBuffer();
        if(route.getCharacteristics() == RouteCharacteristics.Waypoints) {
            // TODO segment and limit position count
            for (Wgs84Position position : positions) {
                waypointsBuffer.append("new GMarker(new GLatLng(").append(position.getLatitude()).append(",").
                        append(position.getLongitude()).append("), { title: \")").
                        append(position.getComment()).append("\", clickable: false, icon: markerIcon }),");
            }
        }
        
        Wgs84Position northEast = Positions.northEast(positions);
        Wgs84Position southWest = Positions.southWest(positions);
        StringBuffer boundsBuffer = new StringBuffer().
                append("new GLatLng(").append(northEast.getLatitude()).append(",").
                append(northEast.getLongitude()).append("),").
                append("new GLatLng(").append(southWest.getLatitude()).append(",").
                append(southWest.getLongitude()).append(")");
        Wgs84Position center = Positions.center(positions);
        StringBuffer centerBuffer = new StringBuffer().
                append("new GLatLng(").append(center.getLatitude()).append(",").
                append(center.getLongitude()).append(")");

        String output = template.replaceAll("INSERT_ROUTENAME", route.getName()).
                replaceAll("INSERT_TRACK", routeBuffer.toString()).
                replaceAll("INSERT_ROUTE", trackBuffer.toString()).
                replaceAll("INSERT_WAYPOINTS", waypointsBuffer.toString()).
                replaceAll("INSERT_BOUNDS", boundsBuffer.toString()).
                replaceAll("INSERT_CENTER", centerBuffer.toString());
        writer.println(output);
    }
}