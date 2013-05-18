package caching;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import chunking.Chunk;

public class RandomCache implements ICache {

	private Map<Integer, Chunk> cache;
	private final int size;
	private int capacity;
	private boolean isMiss;

	public RandomCache(int capacity) {
		this.capacity = capacity;
		this.size = 0;
		this.isMiss = false;
		this.cache = new HashMap<Integer, Chunk>();
	}

	@Override
	public Chunk get(int fingerprint) {
		return cache.get(fingerprint);
	}

	@Override
	public void put(int fingerprint, Chunk chunk) {
		isMiss = false;
		// if we don't have it in the cache - put it
		if (!cache.containsKey(fingerprint)) {
			if (capacity > 0) {
				cache.put(fingerprint, chunk);
				capacity--;
			}
			else {
				// choose a random element to replace
				Random rand = new Random();
				rand.setSeed(100);
				cache.remove((Integer)(cache.keySet().toArray())[rand.nextInt(cache.keySet().size())]);
				cache.put(fingerprint, chunk);
			}
			isMiss = true;
		}	
		// otherwise do nothing
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
}
