package com.engagepoint.admincenter.core.infinispan.loader;

import com.engagepoint.admincenter.core.infinispan.InfinispanPrefs;
import com.engagepoint.admincenter.core.infinispan.InfinispanPrefsFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.prefs.Preferences;

import static junit.framework.Assert.assertEquals;

public class infinispanPrefsTreeCacheTest {

    private static final String TEST_KEY_1= "key1";
    private static final String TEST_VALUE_1= "value1";
    private static Preferences prefs;

    @Before
    public void init() {
        prefs = new InfinispanPrefs();
    }

    @Test
    public void test1() {
        prefs.put(TEST_KEY_1,TEST_VALUE_1);
        assertEquals(TEST_VALUE_1,prefs.get(TEST_KEY_1,null));
    }
}
