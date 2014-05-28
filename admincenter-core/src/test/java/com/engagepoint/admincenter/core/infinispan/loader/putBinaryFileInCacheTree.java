package com.engagepoint.admincenter.core.infinispan.loader;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.tree.Fqn;
import org.infinispan.tree.TreeCache;
import org.infinispan.tree.TreeCacheFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static junit.framework.Assert.assertEquals;

public class putBinaryFileInCacheTree {

    private static final String CACHE_NAME = "testCache";

    private static Configuration configuration;
    private static EmbeddedCacheManager cacheManager;
    private static Cache<Object,Object> cache;
    private static TreeCacheFactory treeCacheFactory;
    private static TreeCache treeCache;

    private static final String PATH_BINARY_FILE = "./tmp/cacheStorage";

    private static final Fqn FQN_1 = Fqn.fromString("/com/enagepoint/architecture/cachestorage");
    private static final String KEY_1 = "key1";
    private static final String KEY_2 = "key_for_file";
    private static final String TEST_VALUE = "test_value";
    private static File BINARY_FILE = null;

    @Before
    public void init() {
        configuration = new ConfigurationBuilder()
                .invocationBatching().enable()
                .persistence()
                .passivation(false)
                .addSingleFileStore()
                .shared(false)
                .preload(true)
                .ignoreModifications(false)
                .purgeOnStartup(false)
                .location(PATH_BINARY_FILE)
                .build();
        cacheManager = new DefaultCacheManager(configuration);
        cache = cacheManager.getCache(CACHE_NAME);
        treeCacheFactory = new TreeCacheFactory();
        treeCache = treeCacheFactory.createTreeCache(cache);

        treeCache.put(FQN_1,KEY_1,TEST_VALUE);

        BINARY_FILE = new File(PATH_BINARY_FILE + cacheManager.getCacheNames());

        treeCache.start();
    }

    @After
    public void shut_down() {
        treeCache.stop();
    }

    @Test
    public void test1() {
        treeCache.put(FQN_1,KEY_2,BINARY_FILE);
        assertEquals(BINARY_FILE,treeCache.get(FQN_1,KEY_2));
    }

}
