package it.eng.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;

public class EndpointManager {

	public static void put(String serviceName, String url, long expire) throws Exception {
		CachedEndpoint ep = new CachedEndpoint();
		long t = expire>0 ? expire : Long.MAX_VALUE;
		ep.expire = t;
		ep.name = serviceName;
		ep.url = url;

		String cacheKey = serviceName+":"+url;
		if (cache.containsKey(cacheKey))
			cache.remove(cacheKey);
		cache.put(cacheKey,  ep);
		cache.expireAt(cacheKey, t);
	}
	
	public static List<String> get(String serviceName) {
		List<String> ret = new ArrayList<String>();
		ConcurrentMap<String, CachedEndpoint> map = cache.asMap();
		for(CachedEndpoint ep: map.values()) {
			if (ep.name.equals(serviceName))
				ret.add(ep.url);
		}
		return ret;
	}
	
	public static Map<String, List<String>> getAll() {
		Map<String, List<String>> ret = new HashMap<String, List<String>>();
		ConcurrentMap<String, CachedEndpoint> map = cache.asMap();
		for(CachedEndpoint ep: map.values()) {
			if (!ret.containsKey(ep.name))
				ret.put(ep.name, new ArrayList<String>());
			List<String> list = ret.get(ep.name);
			if (!list.contains(ep.url))
				list.add(ep.url);
		}
		return ret;
	}
	
	static {
		init();
	}
	
	private static Cache<String, CachedEndpoint> cache;
	
	
	private static void init() {
		cache = new Cache2kBuilder<String, CachedEndpoint>() {}
			.name("unverifiedTokens")
			.expireAfterWrite(5, TimeUnit.MINUTES)
			.build();
	}
	
	public static class CachedEndpoint {
		public String name;
		public String url;
		public long expire;
	}
}
