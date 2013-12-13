package coursework;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An object representing a Flower
 * <ul>
 * 		Types of flowers:
 * 		<li> {@link Rose} </li>
 * 		<li> {@link Fuchsia} </li>
 * 		<li> {@link Daffodil} </li>
 * 		<li> {@link BlueRose} </li>
 * </ul>
 * See {@link default.cfg} for the properties of the flowers
 * @see #extractPollen(int)
 * @see #pollinate(Flower)
 * @see #serialize()
 * @see #deserialize(String)
 */
public abstract class Flower {
	/* Properties */
	
	/**
	 * The name of the type of flower (ex. Rose)
	 */
	protected String name;
	/**
	 * The type of the flower as an int.
	 * <ul>
	 * 		<li> 1 = {@link Rose} </li>
	 * 		<li> 2 = {@link Fuchsia} </li>
	 * 		<li> 3 = {@link Daffodil} </li>
	 * 		<li> 4 = {@link BlueRose Blue Rose} </li>
	 * </ul>
	 */
	protected int type;
	/**
	 * The color of the flower as a string.
	 */
	protected String color; 
	/**
	 * The amount of pollen created each day.
	 */
	protected int growthRate;
	/**
	 * The amount of pollen a Bee (worker) extracts each time.
	 */
	protected int extractAmount;
	/**
	 * The normal lifespan of the flower in days.
	 * When the flowers becomes older than this there is a chance it will wither and die.
	 */
	protected int lifespan;

	/**
	 * The age in days of the flower (currently)
	 */
	protected int age;
	/**
	 * The amount of pollen the flower has (currently).
	 * @see #getPollen()
	 * @see #setPollen(int)
	 * @see #extractPollen()
	 * @see #extractPollen(int)
	 */
	protected int pollen;
	
	/**
	 * The garden the flower is growing in.
	 * @see #laySeed()
	 */
	protected Garden garden;
	
	
	/* Constructors */
	
	/**
	 * Default constructor for all Flower types
	 * <br />Initializes {@link #age} to 0 and {@link #garden} to null
	 */
	Flower () {
		this.garden = null;
		this.age = 0;
	}
	
	/**
	 * Constructor which names a given {@link Garden} as this flower's {@link #garden}
	 */
	Flower (Garden garden) {
		this ();
		this.garden = garden;
		garden.getInterface().event("birth", this);
	}
	
	
	/* Accessors */
	
	/**
	 * Gets the type of the flower.
	 * @return Returns an integer representing the type of the Flower
	 * @see #type
	 * @see #getType(String)
	 */
	public int getType () {
		return this.type;
	}

	/**
	 * Gets the type name of the flower.
	 * @param returnTypeSelector - <b>irrelevant</b> - simply to allow overloading.
	 * @return Returns the type of the flower but as a string.
	 * @see #name
	 * @see #getType()
	 */
	public String getType (String returnTypeSelector) {
		return this.name;
	}

	/**
	 * Gets the amount of pollen the flower has.
	 * @return Returns an int representing the amount of pollen the Flower has.
	 * @see #pollen
	 * @see #setPollen
	 * @see #extractPollen(int)
	 */
	public int getPollen() {
		return this.pollen;
	}

	/** 
	 * Sets the amount of pollen the flower has.
	 * 
	 * @param amount - the amount of pollen the flower should have
	 * @throws NegativeAmountException if the amount specified was negative
	 * 
	 * @see #pollen
	 * @see #getPollen()
	 * @see #extractPollen(int)
	 */
	public void setPollen (int amount) throws NegativeAmountException {
		if (amount <= 0)
			throw new NegativeAmountException ("Cannot set pollen amount to a negative value");
		
		this.pollen = amount;
	}

	
	/* Timeflow */
	
