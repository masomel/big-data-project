package caching;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import chunking.Chunk;

public class LRUCache implements ICache {

	private final int size;
	private int capacity;
	private long time;
	private boolean isMiss;
	
	private Map<Integer, Chunk> cache;
	private Map<Integer, Long> usage;

	public LRUCache(int capacity) {
		this.cache = new HashMap<Integer, Chunk>();
		this.usage = new HashMap<Integer, Long>();
		this.capacity = capacity;
		this.size = capacity;
		this.time = 0;
		this.isMiss = false;
	}

	@Override
	public Chunk get(int fingerprint) {
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
				cache.remove(getLRU());
				cache.put(fingerprint, chunk);
			}
			isMiss = true;
		}
		else {
			adjustUsage(fingerprint);
			isMiss = false;
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
	
	private int getLRU() {
		if (usage.size() > 0) {
			long minn = Long.MAX_VALUE;
			int result = -1;
			for (Entry<Integer, Long> e : usage.entrySet()) {
				if (minn >= e.getValue()) {
					minn = e.getValue();
					result = e.getKey();
				}
			}
			return result;
		}
		else {
			return 0;
		}
	}
	
	private void adjustUsage(int fp) {
		if (time < Long.MAX_VALUE - 1) {
			time++;
			usage.put(fp, time);
		}
		else {
			time = 0;
			Map<Integer, Long> new_usage = new HashMap<Integer, Long>();
			for (Entry<Integer, Long> e : usage.entrySet()) {
				Long val;
				if ((e.getValue() - Long.MIN_VALUE) >= (Long.MAX_VALUE / 2)) {
					val = e.getValue() - Long.MAX_VALUE / 2;
				}
				else {
					val = Long.MIN_VALUE;
				}
				new_usage.put(e.getKey(), val);
			}
			new_usage.put(fp, time);
			usage = new_usage;
		}
	}
}
