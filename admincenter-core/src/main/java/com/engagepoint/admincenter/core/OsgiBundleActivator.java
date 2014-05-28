package com.engagepoint.admincenter.core;

import com.engagepoint.admincenter.core.infinispan.InfinispanPrefs;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

import java.util.prefs.Preferences;

public class OsgiBundleActivator implements BundleActivator,ServiceListener {

    Preferences preferences;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        bundleContext.addServiceListener(this);
        preferences = Preferences.userRoot();
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        bundleContext.removeServiceListener(this);
    }

    @Override
    public void serviceChanged(ServiceEvent serviceEvent) {

    }
}
