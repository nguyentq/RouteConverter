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

package slash.navigation.mm;

import slash.common.io.CompactCalendar;
import slash.navigation.base.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A MagicMaps (.pth) route.
 *
 * @author Christian Pesch
 */

public class MagicMapsPthRoute extends SimpleRoute<GkPosition, MagicMapsPthFormat> {
    public MagicMapsPthRoute(MagicMapsPthFormat format, RouteCharacteristics characteristics, List<GkPosition> positions) {
        super(format, characteristics, positions);
    }

    public MagicMapsPthRoute(RouteCharacteristics characteristics, List<GkPosition> positions) {
        this(new MagicMapsPthFormat(), characteristics, positions);
    }


    protected SimpleRoute asSimpleFormat(SimpleFormat format) {
        List<Wgs84Position> wgs84positions = new ArrayList<Wgs84Position>();
        for (GkPosition position : positions) {
            wgs84positions.add(position.asWgs84Position());
        }
        return new Wgs84Route(format, getCharacteristics(), wgs84positions);
    }

    public MagicMapsPthRoute asMagicMapsPthFormat() {
        return this;
    }


    public GkPosition createPosition(Double longitude, Double latitude, Double elevation, Double speed, CompactCalendar time, String comment) {
        return new GkPosition(longitude, latitude, elevation, speed, time, comment);
    }
}