	/**
	 * Updates the age of this flower and calls {@link #grow()}
	 * <br />
	 * <br /> When the flower becomes older than the {@link #lifespan} specified, there is a chance it dies. 
	 * @return Returns the flower itself
	 * @throws Death if the flower withered and died.
	 * @see #age
	 * @see #lifespan
	 * @see #grow()
	 * @see default.cfg
	 */
	public Flower anotherDay () throws Death
	{
		garden.getInterface().event("newDay", this);
		
		//Time goes by...
		age++;
		if (age >= lifespan) //Is old
			if (Rand.chance((int) Config.get("chance death flower")))
				throw new Death ("A " + color + " " + name + " flower withered and died!");

		grow();
		return this;
	}

	/**
	 * Increases the pollen the flower has by the given rate
	 * @see #pollen
	 * @see #growthRate
	 */
	public void grow () {
		this.pollen += this.growthRate;
	}

	
	/* Behavior */
	
	/**
	 * Extracts a given amount of pollen
	 * @param amount - the amount of pollen requested to be extracted
	 * @return Returns the amount of pollen successfully extracted.
	 * <br /> Note that this may be less than requested
	 * @throws NegativeAmountException if the amount requested was negative
	 * @throws OutOfPollen -disabled- if there was no pollen on the flower.
	 * <br />Disabled because the program shouldn't stop for this.
	 * @see #pollen
	 * @see #extractPollen() 
	 */
	public int extractPollen (int amount) throws OutOfPollen, NegativeAmountException {
		if (amount <= 0)
			throw new NegativeAmountException ("Cannot extract non-positive amounts of pollen");

		if (this.pollen < amount)
			amount = this.pollen;

		this.pollen -= amount;

		if (this.pollen == 0)
			throw new OutOfPollen (this);

		return amount;
	}

	/**
	 * Extracts an amount of pollen determined by the flower type
	 * <br />
	 * <br /> The property {@link #extractAmount} is set by the subclasses
	 * @return {@link #extractPollen(int)}
	 * @see #pollen
	 * @see #extractPollen(int)
	 * @see #extractAmount
	 */
	public int extractPollen () {
		try {
			return extractPollen(extractAmount);
		}
		catch (OutOfPollen e) {
			//System.out.println("\t\t" + e.getMessage());
			garden.getInterface().event("outOfPollen", this, e);
			return 0;
		}
	}

	
	/* Extended Behavior */

	/**
	 * Decides if the flower will lay a seed.
	 * <br /> The chance depends on the color of the flower that pollinated this one.
	 * <br /> To lay a seed basically means to create a new flower of the same type (as this flower)
	 * <br />
	 * <br /> For details on the chance please see {@link default.cfg}
	 * @param flowerMate - the flower that tries to pollinate this one
	 * @return Returns true of the pollination was successful or false otherwise.
	 * @see #laySeed()
	 * @see Rand#chance(int)
	 */
	public boolean pollinate (Flower flowerMate) {
		if (this == flowerMate) //The same flower twice?
		{
			if (!Rand.chance((int) Config.get("chance pollinate self")))
				return false;
		
			laySeed();
			return true;
		}
		
		if (!Rand.chance((int) Config.get("chance pollinate "+this.color+" "+flowerMate.color)))
			return false;
		
		laySeed();
		return true;
	}
	
	/**
	 * Adds a new flower of the same type to the garden
	 * @see #garden
	 */
	public abstract boolean laySeed (); 


	/* Custom Methods */
	
	/**
	 * Provides a human-readable representation of this flower.
	 * <br />
	 * <br /> Format: <code>"A %color$ %name% with %pollen% pollen.</code>
	 * <br /> Example: <code>"A red Rose with 10 pollen.</code>
	 * @return Returns a String representation of this Flower.
	 */
	public String toString () {
		return "A " + color + " " + name + " with " + pollen + " pollen";
	}

	
	/* Serialization */

