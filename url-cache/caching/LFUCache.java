package caching;

import java.io.ObjectInputStream.GetField;
import java.util.HashMap;
import java.util.Map;

import chunking.Chunk;

public class LFUCache implements ICache {

	private final int size;
	private int capacity;
	private boolean isMiss;

	private Map<Integer, Chunk> cache;
	private Map<Integer, Long> usage;

	public LFUCache(int capacity) {
		this.capacity = capacity;
		this.size = capacity;
		this.isMiss = false;
		this.cache = new HashMap<Integer, Chunk>();
	}

	@Override
	public Chunk get(int fingerprint) {
		usage.put(fingerprint, usage.get(fingerprint) + 1);
		return cache.get(fingerprint);
	}

	@Override
	public void put(int fingerprint, Chunk chunk) {
		if (!cache.containsKey(fingerprint)) {
			if (capacity > 0) {
				cache.put(fingerprint, chunk);
				capacity--;
			}
			else {
				cache.remove(getLFU());
				cache.put(fingerprint, chunk);
			}
		}
		else {
			usage.put(fingerprint, usage.get(fingerprint) + 1);
		}
	}

	@Override
	public boolean isMiss() {
		return isMiss;
	}

	@Override
	public int getCapacity() {
		return capacity;
	}

	@Override
	public int getSize() {
		return size;
	}
	
	private Integer getLFU() {
		Integer resulti = -1;
		Long result = Long.MAX_VALUE;
		for (Integer i : usage.keySet()) {
			Long u = usage.get(i);
			if (result > u) {
				resulti = i;
				result = u;
			}
		}
		return resulti;
	}
}
