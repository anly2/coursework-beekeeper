package coursework;


/**
 * An object representing a Bee in general.
 *  
 * <ul>
 * 		<b>Subclasses</b> (Types of bees):
 * 		<li> {@link Queen}. </li>
 * 		<li> {@link Worker}. </li>
 * 		<li> {@link Drone}. </li>
 * 		<li> {@link Egg}. </li>
 * 		<li> {@link Larvae}. </li>
 * 		<li> {@link Pupa}. </li>
 * </ul>
 * 
 * <p> Please see &nbsp; {@link default.cfg} for more details </p>
 * @see #Bee()
 * @see #anotherDay()
 * @see #eat()
 * @see #serialize()
 * @see #deserialize(String)
 */
public abstract class Bee
{
	/* Type-dependent Properties */
	
	/**
	 * The type of the bee as an integer.
	 * <p> Please see &nbsp; {@link default.cfg :: BEE _ type _ id} &nbsp; for the actual values </p>
	 * <p> This is set by a subclass' constructor. {@link Bee Subclasses} <p>
	 * @see #name
	 * @see #getType()
	 */
	protected int typeID;
	/**
	 * The type of the bee as a String.
	 * <p> Please see &nbsp; {@link default.cfg :: BEE _ type _ name} &nbsp; for the actual values </p>
	 * <p> This is set by a subclass' constructor. {@link Bee Subclasses} <p>
	 * @see #typeID
	 * @see #getType(String)
	 */
	protected String name;
	/**
	 * The minimum age of this type of bee.
	 * <p> Please see &nbsp; {@link default.cfg :: BEE _ age _ min} &nbsp; for the actual values </p>
	 * <p> This is set by a subclass' constructor. {@link Bee Subclasses} <p>
	 * @see #age
	 * @see #maxAge
	 */
	protected int minAge;
	/**
	 * The maximum age of this type of bee.
	 * <p> Please see &nbsp; {@link default.cfg :: BEE _ age _ max} &nbsp; for the actual values </p>
	 * <p> This is set by a subclass' constructor. {@link Bee Subclasses} <p>
	 * @see #age
	 * @see #minAge
	 */
	protected int maxAge;
	/**
	 * The type of food eaten by this type of bee.
	 * <p> Please see &nbsp; {@link default.cfg :: BEE _ eaten _ food _ type} &nbsp; for the actual values </p>
	 * <p> This is set by a subclass' constructor. {@link Bee Subclasses} <p>
	 * @see #eatenFoodQuantity
	 * @see #eat()
	 */
	protected Food eatenFood;
	/**
	 * The amount of food eaten by this type of bee.
	 * <p> Please see &nbsp; {@link default.cfg :: BEE _ eaten _ food _ quantity} &nbsp; for the actual values </p>
	 * <p> This is set by a subclass' constructor. {@link Bee Subclasses} <p>
	 * @see #eatenFood
	 * @see #eat()
	 */
	protected int eatenFoodQuantity;
	

	/* Universal Properties */
	
	/**
	 * The hive to which the Bee belongs
	 * @see Hive
	 * @see Hive#addBee(Bee)
	 * @see Hive#cells
	 * @see Hive#getBee(int)
	 */
	protected Hive hive;
	/**
	 * The health of the Bee expressed in points.
	 * 
	 * <p> Please see {@link default.cfg :: bee _ health} </p>
	 * 
	 * @see #anotherDay()
	 * @see #eat()
	 */
	protected int health;
	/**
	 * The age of the Bee in days since being laid as an egg.
	 * 
	 * <p> A newly created bee has its age equal to its type's minimum age. See {@link #minAge} </p> 
	 * 
	 * <p> Please see {@link default.cfg :: BEE _ age </p>
	 * 
	 * @see #getAge()
	 * @see #setAge(int)
	 * @see #anotherDay()	
	 */
	protected int age;
	
	
	/* Constructors */
	
	/**
	 * Initializing constructor for all Bee types
	 * @see #typeID
	 */
	public Bee () {
		//age = minAge;
		health = (int) Config.get("bee health max");
	}
	
	/**
	 * Initializes and assigns the Bee to a Hive
	 * <p> This one is probably never used because it is overridden. </p>
	 * @see #Bee()
	 */
	public Bee (Hive h) {
		this ();
		this.hive = h;
	}
	

	/* Accessors */
	
	/**
	 * Gets the type of the bee.
	 * @return Returns an integer representing the type of the Bee
	 * @see #typeID
	 * @see #getType(String)
	 */
	public int getType () {
		return this.typeID;
	}

	/**
	 * Gets the type name of the bee.
	 * @param returnTypeSelector - <b>irrelevant</b> - simply to allow overloading.
	 * @return Returns the type of the bee but as a string.
	 * @see #name
	 * @see #getType()
	 */
	public String getType (String returnTypeSelector) {
		return this.name;
	}

	/**
	 * Gets the age of the bee.
	 * @return Returns an int representing the age of the Bee in days since laid as an egg
	 * @see #age
	 */
	public int getAge() {
		return this.age;
	}

	/** 
	 * Sets the age of the Bee.
	 * 
	 * <p> <b>Note</b> that if the desired age is not within the proper bounds <br />
	 * 		an exception will be thrown and no change will actually occur. <br />
	 * 		See {@link #minAge} and {@link #maxAge} for the proper bounds.
	 * </p>
	 * 
	 * @param days - the desired age of the bee in days
	 * @throws BeeWouldEvolve if the desired age was not an appropriate age for this bee.
	 * 	<br /> Note that there will be no actual change to {@link #age}!
	 * 
	 * @see #minAge
	 * @see #maxAge
	 */
	public void setAge (int days) throws BeeWouldEvolve {
		// Checks
		if (days < this.minAge || days > this.maxAge)
			throw new BeeWouldEvolve (this);
		
		this.age = days;
	}


