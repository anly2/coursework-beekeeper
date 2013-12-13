package coursework;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * A random number generator object
 * @see #number(int, int)
 * @see #chance(int)
 * @see #chance(int[])
 * @see #serialize()
 * @see #deserialize(String)
 */
public class Rand {
	/* Base */
	
    /* A specific initialization of the core 
    protected static long randomSeed = 181783497276652981L;
    private static Random core = new Random (randomSeed);
    //*/ 
	private static Random core = new Random ();

	/* Random Number */
    
    /**
	 * Returns a random number from 0 to the given <code>max</code>. <br />
     * This is just an encapsulated call of {@link Random#nextInt(int)} with max as argument.
     * @param max - the bound on the random number to be returned. Must be positive.
     */
    public static int number (int max) {
    	if (max == 0)
    		return 0;
    	
        return core.nextInt (max);
    }

    /**
	 * Returns a random number in the range from <code>min</code> to <code>max</code>.
     * @param min - the lower bound on the random number to be returned
     * @param max - the upper bound on the random number to be returned
     * @see #number(int)
     */
    public static int number (int min, int max) {
        return (min + Rand.number(max - min));
    }

    /**
     * Alias of {@link #number(int, int)}
     */
    public static int range (int from, int to) {
        return Rand.number(from, to);
    }


    /* Chance */

    /**
	 * Decides if an event should happen or not based on the given <code>probability</code>.
     * @param probability - the probability in percent (from 0 to 100) for the event to happen
     * @return Returns true if the event should happen or false if it should not.
     * @throws RuntimeException if the <code>probability</code> param is not in the range from 0 to 100
     */
    public static boolean chance (int probability) throws RuntimeException {
        if (probability < 0 || probability > 100)
            throw new RuntimeException("Probabilty must be between 0 and 100!"); //unchecked exception

        return Rand.number(100) < probability;
    }

    
    /* Probability Case Selection */

    /**
	 * A method that picks an element from the array based on the value (probability) it has.
     * @param cases An array of integers which represent the probability for that array element to be chosen (to happen)
     * @return Returns an int specifying which of the cases given had the luck to happen.
     * @see #chance(ArrayList)
     * @see #chance(Integer[])
     */
    public static int chance (int[] cases) {
        //Count the sum of the given cases' probability
        int total = 0;
        for (int probability: cases)
            total += probability;

        int chance = Rand.number(total);

        for (int i = 0; i < cases.length; i++)
        {
            if (chance < cases[i])
                return i;
            chance -= cases[i];
        }

        return -1;
    }

    /**
	 * An overload of {@link #chance(int[])} which
     *  manually casts the given <code>Integer[]</code> to <code>int[]</code>
     *  and passes it to {@link #chance(int[])}
     *  @param cases An <code>array</code> of <code>Integer</code>s to be cast to primitive <code>int[]</code>
     *  @return Returns the result of passing the converted array to {@link #chance(int[])}
     *  @see #chance(ArrayList)
     */
    public static int chance (Integer[] cases) {
        int[] arr = new int[cases.length];
        for (int i = 0; i < cases.length; i++)
            arr[i] = (int) cases[i];
        return chance(arr);
    }

    /**
	 * An overload of {@link #chance(int[])} which
     *  casts the given <code>ArrayList&ltObject></code> to <code>Integer[]</code>
     *  and passes it to {@link #chance(Integer[])}.
     *  @param cases An <code>ArrayList</code> of <code>Object</code>s to be converted to an <code>Integer array</code>
     *  @return Returns the result of passing the converted <code>ArrayList</code> to {@link #chance(Integer[])}
     */
    public static int chance (ArrayList<Object> cases) {
        return chance(cases.toArray(new Integer[0]));
    }


	/* Serialization */

	/**
	 * The pattern of a serialized Random Number Generator.
	 * 
	 * <p> Currently the pattern is: {@value #serializedRandPattern} </p>
	 * <p> Please make sure the Common Replacement used in {@link #serialize()} conforms to this pattern when making changes </p>
	 * @see #serialize()
	 * @see #deserialize(String)
	 */
    public static final String serializedRandPattern = "\\s*(rand.*?)\\s*:(.*)"; 
    
