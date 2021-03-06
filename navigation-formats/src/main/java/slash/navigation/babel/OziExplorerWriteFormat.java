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

package slash.navigation.babel;

import slash.navigation.base.MultipleRoutesFormat;
import slash.navigation.base.RouteCharacteristics;
import slash.navigation.gpx.GpxRoute;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * The base of all OziExplorer write formats.
 *
 * @author Christian Pesch
 */

public abstract class OziExplorerWriteFormat extends BabelFormat implements MultipleRoutesFormat<GpxRoute> {

    protected String getFormatName() {
        return "ozi,pack";
    }

    public boolean isSupportsReading() {
        return false;
    }

    public boolean isSupportsWriting() {
        return true;
    }

    protected boolean isStreamingCapable() {
        return true;
    }

    protected abstract RouteCharacteristics getRouteCharacteristics();

    public void write(GpxRoute route, OutputStream target, int startIndex, int endIndex) throws IOException {
        // otherwise the ozi gpsbabel module would write .rte for Routes, .plt for Tracks and .wpt for Waypoints
        route.setCharacteristics(getRouteCharacteristics());
        super.write(route, target, startIndex, endIndex);
    }

    public void write(List<GpxRoute> routes, OutputStream target) throws IOException {
        for (GpxRoute route : routes)
            // otherwise the ozi gpsbabel module would write .rte for Routes, .plt for Tracks and .wpt for Waypoints
            route.setCharacteristics(getRouteCharacteristics());
        super.write(routes, target);
    }
}