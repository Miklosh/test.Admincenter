package com.engagepoint.admincenter.core.infinispan.loader;

import com.engagepoint.admincenter.core.infinispan.AbstractCacheManagerFactory;
import com.engagepoint.admincenter.core.infinispan.ConfigUtils;
import com.engagepoint.admincenter.core.infinispan.InfinispanPrefs;

import org.infinispan.Cache;

import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.tree.Fqn;
import org.infinispan.tree.Node;
import org.infinispan.tree.TreeCache;
import org.infinispan.tree.TreeCacheFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.infinispan.util.logging.Log;
import org.infinispan.util.logging.LogFactory;

/**
 * User: dmytro.palczewski
 * Date: 1/10/14
 * Time: 12:07 PM
 * <p/>
 * Uses two properties files in the same directory as the <code>loadInitialConfig</code> script itself:<p/>
 * <code>engagepoint-service.properties</code> containing keys with corresponding text values<br/>
 * <code>engagepoint-service-binary.properties</code> containing keys with paths to files with binary data
 * <p/>
 * If You want to use files with different names or in different directories then use:<br/>
 * <code>loadInitialConfig -Dprops=[properties filepath] -Dbinary-props=[binary properties filepath]</code>
 */
public class InitialConfigLoader {

    private static final Log LOG = LogFactory.getLog(InitialConfigLoader.class);

    public static final String DEFAULT_PROPERTIES_FILENAME = "./engagepoint-service.properties";

    public static final String DEFAULT_BINARY_PROPERTIES_FILENAME = "./engagepoint-service-binary.properties";

    public static final String DEFAULT_CONFIG_NODE = "/com/engagepoint/architectSuite";

    public static final Fqn FQN_COM = Fqn.fromString("com");
    public static final Fqn FQN_ENGAGEPOINT = Fqn.fromString("engagepoint");
    public static final Fqn FQN_ARCHITECT_SUITE = Fqn.fromString("architectSuite");

    public static void main(String[] args) {

        if (args.length == 1 && args[0].equals("-?")) {
            return;
        }

        String propsFilepath;
        String binaryPropsFilepath;

        propsFilepath = System.getProperty("props", DEFAULT_PROPERTIES_FILENAME);
        binaryPropsFilepath = System.getProperty("binary-props", DEFAULT_BINARY_PROPERTIES_FILENAME);

        try {
            Properties props = loadProperties(propsFilepath);
            Properties binaryProps = loadProperties(binaryPropsFilepath);

            InitialConfigLoader configLoader = new InitialConfigLoader();
            configLoader.loadConfig(props, binaryProps);
        } catch (Exception e) {
            LOG.infof("",e);
        }

    }

    public void loadConfig(Properties initProps, Properties binaryInitProps) {

        EmbeddedCacheManager embeddedCacheManager = AbstractCacheManagerFactory.createInitialConfigLoaderCacheManager();
        Cache cache = embeddedCacheManager.getCache(InfinispanPrefs.getCacheName());
        TreeCacheFactory treeCacheFactory = new TreeCacheFactory();
        TreeCache treeCache = treeCacheFactory.createTreeCache(cache);
        try {

            treeCache.start();
            Node treeCacheNode = treeCache.getNode(Fqn.fromString(DEFAULT_CONFIG_NODE));
            if (treeCacheNode == null) {
                treeCacheNode = treeCache.getRoot().addChild(FQN_COM).addChild(FQN_ENGAGEPOINT).addChild(FQN_ARCHITECT_SUITE);
            }

            for (String key : initProps.stringPropertyNames()) {
                String value = initProps.getProperty(key);
                treeCacheNode.put(key, value);
            }
            /*
            for (String key : binaryInitProps.stringPropertyNames()) {
                String filePath = binaryInitProps.getProperty(key);
                ConfigUtils.putBinaryConfigOption(key, filePath, cache);
            }
            */

        } finally {
            treeCache.stop();
        }
    }

    private static Properties loadProperties(String propsFilepath) {

        Properties props = null;
        InputStream in = null;

        try {
            props = new Properties();
            in = new FileInputStream(propsFilepath);
            props.load(in);
        } catch (FileNotFoundException e) {
            LOG.infof("",e);
        } catch (IOException e) {
            LOG.infof("",e);
        } finally {
            assert in != null;
            try {
                in.close();
            } catch (IOException e) {
                LOG.infof("",e);
            }
        }
        return props;
    }
}
