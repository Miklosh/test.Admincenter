package com.engagepoint.admincenter.core.infinispan.loader;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.tree.Fqn;
import org.infinispan.tree.Node;
import org.infinispan.tree.TreeCache;
import org.infinispan.tree.TreeCacheFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class treeCacheNodeCreationTest {

    TreeCacheFactory cacheFactory;
    TreeCache cacheTree;
    Cache<String,String> cache;
    private static final Fqn comFQN = Fqn.fromString("com");
    private static final Fqn engagepointFQN = Fqn.fromString("engagepoint");
    private static final Fqn architectureSuiteFQN = Fqn.fromString("ArchitectSuite");
    private static final String KEY1 = "key1";
    private static final String KEY2 = "key2";
    private static final String KEY3 = "key3";
    private static final String KEY_FOR_FILE = "key_file";
    private static final String VALUE1 = "value1";
    private static final String VALUE2 = "value2";
    private static final String VALUE3 = "value3";

    @Before
    public void init() {
        cache = new DefaultCacheManager( new ConfigurationBuilder()
                .invocationBatching().enable()
                .build()).getCache("TestCache");
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
        Node comNode = cacheTree.getRoot().addChild(comFQN);
        Node engagePNode = comNode.addChild(engagepointFQN);
        Node<String,String> archNode = engagePNode.addChild(architectureSuiteFQN);
        archNode.put("Miles","Davis");
        assertEquals("Davis", archNode.get("Miles"));
    }

}
