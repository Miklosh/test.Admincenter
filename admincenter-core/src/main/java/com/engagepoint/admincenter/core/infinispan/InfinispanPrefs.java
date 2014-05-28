package com.engagepoint.admincenter.core.infinispan;

import com.engagepoint.admincenter.core.InfinispanPropsLoader;
import org.infinispan.Cache;
import org.infinispan.tree.Fqn;
import org.infinispan.tree.TreeCache;
import org.infinispan.tree.TreeCacheFactory;
import org.infinispan.util.logging.Log;
import org.infinispan.util.logging.LogFactory;

import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * User: Luis Cifuentes
 * Date: 4/23/13
 * Time: 3:56 PM
 */
public class InfinispanPrefs extends AbstractPreferences {

    private static final Log LOG = LogFactory.getLog(InfinispanPrefs.class);

    private static Cache<String, String> cache;
    private static TreeCache<String, String> treeCache;
    private Preferences defaultUserRootPreferences;
    private static volatile InfinispanPrefs instance;

    private static final String cacheName = InfinispanPropsLoader.getInfinispanPreferencesCacheName();

    public InfinispanPrefs() {
        this(cacheName);
    }

    public InfinispanPrefs(String cacheName) {
        super(null, "");

        try {
            //Programatic base instantiation
            //Using Double checked locking pattern
            if (cache == null) {
                LOG.infof(" start Infinispan cache ");
                synchronized (AbstractCacheManagerFactory.class) {
                    if (cache == null) {
                        cache = AbstractCacheManagerFactory.createCache(cacheName);
                        TreeCacheFactory treeCacheFactory = new TreeCacheFactory();
                        treeCache = treeCacheFactory.createTreeCache(cache);
                        treeCache.start();

                        LOG.infof(" Infinispan cache created ");
                    }
                }
            }

        } catch (Exception e) {
            //LOG.error(e.getMessage(), e);
        }
    }

    public InfinispanPrefs(AbstractPreferences parent, String name) {
        super(parent, name);
        if (treeCache.getNode(parent.absolutePath()).getChild(Fqn.fromString(name)) == null) {
            treeCache.getNode(parent.absolutePath()).addChild(Fqn.fromString(name));
        }
    }

    @Override
    protected void putSpi(String key, String value) {
        treeCache.getNode(Fqn.fromString(absolutePath())).put(key, value);
    }

    @Override
    protected String getSpi(String key) {
        return treeCache.getNode(Fqn.fromString(absolutePath())).get(key);
    }

    @Override
    protected void removeSpi(String key) {
        treeCache.getNode(Fqn.fromString(absolutePath())).remove(key);
    }

    @Override
    protected void removeNodeSpi() throws BackingStoreException {
        treeCache.removeNode(Fqn.fromString(absolutePath()));
    }

    @Override
    protected String[] keysSpi() throws BackingStoreException {
        if (treeCache.getNode(Fqn.fromString(absolutePath())) != null) {
            return treeCache.getNode(Fqn.fromString(absolutePath())).getKeys().toArray(new String[0]);
        } else {
            return new String[0];
        }
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    protected String[] childrenNamesSpi() throws BackingStoreException {
        if (treeCache.getNode(Fqn.fromString(absolutePath())) != null) {
            return treeCache.getNode(Fqn.fromString(absolutePath())).getChildrenNames().toArray(new String[0]);
        } else {
            return new String[0];
        }
    }

    @Override
    protected AbstractPreferences childSpi(String name) {
        return new InfinispanPrefs(this, name);
    }

    @Override
    protected void syncSpi() throws BackingStoreException {
        for(AbstractPreferences cacheChild : this.cachedChildren()){
            if (treeCache.getNode(Fqn.fromString(cacheChild.absolutePath())) == null ){
                cacheChild.removeNode();
            }
        }
    }

    @Override
     protected void flushSpi() throws BackingStoreException {
    }

    public static Preferences getInstance() {
        InfinispanPrefs localInstance = instance;
        if (localInstance == null) {
            synchronized (InfinispanPrefs.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new InfinispanPrefs();
                }
            }
        }
        return localInstance;
    }


    public static Cache<String, String> getCache() {
        return cache;
    }

    public static String getCacheName() {
        return cacheName;
    }

}
