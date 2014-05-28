package com.engagepoint.admincenter.core.infinispan;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.engagepoint.admincenter.core.InfinispanPropsLoader;
import org.infinispan.transaction.LockingMode;
import org.infinispan.transaction.TransactionMode;
import org.infinispan.transaction.lookup.DummyTransactionManagerLookup;
import org.infinispan.transaction.lookup.GenericTransactionManagerLookup;
import org.infinispan.util.logging.Log;
import org.infinispan.util.logging.LogFactory;
import org.omg.CORBA.TRANSACTION_MODE;

public abstract class AbstractCacheManagerFactory {
    private static final Log LOG = LogFactory.getLog(AbstractCacheManagerFactory.class);

    private static EmbeddedCacheManager cacheManager;
    private static final String CACHE_FILE_LOADER_LOCATION =InfinispanPropsLoader.getInfinispanPreferencesLoaderLocation();
    private static boolean xmlSetup = InfinispanPropsLoader.getInfinispanXmlSetup();
    private static String defaultCacheName = "default";
    private static byte THREAD_POOL_SIZE = 5;

    private static Map<String, Cache> caches = new HashMap<String, Cache>();

    //we have to use programmaticaly configuration due to Infinispan bug,
    // see https://issues.jboss.org/browse/ISPN-3032
    private static EmbeddedCacheManager createCacheManagerProgramatically() {

        Configuration configuration = new ConfigurationBuilder()
                .invocationBatching().enable()
                .eviction()
                .maxEntries(InfinispanPropsLoader.getInfinispanPreferencesEvictionMaxEntries())
                .strategy(EvictionStrategy.LIRS)
                .transaction()
                .transactionManagerLookup(new DummyTransactionManagerLookup())
                .transactionMode(TransactionMode.TRANSACTIONAL)
                .persistence()
                .passivation(false)
                .addSingleFileStore()
                .preload(true)
                .shared(false)
                .fetchPersistentState(true)
                .ignoreModifications(false)
                .purgeOnStartup(false)
                .location(CACHE_FILE_LOADER_LOCATION)
                .async().enabled(true)
                .flushLockTimeout(60L, TimeUnit.SECONDS)
                .shutdownTimeout(Long.MAX_VALUE)
                .threadPoolSize(THREAD_POOL_SIZE)
                .clustering()
                .cacheMode(CacheMode.DIST_ASYNC)
                .async().hash()
                .numOwners(InfinispanPropsLoader.getInfinispanPreferencesClusteringHashOwners())
                .l1()
                .enable()
                .lifespan(InfinispanPropsLoader.getInfinispanPreferencesClusteringL1Lifespan())
//                .jmxStatistics()
                .build();

        String clusterName = System.getProperty("infinispan.cluster.name");
        if (clusterName == null) {
            clusterName = InfinispanPropsLoader.getInfinispanPreferencesGlobalTransportClusterName();
        }
        return new DefaultCacheManager(
                GlobalConfigurationBuilder.defaultClusteredBuilder()
                        .classLoader(GlobalConfiguration.class.getClassLoader()) //workaround for Infinispan bug
                        .transport().defaultTransport()
                        .clusterName(clusterName)
                        .addProperty("configurationFile", "admincenter-jgroups-tcp.xml")
                        .globalJmxStatistics()
                        .enabled(InfinispanPropsLoader.getInfinispanPreferencesGlobalJmxEnabled())
                        .cacheManagerName(InfinispanPropsLoader.getInfinispanPreferencesCacheManagerName())
                        .allowDuplicateDomains(InfinispanPropsLoader.getInfinispanPreferencesGlobalJmxDuplicatedomains())
                        .shutdown()
                        .build()
                ,
                configuration

        );
    }
    public static EmbeddedCacheManager createInitialConfigLoaderCacheManager() {

        Configuration configuration = new ConfigurationBuilder()
                .invocationBatching().enable()
                .transaction()
                .transactionManagerLookup(new DummyTransactionManagerLookup())
                .transactionMode(TransactionMode.TRANSACTIONAL)
                .persistence()
                .passivation(false)
                .addSingleFileStore()
                .shared(false)
                .preload(true)
                .location(InfinispanPropsLoader.getInfinispanPreferencesLoaderLocation())
                .purgeOnStartup(true)
                .build();

        return new DefaultCacheManager(
                GlobalConfigurationBuilder.defaultClusteredBuilder()
                        .shutdown()
                        .build()
                ,
                configuration
        );
    }

    private static EmbeddedCacheManager createCacheManagerFromXml() throws IOException {
        EmbeddedCacheManager embeddedCacheManager;

        try {
            embeddedCacheManager = new DefaultCacheManager("infinispan-setup-" + InfinispanPropsLoader.getInfinispanVersion() + ".xml");
        } catch (Exception e) {
            LOG.infof("Load default infinispan cache manager", e);
            embeddedCacheManager = new DefaultCacheManager("infinispan-setup-default.xml");
        }
        return embeddedCacheManager;
    }


    public AbstractCacheManagerFactory() {
        initCacheManager();
    }

    private static void initCacheManager() {
        if (xmlSetup) {
            try {
                cacheManager = createCacheManagerFromXml();
            } catch (IOException e) {
               LOG.error(e.getMessage(),e);
            }
        } else {
            cacheManager = createCacheManagerProgramatically();
        }
    }

    public static EmbeddedCacheManager getCacheManager() {
        if (cacheManager == null) {
            initCacheManager();
        }
        return cacheManager;
    }

    public static void removeCache(String cacheName) {
        try {
            if (caches != null && caches.get(cacheName) != null) {
                caches.remove(cacheName);
                getCacheManager().removeCache(cacheName);
                getCacheManager().stop();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }


    public static Cache createCache(String cacheName) {

        if (caches.get(cacheName) == null) {

            Cache cache = null;

            if (cacheName != null && !"".equals(cacheName)) {
                cache = getCacheManager().getCache(cacheName);
            } else {
                cache = getCacheManager().getCache();
            }

            cache.addListener(new CacheModificationListener(CacheModificationController.getInstance(cacheName != null && !"".equals(cacheName) ? cacheName : defaultCacheName, null)));

            caches.put(cacheName, cache);
        }

        return caches.get(cacheName);

    }

    protected abstract int getNodeId(String cacheName);

}
