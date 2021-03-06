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

package slash.navigation.nmea;

import slash.navigation.base.BaseNavigationPosition;
import slash.navigation.base.RouteCharacteristics;
import slash.common.io.Transfer;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.prefs.Preferences;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads and writes Magellan Route (.rte) files.
 * <p/>
 * Header: $PMGNFMT,%RTE,NUM_MSG,ID,FLAG,NUM,NAME,WPT_NAME1,ICON1,WPT_NAME2,ICON2,CHKSUM ?%WPL,LAT,HEMI,LON,HEMI,ALT,UNIT,NAME,MSG,ICON,CHKSUM,%META,ASCII<br/>
 * Format: $PMGNWPL,4809.43440,N,01135.06121,E,0,M,Muenchner-Freiheit,,a*10<br/>
 * $PMGNRTE,3,1,c,1,Muenchen_Route,Muenchner-Freiheit,a,Engl-Garten-1,a*60
 *
 * @author Christian Pesch
 */

public class MagellanRouteFormat extends BaseNmeaFormat {
    static {
        log = Logger.getLogger(MagellanRouteFormat.class.getName());
    }
    private static final Preferences preferences = Preferences.userNodeForPackage(MagellanRouteFormat.class);

    private static final NumberFormat LONGITUDE_NUMBER_FORMAT = DecimalFormat.getNumberInstance(Locale.US);
    private static final NumberFormat LATITUDE_NUMBER_FORMAT = DecimalFormat.getNumberInstance(Locale.US);

    static {
        LONGITUDE_NUMBER_FORMAT.setGroupingUsed(false);
        LONGITUDE_NUMBER_FORMAT.setMinimumFractionDigits(5);
        LONGITUDE_NUMBER_FORMAT.setMaximumFractionDigits(5);
        LONGITUDE_NUMBER_FORMAT.setMinimumIntegerDigits(5);
        LONGITUDE_NUMBER_FORMAT.setMaximumIntegerDigits(5);
        LATITUDE_NUMBER_FORMAT.setGroupingUsed(false);
        LATITUDE_NUMBER_FORMAT.setMinimumFractionDigits(5);
        LATITUDE_NUMBER_FORMAT.setMaximumFractionDigits(5);
        LATITUDE_NUMBER_FORMAT.setMinimumIntegerDigits(4);
        LATITUDE_NUMBER_FORMAT.setMaximumIntegerDigits(4);
    }

    private static final String HEADER_LINE = "$PMGNFMT,%RTE,NUM_MSG,ID,FLAG,NUM,NAME,WPT_NAME1,ICON1,WPT_NAME2,ICON2,CHKSUM ?%WPL,LAT,HEMI,LON,HEMI,ALT,UNIT,NAME,MSG,ICON,CHKSUM,%META,ASCII";

    private static final Pattern WPL_PATTERN = Pattern.
            compile("^\\$PMGNWPL" + SEPARATOR +
                    "([\\d\\.]+)" + SEPARATOR + "([NS])" + SEPARATOR +
                    "([\\d\\.]+)" + SEPARATOR + "([WE])" + SEPARATOR +
                    "(-?[\\d\\.]+)" + SEPARATOR +
                    "M" + SEPARATOR +
                    "([^" + SEPARATOR + "]*)" + SEPARATOR +          // Comment
                    "[^" + SEPARATOR + "]*" + SEPARATOR +            // copy of the comment above
                    "a" +
                    END_OF_LINE);

    public String getExtension() {
        return ".rte";
    }

    public String getName() {
        return "Magellan Route (*" + getExtension() + ")";
    }

    protected RouteCharacteristics getCharacteristics() {
        return RouteCharacteristics.Route;
    }

    public int getMaximumPositionCount() {
        return preferences.getInt("maximumMagellanRoutePositionCount", 49);
    }

    @SuppressWarnings({"unchecked"})
    public <P extends BaseNavigationPosition> NmeaRoute createRoute(RouteCharacteristics characteristics, String name, List<P> positions) {
        return new NmeaRoute(this, characteristics, (List<NmeaPosition>) positions);
    }

