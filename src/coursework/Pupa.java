package coursework;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An object representing a {@link Bee} Pupa.
 * @see #Pupa()
 * @see #anotherDay()
 * @see #serialize()
 * @see #deserialize(String)
 */
class Pupa extends Bee
{
	/* Constructors */

	/**
	 * Initializes a new {@link Bee} and sets the properties of a Pupa
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
	Pupa () {
		super();
		
		typeID =  (int) Config.get("pupa type id");;
		name = (String) Config.get("pupa type name");

		age = minAge = (int) Config.get("pupa age min");
		maxAge = (int) Config.get("pupa age max");

		/* Unneeded:
		eatenFood = Food.byName((String) Config.get("pupa eaten food type")); // Error Eater; Is irrelevant because eatenQuantity is 0
		eatenFoodQuantity = (int) Config.get("pupa  eaten food quantity"); 
		*/
		
		hive = null;
	}

	/**
	 * Initializes a Pupa and assigns it to a given Hive 
	 * @see #Pupa()
	 * @see Bee#hive
	 */
	Pupa (Hive h) {
		this ();
		hive = h;
		hive.garden.getInterface().event("birth",  this);
	}


	/* Timeflow Methods */

	/**
	 * Wriggle around and do nothing cause Pupas are like that
	 * @return always true
	 * @see Bee#eat()
	 * @see #anotherDay()
	 */
	public boolean eat () {
		//Do nothing (Wriggle)
		return true;
	}
	
	/**
	 * Simulates a day for the Pupa.
	 * 
	 * <ul>
	 * 		Every day the Larvae
	 *  	<li> gets older by a day (duh) (See {@link Bee#age}), </li>
	 *  	<li> does NOT really eat (See {@link #eat()}), </li>
	 *  	<li> evolves if is old enough. (See paragraph below). </li>
	 * </ul>
	 * 
	 * <p> If the pupa is old enough it will evolve. <br />
	 * 		It can evolve into a {@link Queen} , {@link Worker} or {@link Drone} <br />
	 * 		A few things are considered when choosing what Bee to evolve into. <br /> <br />
	 * 
	 * 		<ul>
	 * 			The different chance groups are 
	 * 			<li> <b><code>"noqueen"</code></b> if the hive queen is dead, </li>
	 * 			<li> <b><code>"colonizers"</code></b> if the hive is full above a certain threshold, </li>
	 * 			<li> <b><code>"farmers"</code></b> otherwise (normal case). </li>
	 * 		</ul>
	 * 		Please see &nbsp; {@link default.cfg :: chance _ birth} &nbsp; for the actual values in the chance groups.
	 * </p>
	 * 
	 * <dl> <dt>Note</dt>
	 * 		<dd>
	 * 			If the Pupa evolved into a Queen, <br />
	 * 			it will call {@link Hive#appointQueen(Queen)} <br />
	 * 			but will throw {@link BeeDeath} exception!
	 * 		</dd>
	 * </dl>
	 * 
	 * @return Returns the Pupa itself
	 *  <br /> or a new Bee* (read above) if it evolved,
	 * @throws BeeDeath if the Pupa died
	 * <br /> Please see note above.
	 */
	public Bee anotherDay () throws BeeDeath
	{
		hive.garden.getInterface().event("newDay", this);
		
		//Time goes by...
		age++;
		
		//If the pupa is not old enough, return itself
		if (age <= maxAge)
			return this;

		//If the pupa IS old enough, evolve and return the new Bee
		String birthChanceGroup;
		if (hive.queen == null)
			birthChanceGroup = "chance birth noqueen";
		else
		if (hive.cells.size() > (int) Config.get("hive threshold colonizers"))
			birthChanceGroup = "chance birth colonizers";
		else
			birthChanceGroup = "chance birth farmers";
			
		
		int beeChoice = Rand.chance (Config.getGroup (birthChanceGroup));
		Bee evolved = null;
		
		switch (beeChoice) {
			case 0:
				evolved = new Drone(hive);
				break;
				
			case 1:
				evolved = new Queen(hive);
				hive.garden.getInterface().event("evolution", this, evolved);
				
				try {
					hive.appointQueen ((Queen)evolved);
				}
				catch (QueenLeftOut e) {
					//System.out.println("\t\t" + e.getMessage());
					hive.garden.getInterface().event("queenLeftOut", evolved, e);
				}
				
				throw new BeeDeath (this, "turning into a Queen");
				//break;
				
			case 2:
				evolved = new Worker(hive);
				break;
				
			default:
				throw new BeeDeath (this);
		}

		hive.garden.getInterface().event("evolution", this, evolved);
		return evolved;
	}


	/* Serialization */
	
	/**
	 * Serializes this {@link Pupa}
	 * <br />
	 * <br /> Format: &nbsp; <b><code>pupa: %health% , %age%</code></b>
	 * <br /> <b>Note</b> the format must conform to {@value #serializedPupaPattern}
	 * @see #serializedPupaPattern
	 * @see #deserialize(String)
	 * @see Bee#serializationSpacing
	 */
	public String serialize () {
		String stream = "";

		stream += "pupa" + ":";
		stream +=       serializationSpacing + health;
		stream += "," + serializationSpacing + age;

		return stream;
	}

	
	/* Deserialization */
	
	/**
	 * The pattern of a serialized Pupa
	 * <p> Currently: {@value #serializedPupaPattern} </p>
	 * @see #serialize()
	 * @see #deserialize(String)
	 */
	public static final String serializedPupaPattern = "\\s*(pupa)\\s*:\\s*(\\d+)\\s*,\\s*(\\d+).*";
	
	/**
	 * Tries to deserialize the given stream
	 * @param stream - the stream to deserialize
	 * @return Returns a {@link Pupa} (if a serialized one is found)
	 * @throws NoSerializedFound if no serialized Pupa was recognized
	 * @see #serializedPupaPattern
	 * @see #serialize()
	 */
	public static Pupa deserialize (String stream) throws NoSerializedFound {
		Matcher matcher = Pattern.compile(serializedPupaPattern).matcher(stream);

		if (!matcher.find(0))
			throw new NoSerializedFound("Worker (Bee)");

		Pupa bee = new Pupa();


		try {
			if (matcher.group(2) == null)
				throw new Exception ("Parameter for Health of Pupa (Bee) not found!");

			Integer health = new Integer (matcher.group(2));
			//The NumberFormatException falls in the same category as other exceptions

			bee.health = health;
		}
		catch (Exception e) {
			if (!allowDefaults)
				throw new NoSerializedFound("Pupa (Bee)", stream);
		}


		try {
			if (matcher.group(3) == null)
				throw new Exception ("Parameter for Age of Pupa (Bee) not found!");

			Integer age = new Integer (matcher.group(3));
			//The NumberFormatException falls in the same category as other exceptions

			bee.age = age;
		}
		catch (Exception e) {
			if (!allowDefaults)
				throw new NoSerializedFound("Pupa (Bee)", stream);
		}


		return bee;
	}
}