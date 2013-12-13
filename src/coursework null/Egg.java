package coursework;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An object representing a {@link Bee} Egg.
 * @see #Egg()
 * @see #anotherDay()
 * @see #serialize()
 * @see #deserialize(String)
 */
class Egg extends Bee
{
	/* Constructors */

	/**
	 * Initializes a new {@link Bee} and sets the properties of an Egg
	 * @see Bee#Bee()
	 * @see Bee#hive
	 * @see Bee#typeID
	 * @see Bee#name
	 * @see Bee#age
	 * @see Bee#minAge
	 * @see Bee#maxAge 
	 * @see Bee#eatenFood
	 * @see Bee#eatenFoodQuantity
	 */
	Egg () {
		super();

		typeID =  (int) Config.get("egg type id");;
		name = (String) Config.get("egg type name");

		age = minAge = (int) Config.get("egg age min");
		maxAge = (int) Config.get("egg age max");

		/* Unneeded:
		eatenFood = Food.byName((String) Config.get("egg eaten food type")); // Error Eater; Is irrelevant because eatenQuantity is 0
		eatenFoodQuantity = (int) Config.get("egg eaten food quantity"); 
		 */

		hive = null;
	}

	/**
	 * Initializes an Egg and assigns it to a given Hive 
	 * @see #Egg()
	 * @see Bee#hive
	 */
	Egg (Hive h) {
		this ();
		hive = h;
	}


	/* Timeflow Methods */

	/**
	 * Eat yolk and do nothing cause Eggs are like that
	 * @return always true
	 * @see Bee#eat()
	 * @see #anotherDay()
	 */
	public boolean eat () {
		//Do nothing (Eat yolk)
		return true;
	}

	/**
	 * Simulates a day for the Egg.
	 * 
	 * <ul>
	 * 		Every day the Egg
	 *  	<li> gets older by a day (duh) (See {@link Bee#age}), </li>
	 *  	<li> does NOT really eat (See {@link #eat()}), </li>
	 *  	<li> hatches if is old enough. There is a chance it may hatch dead. </li>
	 * </ul>
	 *  
	 * <p> Please see &nbsp; {@link default.cfg :: chance _ death _ egg} </p>
	 *  
	 * @return the Egg itself,
	 *  <br /> a new Larvae if it hatched,
	 *  <br /> or null if it died.
	 */
	public Bee anotherDay ()
	{
		//Time goes by...
		age++;

		//If the egg is not old enough to hatch, return itself
		if (age <= maxAge)
			return this;

		//If the egg is about to hatch, see if fate isn't against it
		if (Rand.chance((int) Config.get("chance death egg")))
			return null;

		//If the egg hatched return Larvae it turned into
		return new Larvae(this.hive);
	}


	/* Serialization */
	
	/**
	 * Serializes this {@link Egg}
	 * <br />
	 * <br /> Format: &nbsp; <b><code>egg: %health% , %age%</code></b>
	 * <br /> <b>Note</b> the format must conform to {@value #serializedEggPattern}
	 * @see #serializedEggPattern
	 * @see #deserialize(String)
	 * @see Bee#serializationSpacing
	 */	
	public String serialize () {
		String stream = "";

		stream += "egg" + ":";
		stream +=       serializationSpacing + health;
		stream += "," + serializationSpacing + age;

		return stream;
	}

	
	/* Deserialization */
	
	/**
	 * The pattern of a serialized Egg
	 * <p> Currently: {@value #serializedEggPattern} </p>
	 * @see #serialize()
	 * @see #deserialize(String)
	 */
	public static final String serializedEggPattern = "\\s*(egg)\\s*:\\s*(\\d+)\\s*,\\s*(\\d+).*";
	
	/**
	 * Tries to deserialize the given stream
	 * @param stream - the stream to deserialize
	 * @return Returns a {@link Egg} (if a serialized one is found)
	 * @throws NoSerializedFound if no serialized Egg was recognized
	 * @see #serializedEggPattern
	 * @see #serialize()
	 */
	public static Egg deserialize (String stream) throws NoSerializedFound {
		Matcher matcher = Pattern.compile(serializedEggPattern).matcher(stream);

		if (!matcher.find(0))
			throw new NoSerializedFound("Egg (Bee)");

		Egg bee = new Egg();


		try {
			if (matcher.group(2) == null)
				throw new Exception ("Parameter for Health of Egg (Bee) not found!");

			Integer health = new Integer (matcher.group(2));
			//The NumberFormatException falls in the same category as other exceptions

			bee.health = health;
		}
		catch (Exception e) {
			if (!allowDefaults)
				throw new NoSerializedFound("Egg (Bee)", stream);
		}


		try {
			if (matcher.group(3) == null)
				throw new Exception ("Parameter for Age of Egg (Bee) not found!");

			Integer age = new Integer (matcher.group(3));
			//The NumberFormatException falls in the same category as other exceptions

			bee.age = age;
		}
		catch (Exception e) {
			if (!allowDefaults)
				throw new NoSerializedFound("Egg (Bee)", stream);
		}


		return bee;
	}
}