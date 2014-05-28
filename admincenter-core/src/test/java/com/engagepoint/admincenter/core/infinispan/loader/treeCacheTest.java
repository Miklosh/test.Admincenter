package com.engagepoint.admincenter.core.infinispan.loader;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.tree.Fqn;
import org.infinispan.tree.TreeCache;
import org.infinispan.tree.TreeCacheFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static junit.framework.Assert.assertEquals;

public class treeCacheTest {

    TreeCacheFactory cacheFactory;
    TreeCache cacheTree;
    Cache<String,String> cache;
    private static final Fqn FQN_PATH_1 = Fqn.fromString("/ArchitectSuite/com/engagepoint/service/");
    private static final Fqn FQN_PATH_2 = Fqn.fromString("/ArchitectSuite/com/engagepoint/");
    private static final String KEY1 = "key1";
    private static final String KEY2 = "key2";
    private static final String KEY3 = "key3";
    private static final String KEY_FOR_FILE = "key_file";
    private static final String VALUE1 = "value1";
    private static final String VALUE2 = "value2";
    private static final String VALUE3 = "value3";
    private static final String PATH_BINARY_FILE = "C:\\tmp\\single1\\preferencesCache.dat";
    private static File VALUE_BINARY_File = null;

    @Before
    public void init() {
        VALUE_BINARY_File = new File(PATH_BINARY_FILE);
        cache = new DefaultCacheManager( new GlobalConfigurationBuilder()
                .globalJmxStatistics()
                .allowDuplicateDomains(Boolean.TRUE)
                .build(),
                new ConfigurationBuilder()
                .invocationBatching().enable()
                .build())
                .getCache("TestCache");
        cacheFactory = new TreeCacheFactory();
        cacheTree = cacheFactory.createTreeCache(cache);
        cacheTree.start();
    }

    @After
    public void shutDown() {
        cacheTree.stop();
    }

    @Test
    public void test1() {
        cacheTree.put(FQN_PATH_1, KEY1, VALUE1);
        assertEquals(VALUE1,cacheTree.get(FQN_PATH_1, KEY1));
    }

    @Test
    public void test2() {
        cacheTree.put(FQN_PATH_1, KEY1, VALUE1);
        cacheTree.put(FQN_PATH_2,KEY2,VALUE2);
        cacheTree.put(FQN_PATH_2,KEY3,VALUE3);

        cacheTree.put(FQN_PATH_1,KEY_FOR_FILE,VALUE_BINARY_File);

        assertEquals(VALUE1,cacheTree.get(FQN_PATH_1, KEY1));
        assertEquals(VALUE2,cacheTree.get(FQN_PATH_2, KEY2));
        assertEquals(VALUE3,cacheTree.get(FQN_PATH_2, KEY3));

        assertEquals(VALUE_BINARY_File,cacheTree.get(FQN_PATH_1, KEY_FOR_FILE));
    }
}
