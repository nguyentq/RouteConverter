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

package slash.navigation.gopal;

import slash.navigation.jaxb.JaxbUtils;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class GoPalUtil {
    private static final JAXBContext CONTEXT_3 = JaxbUtils.newContext(slash.navigation.gopal.binding3.ObjectFactory.class);
    private static final JAXBContext CONTEXT_5 = JaxbUtils.newContext(slash.navigation.gopal.binding5.ObjectFactory.class);

    private static final String GOPAL_NAMESPACE_URI = "";

    private static Unmarshaller newUnmarshaller3() {
        return JaxbUtils.newUnmarshaller(CONTEXT_3);
    }

    private static Marshaller newMarshaller3() {
        Marshaller marshaller = JaxbUtils.newMarshaller(CONTEXT_3);
        try {
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");
        } catch (PropertyException e) {
            // intentionally left empty
        }
        return marshaller;
    }

    private static Unmarshaller newUnmarshaller5() {
        return JaxbUtils.newUnmarshaller(CONTEXT_5);
    }

    private static Marshaller newMarshaller5() {
        return JaxbUtils.newMarshaller(CONTEXT_5);
    }


    public static slash.navigation.gopal.binding3.Tour unmarshal3(InputStream in) throws JAXBException {
        slash.navigation.gopal.binding3.Tour result = null;
        try {
            result = (slash.navigation.gopal.binding3.Tour) newUnmarshaller3().unmarshal(in);
        } catch (ClassCastException e) {
            throw new JAXBException("Parse error with " + result + ": " + e.getMessage(), e);
        }
        return result;
    }

    public static slash.navigation.gopal.binding5.Tour unmarshal5(InputStream in) throws JAXBException {
        slash.navigation.gopal.binding5.Tour result = null;
        try {
            result = (slash.navigation.gopal.binding5.Tour) newUnmarshaller5().unmarshal(in);
        } catch (ClassCastException e) {
            throw new JAXBException("Parse error with " + result + ": " + e.getMessage(), e);
        }
        return result;
    }


    public static void marshal3(slash.navigation.gopal.binding3.Tour tour, OutputStream out) throws JAXBException {
        try {
            try {
                newMarshaller3().marshal(new JAXBElement<slash.navigation.gopal.binding3.Tour>(new QName(GOPAL_NAMESPACE_URI, "tour"), slash.navigation.gopal.binding3.Tour.class, tour), out);
            }
            finally {
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            throw new JAXBException("Error while marshalling: " + e.getMessage());
        }
    }

    public static void marshal5(slash.navigation.gopal.binding5.Tour tour, OutputStream out) throws JAXBException {
        try {
            try {
                newMarshaller5().marshal(new JAXBElement<slash.navigation.gopal.binding5.Tour>(new QName(GOPAL_NAMESPACE_URI, "Tour"), slash.navigation.gopal.binding5.Tour.class, tour), out);
            }
            finally {
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            throw new JAXBException("Error while marshalling: " + e.getMessage());
        }
    }
}