	/**
	 * The spacing that is applied between every parameter of the flower (type, pollen, age)
	 * <br />
	 * <br /> Current spacing: {@value #serializationSpacing}
	 * <br /> Recommended spacing: <code>" "</code>
	 * @see #serialize()
	 */
	public static final String serializationSpacing = " ";
	
	/**
	 * Please see the implementations in the subclasses.
	 * @see Rose#serialize()
	 * @see Fuchsia#serialize()
	 * @see Daffodil#serialize()
	 * @see BlueRose#serialize()
	 */
	public abstract String serialize();

	
	/* Deserialization */
	
	/**
	 * Determines whether a missing parameter in the serialization is allowed.
	 * <p> Affects {@link #deserialize(String)} in subclasses </p>
	 */
	public static final boolean allowDefaults = true;
	
	/**
	 * Looks for a serialized flower in the given stream.
	 * <ul>
	 * 		Flowers looked for:
	 * 		<li> {@link Rose}. See {@link Rose#deserialize(String)}</li>
	 * 		<li> {@link Fuchsia}. See {@link Fuchsia#deserialize(String)} </li>
	 * 		<li> {@link Daffodil}. See {@link Daffodil#deserialize(String)} </li>
	 * 		<li> {@link BlueRose}. See {@link BlueRose#deserialize(String)} </li>
	 * </ul>
	 * @param stream - the stream to try and deserialize
	 * @return Returns a {@link Flower} (if a serialized one is found)
	 * @throws NoSerializedFound if no serialized Flower was recognized
	 */
	public static Flower deserialize (String stream) throws NoSerializedFound {
		//Look for a serialized Rose
		try {
			return Rose.deserialize(stream);
		}
		catch (NoSerializedFound e) {}
		
		
		//Look for a serialized Fuchsia
		try {
			return Fuchsia.deserialize(stream);
		}
		catch (NoSerializedFound e) {}
		
		
		//Look for a serialized Daffodil
		try {
			return Daffodil.deserialize(stream);
		}
		catch (NoSerializedFound e) {}
		
		
		//Look for serialized BlueRose
		try {
			return BlueRose.deserialize(stream);
		}
		catch (NoSerializedFound e) {}
		
		
		//If we came here then no flower was recognized
		throw new NoSerializedFound ("Flower");
	}
}


/**
 * An object representing a Rose {@link Flower}
 * <br />
 * <br /> See {@link default.cfg} for the properties of the flowers
 * @see #Rose()
 * @see #laySeed()
 * @see #serialize()
 * @see #deserialize(String)
 */
class Rose extends Flower {
	/* Constructors */
	
	/**
	 * Sets the details for this Flower Type
	 * <br />
	 * <br /> Please see {@link default.cfg} for the actual values of the properties
	 * @see Flower#name
	 * @see Flower#type
	 * @see Flower#color
	 * @see Flower#pollen
	 * @see Flower#growthRate
	 * @see Flower#extractAmount
	 * @see Flower#lifespan
	 */
	Rose () {
		super ();
		this.name = (String) Config.get("flower rose name");
		this.pollen = (int) Config.get("flower rose pollen");
		this.type = (int) Config.get("flower rose type");
		this.color = (String) Config.get("flower rose color");
		this.growthRate = (int) Config.get("flower rose growth");
		this.extractAmount = (int) Config.get("flower rose extract");
		this.lifespan = (int) Config.get("flower rose lifespan");
	}
	
	/**
	 * Sets the details and also names a given {@link Garden} as this flower's {@link Flower#garden garden}
	 */
	Rose (Garden g) {
		this ();
		this.garden = g;
		garden.getInterface().event("birth", this);
	}
	
	
	/* Extended Behavior */
	
	/**
	 * Adds a new flower of type {@link Rose} to the {@link Flower#garden garden}
	 * @see Flower#laySeed()
	 * @see Flower#garden
	 */
	public boolean laySeed () {
		garden.addFlower (new Rose(garden));
		return true;
	}

	
	/* Serialization */
	
