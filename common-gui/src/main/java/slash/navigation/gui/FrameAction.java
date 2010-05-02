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

package slash.navigation.gui;

import slash.navigation.gui.Application;
import slash.navigation.gui.Constants;
import slash.navigation.gui.SingleFrameApplication;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * An {@link Action} and {@link ActionListener} that starts and stops the wait cursor on the application frame.
 *
 * @author Christian Pesch
 */

public abstract class FrameAction extends AbstractAction implements ActionListener {
    protected JFrame getFrame() {
        Application application = Application.getInstance();
        if (application instanceof SingleFrameApplication)
            return ((SingleFrameApplication) application).getFrame();
        throw new UnsupportedOperationException("FrameAction only works on SingleFrameApplication");
    }

    public final void actionPerformed(ActionEvent e) {
        Constants.startWaitCursor(getFrame().getRootPane());
        try {
            run();
        } finally {
            Constants.stopWaitCursor(getFrame().getRootPane());
        }
    }

    public abstract void run();
}