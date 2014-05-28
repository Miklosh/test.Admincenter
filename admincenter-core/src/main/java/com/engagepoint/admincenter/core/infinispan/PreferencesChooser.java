package com.engagepoint.admincenter.core.infinispan;


import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;


public class PreferencesChooser extends AbstractPreferences {

    private static final String COM_ENGAGEPOINT_NODE_PATH = "/com/engagepoint";

    private Preferences defaultUserRootPreferences;
    private Preferences defaultSystemRootPreferences;
    private Preferences infinispanPreferences;
    private String nodesPath = this.absolutePath();

    public PreferencesChooser() {
        super(null, "");
    }

    public PreferencesChooser(AbstractPreferences parent, String name) {
        super(parent, name);
    }

    public void setDefaultUserRootPreferences(Preferences defaultUserRootPreferences) {
        this.defaultUserRootPreferences = defaultUserRootPreferences;
    }

    public void setDefaultSystemRootPreferences(Preferences defaultSystemRootPreferences) {
        this.defaultSystemRootPreferences = defaultSystemRootPreferences;
    }

    public void setInfinispanPreferences(Preferences infinispanPreferences) {
        this.infinispanPreferences = infinispanPreferences;
    }

    @Override
    protected void putSpi(String key, String value) {
        if (nodesPath.startsWith(COM_ENGAGEPOINT_NODE_PATH)) {
            infinispanPreferences.put(key, value);
        } else {
            if (defaultUserRootPreferences != null) {
                defaultUserRootPreferences.put(key, value);
            } else {
                infinispanPreferences.put(key, value);
            }
        }
    }

    @Override
    protected String getSpi(String key) {
        if (nodesPath.startsWith(COM_ENGAGEPOINT_NODE_PATH)) {
            return infinispanPreferences.get(key, null);
        } else {
            if (defaultUserRootPreferences != null) {
                return defaultUserRootPreferences.get(key, null);
            } else {
                return infinispanPreferences.get(key, null);
            }

        }

    }

    @Override
    protected void removeSpi(String key) {
        if (nodesPath.startsWith(COM_ENGAGEPOINT_NODE_PATH)) {
            infinispanPreferences.remove(key);
        } else {
            if (defaultUserRootPreferences != null) {
                defaultUserRootPreferences.remove(key);
            } else {
                infinispanPreferences.remove(key);
            }
        }
    }

    @Override
    protected void removeNodeSpi() throws BackingStoreException {
        if (nodesPath.startsWith(COM_ENGAGEPOINT_NODE_PATH)){
            infinispanPreferences.removeNode();
        } else {
            if (defaultUserRootPreferences != null) {
                defaultUserRootPreferences.removeNode();
            }
        }
    }

    @Override
    protected String[] keysSpi() throws BackingStoreException {
        if (nodesPath.startsWith(COM_ENGAGEPOINT_NODE_PATH)) {
            return infinispanPreferences.keys();
        } else {
            if (defaultUserRootPreferences != null) {
                return defaultUserRootPreferences.keys();
            } else {
                return infinispanPreferences.keys();
            }
        }
    }

    @Override
    protected String[] childrenNamesSpi() throws BackingStoreException {
        if (nodesPath.startsWith(COM_ENGAGEPOINT_NODE_PATH)) {
            return infinispanPreferences.childrenNames();
        } else {
            if (defaultUserRootPreferences != null) {
                return defaultUserRootPreferences.childrenNames();
            } else {
                return infinispanPreferences.childrenNames();
            }
        }
    }

    PreferencesChooser newGeneration;

    private AbstractPreferences instantiateNewGeneration(String name) {
        newGeneration = new PreferencesChooser(this, name);
        newGeneration.setInfinispanPreferences(infinispanPreferences.node(name));
        if (defaultUserRootPreferences != null) {
            newGeneration.setDefaultUserRootPreferences(defaultUserRootPreferences.node(name));
        }
        return newGeneration;
    }

    @Override
    protected AbstractPreferences childSpi(String name) {

        if (nodesPath.startsWith(COM_ENGAGEPOINT_NODE_PATH)) {
            infinispanPreferences.node(name);
        } else {
            if (defaultUserRootPreferences != null) {
                defaultUserRootPreferences.node(name);
            } else {
                infinispanPreferences.node(name);
            }
        }
        return instantiateNewGeneration(name);
    }

    @Override
    protected void syncSpi() throws BackingStoreException {
    }

    @Override
    protected void flushSpi() throws BackingStoreException {
    }

}