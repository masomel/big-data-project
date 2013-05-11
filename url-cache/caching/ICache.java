package caching;

import chunking.Chunk;

/**
 * Interface specifying the functions of a cache.
 * @author Nayden Nedev, Marcela Melara
 */
public interface ICache {
    
    /**
     * Gets the chunk in the cache that corresponds to the given fingerprint. Returns null if
     * the item is not in the cache.
     * @param fingerprint the fingerprint that is checked for its value in the cache
     * @return the corresponding chunk of the given fingerprint and null if it is not cached
     */
    public Chunk get(int fingerprint);
    
    public Chunk remove(int fp);
    
    /**
     * Put a given chunk in the cache 
     * @param fingerprint an integer representing 
     * @param chunk the corresponding chunk to the given fingerprint that needs to be cached, 
     *        represented by a Chunk object
     */
    public void put(int fingerprint, Chunk chunk);
    
    /**
     * Was the last chunk put into the cache a miss?
     * @return true if the chunk was put into the cache, false otherwise.
     */
    public boolean isMiss();
    
    /**
     * Gets the current capacity of the cache.
     * @return an integer representing the capacity of the cache
     */
    public int getCapacity();
    
    /**
     * Gets the size current size of the cache.
     * @return an integer representing the current size of the cache
     */
    public int getSize();
}
