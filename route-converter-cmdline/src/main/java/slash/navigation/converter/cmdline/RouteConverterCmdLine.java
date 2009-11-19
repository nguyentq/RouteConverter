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
    along with Foobar; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

    Copyright (C) 2007 Christian Pesch. All Rights Reserved.
*/

package slash.navigation.converter.cmdline;

import slash.navigation.*;
import slash.common.io.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * A simple command line user interface for the route conversion
 * running under Java 5.
 *
 * @author Christian Pesch
 */

public class RouteConverterCmdLine {
    private static final Logger log = Logger.getLogger(RouteConverterCmdLine.class.getName());

    private void initializeLogging() {
        try {
            LogManager.getLogManager().readConfiguration(RouteConverterCmdLine.class.getResourceAsStream("cmdline.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logFormatNames(List<NavigationFormat> formats) {
        log.info("Supported formats:");
        for(NavigationFormat format : formats)
            log.info(format.getClass().getSimpleName() + " for " + format.getName());
    }

    private BaseNavigationFormat findFormat(String formatName) {
        List<NavigationFormat> formats = NavigationFormats.getWriteFormats();
        for(NavigationFormat format : formats)
            if(formatName.equals(format.getClass().getSimpleName()))
                return (BaseNavigationFormat) format;
        return null;
    }

    private void run(String[] args) {
        log.info("Started RouteConverter on " + Platform.getOsName() + " with " + Platform.getJvm());
        if (args.length != 3) {
            log.info("Usage: java -jar RouteConverterCmdLine.jar <source file> <target format> <target file>");
            logFormatNames(NavigationFormats.getWriteFormatsSortedByName());
            System.exit(5);
        }

        File source = new File(args[0]);
        if (!source.exists()) {
            log.severe("Source '" + source.getAbsolutePath() + "' does not exist; stopping.");
            System.exit(10);
        }

        BaseNavigationFormat format = findFormat(args[1]);
        if (format == null) {
            log.severe("Format '" + args[1] + "' does not exist; stopping.");
            logFormatNames(NavigationFormats.getWriteFormatsSortedByName());
            System.exit(12);
        }

        String baseName = Files.removeExtension(args[2]);
        File target = new File(baseName + format.getExtension());
        if (target.exists()) {
            log.severe("Target '" + target.getAbsolutePath() + "' already exists; stopping.");
            System.exit(13);
        }

        try {
            convert(source, format, target);
        } catch (IOException e) {
            log.severe("Error while converting: " + e.getMessage());
            System.exit(15);
        }

        System.exit(0);
    }

    private void convert(File source, NavigationFormat format, File target) throws IOException {
        NavigationFileParser parser = new NavigationFileParser();
        if (!parser.read(source)) {
            log.severe("Could not read source '" + source.getAbsolutePath() + "'");
            logFormatNames(NavigationFormats.getReadFormatsSortedByName());
            System.exit(20);
        }

        if (format.isSupportsMultipleRoutes()) {
            parser.write(parser.getAllRoutes(), (MultipleRoutesFormat) format, target);
        } else {
            parser.write(parser.getTheRoute(), format, false, true, target);
        }
    }

    public static void main(String[] args) {
        RouteConverterCmdLine cmdLine = new RouteConverterCmdLine();
        cmdLine.initializeLogging();
        cmdLine.run(args);
    }
}