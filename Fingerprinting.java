import com.planetj.math.rabinhash.*;

/** Takes in a single packet chunk and creates the fingerprints of the chunk.
 * We use the RabinHashFunction32 implementation on http://rabinhash.sourceforge.net/ for the actual computation of the fingerprint
 *
 *@author Marcela S. Melara
 *@since 14 April 2013
 */
public class Fingerprinting{

    /** Creates the 32-bit Rabin fingerprint of a given chunk using the default hash function
     * given in the RabinHashFunction32 class. 
     * In this case, one chunk is represented as an array of ints, where each int contains 4 packet bytes.
     */
    public static int fingerprint(int[] chunk){

	return RabinHashFunction32.DEFAULT_HASH_FUNCTION.hash(chunk);	

    } //ends fingerprint()

} //ends Fingerprinting class