	/* Timeflow Methods */
	
	/**
	 * Tries to eat and heals or damages the bee accordingly.
	 * 
	 * <ul>
	 * 		<li>First, checks if the Hive has enough food for this bee. <br />
	 * 			(See {@link Hive#hasFood(Food, int)}) </li>
	 * 		<li>Then, heals if there was enough food, but not beyond the max health. <br />
	 * 			(See {@link default.cfg :: bee _ health _ gain}) <br />
	 * 			(See {@link default.cfg :: bee _ health _ max}) </li>
	 * 		<li>If there wasn't enough food, decrease the {@link #health} and react if it died. <br />
	 * 			(See {@link default.cfg :: bee _ health _ loss}) <br />
	 * 			(See {@link default.cfg :: bee _ health _ min}) </li>
	 * </ul>
	 * 
	 * @return Returns true if there was enough food or false otherwise.
	 * @throws BeeDeath if the bee died of hunger
	 */
	public boolean eat () throws BeeDeath {
		if (!hive.hasFood(eatenFood, eatenFoodQuantity))
		{
			// Goes worse because of starving
			health -= (int)Config.get("bee health loss");
			
			if (health <= (int)Config.get("bee health min"))
				throw new BeeDeath (this);
			
			return false;
		}
		
		//Consume the food
		hive.takeFood(eatenFood, eatenFoodQuantity);
		
		// Heal if possible
		if (health < (int)Config.get("bee health max"))
			health += (int)Config.get("bee health gain");
		
		return true;
	}

	/**
	 * Performs the daily duties of the bee
	 * <p> Please see {@link Bee Subclasses} </p>
	 */
	public abstract Bee anotherDay ();
	
	
	/* Custom Methods */
	
	/**
	 * Provides a human-readable representation of this Bee.
	 * <br />
	 * <br /> Format: <code>"Bee %type% is %age% days old and has %health% HP"</code>
	 * <br /> Example: <code>"Bee Worker is 14 days old and has 3 HP"</code>
	 * @return Returns a String representation of this Bee.
	 */
	public String toString () {
		String description = "";
		description += "Bee "+this.getType("as name string");
		description += " is "+this.getAge()+" days old";
		description += " and has "+this.health+" HP";
		return description;
	}
	
	/**
	 * Please use {@link #toString()} instead.
	 * @deprecated The here-added effect is done as part of {@link Hive#toString()}.
	 * @see #toString()
	 * @see Hive#toString()
	 */
	public String toString (Hive hive) {
		String description = "Cell "+hive.cells.indexOf(this)+": ";
		description += this.toString();
		return description;
	}
	
	
	/* Serialization */
	
	/**
	 * The spacing that is applied between every parameter of the bee (health and age)
	 * <br />
	 * <br /> Current spacing: {@value #serializationSpacing}
	 * <br /> Recommended spacing: <code>" "</code>
	 * @see #serialize()
	 */
	public static final String serializationSpacing = " ";

	/**
	 * Please see the implementations in the subclasses.
	 * @see Queen#serialize()
	 * @see Worker#serialize()
	 * @see Drone#serialize()
	 * @see Egg#serialize()
	 * @see Larvae#serialize()
	 * @see Pupa#serialize()
	 */
	public abstract String serialize ();
	
	
	/* Deserialization */
	
	/**
	 * Determines whether a missing parameter in the serialization is allowed.
	 * <p> Affects {@link #deserialize(String)} in subclasses </p>
	 */
	public static final boolean allowDefaults = true;
	
	/**
	 * Looks for a serialized bee in the given stream.
	 * <ul>
	 * 		Bees looked for:
	 * 		<li> {@link Queen}. See {@link Queen#deserialize(String)}</li>
	 * 		<li> {@link Worker}. See {@link Worker#deserialize(String)} </li>
	 * 		<li> {@link Drone}. See {@link Drone#deserialize(String)} </li>
	 * 		<li> {@link Egg}. See {@link Egg#deserialize(String)} </li>
	 * 		<li> {@link Larvae}. See {@link Larvae#deserialize(String)}</li>
	 * 		<li> {@link Pupa}. See {@link Pupa#deserialize(String)}</li>
	 * </ul>
	 * @param stream - the stream to try and deserialize
	 * @return Returns a {@link Bee} (if a serialized one is found)
	 * @throws NoSerializedFound if no serialized Bee was recognized
	 */
	public static Bee deserialize (String stream) throws NoSerializedFound {
		try {
			return Queen.deserialize(stream);
		}
		catch (NoSerializedFound e) {}
		
		
		try {
			return Worker.deserialize(stream);
		}
		catch (NoSerializedFound e) {}
		
		
		try {
			return Drone.deserialize(stream);
		}
		catch (NoSerializedFound e) {}
		
		
		try {
			return Egg.deserialize(stream);
		}
		catch (NoSerializedFound e) {}
		
		
		try {
			return Larvae.deserialize(stream);
		}
		catch (NoSerializedFound e) {}
		
		
		try {
			return Pupa.deserialize(stream);
		}
		catch (NoSerializedFound e) {}
		
		
		throw new NoSerializedFound ("Bee");
	}
}