	/**
	 * Serializes this Flower
	 * <br />
	 * <br /> Format: &nbsp; <b><code>rose: %pollen% , %age%</code></b>
	 * <br /> <b>Note</b> the format must conform to {@value #serializedRosePattern}
	 * @see #serializedRosePattern
	 * @see #deserialize(String)
	 */
	public String serialize () {
		String stream = "";
		
		stream += "rose" + ":";
		stream += serializationSpacing + pollen;
		stream += serializationSpacing + ",";
		stream += serializationSpacing + age;
		
		return stream;	
	}

	
	/* Deserialization */
	
	/**
	 * The pattern of a serialized Rose
	 * <p> Currently: {@value #serializedRosePattern} </p>
	 * @see #serialize()
	 * @see #deserialize(String)
	 */
	public static final String serializedRosePattern = "^\\s*(rose)\\s*:\\s*(\\d+)(\\s*,\\s*(\\d+))?.*";

	/**
	 * Tries to deserialize the given stream
	 * @param stream - the stream to deserialize
	 * @return Returns a {@link Rose} (if a serialized one is found)
	 * @throws NoSerializedFound if no serialized Rose was recognized
	 * @see #serializedRosePattern
	 * @see #serialize()
	 */
	public static Rose deserialize (String stream) throws NoSerializedFound {
		Matcher matcher = Pattern.compile(serializedRosePattern).matcher(stream);
		
		if (!matcher.find(0))
			throw new NoSerializedFound("Rose (Flower)");
		
		Rose flower = new Rose();
		
		//Look for the pollen parameter
		try {
			if (matcher.group(2) == null)
				throw new Exception ("Parameter for amount of Pollen in Rose (Flower) not found!");

			Integer amount = new Integer (matcher.group(2));
			//The NumberFormatException falls in the same category as other exceptions

			flower.pollen = amount;
		}
		catch (Exception e) {
			if (!allowDefaults)
				throw new NoSerializedFound("Rose (Flower)", stream);
		}
		
		
		//Look for the age parameter
		try {
			if (matcher.group(4) == null)
				throw new Exception ("Parameter for the age of the Rose (Flower) not found!");

			Integer age = new Integer (matcher.group(4));
			//The NumberFormatException falls in the same category as other exceptions

			flower.age = age;
		}
		catch (Exception e) {
			if (!allowDefaults)
				throw new NoSerializedFound("Rose (Flower)", stream);
		}
		
		return flower;
	}
}


/**
 * An object representing a Fuchsia {@link Flower}
 * <br />
 * <br /> See {@link default.cfg} for the properties of the flowers
 * @see #Fuchsia()
 * @see #laySeed()
 * @see #serialize()
 * @see #deserialize(String)
 */
class Fuchsia extends Flower {
	/* Constructors */
	
	/**
	 * Sets the details for this Flower Type
	 * <br />
	 * <br /> Please see {@link default.cfg} for the actual values of the properties
	 * @see Flower#name
	 * @see Flower#type
	 * @see Flower#color
	 * @see Flower#pollen
	 * @see Flower#growthRate
	 * @see Flower#extractAmount
	 * @see Flower#lifespan
	 */
	Fuchsia () {
		super ();
		this.name = (String) Config.get("flower fuchsia name");
		this.pollen = (int) Config.get("flower fuchsia pollen");
		this.type = (int) Config.get("flower fuchsia type");
		this.color = (String) Config.get("flower fuchsia color");
		this.growthRate = (int) Config.get("flower fuchsia growth");
		this.extractAmount = (int) Config.get("flower fuchsia extract");
		this.lifespan = (int) Config.get("flower fuchsia lifespan");
	}
	
	/**
	 * Sets the details and also names a given {@link Garden} as this flower's {@link Flower#garden garden}
	 */
	Fuchsia (Garden garden) {
		this ();
		this.garden = garden;
		garden.getInterface().event("birth", this);
	}

	
	/* Extended Behavior */
	