    /**
     * The common part of a serialized Random object that is always the same.
     * 
     * <p> It is highly inconvenient to save bytes in a text file.
     * Most of the encodings will mess up some of the bytes
     * and even if they don't it is still gibberish. </p>
     * 
     * <p> Instead we replace this common bit with <b><code>"random seed:"</code></b>
     * followed by the numbers representing the rest of the bytes.</p>
     * 
     * @see #serialize()
     * @see #deserialize(String)
     * @see #encode(byte[])
     * @see #decode(String)
     * @see #serializedRandPattern
     */
    private static final String serializedRandCommon = "-84,-19,0,5,115,114,0,16,106,97,118,97,46,117,116,105,108,46,82,97,110,100,111,109,54,50,-106,52,75,-16,10,83,3,0,3,90,0,20,104,97,118,101,78,101,120,116,78,101,120,116,71,97,117,115,115,105,97,110,68,0,16,110,101,120,116,78,101,120,116,71,97,117,115,115,105,97,110,74,0,4,115,101,101,100,120,112,0,0,0,0,0,0,0,0,0,0,0" + ",";

    
    /**
     * Serializes the Random Number Generator.
     * <br />
     * <br /> Uses the fact that a Random object (like {@link #core})
     * <br /> implements {@link Serializable} and turns it into a stream of bytes.
     * <br /> The bytes are hard to store in a proper text file
     * <br /> so we encode them as a simple string using {@link #encode(byte[])}.
     * <br /> Then we make the {@linkplain #serializedRandCommon common bit} which identifies that it is a Random object
     * <br /> something much shorter and actually readable.
     * <p> Current Common replacement: &nbsp; <b><code>random seed:</code></b>
     * <br /> Please make sure the Common Replacement conforms to {@link #serializedRandPattern} when making changes </p>
     * @return Returns a String representation of the current state of this Random Number Generator
     * @see #encode(byte[])
     * @see #deserialize(String)
     */
    public static String serialize () {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            
            ObjectOutputStream objectStream = new ObjectOutputStream( byteStream );
            objectStream.writeObject (Rand.core);
            objectStream.close();
            
            String serialized = encode(byteStream.toByteArray());            
            serialized = serialized.replace(serializedRandCommon, "random seed:"); //replacement must conform to #serializedRandPattern
            
            return serialized;
        }
        catch (IOException e) {
            System.err.println("Unable to serialize the Random Number Generator Object");
        }
        return null;
    }

	/**
	 * Tries to deserialize the given stream
	 * <br />
	 * <br /> Replaces the {@linkplain #serializedRandPattern Common Replacement} with the proper data for denoting a serialized Random object
	 * <br /> Decodes the string into an array of bytes using {@link #decode(String)}
	 * <br /> If something wrong was decoded {@link #decode(String) decode()} will throw a {@link NumberFormatException}
	 * <br /> In that case the stream given is not a serialized {@link Rand}
	 * <br /> If decoding passed successfully, do the actual deserialization of the ByteStream.
	 * <br /> If there is something wrong a {@link ClassNotFoundException} will be thrown and caught
	 * <br /> In that case the stream given is not a serialized {@link Rand}
	 * <br /> If we reached the end then we have a Random object.
	 * <br /> Now just store it in {@link #core}
	 * @param stream - the stream to deserialize
	 * @throws NoSerializedFound if {@link #serializedRandPattern} matched some corrupted data or did not match at all
	 * @see #decode(String)
	 * @see #serialize()
	 * @see #serializedRandPattern
	 * @see #serializedRandCommon
	 */
    public static void deserialize (String stream) throws NoSerializedFound {
		stream = serializedRandCommon + stream.replaceAll(serializedRandPattern, "$2");
		byte[] data;
		
		try {
    		data = decode (stream);
		}
		catch (NumberFormatException e) {
			throw new NoSerializedFound ("Rand (Random)"); 
		}

        try {
            ObjectInputStream objectStream = new ObjectInputStream (new ByteArrayInputStream (data));
            Rand.core  = (Random) objectStream.readObject();
            objectStream.close();
        }
        catch (ClassNotFoundException e) {
            throw new NoSerializedFound ("Rand (Random)");
        }
        catch (IOException e) {
        	System.err.println(e);
        }
    }
    
    
	/**
	 * Encodes all the given bytes sequentially into a String
	 * <br />
	 * <br />Simply turns all the bytes into a numerical string representing their value
	 * <br />Then joins them into one string using "," (comma) as delimiter
	 * @param args - a number of byte arrays to encode
	 * @return Returns a concatenation of all the values of the given bytes as numerical strings.
	 * @see #decode(String)
	 * @see #serialize()
	 */
    public static String encode (byte[]... args) {
    	String encoded = "";
    	
    	for (byte[] arr: args)
    		for (byte b: arr)
    			encoded += b +",";
    	
    	encoded = encoded.substring(0, encoded.length()-1); //trim the last comma
    	
    	return encoded;
    }

	/**
	 * Tries to decode the given string into an array of bytes.
	 * <br />
	 * <br /> Splits the string using "," (comma) as delimiter
	 * <br /> Then tries to convert each segment into a byte with that numerical value
	 * @param encoded - the string to decode into bytes
	 * @return Returns an array of bytes
	 * @throws NumberFormatException if something was wrong with the given string
	 * @see #encode(byte[])
	 * @see #deserialize(String)
	 */
    public static byte[] decode (String encoded) throws NumberFormatException {
    	String[] split = encoded.split(",");
    	byte[] arr = new byte[split.length];
    	
    	for (int i = 0; i < split.length; i++)
    		arr[i] = (byte) new Byte (split[i]);
    	
    	return arr;
    }
}