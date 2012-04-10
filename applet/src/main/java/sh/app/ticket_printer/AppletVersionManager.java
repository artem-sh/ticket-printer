package sh.app.ticket_printer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


public class AppletVersionManager {
    
    private static final String VERSION_FILE_ENCODING = "UTF-8";
    private static final String VERSION_FILE_NAME = "/META-INF/VERSION";

    public static int compareInternalToExternalVersions(String externalVersion) throws IOException {
        String internalVersion = getInternalVersion();
        
        if (PrinterApplet.isLogEnabled()) {
            System.out.println("Internal applet version is: " + internalVersion);
            System.out.println("External applet version is: " + externalVersion);
        }
        
        return internalVersion.compareToIgnoreCase(externalVersion);
    }

    private static String getInternalVersion() throws UnsupportedEncodingException, IOException {
        InputStream is = AppletVersionManager.class.getResourceAsStream(VERSION_FILE_NAME);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, VERSION_FILE_ENCODING));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        return sb.toString();
    }
}
