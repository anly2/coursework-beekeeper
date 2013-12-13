package coursework;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An object representing a {@link Bee} Queen.
 * @see #Queen()
 * @see #anotherDay()
 * @see #serialize()
 * @see #deserialize(String)
 */
class Queen extends Bee
{
	/* Properties */

	/**
	 * The number of days till the queen lays another {@link Egg}
	 * @see #anotherDay()
	 * @see #nextBirth()
	 */
	protected int nextBirth;


	/* Constructors */

	/**
	 * Initializes a new {@link Bee} and sets the properties of a Queen
	 * <p> Please see {@link #nextBirth()} </p>
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
	Queen () {
		super();
		this.hive = null;

		typeID = (int) Config.get("queen type id");
		name =  (String) Config.get("queen type name");;

		age = minAge = (int) Config.get("queen age min");
		maxAge = (int) Config.get("queen age max");

		eatenFood = Food.byName((String) Config.get("queen eaten food type"));
		eatenFoodQuantity = (int) Config.get("queen eaten food quantity");

		nextBirth = nextBirth();	
	}
	
	/**
	 * Initializes a Queen and assigns her to a given Hive 
	 * @see #Queen()
	 * @see Bee#hive
	 */
	Queen (Hive hive) {
		this ();
		this.hive = hive;
	}
	

	/* Timeflow Methods */
	
	//public eat () is defined in the Bee class
	
	/**
	 * Simulates a day for the Queen.
	 * 
	 * <ul>
	 * 		Every day the Queen
	 *  	<li> gets older by a day (duh) (See {@link Bee#age}), </li>
	 *  	<li> eats and heals if possible (See {@link Bee#eat() eat()} and {@link Bee#health health}), </li>
	 *  	<li> gives birth if it is time to (See {@link #nextBirth()} , {@link Egg} and {@link Hive#addBee(Bee) addBee(Bee)}. </li>
	 * </ul>
	 * 
	 * <p> Please see &nbsp; {@link default.cfg :: chance _ death _ bee} </p>
	 * 
	 * @return Returns the Queen itself or null if it died.
	 */
	public Queen anotherDay ()
	{
		//Time goes by...
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

		//Queen's duty
		if (--nextBirth <= 0) // the "less than" part is also important
		{				
			try {
				hive.addBee (new Egg(hive));
				nextBirth = nextBirth(); //in days
				//If could not lay an egg because of no available cell, leave it pending for the next day :D
			}
			catch (HiveOverflow e) {
				System.err.println("There were no available cells for the Queen to lay an Egg in!");
			}
		}

		return this; //survived the day
	}

	/**
	 * A helper method to randomly choose after how many days the Queen will give birth again
	 * 
	 * <p> Instead of fixing it to the static 3 days,
	 * 		we set the average birth rate to be 3 days. <br />
	 * 		Thus, sometimes the queen may lay an egg earlier
	 * 		and sometimes later than 3 days. <br />
	 * 		This adds some entropy and makes the simulation more interesting.
	 * </p>
	 * 
	 * <p> Please see {@link Rand#range(int, int)} and {@link default.cfg :: queen _ birth _ rate _ range}. </p>
	 * 
	 * @return the number of days till the next birth
	 */
	protected static int nextBirth () {
		int birthRangeMin = (int)Config.get("queen birth rate range min");
		int birthRangeMax = (int)Config.get("queen birth rate range max");

		return Rand.range(birthRangeMin, birthRangeMax);
	}


	/* Serialization */
	
	/**
	 * Serializes this {@link Queen}
	 * <br />
	 * <br /> Format: &nbsp; <b><code>queen: %health% , %age% , %nextBirth%</code></b>
	 * <br /> <b>Note</b> the format must conform to {@value #serializedQueenPattern}
	 * @see #serializedQueenPattern
	 * @see #deserialize(String)
	 * @see Bee#serializationSpacing
	 */	
	public String serialize () {
		String stream = "";

		stream += "queen" + ":";
		stream +=       serializationSpacing + health;
		stream += "," + serializationSpacing + age;
		stream += "," + serializationSpacing + nextBirth;

		return stream;
	}

	
	/* Deserialization */
	
	/**
	 * The pattern of a serialized Queen
	 * <p> Currently: {@value #serializedQueenPattern} </p>
	 * @see #serialize()
	 * @see #deserialize(String)
	 */
	public static final String serializedQueenPattern = "\\s*(queen)\\s*:\\s*(\\d+)\\s*,\\s*(\\d+)(\\s*,\\s*(\\d+))?.*";
	
	/**
	 * Tries to deserialize the given stream
	 * 
	 * <p> <b>Please note</b> that the very creation of a new Queen <br />
	 * 		involves a call to {@link Rand#number(int, int)} via {@link #nextBirth()}. <br />
	 * 		Thus the {@link Rand Random Number Generator} is offset by <b>+1</b> from the actual seed. <br />
	 * 		The effect is small but may actually lead to a slightly different simulation than the one saved.
	 * </p>
	 * 
	 * @param stream - the stream to deserialize
	 * @return Returns a {@link Queen} (if a serialized one is found)
	 * @throws NoSerializedFound if no serialized Queen was recognized
	 * @see #serializedQueenPattern
	 * @see #serialize()
	 */
	public static Queen deserialize (String stream) throws NoSerializedFound {
		Matcher matcher = Pattern.compile(serializedQueenPattern).matcher(stream);

		if (!matcher.find(0))
			throw new NoSerializedFound("Queen (Bee)");

		Queen bee = new Queen();

		//Look for the health parameter
		try {
			if (matcher.group(2) == null)
				throw new Exception ("Parameter for Health of Queen (Bee) not found!");

			Integer health = new Integer (matcher.group(2));
			//The NumberFormatException falls in the same category as other exceptions

			bee.health = health;
		}
		catch (Exception e) {
			if (!allowDefaults)
				throw new NoSerializedFound("Queen (Bee)", stream);
		}


		//Look for the age parameter
		try {
			if (matcher.group(3) == null)
				throw new Exception ("Parameter for Age of Queen (Bee) not found!");

			Integer age = new Integer (matcher.group(3));
			//The NumberFormatException falls in the same category as other exceptions

			bee.age = age;
		}
		catch (Exception e) {
			if (!allowDefaults)
				throw new NoSerializedFound("Queen (Bee)", stream);
		}

		
		//Look for the nextBirth parameter
		try {
			if (matcher.group(5) == null)
				throw new Exception ("Parameter for nextBirth of Queen (Bee) not found!");

			Integer nextBirth = new Integer (matcher.group(5));
			//The NumberFormatException falls in the same category as other exceptions

			bee.nextBirth = nextBirth;
		}
		catch (Exception e) {
			if (!allowDefaults)
				throw new NoSerializedFound("Queen (Bee)", stream);
		}


		return bee;
	}
}