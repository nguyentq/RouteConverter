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

import slash.common.io.Transfer;
import slash.navigation.base.UrlFormat;
import slash.navigation.base.Wgs84Position;
import slash.navigation.base.Wgs84Route;

import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads and writes Google Maps URLs from/to files.
 *
 * @author Christian Pesch
 */

public class GoogleMapsUrlFormat extends UrlFormat {
    private static final Logger log = Logger.getLogger(GoogleMapsUrlFormat.class.getName());
    private static final Preferences preferences = Preferences.userNodeForPackage(GoogleMapsUrlFormat.class);
    private static final Pattern URL_PATTERN = Pattern.compile(".*http://.+\\.google\\..+/maps[\\?/]([^\\s]+).*");
    private static final Pattern BOOKMARK_PATTERN = Pattern.compile(".*InternetShortcut(.+)IconFile.*");
    private static final Pattern PLAIN_POSITION_PATTERN = Pattern.compile("(\\s*[-|\\d|\\.]+\\s*),(\\s*[-|\\d|\\.]+\\s*)");
    private static final Pattern COMMENT_POSITION_PATTERN = Pattern.
            compile("(" + WHITE_SPACE + ".*?" + WHITE_SPACE + ")" +
                    "(@?(" + WHITE_SPACE + "[-|\\d|\\.]+" + WHITE_SPACE + ")," +
                    "(" + WHITE_SPACE + "[-|\\d|\\.]+" + WHITE_SPACE + "))?");
    private static final String DESTINATION_SEPARATOR = "to:";

    public String getExtension() {
        return ".url";
    }

    public String getName() {
        return "Google Maps URL (*" + getExtension() + ")";
    }

    public int getMaximumPositionCount() {
        return preferences.getInt("maximumGoogleMapsUrlPositionCount", 15);
    }

    public static boolean isGoogleMapsUrl(URL url) {
        return internalFindUrl(url.toExternalForm()) != null;
    }

    private static String internalFindUrl(String text) {
        text = text.replaceAll("[\n|\r]", "&");
        Matcher bookmarkMatcher = BOOKMARK_PATTERN.matcher(text);
        if (bookmarkMatcher.matches())
            text = bookmarkMatcher.group(1);
        Matcher urlMatcher = URL_PATTERN.matcher(text);
        if (!urlMatcher.matches())
            return null;
        return Transfer.trim(urlMatcher.group(1));
    }

    protected String findURL(String text) {
        return internalFindUrl(text);
    }

    Wgs84Position parsePlainPosition(String coordinates) {
        Matcher matcher = PLAIN_POSITION_PATTERN.matcher(coordinates);
        if (!matcher.matches())
            throw new IllegalArgumentException("'" + coordinates + "' does not match");
        Double latitude = Transfer.parseDouble(matcher.group(1));
        Double longitude = Transfer.parseDouble(matcher.group(2));
        return new Wgs84Position(longitude, latitude, null, null, null, null);
    }

    Wgs84Position parseCommentPosition(String position) {
        Matcher matcher = COMMENT_POSITION_PATTERN.matcher(position);
        if (!matcher.matches())
            throw new IllegalArgumentException("'" + position + "' does not match");
        String comment = Transfer.trim(matcher.group(1));
        Double latitude = Transfer.parseDouble(matcher.group(3));
        Double longitude = Transfer.parseDouble(matcher.group(4));
        return new Wgs84Position(longitude, latitude, null, null, null, comment);
    }

    List<Wgs84Position> parseDestinationPositions(String destinationComments) {
        List<Wgs84Position> result = new ArrayList<Wgs84Position>();
        int startIndex = 0;
        while (startIndex < destinationComments.length()) {
            int endIndex = destinationComments.indexOf(DESTINATION_SEPARATOR, startIndex);
            if (endIndex == -1)
                endIndex = destinationComments.length();
            String position = destinationComments.substring(startIndex, endIndex);
            result.add(parseCommentPosition(position));
            startIndex = endIndex + 3 /* DESTINATION_SEPARATOR */;
        }
        return result;
    }

