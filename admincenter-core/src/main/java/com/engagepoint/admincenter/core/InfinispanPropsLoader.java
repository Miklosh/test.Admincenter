package com.engagepoint.admincenter.core;

import org.infinispan.util.logging.Log;
import org.infinispan.util.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;

public class InfinispanPropsLoader {

    private static ResourceBundle app = ResourceBundle.getBundle("app");
    private static Properties prop = new Properties();
    private static String INFINISPAN_VERSION = "infinispan.version";
    private static String INFINISPAN_XML_SETUP = "infinispan.xml.setup";
    private static String INFINISPAN_PREFERENCES_CACHE_NAME = "infinispan.preferences.cache.name";
    private static String INFINISPAN_PREFERENCES_CACHEMANAGER_NAME = "infinispan.preferences.cachemanager.name";
    private static String INFINISPAN_PREFERENCES_LOADER_LOCATION = "infinispan.preferences.loader.location";
    private static String INFINISPAN_PREFERENCES_ALLOW_DUPLICATE_DOMAINS = "infinispan.preferences.allow.duplicate.domains";
    private static String INFINISPAN_PREFERENCES_SINGLE_CACHE_MAX_ENTRIES = "infinispan.preferences.singlecache.maxentries";
    private static String INFINISPAN_PREFERENCES_CLUSTERING_L1_LIFESPAN = "infinispan.preferences.clustering.l1.lifespan";
    private static String INFINISPAN_PREFERENCES_CLUSTERING_HASH_OWNERS = "infinispan.preferences.clustering.hash.owners";
    private static String INFINISPAN_PREFERENCES_GLOBAL_TRANSPORT_CLUSTER_NAME = "infinispan.preferences.global.transport.cluster.name";
    private static String INFINISPAN_PREFERENCES_GLOBAL_JMX_ENABLED = "infinispan.preferences.global.jmx.enabled";
    private static String INFINISPAN_PREFERENCES_GLOBAL_JMX_DUPLICATEDOMAINS = "infinispan.preferences.global.jmx.duplicatedomains";
    private static String INFINISPAN_PREFERENCES_EVICTION_MAX_ENTRIES = "infinispan.preferences.eviction.max.entries";
    private static String INFINISPAN_PREFERENCES_PERSISTENCE_FLUSH_LOCK_TIMEOUT = "infinispan.preferences.persistence.flush.lock.timeout";
    private static String INFINISPAN_MULTICAST_ADDR = "infinispan.multicast.addr";
    private static String INFINISPAN_MULTICAST_PORT = "infinispan.multicast.port";
    private static String INFINISPAN_MULTICAST_IP_TTL = "infinispan.multicast.ip.ttl";
    private static String JGROUPS_BIND_ADDR = "jgroups.bind_addr";
    private static String JGROUPS_TCP_PORT = "jgroups.tcp.port";

    private static final Log LOG = LogFactory.getLog(InfinispanPropsLoader.class);


    static {
        InputStream is = null;
        try {
            is = InfinispanPropsLoader.class.getResourceAsStream("/infinispan.properties");
            if (is != null) {
                prop.load(is);
                LOG.infof(" /infinispan.properties found ");
            } else {
                is = InfinispanPropsLoader.class.getResourceAsStream("infinispan.properties");
                LOG.infof(" infinispan.properties found ");
                if (is != null) {
                    prop.load(is);
                }
            }
        } catch (IOException e) {
            LOG.warn(e.getMessage(), e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
        }
    }

    private static String getValue(String key) {
        String value = null;
        if (prop != null) {
            value = prop.getProperty(key);
            if (value == null || value.trim().equals("")) {
                if (app != null) {
                    value = app.getString(key);
                    LOG.infof("read from app1");
                }
            }
        } else if (app != null) {
            value = app.getString(key);
            LOG.infof("read from app2");
        }
        return value;
    }

    public static String getProperty(String key) {
        String value = System.getProperty(key);
        if (value == null || value.trim().equals("")) {
            value = getValue(key);
        }
        LOG.infof(key + ":" + value);
        return value;
    }

    public static void setSystemProperty(String property, String value) {
        if (value != null && !"".equals(value)) {
            System.setProperty(property, value);
        }
    }

    public static String getInfinispanVersion() {
        return getProperty(INFINISPAN_VERSION);
    }

    public static boolean getInfinispanXmlSetup() {
        return getProperty(INFINISPAN_XML_SETUP).equals("true");
    }

    public static String getInfinispanPreferencesCacheName() {
        return getProperty(INFINISPAN_PREFERENCES_CACHE_NAME);
    }

    public static String getInfinispanPreferencesCacheManagerName() {
        return getProperty(INFINISPAN_PREFERENCES_CACHEMANAGER_NAME);
    }

    public static String getInfinispanPreferencesLoaderLocation() {
        return getProperty(INFINISPAN_PREFERENCES_LOADER_LOCATION);
    }

    public static boolean getInfinispanPreferencesAllowDuplicateDomains() {
        return getProperty(INFINISPAN_PREFERENCES_ALLOW_DUPLICATE_DOMAINS).equals("true");
    }

    public static String getInfinispanPreferencesSingleCacheMaxEntries() {
        return getProperty(INFINISPAN_PREFERENCES_SINGLE_CACHE_MAX_ENTRIES);
    }

    public static long getInfinispanPreferencesClusteringL1Lifespan() {
        return Long.valueOf(getProperty(INFINISPAN_PREFERENCES_CLUSTERING_L1_LIFESPAN));
    }

    public static int getInfinispanPreferencesClusteringHashOwners() {
        return Integer.valueOf(getProperty(INFINISPAN_PREFERENCES_CLUSTERING_HASH_OWNERS));
    }

    public static String getInfinispanPreferencesGlobalTransportClusterName() {
        return getProperty(INFINISPAN_PREFERENCES_GLOBAL_TRANSPORT_CLUSTER_NAME);
    }

    public static boolean getInfinispanPreferencesGlobalJmxEnabled() {
        return getProperty(INFINISPAN_PREFERENCES_GLOBAL_JMX_ENABLED).equals("true");
    }

    public static boolean getInfinispanPreferencesGlobalJmxDuplicatedomains() {
        return getProperty(INFINISPAN_PREFERENCES_GLOBAL_JMX_DUPLICATEDOMAINS).equals("true");
    }

    public static int getInfinispanPreferencesEvictionMaxEntries() {
        return Integer.valueOf(getProperty(INFINISPAN_PREFERENCES_EVICTION_MAX_ENTRIES));
    }

    public static long getInfinispanPreferencesPersistenceFlushLockTimeout() {
        return Long.valueOf(getProperty(INFINISPAN_PREFERENCES_PERSISTENCE_FLUSH_LOCK_TIMEOUT));
    }

    public static String getInfinispanMulticastAddr() {
        return getProperty(INFINISPAN_MULTICAST_ADDR);
    }

    public static String getInfinispanMulticastPort() {
        return getProperty(INFINISPAN_MULTICAST_PORT);
    }

    public static String getInfinispanMulticastIPTTL() {
        return getProperty(INFINISPAN_MULTICAST_IP_TTL);
    }

    public static String getJGroupsBindAddr() {
        return getProperty(JGROUPS_BIND_ADDR);
    }

    public static String getJGroupsTcpPort() {
        return getProperty(JGROUPS_TCP_PORT);
    }
}
