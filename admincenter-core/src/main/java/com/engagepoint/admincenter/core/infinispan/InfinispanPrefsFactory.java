package com.engagepoint.admincenter.core.infinispan;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

public class InfinispanPrefsFactory implements PreferencesFactory {
    private PreferencesFactory defaultFactory;
    private PreferencesChooser preferencesChooser;

    public InfinispanPrefsFactory() {
        preferencesChooser = new PreferencesChooser();
        preferencesChooser.setInfinispanPreferences(InfinispanPrefs.getInstance());
    }

    public void setDefaultFactory(PreferencesFactory defaultFactory) {
        //this trick is used cause we have different class loaders
        if (this.getClass().getCanonicalName().equals(defaultFactory.getClass().getCanonicalName())) {
            Method m = null;
            try {
                m = defaultFactory.getClass().getDeclaredMethod("getDefaultFactory");
                this.defaultFactory = (PreferencesFactory) m.invoke(defaultFactory);
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(e);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException(e);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        } else {
            this.defaultFactory = defaultFactory;
        }
    }

    public PreferencesFactory getDefaultFactory() {
        return defaultFactory;
    }

    public Preferences userRoot() {
        if (defaultFactory != null) {
            preferencesChooser.setDefaultUserRootPreferences(defaultFactory.userRoot());
        }
        return preferencesChooser;
    }

    public Preferences systemRoot() {
        if (defaultFactory != null) {
            preferencesChooser.setDefaultSystemRootPreferences(defaultFactory.systemRoot());
        }
        return preferencesChooser;
    }

}