    protected boolean isPosition(String line) {
        Matcher matcher = WPL_PATTERN.matcher(line);
        return matcher.matches() && hasValidChecksum(line);
    }

    protected NmeaPosition parsePosition(String line) {
        Matcher matcher = WPL_PATTERN.matcher(line);
        if (matcher.matches()) {
            String latitude = matcher.group(1);
            String northOrSouth = matcher.group(2);
            String longitude = matcher.group(3);
            String westOrEast = matcher.group(4);
            String altitude = matcher.group(5);
            String comment = Transfer.toMixedCase(matcher.group(6));
            return new NmeaPosition(Transfer.parseDouble(longitude), westOrEast, Transfer.parseDouble(latitude), northOrSouth,
                    Double.parseDouble(altitude), null, null, null, Transfer.trim(comment));
        }
        throw new IllegalArgumentException("'" + line + "' does not match");
    }


    protected String formatLongitude(Double longitude) {
        if (longitude == null)
            return "";
        return LONGITUDE_NUMBER_FORMAT.format(longitude);
    }

    protected String formatLatititude(Double latitude) {
        if (latitude == null)
            return "";
        return LATITUDE_NUMBER_FORMAT.format(latitude);
    }

    String formatRouteName(String name) {
        if (name != null) {
            StringBuffer buffer = new StringBuffer(name.toLowerCase().trim().replaceAll(" ", "-"));
            int i = 0;
            while (i < buffer.length()) {
                char c = buffer.charAt(i);
                if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '-')
                    i++;
                else
                    buffer.deleteCharAt(i);
            }
            if (buffer.length() > 0)
                return buffer.toString().substring(0, Math.min(buffer.length(), 20));
        }
        return "route01";
    }

    public void write(NmeaRoute route, PrintWriter writer, int startIndex, int endIndex) {
        writeHeader(writer);

        List<NmeaPosition> positions = route.getPositions();
        for (int i = startIndex; i < endIndex; i++) {
            NmeaPosition position = positions.get(i);
            writePosition(position, writer, i);
        }

        String routeName = formatRouteName(route.getName());
        int count = Transfer.ceiling(endIndex - startIndex, 2, true);
        for (int i = startIndex; i < endIndex; i += 2) {
            NmeaPosition start = positions.get(i);
            NmeaPosition end = positions.size() > i + 1 ? positions.get(i + 1) : null;
            writeRte(start, end, writer, count, i / 2, routeName);
        }

        writeFooter(writer);
    }

    protected void writeHeader(PrintWriter writer) {
        writer.println(HEADER_LINE);
    }

    protected void writePosition(NmeaPosition position, PrintWriter writer, int index) {
        String longitude = formatLongitude(position.getLongitudeAsDdmm());
        String westOrEast = position.getWestOrEast();
        String latitude = formatLatititude(position.getLatitudeAsDdmm());
        String northOrSouth = position.getNorthOrSouth();
        String comment = formatComment(position.getComment());
        String altitude = Transfer.formatIntAsString(position.getElevation() != null ? position.getElevation().intValue() : null);

        String wpl = "PMGNWPL" + SEPARATOR +
                latitude + SEPARATOR + northOrSouth + SEPARATOR + longitude + SEPARATOR + westOrEast + SEPARATOR +
                altitude + SEPARATOR + "M" + SEPARATOR + comment + SEPARATOR + SEPARATOR + "a";
        writeSentence(writer, wpl);
    }

    private void writeRte(NmeaPosition start, NmeaPosition end, PrintWriter writer, int count, int index, String routeName) {
        String startName = formatComment(start.getComment());

        String rte = "PMGNRTE" + SEPARATOR + count + SEPARATOR + (index + 1) + SEPARATOR +
                "c" + SEPARATOR + "01" + SEPARATOR + routeName + SEPARATOR + startName + SEPARATOR + "a";
        if (end != null) {
            String endName = formatComment(end.getComment());
            rte += SEPARATOR + endName + SEPARATOR + "a";
        }
        writeSentence(writer, rte);
    }

    protected void writeFooter(PrintWriter writer) {
        writeSentence(writer, "PMGNCMD,END");
    }
}