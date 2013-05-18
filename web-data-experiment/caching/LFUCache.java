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
		this.usage = new HashMap<Integer, Long>();
	}

	@Override
	public Chunk get(int fingerprint) {
		Long u = usage.get(fingerprint);
		if (u != null) {
			usage.put(fingerprint, usage.get(fingerprint) + 1);
		} else {
			usage.put(fingerprint, Long.valueOf(1));
		}
		return cache.get(fingerprint);
	}

	@Override
	public void put(int fingerprint, Chunk chunk) {
		isMiss = false;
		if (!cache.containsKey(fingerprint)) {
			if (capacity > 0) {
				cache.put(fingerprint, chunk);
				capacity--;
			}
			else {
				cache.remove(getLFU());
				cache.put(fingerprint, chunk);
			}
			isMiss = true;
		}
		else {
			Long u = usage.get(fingerprint);
			if (u != null) {
				usage.put(fingerprint, u + 1);
			} else {
				usage.put(fingerprint, Long.valueOf(1));
			}
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
