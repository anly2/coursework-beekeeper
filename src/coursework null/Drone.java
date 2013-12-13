package coursework;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An object representing a {@link Bee} Drone.
 * @see #Drone()
 * @see #anotherDay()
 * @see #serialize()
 * @see #deserialize(String)
 */
class Drone extends Bee
{
	/* Constructors */

	/**
	 * Initializes a new {@link Bee} and sets the properties of a Drone
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
	Drone () {
		super();
		
		typeID = (int) Config.get("drone type id");
		name =  (String) Config.get("drone type name");;

		age = minAge = (int) Config.get("drone age min");
        maxAge = (int) Config.get("drone age max");
 
		eatenFood = Food.byName((String) Config.get("drone eaten food type"));
		eatenFoodQuantity = (int) Config.get("drone eaten food quantity");
		
		hive = null;
	}
	
	/**
	 * Initializes a Drone and assigns her to a given Hive 
	 * @see #Drone()
	 * @see Bee#hive
	 */
	Drone (Hive h) {
		this ();
		this.hive = h;
	}
	

	/* Timeflow Methods */
	
	//public eat () is defined in the Bee class
	
	/**
	 * Simulates a day for the Drone.
	 * 
	 * <ul>
	 * 		Every day the Drone
	 *  	<li> gets older by a day (duh) (See {@link Bee#age}), </li>
	 *  	<li> eats and heals if possible (See {@link Bee#eat() eat()} and {@link Bee#health health}). </li>
	 * </ul>
	 * 
	 * <p> Please see &nbsp; {@link default.cfg :: chance _ death _ bee} </p>
	 * 
	 * @return Returns the Drone itself or null if it died.
	 */
	public Drone anotherDay ()
	{
		//Time goes by
		age++;
		if (age >= maxAge) //Is old
			if (Rand.chance((int) Config.get("chance death bee")))
				return null;
		
		//Try to eat
		try {
			eat();
		}
		catch (BeeDeath e) {
			return null;
		}
        
        return this;
	}


	/* Serialization */
	
	/**
	 * Serializes this {@link Drone}
	 * <br />
	 * <br /> Format: &nbsp; <b><code>drone: %health% , %age%</code></b>
	 * <br /> <b>Note</b> the format must conform to {@value #serializedDronePattern}
	 * @see #serializedDronePattern
	 * @see #deserialize(String)
	 * @see Bee#serializationSpacing
	 */	
	public String serialize () {
		String stream = "";

		stream += "drone" + ":";
		stream +=       serializationSpacing + health;
		stream += "," + serializationSpacing + age;

		return stream;
	}

	
	/* Deserialization */
	
	/**
	 * The pattern of a serialized Drone
	 * <p> Currently: {@value #serializedDronePattern} </p>
	 * @see #serialize()
	 * @see #deserialize(String)
	 */
	public static final String serializedDronePattern = "\\s*(drone)\\s*:\\s*(\\d+)\\s*,\\s*(\\d+).*";
	
	/**
	 * Tries to deserialize the given stream
	 * @param stream - the stream to deserialize
	 * @return Returns a {@link Drone} (if a serialized one is found)
	 * @throws NoSerializedFound if no serialized Drone was recognized
	 * @see #serializedDronePattern
	 * @see #serialize()
	 */
	public static Drone deserialize (String stream) throws NoSerializedFound {
		Matcher matcher = Pattern.compile(serializedDronePattern).matcher(stream);

		if (!matcher.find(0))
			throw new NoSerializedFound("Drone (Bee)");

		Drone bee = new Drone();


		try {
			if (matcher.group(2) == null)
				throw new Exception ("Parameter for Health of Drone (Bee) not found!");

			Integer health = new Integer (matcher.group(2));
			//The NumberFormatException falls in the same category as other exceptions

			bee.health = health;
		}
		catch (Exception e) {
			if (!allowDefaults)
				throw new NoSerializedFound("Drone (Bee)", stream);
		}


		try {
			if (matcher.group(3) == null)
				throw new Exception ("Parameter for Age of Drone (Bee) not found!");

			Integer age = new Integer (matcher.group(3));
			//The NumberFormatException falls in the same category as other exceptions

			bee.age = age;
		}
		catch (Exception e) {
			if (!allowDefaults)
				throw new NoSerializedFound("Drone (Bee)", stream);
		}


		return bee;
	}
}