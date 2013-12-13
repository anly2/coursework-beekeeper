package coursework;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * An object that represents a Garden with Flowers and Hives
 * @see #anotherDay()
 * @see Flower
 * @see Hive
 * @see #serialize()
 * @see #deserialize(String)
 */
public class Garden {
	/* Properties */
	protected ClientInterface clientInterface;
	protected ArrayList<Flower> flowers;
	protected ArrayList<Hive> hives;
	
	
	/* Constructors */
	
	/**
	 * Default constructor that initializes the instance variables
	 */
	public Garden () {
		flowers = new ArrayList<Flower>(); //start with an empty garden
		hives = new ArrayList<Hive>(); //start with an empty garden
		clientInterface = null;
	}

	
	public Garden (ClientInterface intf) {
		this ();
		setInterface (intf);
	}
	
	
	/* Sample Filling */
	
	/**
	 * Adds a random number of flowers to this Garden
	 * <br />
	 * <br /> For each type of Flower adds a random number between 0 and 10 of them.
	 * <ul>
	 * 		Types of flowers:
	 * 		<li> {@link Rose} </li>
	 * 		<li> {@link Fuchsia} </li>
	 * 		<li> {@link Daffodil} </li>
	 * 		<li> {@link BlueRose} </li>
	 * </ul>
	 * @see Rand#number(int)
	 */
	protected void addSampleFlowers() {
		//Add Daffodil flowers
		for (int i = Rand.number(10); i >= 0; i--)
			flowers.add(new Daffodil(this));
		
		//Add Fuchsia flowers
		for (int i = Rand.number(10); i >= 0; i--)
			flowers.add(new Fuchsia(this));
		
		//Add (Red) Rose flowers
		for (int i = Rand.number(10); i >= 0; i--)
			flowers.add(new Rose(this));
		
		//Add Blue Rose flowers
		for (int i = Rand.number(10); i >= 0; i--)
			flowers.add(new BlueRose(this));
	}


	/* Setters */

	/**
	 * Add a Flower to this Garden
	 * @param flower - the Flower to be added
	 * @see #flowers
	 */
	public void addFlower (Flower flower) {
		flowers.add(flower);
		flower.garden = this;
	}

	/**
	 * Adds a Hive to this Garden
	 * @param hive - the Hive to be added
	 * @see #hives
	 */
	public void addHive (Hive hive) {
		hives.add(hive);
		hive.garden = this;
	}
	
	
	public void setInterface (ClientInterface clientInterface) {
		this.clientInterface = clientInterface;
	}
	
	
	/* Getters */
	
	/**
	 * Gets a Flower in the garden.
	 * <p> <b>Note</b> that a dead flower is represented by <code>null</code> </p>
	 * 
	 * @param n - the index of the flower wanted
	 * @return Returns the <code>n-th</code> Flower in this garden.
	 * <br /> or null if there was no such flower
	 *
	 * @see #findFlower()
	 * @see #flowers
	 * @see #size()
	 * @see #addFlower(Flower)
	 */
	public Flower getFlower (int n) {
		if (n < 0 || n >= this.flowers.size())
			return null;
		
		return this.flowers.get(n);
	}

	/**
	 * Gets the number of flowers in the garden.
	 * @return Returns the size of {@link #flowers}
	 * @see #flowers
	 * @see #getFlower(int)
	 * @see #findFlower()
	 */
	public int size () {
		return this.flowers.size();
	}
	
	
	public ClientInterface getInterface () {
		return this.clientInterface;
	}
	

	/* Main Methods */
	
	/**
	 * Finds a random Flower using {@link Rand#number(int)}
	 * @return Returns a random Flower from this Garden 
	 */
	public Flower findFlower () {
		if (flowers.size() <= 0)
			return null;
		
		return flowers.get(Rand.number(flowers.size()));
	}
	
	
	/* Recursive Convenience */
	
