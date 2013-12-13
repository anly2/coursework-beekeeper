package coursework;

/**
 * Something died =/
 * @see Death#Death(String)
 */
class Death extends Exception {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Provides a clarification message on the death
	 * @param message - the message explaining he death
	 */
	public Death (String message) {
 		super (message);
	}
}

/**
 * A Bee has died.
 * @see BeeDeath#BeeDeath(Bee, String)
 * @see Bee#anotherDay()
 */
class BeeDeath extends Death {
	protected Bee deadBee;
	protected String reason;

	/**
	 * Specifies exactly which Bee died
	 * @param bee - the Bee that died
	 * @see #Exceptions(Bee, String)
	 */
	public BeeDeath (Bee bee) {
		super ("Bee died!");
		this.deadBee = bee;
		this.reason = "";
	}

	/**
	 * Specifies exactly which Bee died and why.
	 * @param bee - the Bee that died
	 * @param reason - "the bee died of _"
	 * @see #Exceptions(Bee)
	 */
	public BeeDeath (Bee bee, String reason) {
		this (bee);
		this.reason = reason;
	}
	
	public Bee getBee () {
		return this.deadBee;
	}
	public Bee bee () {
		return this.deadBee;
	}

	public String getReason () {
		return this.reason;
	}
	public String reason () {
		return this.reason;
	}

	public String getLocalizedMessage () {
		String message = "Bee " + this.getBee().getType("as string") + " died";
		
		if (!reason.equals(""))
			message += " because of " + reason + ".";
		else
			message += "!";
		
		return message;
		// return "Bee " + this.getBee().getType("as string") + " died" + ((!reason.equals("")) ? " because of " + reason : "!" );
	}
}


/**
 * A Bee would evolve in a different type.
 * <p> Thrown when a Bee is set an inappropriate age </p>
 * @see BeeWouldEvolve#BeeWouldEvolve(Bee, Bee)
 * @see Bee#setAge(int)
 */
class BeeWouldEvolve extends RuntimeException {
	private static final long serialVersionUID = 1L;
	protected Bee beeNormal;
	protected Bee beeEvolved;

	/**
	 * Just makes a notice that a Bee would evolve
	 * @see BeeWouldEvolve#BeeWouldEvolve(Bee)
	 * @see BeeWouldEvolve
	 */
	public BeeWouldEvolve () {
		super ("A bee would evolve!");
		this.beeNormal = null;
		this.beeEvolved = null;
	}

	/**
	 * Specifies the Bee that would evolve.
	 * @param beeNormal - the Bee that would evolve
	 * @see BeeWouldEvolve#BeeWouldEvolve()
	 */
	public BeeWouldEvolve (Bee beeNormal) {
		this ();
		this.beeNormal = beeNormal;
	}
	
	/**
	 * Specifies which Bee would evolve and into what.
	 * @param beeNormal - the Bee that would evolve
	 * @param beeEvolved - the Bee that it would turn into
	 * @see BeeWouldEvolve#BeeWouldEvolve(Bee)
	 */
	public BeeWouldEvolve (Bee beeNormal, Bee beeEvolved) {
		this ();
		this.beeNormal = beeNormal;
		this.beeEvolved = beeEvolved;
	}
	
	
	public String getLocalizedMessage () {
		String message = "A bee ";

		if (this.beeNormal != null)
			message += this.beeNormal.getType("as string");
		
		message += " would ";
				
		if (this.beeEvolved != null)
			message += "turn into " + this.beeEvolved.getType("as string") + "!";
		else
			message += "evolve!";
		
		return message;
	}
	
	public Bee getNormalBee () {
		return this.beeNormal;	
	}
	public Bee getEvolvedBee () {
		return this.beeEvolved;
	}
}


/**
 * A Hive has reached its maximum capacity.
 * <p> Thrown by {@link Hive#addBee(Bee)} when {@link Hive#cells} is full</p>
 * @see HiveOverflow#HiveOverflow(Hive, Bee)
 * @see Hive#addBee(Bee)
 * @see Hive#cells
 * @see default.cfg :: hive _ size _ max
 */
class HiveOverflow extends Exception {
	private static final long serialVersionUID = 1L;
	protected Hive hive;
	protected Bee bee;
	
	/**
	 * Just makes a notice that a Hive overflowed
	 * @see HiveOverflow#HiveOverflow(Hive)
	 * @see HiveOverflow#HiveOverflow(Hive, Bee)
	 * @see HiveOverflow
	 */
	public HiveOverflow () {
		super ("Maximum number of Bees in the Hive reached!");
		this.hive = null;
		this.bee = null;
	}
	
	/**
	 * Specifies the Hive that overflowed
	 * @param hive - the Hive that overflowed
	 * @see HiveOverflow#HiveOverflow(Hive, Bee)
	 * @see HiveOverflow#HiveOverflow()
	 * @see HiveOverflow
	 */
	public HiveOverflow (Hive hive) {
		this ();
		this.hive = hive;
	}
	
	/**
	 * Specifies the Hive that overflowed and the Bee that was left out
	 * @param hive - the Hive that overflowed
	 * @param bee - the Bee that was left out
	 * @see HiveOverflow#HiveOverflow(Hive)
	 * @see HiveOverflow
	 */
	public HiveOverflow (Hive hive, Bee bee) {
		this ();
		this.hive = hive;
		this.bee = bee;
	}
	
