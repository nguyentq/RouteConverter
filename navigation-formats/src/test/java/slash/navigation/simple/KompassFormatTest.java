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

import slash.navigation.base.NavigationTestCase;
import slash.navigation.base.Wgs84Position;

public class KompassFormatTest extends NavigationTestCase {
    KompassFormat format = new KompassFormat();

    public void testIsPosition() {
        assertTrue(format.isPosition("51.0450383,7.0508300"));
        assertTrue(format.isPosition("51.0450383,7.0508300,"));
        assertTrue(format.isPosition("-51.0450383,-7.0508300,0.0"));
        assertTrue(format.isPosition("51.0450383,7.0508300,100.0"));

        assertFalse(format.isPosition("51.0450383,7.0508300,100,"));
        assertFalse(format.isPosition("51.0450383,7.0508300,13,egal"));
        assertFalse(format.isPosition("8.6180900,50.2175100,\"[61352] AH Kreissl GmbH; Benzstraße 7 [Bad Homburg]\""));
        assertFalse(format.isPosition(" 9.3900000 , 51.5037800 , \"[34369] Donig; Max-Eyth-Str. [Hofgeismar]\" "));
        assertFalse(format.isPosition("11.90206,51.11136,[06618] Finke-Biodiesel;,[06618] Finke-Biodiesel; Gorschen 8 [Gorschen]"));
        assertFalse(format.isPosition("11.10717,49.37578,HOLSTEINBRUCH BEI WORZEL,HOLSTEINBRUCH BEI WORZELDORF B - GC13VV5"));
    }

    public void testParsePosition() {
        Wgs84Position position = format.parsePosition("51.0450383,7.0508300,124.5", null);
        assertEquals(7.0508300, position.getLongitude());
        assertEquals(51.0450383, position.getLatitude());
        assertEquals(124.5, position.getElevation());
        assertNull(position.getComment());
    }

    public void testParsePositionWithoutElevation() {
        Wgs84Position position = format.parsePosition("51.0450383,7.0508300", null);
        assertEquals(7.0508300, position.getLongitude());
        assertEquals(51.0450383, position.getLatitude());
        assertNull(position.getElevation());
        assertNull(position.getComment());
    }
}