	/**
	 * Calls anotherDay() of all Flowers and all Hives in the Garden	
	 * @return Returns the Garden itself
	 * @throws Death if there are no hives in this Garden
	 * @see Flower#anotherDay()
	 * @see Hive#anotherDay()
	 */
	public Garden anotherDay () throws Death {
		//System.out.println(); 
		getInterface().event("newDay", this);
		
		for (int i = 0; i < hives.size(); i++) {
			try {
				hives.set(i, hives.get(i).anotherDay());
			}
			catch (Death e) {
				//System.out.println("\t" + e.getMessage());
				getInterface().event("death", hives.get(i), e);
				hives.remove(i--);
			}
		}
		
		//If all the hives have perished
		if (hives.size() <= 0)
			throw new Death ("The Garden feels dead!");
		
		//Have the flowers grow
		for (int i = 0; i < flowers.size(); i++) {
			try {
				flowers.set(i, flowers.get(i).anotherDay());
			}
			catch (Death e) {
				//System.out.println("\t\t" + "("+i+") " + e.getMessage());
				getInterface().event("death", flowers.get(i), e);
				flowers.remove(i--);
			}
		}
		
		return this; //allows chain calls
	}
	
	/**
	 * Provides a human-readable representation of the Garden
	 * <br />
	 * <br /> Calls {@link Flower#toString() .toString()} on each Flower
	 * <br /> Calls {@link Hive#toString() .toString()} on each Hive.
	 * <br />
	 * <br /> Pads appropriately and returns the resulting String.
	 * @return Returns a human-readable String representation of the Garden
	 * @see Beekeeper#main(String[]) Beekeeper.main() -> <code> println(garden)</code> 
	 */
	public String toString () {
		String raw = " - Garden - \n";
		
		raw += "\t- Flowers -\n";
		
		Iterator<Flower> iFlower = flowers.iterator();
		while (iFlower.hasNext()) {
			Flower flower = iFlower.next();
			raw += "\t\t" + ((flower != null) ? flower.toString() : "Dead flower!") + "\n";
		}

		raw += "\n\n\t- Hives -\n";
		
		ListIterator<Hive> iHive = hives.listIterator();
		while (iHive.hasNext()) {
			raw += "\t\tHive "+iHive.nextIndex() +":\n";
			
			Hive hive = iHive.next();
			
			raw += "\t\t" + ((hive != null) ? hive.toString().replaceAll("\n", "\n\t\t") : "The Hive has perished!" )+ (iHive.hasNext()? "\n" : "");
		}
		
		
		return raw;
	}

	
	
	/* Serialization */

	/**
	 * The padding that is applied to every element in the garden (Flowers and Hives)
	 * <br />
	 * <br /> Current padding: {@value #serializationPadding}
	 * <br /> Recommended padding: <code>"\t"</code>
	 * @see #serialize()
	 */
	protected static final String serializationPadding = ""; // "\t";

	/**
	 * Serialize this Garden object into a near-readable String.
	 * <br />
	 * <br /> Begins with a header for the garden definition. Please see {@link #serializedGardenPattern}
	 * <br /> Then serializes every flower in this garden. See {@link Flower#serialize()}
	 * <br /> Then serializes every hive in this garden. See {@link Hive#serialize()}
	 * @return Returns a String describing this Garden
	 * @see #serializationPadding
	 */
	public String serialize () {
		String stream = "";
		
		stream += "garden:\n";
		
		for (Flower flower: this.flowers) 
			stream += serializationPadding + ((flower != null) ? flower.serialize() : "null") + "\n";
		
		for (Hive hive: this.hives) 
			stream += serializationPadding + ((hive != null) ? hive.serialize().replaceAll("\n", "\n"+serializationPadding)  : "null") + "\n";
		
		return stream;
	}

	
	/* Deserialization */
	
	/**
	 * The format of the header of a serialized garden.
	 * @see #deserialize(String)
	 */
	public static final String serializedGardenPattern = "\\s*(garden)\\s*:.*";
	private boolean serializationMentioned = false;
	