	/**
	 * Adds a new flower of type {@link Fuchsia} to the {@link Flower#garden garden}
	 * @see Flower#laySeed()
	 * @see Flower#garden
	 */
	public boolean laySeed () {
		garden.addFlower (new Fuchsia(garden));
		return true;
	}

	
	/* Serialization */
	
	/**
	 * Serializes this Flower
	 * <br />
	 * <br /> Format: &nbsp; <b><code>fuchsia: %pollen% , %age%</code></b>
	 * <br /> <b>Note</b> the format must conform to {@value #serializedFuchsiaPattern}
	 * @see #serializedFuchsiaPattern
	 * @see #deserialize(String)
	 */
	public String serialize () {
		String stream = "";
		
		stream += "fuchsia" + ":";
		stream += serializationSpacing + pollen;
		stream += serializationSpacing + ",";
		stream += serializationSpacing + age;
		
		return stream;	
	}

	
	/* Deserialization */
	
	/**
	 * The pattern of a serialized Fuchsia
	 * <p> Currently: {@value #serializedFuchsiaPattern} </p>
	 * @see #serialize()
	 * @see #deserialize(String)
	 */
	public static final String serializedFuchsiaPattern = "\\s*(fuchsia)\\s*:\\s*(\\d+)(\\s*,\\s*(\\d+))?.*";

	/**
	 * Tries to deserialize the given stream
	 * @param stream - the stream to deserialize
	 * @return Returns a {@link Fuchsia} (if a serialized one is found)
	 * @throws NoSerializedFound if no serialized Fuchsia was recognized
	 * @see #serializedFuchsiaPattern
	 * @see #serialize()
	 */
	public static Fuchsia deserialize (String stream) throws NoSerializedFound {
		Matcher matcher = Pattern.compile(serializedFuchsiaPattern).matcher(stream);
		
		if (!matcher.find(0))
			throw new NoSerializedFound("Fuchsia (Flower)");
		
		Fuchsia flower = new Fuchsia();
		
		
		//Look for the pollen parameter
		try {
			if (matcher.group(2) == null)
				throw new Exception ("Parameter for amount of Pollen in Fuchsia (Flower) not found!");

			Integer amount = new Integer (matcher.group(2));
			//The NumberFormatException falls in the same category as other exceptions

			flower.pollen = amount;
		}
		catch (Exception e) {
			if (!allowDefaults)
				throw new NoSerializedFound("Fuchsia (Flower)", stream);
		}
		
		
		//Look for the age parameter
		try {
			if (matcher.group(4) == null)
				throw new Exception ("Parameter for the age of the Fuchsia (Flower) not found!");

			Integer age = new Integer (matcher.group(4));
			//The NumberFormatException falls in the same category as other exceptions

			flower.age = age;
		}
		catch (Exception e) {
			if (!allowDefaults)
				throw new NoSerializedFound("Fuchsia (Flower)", stream);
		}
		
		return flower;
	}
}


/**
 * An object representing a Daffodil {@link Flower}
 * <br />
 * <br /> See {@link default.cfg} for the properties of the flowers
 * @see #Daffodil()
 * @see #laySeed()
 * @see #serialize()
 * @see #deserialize(String)
 */
class Daffodil extends Flower {
	/* Constructors */
	
	/**
	 * Sets the details for this Flower Type
	 * <br />
	 * <br /> Please see {@link default.cfg} for the actual values of the properties
	 * @see Flower#name
	 * @see Flower#type
	 * @see Flower#color
	 * @see Flower#pollen
	 * @see Flower#growthRate
	 * @see Flower#extractAmount
	 * @see Flower#lifespan
	 */
	Daffodil () {
		super();
		this.name = (String) Config.get("flower daffodil name");
		this.pollen = (int) Config.get("flower daffodil pollen");
		this.type = (int) Config.get("flower daffodil type");
		this.color = (String) Config.get("flower daffodil color");
		this.growthRate = (int) Config.get("flower daffodil growth");
		this.extractAmount = (int) Config.get("flower daffodil extract");
		this.lifespan =  (int) Config.get("flower daffodil lifespan");
	}
	
