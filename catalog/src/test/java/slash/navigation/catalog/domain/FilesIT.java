package slash.navigation.catalog.domain;

import slash.common.io.InputOutput;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FilesIT extends RouteServiceBase {

    public void testAddFileWithUmlauts() throws Exception {
        String name = "Category for files with umlauts " + System.currentTimeMillis();
        Category root = adminCatalog.getRootCategory();
        Category category = root.addSubCategory(name);

        File in = File.createTempFile("����", ".file");
        FileInputStream fis = new FileInputStream(new File(TEST_PATH + "filestest.gpx"));
        FileOutputStream fos = new FileOutputStream(in);
        InputOutput.copy(fis, fos);
        long inLength = in.length();

        Route route = category.addRoute("File with umlauts", in);
        assertNotNull(route);

        assertTrue(in.delete());

        InputStream out = route.getUrl().openStream();
        assertEquals(inLength, out.available());
    }
}