	/**
	 * Tries to deserialize the given line.
	 * 
	 * <p> If the String <code>line</code> is multiline, split it and send it to {@link #deserialize(String[])} </p>
	 * <p> A garden definition may begin with a line like {@value #serializedGardenPattern}
	 * 
	 * <ul>
	 * 		Looks for:
	 * 		<li> a Flower. See {@link Flower#deserialize(String)}. </li>
	 * 		<li> a Hive. See {@link Hive#deserialize(String)}. </li>
	 * 		<li> a Bee. See {@link Bee#deserialize(String)}. </li>
	 * </ul>
	 * 
	 * @param line - 
	 * @return Returns the Garden object itself with any effects of deserialization applied.
	 * @throws DeserializationError if a second garden definition was encountered.
	 * @throws NoSerializedFound if a line of unknown format was encountered
	 */
	public Garden deserialize (String line) throws DeserializationError, NoSerializedFound {
		//See if this is a proper single line
		if (line.contains("\n"))
			return deserialize(line.split("\n"));
		
		
		//If this is not the only definition of a garden in the deserialized stream  
		if (line.matches(serializedGardenPattern)) {
			if (serializationMentioned)
				throw new DeserializationError("Encountered a second garden definition!");
			
			serializationMentioned = true;
			return this;
		}
		
		
		//See if this line is a serialized Flower
		try {
			this.addFlower (Flower.deserialize (line));
			return this;
		}
		catch (NoSerializedFound e) {}
		
		
		//See if this line is a serialized Hive
		try {
			this.addHive (Hive.deserialize (line));
			return this;
		}
		catch (NoSerializedFound e) {}
		
		
		//See if this line is a serialized Bee
		try {
			Bee bee = Bee.deserialize (line);
			
			if (this.hives.size() <= 0)
				throw new DeserializationError ("There is a Bee not allocated to a Hive");

			
			try {
				this.hives.get(this.hives.size()-1).addBee(bee);
			}
			catch (HiveOverflow e) {
				System.err.println(e);
			}
			
			return this;
		}
		catch (NoSerializedFound e) {}
		
		
		//If no serialized object was recognized
		throw new NoSerializedFound("any", line);
	}

	/**
	 * Calls {@link #deserialize(String)} for each line.
	 * @param lines - an array of lines to deserialize
	 * @return Returns the Garden object itself with any effects of deserialization applied.
	 * @throws DeserializationError if a second garden definition was encountered.
	 */
	public Garden deserialize (String[] lines) throws DeserializationError {
		for (String line: lines) {
			try {
				deserialize (line);
			}
			catch (NoSerializedFound e) {} //Just one wrong line shouldn't ruin the whole deserialization (for example, a blank line..)
		}
		
		return this;
	}
	
	/**
	 * Calls {@link #deserialize(String)} for each line.
	 * @param reader - the reader of the stream for which to deserialize the lines
	 * @return Returns the Garden object itself with any effects of deserialization applied.
	 * @throws DeserializationError if a second garden definition was encountered.
	 */
	public Garden deserialize (BufferedReader reader) throws DeserializationError {
		String line;
		
		try{
			while ((line = reader.readLine()) != null) {
				try {
					deserialize (line);
				}
				catch (NoSerializedFound e) {} //Just one wrong line shouldn't ruin the whole deserialization (for example, a blank line..)
			}
		}
		catch (IOException e) {
			System.err.println("An Error occured while reading lines.");
		}
		
		return this;
	}
	
	
	/* Deserialization Factory Methods */
	
	/**
	 * A factory method for {@link #deserialize(String)}
	 * @param lines - the array of lines to load from
	 * @return Returns a Garden loaded from the provided lines
	 */
	public static Garden load (String [] lines) {
		Garden newGarden = new Garden ();
		newGarden.deserialize(lines);
		return newGarden;
	}
	
	/**
	 * A factory method for {@link #deserialize(BufferedReader)}
	 * @param lines - the stream to load from
	 * @return Returns a Garden loaded from the stream
	 */
	public static Garden load (BufferedReader lines) {
		Garden newGarden = new Garden ();
		newGarden.deserialize(lines);
		return newGarden;
	}
}