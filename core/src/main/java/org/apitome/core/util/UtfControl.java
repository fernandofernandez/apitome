package org.apitome.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

public class UtfControl extends Control {

    private final Charset charset;

    public UtfControl(Charset charset) {
        this.charset = charset;
    }

    public ResourceBundle newBundle(String baseName, Locale locale, String format,
                                    ClassLoader loader, boolean reload)
            throws IllegalAccessException, InstantiationException, IOException {
        if (!format.equals("java.properties")) {
            throw new IllegalArgumentException("Bundle format " + format + " not supported by this Control");
        }
        // This code is copied from the original Java 8 code (properties handling)
        String bundleName = toBundleName(baseName, locale);
        ResourceBundle bundle = null;
        final String resourceName = toResourceName(bundleName, "properties");
        if (resourceName == null) {
            return bundle;
        }
        final ClassLoader classLoader = loader;
        final boolean reloadFlag = reload;
        InputStream stream = null;
        try {
            stream = AccessController.doPrivileged(
                    new PrivilegedExceptionAction<InputStream>() {
                        public InputStream run() throws IOException {
                            InputStream is = null;
                            if (reloadFlag) {
                                URL url = classLoader.getResource(resourceName);
                                if (url != null) {
                                    URLConnection connection = url.openConnection();
                                    if (connection != null) {
                                        // Disable caches to get fresh data for
                                        // reloading.
                                        connection.setUseCaches(false);
                                        is = connection.getInputStream();
                                    }
                                }
                            } else {
                                is = classLoader.getResourceAsStream(resourceName);
                            }
                            return is;
                        }
                    });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getException();
        }
        if (stream != null) {
            // This section is updated to support UTF encoding
            try(Reader reader = new InputStreamReader(stream, charset)) {
                bundle = new PropertyResourceBundle(reader);
            } finally {
                stream.close();
            }
        }
        return bundle;
    }
}