    List<Wgs84Position> extractGeocodePositions(List<Wgs84Position> positions) {
        List<Wgs84Position> result = new ArrayList<Wgs84Position>(positions);
        for (int i = result.size() - 1; i >= 0; i--) {
            Wgs84Position position = result.get(i);
            if (Transfer.trim(position.getComment()) == null)
                result.remove(i);
        }
        return result;
    }

    protected List<Wgs84Position> parsePositions(Map<String, List<String>> parameters) {
        List<Wgs84Position> result = new ArrayList<Wgs84Position>();
        if (parameters == null)
            return result;

        // ignore ll and sll parameters as they contain positions far off the route

        List<String> startPositions = parameters.get("saddr");
        if (startPositions != null) {
            for (String startComment : startPositions) {
                result.add(parseCommentPosition(startComment));
            }
        }

        List<String> destinationPositions = parameters.get("daddr");
        if(destinationPositions != null) {
            for (String destinationPosition : destinationPositions) {
                result.addAll(parseDestinationPositions(destinationPosition));
            }

            List<String> geocode = parameters.get("geocode");
            if(geocode != null && geocode.size() > 0 && result.size() > 0) {
                List<Wgs84Position> geocodePositions = extractGeocodePositions(result);
                StringTokenizer tokenizer = new StringTokenizer(geocode.get(0), ",;");
                int positionIndex = 0;
                while(tokenizer.hasMoreTokens() && positionIndex < geocodePositions.size()) {
                    tokenizer.nextToken();
                    if (tokenizer.hasMoreTokens()) {
                        try {
                            Double latitude = Transfer.parseDouble(tokenizer.nextToken());
                            if (tokenizer.hasMoreTokens()) {
                                Double longitude = Transfer.parseDouble(tokenizer.nextToken());
                                Wgs84Position position = geocodePositions.get(positionIndex++);
                                position.setLongitude(longitude);
                                position.setLatitude(latitude);
                            }
                        }
                        catch (NumberFormatException e) {
                           log.warning("Cannot parse tokens from " + geocode.get(0));
                        }
                    }
                }
            }
        }
        return result;
    }

    String createURL(List<Wgs84Position> positions, int startIndex, int endIndex) {
        StringBuffer buffer = new StringBuffer("http://maps.google.com/maps?ie=UTF8&");
        for (int i = startIndex; i < endIndex; i++) {
            Wgs84Position position = positions.get(i);
            String longitude = position.getLongitude() != null ? Transfer.formatDoubleAsString(position.getLongitude(), 6) : null;
            String latitude = position.getLatitude() != null ? Transfer.formatDoubleAsString(position.getLatitude(), 6) : null;
            String comment = encodeComment(Transfer.trim(position.getComment()));
            if (i == startIndex) {
                buffer.append("saddr=").append(comment);
                if(longitude != null && latitude != null)
                    buffer.append("%40").append(latitude).append(",").append(longitude);
                if (endIndex > startIndex + 1)
                    buffer.append("&daddr=");
            } else {
                if (i > startIndex + 1 && i < endIndex)
                    buffer.append("+").append(DESTINATION_SEPARATOR);
                buffer.append(comment);
                if(longitude != null && latitude != null)
                    buffer.append("%40").append(latitude).append(",").append(longitude);
            }
        }
        return buffer.toString();
    }

    public void write(Wgs84Route route, PrintWriter writer, int startIndex, int endIndex) {
        List<Wgs84Position> positions = route.getPositions();
        writer.println("[InternetShortcut]");
        // idea from forum: add start point from previous route section since your not at the
        // last position of the previous segment heading for the first position of the next segment
        startIndex = Math.max(startIndex - 1, 0);
        writer.println("URL=" + createURL(positions, startIndex, endIndex));
        writer.println();
    }
}