	public String getLocalizedMessage () {
		if (this.bee == null)
			return super.getMessage();
		
		return super.getMessage() + " A "+ this.bee.getType("as string") + " was left aside.";
	}
	
	public Hive getHive () {
		return this.hive;
	}
	public Bee getBee () {
		return this.bee;
	}
}


/**
 * A requested key was not found in the loaded Configuration
 * <p> This could get thrown literally anywhere in the program because the configuration is needed everywhere </p>
 * <p> Do have a look a the configuration file you are loading. (Default is &nbsp; <b><code>default.cfg</code></b>) </p>
 * @see Config
 * @see Configuration
 * @see default.cfg
 */
class ConfigKeyNotFound extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Specify which key was not found
	 * @param key - the key that is undefined
	 * @see ConfigKeyNotFound
	 */
	public ConfigKeyNotFound (String key) {
		super ("Cannot find key \""+key+"\" in this configuration!");
	}
}


/**
 * A desirialization method failed to find a serialized object.
 * <p> This gets thrown whenever a stream does not match a serializedObject pattern </p>
 * Please see {@link Garden#deserialize(String)} and follow the links to the other deserialization methods.
 * @see NoSerializedFound#NoSerializedFound(String)
 * @see NoSerializedFound#NoSerializedFound(String, String)
 */
class NoSerializedFound extends Exception {
	private static final long serialVersionUID = 1L;
	protected String type;
	protected String stream;
	
	/**
	 * Specifies what object was looked for but was not found.
	 * @param typeNotFound - the object looked for but not found.
	 * @see NoSerializedFound#NoSerializedFound(String, String)
	 */
	public NoSerializedFound (String typeNotFound) {
		super (typeNotFound);
		type = typeNotFound;
		stream = null;
	}

	/**
	 * Specifies what object was looked for and the stream looked in.
	 * @param typeNotFound - the object looked for but not found.
	 * @param stream - the stream looked in for a serialized object
	 */
	public NoSerializedFound (String typeNotFound, String stream) {
		this (typeNotFound);
		this.stream = stream;
	}
	
	@Override
	public String getLocalizedMessage() {
		String message = "No Serialized Object of type "+type+" found!";
		
		if (stream != null)
			message += "\nSource: "+stream;
		
		return message;
	}
}

/**
 * Something went wrong during a deserialization.
 * <p> Please see {@link Garden#deserialize(String)} and follow the links to the other deserialization methods. </p>
 */
class DeserializationError extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DeserializationError (String message) {
		super (message);
	}
}

/**
 * A custom exception to show that the user decided NOT to overwrite a file.
 * Please see {@link Beekeeper#save(Garden)}
 */
class AbortWriting extends RuntimeException {
	private static final long serialVersionUID = 1L;
	protected String file;

	/**
	 * Notifies that a file's overwriting was stopped.
	 * @see AbortWriting#AbortWriting(String)
	 */
	public AbortWriting () {
		super ("Overwritting of a file aborted.");
	}
	
	/**
	 * Specifies which file's overwriting was aborted
	 * @param file - the file that will not get overwritten
	 * @see AbortWriting#AbortWriting()
	 */
	public AbortWriting (String file) {
		super ("Overwritting of file \""+file+"\" aborted.");
		this.file = file;
	}
	
	public String getFile () {
		return this.file;
	}
	public String file () {
		return this.file;	
	}
}


/**
 * A negative value was given for a parameter that can only be zero or more.
 * @see Flower#setPollen(int)
 * @see Flower#extractPollen(int)
 * @see NegativeAmountException#NegativeAmountException(String)
 */
class NegativeAmountException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Specifies a clarification message to further explain the error that occured.
	 * @param message - an explanation message 
	 * @see NegativeAmountException
	 */
	public NegativeAmountException (String message) {
		super (message);
	}
}

/**
 * A flower has no pollen for a Worker bee to extract
 * <p> Please see {@link Flower#extractPollen()} </p>
 * @see OutOfPollen#OutOfPollen(Flower)
 */
class OutOfPollen extends Exception {
	private static final long serialVersionUID = 1L;
	protected Flower exhaustedFlower;

	/**
	 * Specify which flower was out of pollen
	 * @param exhaustedFlower - the flower that had no pollen left
	 */
	public OutOfPollen (Flower exhaustedFlower) {
		super ("A " + exhaustedFlower.color + " " + exhaustedFlower.name + " flower ran out of pollen.");
		this.exhaustedFlower = exhaustedFlower;
	}
	
	
	public Flower getFlower () {
		return this.exhaustedFlower;
	}
	
	public Flower flower () {
		return this.exhaustedFlower;
	}
}

/**
 * A contending Queen has been left out alone.
 * <p> Please see Hive#appointQueen(Queen) </p>
 * @see QueenLeftOut#QueenConflict()
 */
class QueenLeftOut extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * Just makes a notice that a Queen has been left out.
	 * @see QueenLeftOut
	 */
	public QueenLeftOut () {
		super ("A Queen has been left out alone!");
	}
}


/**
 * A colony creation failed because they were not allowed enough resources.
 * <p> Please see {@link Hive#colonize(Queen)} for details </p>
 */
class ColonyResourceLack extends Exception {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Specify what resource was in need and insufficient
	 * @param resource - the demanded resource which was not enough
	 * @see ColonyResourceLack
	 */
	public ColonyResourceLack (String resource) {
		super ("Not enough "+resource+" for the new colony");
	}
}