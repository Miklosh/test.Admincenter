package com.engagepoint.admincenter.core.infinispan.loader;

import com.engagepoint.admincenter.core.infinispan.ConfigUtils;
import com.engagepoint.admincenter.core.infinispan.InfinispanPrefs;

import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.Properties;
import java.util.prefs.Preferences;

import static org.testng.AssertJUnit.assertEquals;

/**
 * User: dmytro.palczewski
 * Date: 1/13/14
 * Time: 11:50 AM
 */
public class InitialConfigLoaderTest {
    public static final String INIT_PROPERTIES_FILENAME = "engagepoint-service.properties";
    public static final String INIT_BINARY_PROPERTIES_FILENAME = "engagepoint-service-binary.properties";

    @Test
    public void testLoadConfig() throws Exception{
        Properties initProps = getInitProperties(INIT_PROPERTIES_FILENAME);
        Properties initBinaryProps = getInitProperties(INIT_BINARY_PROPERTIES_FILENAME);

        InitialConfigLoader loader = new InitialConfigLoader();
        loader.loadConfig(initProps, initBinaryProps);


        Preferences preferences = new InfinispanPrefs();
        preferences = preferences.node("/com/engagepoint/architectSuite");
        assertEquals("http://192.168.32.154:5678", preferences.get("test.com.engagepoint.service.websphere.esb.gateway.url", null));
        assertEquals("jms/ezServiceStatistic", preferences.get("test.com.engagepoint.service.statistic.queue.name", null));
        assertEquals("queue:auditQueue", preferences.get("test.com.engagepoint.service.audit.queue.name", null));

//        byte[] templateBytes = ConfigUtils.getBinaryConfigOption(preferences, "test.com.engagepoint.service.bundle.template.filename");
//        assertEquals("foo", new String(templateBytes));
    }

    Properties getInitProperties(String propsName) throws Exception {
        Properties properties = new Properties();
        InputStream inputStream = InitialConfigLoader.class.getResourceAsStream("/" + propsName);
        properties.load(inputStream);
        return properties;
    }
}
