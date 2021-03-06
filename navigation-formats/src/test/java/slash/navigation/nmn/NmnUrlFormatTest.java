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

package slash.navigation.nmn;

import org.junit.Test;
import slash.navigation.base.Wgs84Position;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static slash.common.TestCase.assertDoubleEquals;

public class NmnUrlFormatTest {
    private static final String TARGET_COORDINATE_URL = "<a href=\"navigonDEU://route/?target=coordinate//7.273085/51.411354&amp;target=coordinate//7.305422/51.410168&amp;target=coordinate//7.340269/51.428253&amp;target=coordinate//7.388512/51.407173&amp;target=coordinate//7.455643/51.422134&amp;target=coordinate//7.506969/51.422310&amp;target=coordinate//7.538937/51.405663&amp;target=coordinate//7.591082/51.411518&amp;target=coordinate//7.654833/51.411278&amp;target=coordinate//7.677935/51.402477&amp;target=coordinate//7.720705/51.407127&amp;target=coordinate//7.746279/51.434059&amp;target=coordinate//7.838520/51.438408&amp;target=coordinate//7.911991/51.426926&amp;target=coordinate//7.932663/51.411076&amp;target=coordinate//7.935606/51.377350&amp;target=coordinate//7.933142/51.357689&amp;target=coordinate//7.942778/51.343704&amp;target=coordinate//8.006210/51.285271&amp;target=coordinate//8.079313/51.290840&amp;target=coordinate//8.079969/51.346352&amp;target=coordinate//8.055506/51.392109&amp;target=coordinate//8.076488/51.437889&amp;target=coordinate//8.118202/51.483131&amp;target=coordinate//8.163364/51.491737\">51°24'41\"N, 07°16'23\"O, Kämpenstrasse, 58456 Witten ? 51°24'37\"N, 07°18'20\"O, Rauendahlstrasse, 58452 Witten ? 51°25'42\"N, 07°20'25\"O, Ruhrdeich, 58452 Witten ? 51°24'26\"N, 07°23'19\"O, Ender Talstrasse, 58313 Herdecke ? 51°25'20\"N, 07°27'20\"O, Wittbräucker Strasse, 58313 Herdecke ? 51°25'20\"N, 07°30'25\"O, Westhofener Strasse, 44265 Dortmund ? 51°24'20\"N, 07°32'20\"O, Ruhrtalstrasse, 58239 Schwerte ? 51°24'41\"N, 07°35'28\"O, Steinberg, 58239 Schwerte ? 51°24'41\"N, 07°39'17\"O, Schirrnbergstrasse, 58640 Iserlohn ? 51°24'09\"N, 07°40'41\"O, Leckingser Strasse, 58640 Iserlohn ? 51°24'26\"N, 07°43'15\"O, Landhauser Strasse, 58640 Iserlohn ? 51°26'03\"N, 07°44'47\"O, Bräukerweg, 58708 Menden (Sauerland) ? 51°26'18\"N, 07°50'19\"O, K21, 58708 Menden (Sauerland) ? 51°25'37\"N, 07°54'43\"O, Bieberstrasse, 59757 Arnsberg ? 51°24'40\"N, 07°55'58\"O, K1, 59757 Arnsberg ? 51°22'38\"N, 07°56'08\"O, L544, 59846 Sundern (Sauerland) ? 51°21'28\"N, 07°55'59\"O, L544, 59846 Sundern (Sauerland) ? 51°20'37\"N, 07°56'34\"O, L687, 59846 Sundern (Sauerland) ? 51°17'07\"N, 08°00'22\"O, L842, 59846 Sundern (Sauerland) ? 51°17'27\"N, 08°04'46\"O, K24, 59846 Sundern (Sauerland) ? 51°20'47\"N, 08°04'48\"O, L839, 59846 Sundern (Sauerland) ? 51°23'32\"N, 08°03'20\"O, Altes Feld, 59821 Arnsberg ? 51°26'16\"N, 08°04'35\"O, K8, 59823 Arnsberg ? 51°28'59\"N, 08°07'06\"O, L857, 59519 Möhnesee ? 51°29'30\"N, 08°09'48\"O, Seeuferstrasse, 59519 Möhnesee</a>";
    private static final String TARGET_ADDRESS_URL = "<a href=\"navigonDEU://route/?target=address//DEU/44797/BOCHUM/UNTERM%20KOLM/11/7.23153/51.43851&amp;target=address//DEU/44227/DORTMUND/MARTIN-SCHMEISSER-WEG/8/7.40361/51.49144\">Unterm Kolm 11, 44797 Bochum ? Martin-Schmeisser-Weg 8, 44227 Dorstfeld, Dortmund</a>";
    private static final String USA_URL = "navigonUSA-CA://route/?target=address//USA-CA/CA%2092120/SAN%20DIEGO/ALVARADO%20CANYON%20RD/4620/-117.09521/32.78042&amp;target=address//USA-NV/NV%2089101/LAS%20VEGAS///-115.13997/36.17191";