	/**
	 * Sets the details and also names a given {@link Garden} as this flower's {@link Flower#garden garden}
	 */
	Daffodil (Garden garden) {
		this ();
		this.garden = garden;
		garden.getInterface().event("birth", this);
	}

	
	/* Extended Behavior */
	
	/**
	 * Adds a new flower of type {@link Daffodil} to the {@link Flower#garden garden}
	 * @see Flower#laySeed()
	 * @see Flower#garden
	 */
	public boolean laySeed () {
		garden.addFlower (new Daffodil(garden));		
		return true;
	}

	
	/* Serialization */
	
	/**
	 * Serializes this Flower
	 * <br />
	 * <br /> Format: &nbsp; <b><code>daffodil: %pollen% , %age%</code></b>
	 * <br /> <b>Note</b> the format must conform to {@value #serializedDaffodilPattern}
	 * @see #serializedDaffodilPattern
	 * @see #deserialize(String)
	 */
	public String serialize () {
		String stream = "";
		
		stream += "daffodil" + ":";
		stream += serializationSpacing + pollen;
		stream += serializationSpacing + ",";
		stream += serializationSpacing + age;
		
		return stream;	
	}

	
	/* Deserialization */
	
	/**
	 * The pattern of a serialized Daffodil
	 * <p> Currently: {@value #serializedDaffodilPattern} </p>
	 * @see #serialize()
	 * @see #deserialize(String)
	 */
	public static final String serializedDaffodilPattern = "\\s*(daffodil)\\s*:\\s*(\\d+)(\\s*,\\s*(\\d+))?.*";

	/**
	 * Tries to deserialize the given stream
	 * @param stream - the stream to deserialize
	 * @return Returns a {@link Daffodil} (if a serialized one is found)
	 * @throws NoSerializedFound if no serialized Daffodil was recognized
	 * @see #serializedDaffodilPattern
	 * @see #serialize()
	 */
	public static Daffodil deserialize (String stream) throws NoSerializedFound {
		Matcher matcher = Pattern.compile(serializedDaffodilPattern).matcher(stream);
		
		if (!matcher.find(0))
			throw new NoSerializedFound("Daffodil (Flower)");
		
		Daffodil flower = new Daffodil();
		
		
		//Look for the pollen parameter
		try {
			if (matcher.group(2) == null)
				throw new Exception ("Parameter for amount of Pollen in Daffodil (Flower) not found!");

			Integer amount = new Integer (matcher.group(2));
			//The NumberFormatException falls in the same category as other exceptions

			flower.pollen = amount;
		}
		catch (Exception e) {
			if (!allowDefaults)
				throw new NoSerializedFound("Daffodil (Flower)", stream);
		}
		
		
		//Look for the age parameter
		try {
			if (matcher.group(4) == null)
				throw new Exception ("Parameter for the age of the Daffodil (Flower) not found!");

			Integer age = new Integer (matcher.group(4));
			//The NumberFormatException falls in the same category as other exceptions

			flower.age = age;
		}
		catch (Exception e) {
			if (!allowDefaults)
				throw new NoSerializedFound("Daffodil (Flower)", stream);
		}
		
		return flower;
	}
}


/**
 * An object representing a Blue Rose {@link Flower}
 * <br />
 * <br /> See {@link default.cfg} for the properties of the flowers
 * @see #BlueRose()
 * @see #laySeed()
 * @see #serialize()
 * @see #deserialize(String)
 */
class BlueRose extends Rose {
	/* Constructors */
	
