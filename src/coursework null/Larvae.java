package coursework;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An object representing a {@link Bee} Larvae.
 * @see #Larvae()
 * @see #anotherDay()
 * @see #serialize()
 * @see #deserialize(String)
 */
class Larvae extends Bee
{
	/* Constructors */

	/**
	 * Initializes a new {@link Bee} and sets the properties of a Larvae
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
	Larvae () {
		super();
		
		typeID = (int) Config.get("larvae type id");
		name =  (String) Config.get("larvae type name");;

		age = minAge = (int) Config.get("larvae age min");
		maxAge = (int) Config.get("larvae age max");
 
		eatenFood = Food.byName((String) Config.get("larvae eaten food type"));
		eatenFoodQuantity = (int) Config.get("larvae eaten food quantity");
		
		hive = null;	
	}

	/**
	 * Initializes a Larvae and assigns it to a given Hive 
	 * @see #Larvae()
	 * @see Bee#hive
	 */
	Larvae (Hive h) {
		this ();
		hive = h;
	}
	

	/* Timeflow Methods */
	
	//public eat () is defined in the Bee class
	
	/**
	 * Simulates a day for the Larvae.
	 * 
	 * <ul>
	 * 		Every day the Larvae
	 *  	<li> gets older by a day (duh) (See {@link Bee#age}), </li>
	 *  	<li> eats and heals if possible (See {@link Bee#eat() eat()} and {@link Bee#health health}), </li>
	 *  	<li> evolves if is old enough. </li>
	 * </ul>
	 *  
	 * @return Returns the Larvae itself,
	 *  <br /> a new Pupa if it evolved,
	 *  <br /> or null if it died.
	 */
	public Bee anotherDay ()
	{
		//Time goes by...
		age++;
		
		//Try to eat
		try {
			eat();
		}
		catch (BeeDeath e) {
			return null;
		}
		
		//If the larvae is not old enough, return itself
		if (age <= maxAge)
			return this;
		
		//If the larvae is mature return the Pupa it turned into
		return new Pupa(this.hive);
	}


	/* Serialization */
	
	/**
	 * Serializes this {@link Larvae}
	 * <br />
	 * <br /> Format: &nbsp; <b><code>larvae: %health% , %age%</code></b>
	 * <br /> <b>Note</b> the format must conform to {@value #serializedLarvaePattern}
	 * @see #serializedLarvaePattern
	 * @see #deserialize(String)
	 * @see Bee#serializationSpacing
	 */	
	public String serialize () {
		String stream = "";

		stream += "larvae" + ":";
		stream +=       serializationSpacing + health;
		stream += "," + serializationSpacing + age;

		return stream;
	}

	
	/* Deserialization */
	
	/**
	 * The pattern of a serialized Larvae
	 * <p> Currently: {@value #serializedLarvaePattern} </p>
	 * @see #serialize()
	 * @see #deserialize(String)
	 */
	public static final String serializedLarvaePattern = "\\s*(larvae)\\s*:\\s*(\\d+)\\s*,\\s*(\\d+).*";
	
	/**
	 * Tries to deserialize the given stream
	 * @param stream - the stream to deserialize
	 * @return Returns a {@link Larvae} (if a serialized one is found)
	 * @throws NoSerializedFound if no serialized Larvae was recognized
	 * @see #serializedLarvaePattern
	 * @see #serialize()
	 */
	public static Larvae deserialize (String stream) throws NoSerializedFound {
		Matcher matcher = Pattern.compile(serializedLarvaePattern).matcher(stream);

		if (!matcher.find(0))
			throw new NoSerializedFound("Larvae (Bee)");

		Larvae bee = new Larvae();


		try {
			if (matcher.group(2) == null)
				throw new Exception ("Parameter for Health of Larvae (Bee) not found!");

			Integer health = new Integer (matcher.group(2));
			//The NumberFormatException falls in the same category as other exceptions

			bee.health = health;
		}
		catch (Exception e) {
			if (!allowDefaults)
				throw new NoSerializedFound("Larvae (Bee)", stream);
		}


		try {
			if (matcher.group(3) == null)
				throw new Exception ("Parameter for Age of Larvae (Bee) not found!");

			Integer age = new Integer (matcher.group(3));
			//The NumberFormatException falls in the same category as other exceptions

			bee.age = age;
		}
		catch (Exception e) {
			if (!allowDefaults)
				throw new NoSerializedFound("Larvae (Bee)", stream);
		}


		return bee;
	}
}