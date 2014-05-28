package com.engagepoint.admincenter.core.infinispan;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
import org.infinispan.notifications.cachelistener.annotation.DataRehashed;
import org.infinispan.notifications.cachelistener.annotation.TopologyChanged;
import org.infinispan.notifications.cachelistener.annotation.TransactionCompleted;
import org.infinispan.notifications.cachelistener.annotation.TransactionRegistered;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;
import org.infinispan.notifications.cachelistener.event.DataRehashedEvent;
import org.infinispan.notifications.cachelistener.event.TopologyChangedEvent;
import org.infinispan.notifications.cachelistener.event.TransactionCompletedEvent;
import org.infinispan.notifications.cachelistener.event.TransactionRegisteredEvent;
import org.infinispan.notifications.cachemanagerlistener.annotation.CacheStarted;
import org.infinispan.notifications.cachemanagerlistener.annotation.CacheStopped;
import org.infinispan.notifications.cachemanagerlistener.annotation.Merged;
import org.infinispan.notifications.cachemanagerlistener.annotation.ViewChanged;
import org.infinispan.notifications.cachemanagerlistener.event.CacheStartedEvent;
import org.infinispan.notifications.cachemanagerlistener.event.CacheStoppedEvent;
import org.infinispan.notifications.cachemanagerlistener.event.MergeEvent;
import org.infinispan.notifications.cachemanagerlistener.event.ViewChangedEvent;
import org.infinispan.util.logging.Log;
import org.infinispan.util.logging.LogFactory;

/**
 * An Infinispan listener that logs cache entries being created and
 * removed and call the executor in case it has been configured
 *
 * @author Luis Cifuentes
 *
 */
@Listener (sync = false)
public class CacheModificationListener {

   private static final Log LOG = LogFactory.getLog(CacheModificationListener.class);

   private CacheModificationController cacheModificationController;
   private boolean existExecutor = false;

   public CacheModificationListener(CacheModificationController cacheModificationController){
	   this.cacheModificationController = cacheModificationController;
	   existExecutor = cacheModificationController!=null && cacheModificationController.getCacheModificationExecutor()!=null;
   }

   @CacheEntryCreated
   public void observeAdd(CacheEntryCreatedEvent<?, ?> event) {
         LOG.infof(" Cache entry with key %s added in cache %s", event.getKey(), event.getCache());
         if(existExecutor){
           	try {
           		cacheModificationController.getCacheModificationExecutor().cacheEntryCreated();
    		} catch (Exception e) {
    			LOG.error("ERROR @CacheEntryCreated ", e);
    		}
         }
   }
   @CacheEntryRemoved
   public void observeRemove(CacheEntryRemovedEvent<?, ?> event) {
      LOG.infof(" Cache entry with key %s removed in cache %s", event.getKey(), event.getCache());
      if(existExecutor){

      	try {
      		cacheModificationController.getCacheModificationExecutor().cacheEntryRemoved();
		} catch (Exception e) {
			LOG.error("ERROR @CacheEntryRemoved ", e);
		}
      }
   }
   @CacheEntryModified
   public void observeModified(CacheEntryModifiedEvent<?, ?> event) {
      LOG.infof(" Cache entry with key %s modified in cache %s", event.getKey(), event.getCache());
      if(existExecutor){
    	try {
        	  cacheModificationController.getCacheModificationExecutor().cacheEntryModified();
		} catch (Exception e) {
			LOG.error("ERROR @CacheEntryModified ", e);
		}
      }
   }

   @ViewChanged
   public void observeViewChanged(ViewChangedEvent event) {
      LOG.infof(" Cache entry View Changed ");
   }
   @DataRehashed
   public void observeDataRehashed(DataRehashedEvent event) {
      LOG.infof(" Cache entry Data Rehashed ");
   }
   @TopologyChanged
   public void observeTopologyChanged(TopologyChangedEvent event) {
      LOG.infof(" Cluster Topology Changed ");
   }

   @CacheStarted
   public void observeCacheStarted(CacheStartedEvent event) {
      LOG.infof(" Cache started cache ");
   }
   @CacheStopped
   public void observeCacheStopped(CacheStoppedEvent event) {
      LOG.infof(" CacheStopped cache ");
   }
   @Merged
   public void observeCacheStarted(MergeEvent event) {
      LOG.infof(" Cache merged ");
   }



    public boolean isExistExecutor() {
        return existExecutor;
    }

    public void setExistExecutor(boolean existExecutor) {
        this.existExecutor = existExecutor;
    }

    public CacheModificationController getCacheModificationController() {
        return cacheModificationController;
    }

    public void setCacheModificationController(CacheModificationController cacheModificationController) {
        this.cacheModificationController = cacheModificationController;
    }
}