	/**
	 * Sets the details for this Flower Type
	 * <br />
	 * <br /> Please see {@link default.cfg} for the actual values of the properties
	 * @see Flower#name
	 * @see Flower#type
	 * @see Flower#color
	 * @see Flower#pollen
	 * @see Flower#growthRate
	 * @see Flower#extractAmount
	 * @see Flower#lifespan
	 */
	BlueRose () {
		super();
		this.name = (String) Config.get("flower blue rose name");
		this.pollen = (int) Config.get("flower blue rose pollen");
		this.type = (int) Config.get("flower blue rose type");
		this.color = (String) Config.get("flower blue rose color");
		this.growthRate = (int) Config.get("flower blue rose growth");
		this.extractAmount = (int) Config.get("flower blue rose extract");
		this.lifespan =  (int) Config.get("flower blue rose lifespan");
	}

	/**
	 * Sets the details and also names a given {@link Garden} as this flower's {@link Flower#garden garden}
	 */
	BlueRose (Garden garden) {
		this ();
		this.garden = garden;
		garden.getInterface().event("birth", this);
	}

	
	/* Extended Behavior */
	
	/**
	 * Adds a new flower of type {@link BlueRose} to the {@link Flower#garden garden}
	 * @see Flower#laySeed()
	 * @see Flower#garden
	 */
	public boolean laySeed () {
		garden.addFlower (new BlueRose(garden));
		return true;
	}

	
	/* Serialization */
	
	/**
	 * Serializes this Flower
	 * <br />
	 * <br /> Format: &nbsp; <b><code>bluerose: %pollen% , %age%</code></b>
	 * <br /> <b>Note</b> the format must conform to {@value #serializedBlueRosePattern}
	 * @see #serializedBlueRosePattern
	 * @see #deserialize(String)
	 */
	public String serialize () {
		String stream = "";
		
		stream += "bluerose" + ":";
		stream += serializationSpacing + pollen;
		stream += serializationSpacing + ",";
		stream += serializationSpacing + age; 
		
		return stream;	
	}

	
	/* Deserialization */
	
	/**
	 * The pattern of a serialized Blue Rose
	 * <p> Currently: {@value #serializedBlueRosePattern} </p>
	 * @see #serialize()
	 * @see #deserialize(String)
	 */
	public static final String serializedBlueRosePattern = "\\s*(bluerose)\\s*:\\s*(\\d+)(\\s*,\\s*(\\d+))?.*";

	/**
	 * Tries to deserialize the given stream
	 * @param stream - the stream to deserialize
	 * @return Returns a {@link BlueRose} (if a serialized one is found)
	 * @throws NoSerializedFound if no serialized Blue Rose was recognized
	 * @see #serializedBlueRosePattern
	 * @see #serialize()
	 */
	public static BlueRose deserialize (String stream) throws NoSerializedFound {
		Matcher matcher = Pattern.compile(serializedBlueRosePattern).matcher(stream);
		
		if (!matcher.find(0))
			throw new NoSerializedFound("BlueRose (Flower)");
		
		BlueRose flower = new BlueRose();
		
		//Look for the pollen parameter
		try {
			if (matcher.group(2) == null)
				throw new Exception ("Parameter for amount of Pollen in BlueRose (Flower) not found!");

			Integer amount = new Integer (matcher.group(2));
			//The NumberFormatException falls in the same category as other exceptions

			flower.pollen = amount;
		}
		catch (Exception e) {
			if (!allowDefaults)
				throw new NoSerializedFound("BlueRose (Flower)", stream);
		}
		
		
		//Look for the age parameter
		try {
			if (matcher.group(4) == null)
				throw new Exception ("Parameter for the age of the BlueRose (Flower) not found!");

			Integer age = new Integer (matcher.group(4));
			//The NumberFormatException falls in the same category as other exceptions

			flower.age = age;
		}
		catch (Exception e) {
			if (!allowDefaults)
				throw new NoSerializedFound("BlueRose (Flower)", stream);
		}
		
		return flower;
	}
}