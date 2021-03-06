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
package slash.navigation.catalog.domain;

import java.io.File;
import java.util.List;

public class RoutesIT extends RouteServiceBase {

    public void testAddRoute() throws Exception {
        String name = "Category " + System.currentTimeMillis();
        Category root = adminCatalog.getRootCategory();
        Category category = root.addSubCategory(name);

        String description = "Route " + System.currentTimeMillis();
        Route route = category.addRoute(description, new File(TEST_PATH + "filestest.gpx"));
        assertNotNull(route);
        assertEquals(description, route.getDescription());
        // TODO check if file exists
        // TODO compare file
        // TODO assertEquals(description, route.getFileUrl());
        List<Route> routes = category.getRoutes();
        assertTrue(routes.contains(route));
    }

    public void testAddRouteWithUmlauts() throws Exception {
        String name = "Category ������� Umlauts " + System.currentTimeMillis();
        Category root = adminCatalog.getRootCategory();
        Category category = root.addSubCategory(name);

        String description = "Route ������� " + System.currentTimeMillis();
        Route route = category.addRoute(description, new File(TEST_PATH + "filestest.gpx"));
        assertNotNull(route);
        assertEquals(description, route.getDescription());
        // TODO check if file exists
        // TODO compare file
        // TODO assertEquals(description, route.getFileUrl());
        List<Route> routes = category.getRoutes();
        assertTrue(routes.contains(route));
    }

    public void testUpdateRouteViaCategory() throws Exception {
        String sourceName = "Source Category " + System.currentTimeMillis();
        Category root = adminCatalog.getRootCategory();
        Category source = root.addSubCategory(sourceName);

        String sourceDescription = "Route " + System.currentTimeMillis();
        Route route = source.addRoute(sourceDescription, new File(TEST_PATH + "filestest.gpx"));
        assertNotNull(route);
        assertEquals(sourceDescription, route.getDescription());
        List<Route> routesBefore = source.getRoutes();
        assertTrue(routesBefore.contains(route));

        String targetDescription = "NEW Description";
        source.updateRoute(route, source, targetDescription);

        List<Route> afterDescriptionChange = source.getRoutes();
        assertTrue(afterDescriptionChange.contains(route));
        route = afterDescriptionChange.get(0);
        assertTrue(afterDescriptionChange.contains(route));
        assertEquals(targetDescription, route.getDescription());

        String targetName = "Target Category " + System.currentTimeMillis();
        Category target = root.addSubCategory(targetName);
        source.updateRoute(route, target, targetDescription);

        List<Route> sourceAfterCategoryChange = source.getRoutes();
        assertFalse(sourceAfterCategoryChange.contains(route));

        List<Route> targetAfterCategoryChange = target.getRoutes();
        assertTrue(targetAfterCategoryChange.contains(route));
        route = targetAfterCategoryChange.get(0);
        assertTrue(targetAfterCategoryChange.contains(route));
        assertEquals(targetDescription, route.getDescription());
    }

    public void testDeleteRouteViaCategory() throws Exception {
         String name = "Category " + System.currentTimeMillis();
         Category root = adminCatalog.getRootCategory();
         Category category = root.addSubCategory(name);

         String description = "Route " + System.currentTimeMillis();
         Route route = category.addRoute(description, new File(TEST_PATH + "filestest.gpx"));
         assertNotNull(route);
         assertEquals(description, route.getDescription());
         List<Route> routesBefore = category.getRoutes();
         assertTrue(routesBefore.contains(route));

         category.deleteRoute(route);

         List<Route> routesAfter = category.getRoutes();
         assertFalse(routesAfter.contains(route));

         // TODO check if file still exists
     }

     public void testDeleteRouteDirectly() throws Exception {
        String name = "Category " + System.currentTimeMillis();
        Category root = adminCatalog.getRootCategory();
        Category category = root.addSubCategory(name);

        String description = "Route " + System.currentTimeMillis();
        Route route = category.addRoute(description, new File(TEST_PATH + "filestest.gpx"));
        assertNotNull(route);
        assertEquals(description, route.getDescription());

        route.delete();

        // TODO check if file still exists
    }
}