    NmnUrlFormat urlFormat = new NmnUrlFormat();

    @Test
    public void testFindURL() {
        String url = urlFormat.findURL(TARGET_COORDINATE_URL);
        assertNotNull(url);
        assertTrue(url.startsWith("target=coordinate"));
        assertNull(urlFormat.findURL("don't care"));
    }

    @Test
    public void testCoordinatesPosition() {
        Wgs84Position position = urlFormat.parsePosition("coordinate//7.273085/51.411354");
        assertDoubleEquals(7.273085, position.getLongitude());
        assertDoubleEquals(51.411354, position.getLatitude());
    }

    @Test
    public void testAddressPosition() {
        Wgs84Position position = urlFormat.parsePosition("address//DEU/44797/BOCHUM/UNTERM%20KOLM/11/7.23153/51.43851");
        assertDoubleEquals(7.23153, position.getLongitude());
        assertDoubleEquals(51.43851, position.getLatitude());
        assertEquals("44797 Bochum, Unterm Kolm 11", position.getComment());
    }

    @Test
    public void testParsePositionsFromTargetCoordinateUrl() {
        List<Wgs84Position> positions = urlFormat.parsePositions(TARGET_COORDINATE_URL);
        assertNotNull(positions);
        assertEquals(25, positions.size());
        Wgs84Position position6 = positions.get(5);
        assertDoubleEquals(7.506969, position6.getLongitude());
        assertDoubleEquals(51.42231, position6.getLatitude());
        assertNull(position6.getComment());
    }

    @Test
    public void testParsePositionsFromTargetAddressUrl() {
        List<Wgs84Position> positions = urlFormat.parsePositions(TARGET_ADDRESS_URL);
        assertNotNull(positions);
        assertEquals(2, positions.size());
        Wgs84Position position2 = positions.get(1);
        assertDoubleEquals(7.40361, position2.getLongitude());
        assertDoubleEquals(51.49144, position2.getLatitude());
        assertEquals("44227 Dortmund, Martin-Schmeisser-Weg 8", position2.getComment());
    }

    @Test
    public void testParsePositionsFromUsaUrl() {
        List<Wgs84Position> positions = urlFormat.parsePositions(USA_URL);
        assertNotNull(positions);
        assertEquals(2, positions.size());
        Wgs84Position position2 = positions.get(1);
        assertDoubleEquals(-115.13997, position2.getLongitude());
        assertDoubleEquals(36.17191, position2.getLatitude());
        assertEquals("Nv 89101 Las Vegas", position2.getComment());
    }

    @Test
    public void testCreateDeURL() {
        List<Wgs84Position> positions = new ArrayList<Wgs84Position>();
        positions.add(new Wgs84Position(10.02571156, 53.57497745, null, 5.5, null, "Hamburg, Germany"));
        positions.add(new Wgs84Position(10.20026067, 53.57662034, null, 4.5, null, "Stemwarde, Germany"));
        positions.add(new Wgs84Position(10.35735078, 53.59171021, null, 3.5, null, "Großensee, Germany"));
        positions.add(new Wgs84Position(10.45696089, 53.64781001, null, 2.5, null, "Linau, Germany"));
        String expected = "navigonDEU://route/?target=coordinate//10.025711/53.574977&target=coordinate//10.200260/53.576620&target=coordinate//10.357350/53.591710&target=coordinate//10.456960/53.647810";
        String actual = urlFormat.createURL(positions, 0, positions.size());
        assertEquals(expected, actual);
    }

    @Test
    public void testCreateUsaURL() {
        List<Wgs84Position> positions = new ArrayList<Wgs84Position>();
        positions.add(new Wgs84Position(-113.240014, 36.114526, 1134.0, null, null, "Grand Canyon, Arizona, USA"));
        positions.add(new Wgs84Position(-115.139973, 53.574977, 648.0, null, null, "Las Vegas, Nevada, USA"));
        String expected = "navigonUSA-CA://route/?target=coordinate//-113.240014/36.114526&target=coordinate//-115.139973/53.574977";
        String actual = urlFormat.createURL(positions, 0, positions.size());
        assertEquals(expected, actual);
    }
}
