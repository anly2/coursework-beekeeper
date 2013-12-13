package coursework;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An object representing a {@link Bee} Worker.
 * @see #Worker()
 * @see #anotherDay()
 * @see #serialize()
 * @see #deserialize(String)
 */
class Worker extends Bee
{
	/* Constructors */

	/**
	 * Initializes a new {@link Bee} and sets the properties of a Worker
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
	Worker () {
		super();

		typeID = (int) Config.get("worker type id");
		name =  (String) Config.get("worker type name");;

		age = minAge = (int) Config.get("worker age min");
    	maxAge = (int) Config.get("worker age max");

		eatenFood = Food.byName((String) Config.get("worker eaten food type"));
		eatenFoodQuantity = (int) Config.get("worker eaten food quantity");

		hive = null;
	}
	
	/**
	 * Initializes a Worker and assigns her to a given Hive 
	 * @see #Worker()
	 * @see Bee#hive
	 */
	Worker (Hive h) {
		this ();
		this.hive = h;
	}
	

	/* Timeflow Methods */
	
	//public eat () is defined in the Bee class
	
	/**
	 * Simulates a day for the Worker.
	 * 
	 * <ul>
	 * 		Every day the Worker
	 *  	<li> gets older by a day (duh) (See {@link Bee#age}), </li>
	 *  	<li> eats and heals if possible (See {@link Bee#eat() eat()} and {@link Bee#health health}), </li>
	 *  	<li> finds two flowers and extracts some pollen <br />
	 *  		 (See {@link Garden#findFlower()} and {@link Flower#extractPollen()}), </li>
	 *  	<li> converts the pollen gathered and the pollen in the hive <br />
	 *  		 (See &nbsp; {@link default.cfg :: conversion _ ratio}). </li>
	 * </ul>
	 * 
	 * <p> Please see &nbsp; {@link default.cfg :: chance _ death _ bee} </p>
	 * 
	 * @return Returns the Worker itself or null if it died.
	 */
	public Worker anotherDay () throws RuntimeException
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

		//Worker's duty
		if (hive.garden == null)
			throw new RuntimeException("This Bee's Hive does not belong to a garden!");

		Flower flower1 = hive.garden.findFlower();
		Flower flower2 = hive.garden.findFlower();

		int pollen = 0;
		if (flower1 != null)
			pollen += flower1.extractPollen();
		if (flower2 != null)
			pollen += flower2.extractPollen();
		
		if (flower1 != null && flower2 != null)
			flower2.pollinate(flower1); //the chance is handled there
		
		hive.addPollen(pollen);
		
		
		int remainingPollen = hive.getPollen(); //process all pollen in hive or just the amount it brought?!  
		
		int pollenForJelly = (int) Config.get("conversion ratio pollen to royaljelly");
		if (remainingPollen >= pollenForJelly)
		{
			hive.addRoyalJelly(1);
			hive.takePollen(pollenForJelly);
			remainingPollen -= pollenForJelly;
		}
		
		if (remainingPollen <= 0)
			return this; //no point continuing
		
		int pollenForHoney = (int) Config.get("conversion ratio pollen to honey");
		while (remainingPollen >= pollenForHoney)
		{
			hive.addHoney(1);
			hive.takePollen(pollenForHoney);
			remainingPollen -= pollenForHoney;
		}
		
		hive.addPollen (remainingPollen);
		return this; //survived the day
	}


	/* Serialization */
	
	/**
	 * Serializes this {@link Worker}
	 * <br />
	 * <br /> Format: &nbsp; <b><code>worker: %health% , %age%</code></b>
	 * <br /> <b>Note</b> the format must conform to {@value #serializedWorkerPattern}
	 * @see #serializedWorkerPattern
	 * @see #deserialize(String)
	 * @see Bee#serializationSpacing
	 */	
	public String serialize () {
		String stream = "";

		stream += "worker" + ":";
		stream +=       serializationSpacing + health;
		stream += "," + serializationSpacing + age;

		return stream;
	}

	
	/* Deserialization */
	
	/**
	 * The pattern of a serialized Worker
	 * <p> Currently: {@value #serializedWorkerPattern} </p>
	 * @see #serialize()
	 * @see #deserialize(String)
	 */
	public static final String serializedWorkerPattern = "\\s*(worker)\\s*:\\s*(\\d+)\\s*,\\s*(\\d+).*";
	
	/**
	 * Tries to deserialize the given stream
	 * @param stream - the stream to deserialize
	 * @return Returns a {@link Worker} (if a serialized one is found)
	 * @throws NoSerializedFound if no serialized Worker was recognized
	 * @see #serializedWorkerPattern
	 * @see #serialize()
	 */
	public static Worker deserialize (String stream) throws NoSerializedFound {
		Matcher matcher = Pattern.compile(serializedWorkerPattern).matcher(stream);

		if (!matcher.find(0))
			throw new NoSerializedFound("Worker (Bee)");

		Worker bee = new Worker();


		try {
			if (matcher.group(2) == null)
				throw new Exception ("Parameter for Health of Worker (Bee) not found!");

			Integer health = new Integer (matcher.group(2));
			//The NumberFormatException falls in the same category as other exceptions

			bee.health = health;
		}
		catch (Exception e) {
			if (!allowDefaults)
				throw new NoSerializedFound("Worker (Bee)", stream);
		}


		try {
			if (matcher.group(3) == null)
				throw new Exception ("Parameter for Age of Worker (Bee) not found!");

			Integer age = new Integer (matcher.group(3));
			//The NumberFormatException falls in the same category as other exceptions

			bee.age = age;
		}
		catch (Exception e) {
			if (!allowDefaults)
				throw new NoSerializedFound("Worker (Bee)", stream);
		}


		return bee;
	}
}