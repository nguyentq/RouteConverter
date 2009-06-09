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

package slash.navigation.converter.gui.actions;

import slash.navigation.NavigationFileParser;
import slash.navigation.converter.gui.RouteConverter;
import slash.navigation.converter.gui.models.PositionsModel;
import slash.navigation.converter.gui.panels.ConvertPanel;
import slash.navigation.gui.Constants;
import slash.navigation.util.Files;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * {@link ActionListener} that imports a file to the current position list of a {@link PositionsModel} at
 * the selected rows of a {@link JTable}.
 *
 * @author Christian Pesch
 */

public class ImportPositionList implements ActionListener {
    private static final Logger log = Logger.getLogger(ImportPositionList.class.getName());

    private JFrame frame;
    private ConvertPanel panel;
    private JTable tablePositions;
    private PositionsModel positionsModel;

    public ImportPositionList(JFrame frame, ConvertPanel panel, JTable tablePositions, PositionsModel positionsModel) {
        this.frame = frame;
        this.panel = panel;
        this.tablePositions = tablePositions;
        this.positionsModel = positionsModel;
    }

    public void actionPerformed(ActionEvent e) {
        int selectedRow = tablePositions.getSelectedRow() + 1;
        Constants.startWaitCursor(frame.getRootPane());

        try {
            File[] files = panel.selectFilesToImport();
            if (files == null)
                return;

            importPositionList(selectedRow, Files.toUrls(files));
        } finally {
            Constants.stopWaitCursor(frame.getRootPane());
        }
    }


    private List<URL> reverse(List<URL> urls) {
        List<URL> result = new ArrayList<URL>();
        for (URL url : urls)
            result.add(0, url);
        return result;
    }

    private void importPositionList(final int row, final List<URL> urls) {
        final RouteConverter r = RouteConverter.getInstance();

        new Thread(new Runnable() {
            public void run() {
                try {
                    for (URL url : reverse(urls)) {
                        final String path = Files.createReadablePath(url);

                        final NavigationFileParser parser = new NavigationFileParser();
                        if (parser.read(url)) {
                            log.info("Imported: " + path);

                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    try {
                                        positionsModel.add(row, parser.getTheRoute());
                                    } catch (IOException e) {
                                        log.severe("Open error: " + e.getMessage());
                                        r.handleOpenError(e, path);
                                    }
                                }
                            });

                        } else {
                            log.severe("Unsupported format: " + path);
                            r.handleUnsupportedFormat(path);
                        }
                    }
                } catch (Exception e) {
                    log.severe("Append error: " + e.getMessage());
                    r.handleOpenError(e, urls);
                }
            }
        }, "FileImporter").start();
    }
}