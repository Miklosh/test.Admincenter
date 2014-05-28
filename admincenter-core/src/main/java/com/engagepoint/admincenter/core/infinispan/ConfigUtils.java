package com.engagepoint.admincenter.core.infinispan;

import com.engagepoint.admincenter.core.exception.PreferenceNotFoundException;

import org.infinispan.Cache;

import javax.print.DocFlavor;
import java.io.*;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * User: dmytro.palczewski
 * Date: 3/19/14
 * Time: 1:42 PM
 */
public final class ConfigUtils {

    private static final Logger LOGGER = Logger.getLogger(ConfigUtils.class.getName());
    private static final int BYTE_ARRAY_SIZE = 512;

    private ConfigUtils(){
    }

    public static byte[] getBinaryConfigOption(Preferences preferences, String propertyName){
        String encodedStr =  preferences.get(propertyName, null);

        if(encodedStr == null){
            String msg = "value for property [" + propertyName + "] is not found in the Infinispan cache";
            LOGGER.severe(msg);
            throw new PreferenceNotFoundException(msg);
        }
        return org.infinispan.commons.util.Base64.decode(encodedStr);
    }

    public static void putBinaryConfigOption(String key, String filePath, Cache<String, String> cache) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            byte[] bytes = new byte[BYTE_ARRAY_SIZE];
            ByteArrayOutputStream buff = new ByteArrayOutputStream();
            int count = 0;
            while((count = inputStream.read(bytes)) != -1){
            	buff.write(bytes, 0, count);
            }
            String encodedStr = org.infinispan.commons.util.Base64.encodeBytes(buff.toByteArray());
            cache.put(key, encodedStr);
        } catch (FileNotFoundException e) {
            LOGGER.warning(e.toString());
        } catch (IOException e) {
            LOGGER.warning(e.toString());
        } finally {
        	if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOGGER.warning(e.toString());
                }
            }
        }
    }
}
