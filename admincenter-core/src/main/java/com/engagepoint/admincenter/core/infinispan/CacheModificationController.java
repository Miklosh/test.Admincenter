package com.engagepoint.admincenter.core.infinispan;

import java.util.HashMap;
import java.util.Map;

public final class CacheModificationController {
	
	private static Map<String,CacheModificationController> instances = new HashMap<String,CacheModificationController>();
	
	private CacheModificationExecutor cacheModificationExecutor;  
	
    //Marking default constructor private 
    //to avoid direct instantiation.
    private CacheModificationController() {
    }
 
    //Get instance for class SimpleSingleton
    public synchronized static CacheModificationController getInstance(String cacheName, CacheModificationExecutor cacheModificationExecutor) {
    	
    	if(instances.get(cacheName)==null){
    		instances.put(cacheName, new CacheModificationController());
    	}
    	
    	if(instances.get(cacheName).cacheModificationExecutor==null){
			instances.get(cacheName).cacheModificationExecutor = cacheModificationExecutor;
		}
    	
    	return instances.get(cacheName);

    }
    
    public void setCacheModificationExecutor(CacheModificationExecutor cacheModificationExecutor){
    	this.cacheModificationExecutor = cacheModificationExecutor;
    }
    
    public CacheModificationExecutor getCacheModificationExecutor(){
    	return this.cacheModificationExecutor;
    }
    
    public void setExecutorProperties(Object[] properties){
    	cacheModificationExecutor.setProperties(properties);
    }
    
}
