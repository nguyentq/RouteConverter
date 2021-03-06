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

import slash.navigation.base.NavigationFileParser;
import slash.navigation.base.ReadWriteBase;
import slash.navigation.base.SimpleRoute;
import slash.navigation.base.Wgs84Position;

import java.io.IOException;

public class iBlue747ReadWriteRoundtripIT extends ReadWriteBase {

    public void testRoundtrip() throws IOException {
        readWriteRoundtrip(TEST_PATH + "from-iblue747.csv", new NavigationFileParserCallback() {
            public void test(NavigationFileParser source, NavigationFileParser target) {
                SimpleRoute sourceRoute = (SimpleRoute) source.getAllRoutes().get(0);
                SimpleRoute targetRoute = (SimpleRoute) target.getAllRoutes().get(0);
                for(int i=0; i < sourceRoute.getPositionCount(); i++) {
                    Wgs84Position sourcePosition = (Wgs84Position) sourceRoute.getPosition(i);
                    Wgs84Position targetPosition= (Wgs84Position) targetRoute.getPosition(i);
                    assertEquals(targetPosition.getElevation(), sourcePosition.getElevation());
                    assertEquals(targetPosition.getSpeed(), sourcePosition.getSpeed());
                    assertEquals(targetPosition.getHeading(), sourcePosition.getHeading());
                }
            }
        });
    }
}