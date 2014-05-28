package com.engagepoint.admincenter.core.infinispan;

public interface CacheModificationExecutor {
	 boolean cacheEntryCreated();
	 boolean cacheEntryRemoved();
	 boolean cacheEntryModified();
	 void setProperties(Object[] properties);
}
