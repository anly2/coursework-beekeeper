package coursework;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An object that represents a Hive with a Queen and Bees
 * @see #Hive()
 * @see #queen
 * @see #cells
 * @see #serialize()
 * @see #deserialize(String)
 */
public class Hive
{
	/* Instance Variables */
	
	/**
	 * An array of cells with bees
	 * @see Bee
	 * @see #addBee(Bee)
	 * @see #anotherDay()
	 * @see #beeIterator()
	 * @see #size()
	 * @see #colonize(Queen)
	 */
	protected ArrayList<Bee> cells; //array of cell_index => Bee
	/**
	 * A hashmap with the different food types and the amount stored in the hive.
	 * @see Food
	 * @see #addFood(Food, int)
	 * @see #takeFood(Food, int)
	 * @see #hasFood(Food, int)
	 * @see #getFood(Food)
	 * @see #colonize(Queen)
	 */
	protected HashMap<Food, Integer> storage; //food store
	/**
	 * The queen of this hive.
	 * @see Queen
	 * @see #appointQueen(Queen)
	 * @see #anotherDay()
	 * @see #colonize(Queen)
	 */
	protected Queen queen;
	/**
	 * The garden the hive is in.
	 * @see Garden
	 * @see Garden#anotherDay()
	 * @see Garden#findFlower()
	 */
	protected Garden garden;


	/* Constructors */
	
	/**
	 * Initialize the instance variables
	 * @see #Hive(Queen)
	 * @see #Hive(Queen, Garden)
	 */
	Hive () {
		this.queen = null;
		this.cells = new ArrayList<Bee>(); //start with empty cells
		this.storage = new HashMap<Food, Integer>();
		this.garden = null; //should be overwritten
		
		this.storage = new HashMap<Food, Integer>();
		for (Food type: Food.values())
			this.storage.put(type, 0);
	}

	/**
	 * Initializes and assigns a Hive Queen
	 * @param queen - the queen of this new hive
	 * @see #Hive()
	 * @see #Hive(Queen, Garden)
	 */
	Hive (Queen queen) {
		this ();
		
		try {
			this.appointQueen (queen);
		}
		catch (QueenLeftOut e) {
			//System.out.println("\t\t" + e.getMessage());
			garden.getInterface().event("queenLeftOut", queen, e);
		}
	}

	/**
	 * Initializes and assigns this Hive to a Garden
	 * @param garden - the garden to assign this hive to
	 * @see #Hive()
	 * @see #Hive(Queen, Garden)
	 */
	Hive (Garden garden) {
		this ();
		this.garden = garden;
		garden.getInterface().event("birth", this);
	}

	/**
	 * Initializes, assigns a Hive Queen and adds this Hive to a Garden
	 * @param queen - the queen of this new hive
	 * @param garden - the garden this hive is in
	 * @see #Hive()
	 */
	Hive (Queen queen, Garden garden) {
		this ();
		this.queen = queen;
		this.garden = garden;
		garden.getInterface().event("birth", this);
	}
	
	/**
	 * Alias of {@link #Hive(Queen, Garden)}
	 */
	Hive (Garden garden, Queen queen) {
		this (queen, garden);
	}

	
	/* Sample Filling */
	
	/**
	 * Adds a random number of eggs to the hive
	 * <p> The number of eggs is between <b>0</b> and <b>5</b> </p>
	 * @see Egg
	 * @see #Hive()
	 * @see #initialFood()
	 */
	public void addSampleEggs () {
		//Add a random number of eggs
		try {
			for (int i = Rand.number(5); i >= 0; i--)
				addBee(new Egg(this));
		}
		catch (HiveOverflow e) {
			//System.err.println(e);
			garden.getInterface().event("hiveOverflow", this, e);
		}
	}

	/**
	 * Adds the needed amount of food to the hive.
	 * 
	 * <p> The configuration file (<code>default.cfg</code>)
	 * specifies the amount of food required for a new colony
	 * to be successfully established. <br />
	 * Essentially, that describes what a new Hive would need to start with.</p>
	 * 
	 *  <p>Please see {@link default.cfg} for more details</o>
	 *  @see #addSampleEggs()
	 *  @see #storage
	 */
	public void initialFood () {
		//have some initial food reserves
		addHoney ((int) Config.get("colony needed food honey"));
		addRoyalJelly ((int) Config.get("colony needed food royaljelly"));
		addPollen ((int) Config.get("colony needed food pollen"));
	}

	
	/* Add-Food Methods */
	
	/**
	 * Adds some amount of a given food to the hive's storage.
	 * 
	 * <p>Make sure you don't skip the intializing constructor {@link #Hive()} </p>
	 * 
	 * @param type - the type of food to add
	 * @param quantity - the amount of food to add
	 * 
	 * @see #storage
	 * @see #addHoney(int)
	 * @see #addRoyalJelly(int)
	 * @see #addPollen(int)
	 */
	public void addFood (Food type, int quantity)
	{
		this.storage.put(type, this.storage.get(type) + quantity); //storage[type] = storage[type] + quantity
	}

	/**
	 * Adds some amount of {@link Food#Honey Honey} to the hive's storage. 
	 * <p> This is just a convenience method. </p>
	 * <p> Alias of {@link #addFood(Food, int) addFood(Food.Honey, int)} </p>
	 * @see #addRoyalJelly(int)
	 * @see #addPollen(int)
	 */
	public void addHoney(int quantity) {
		addFood(Food.Honey, quantity);
	}

	/**
	 * Adds some amount of {@link Food#RoyalJelly Royal Jelly} to the hive's storage. 
	 * <p> This is just a convenience method. </p>
	 * <p> Alias of {@link #addFood(Food, int) addFood(Food.RoyalJelly, int)} </p>
	 * @see #addHoney(int)
	 * @see #addPollen(int)
	 */
	public void addRoyalJelly(int quantity) {
		addFood(Food.RoyalJelly, quantity);
	}

	/**
	 * Adds some amount of {@link Food#Pollen Pollen} to the hive's storage. 
	 * <p> This is just a convenience method. </p>
	 * <p> Alias of {@link #addFood(Food, int) addFood(Food.Pollen, int)} </p>
	 * @see #addHoney(int)
	 * @see #addRoyalJelly(int)
	 */
	public void addPollen(int quantity) {
		addFood(Food.Pollen, quantity);
	}
	

	/* Take-Food Methods */

	/**
	 * Take some amount of a given food from the hive's storage.
	 * 
	 * <p> If there wasn't as much food of that type as requested
	 * 		takes all the available food and returns that amount. </p>
	 * 	
	 * <p> Make sure you don't skip the intializing constructor {@link #Hive()} </p>
	 * 
	 * @param type - the type of food to take
	 * @param quantity - the amount of food to take
	 * @return Returns the amount of food actually taken
	 * @throws RuntimeException if a negative amount of food was requested
	 * 
	 * @see #storage
	 * @see #takeHoney(int)
	 * @see #takeRoyalJelly(int)
	 * @see #takePollen(int)
	 */
	public int takeFood(Food type, int quantity) throws RuntimeException
	{
		if (quantity < 0)
			throw new RuntimeException ("Cannot take a negative quantity of food!");
		
		if (storage.get(type) < quantity)
			quantity = storage.get(type);
		
		storage.put(type, storage.get(type) - quantity);
		return quantity;
	}

	/**
	 * Takes some amount of {@link Food#Honey Honey} from the hive's storage. 
	 * <p> This is just a convenience method. </p>
	 * <p> Alias of {@link #takeFood(Food, int) takeFood(Food.Honey, int)} </p>
	 * @throws RuntimeException if a negative amount of food was requested
	 * @see #takeRoyalJelly(int)
	 * @see #takePollen(int)
	 */
	public int takeHoney(int quantity) {
		return takeFood(Food.Honey, quantity);
	}
	
	/**
	 * Takes some amount of {@link Food#RoyalJelly Royal Jelly} from the hive's storage. 
	 * <p> This is just a convenience method. </p>
	 * <p> Alias of {@link #takeFood(Food, int) takeFood(Food.RoyalJelly, int)} </p>
	 * @throws RuntimeException if a negative amount of food was requested
	 * @see #takeHoney(int)
	 * @see #takePollen(int)
	 */
	public int takeRoyalJelly(int quantity) {
		return takeFood(Food.RoyalJelly, quantity);
	}
	
	/**
	 * Takes some amount of {@link Food#Pollen Pollen} from the hive's storage. 
	 * <p> This is just a convenience method. </p>
	 * <p> Alias of {@link #takeFood(Food, int) takeFood(Food.Pollen, int)} </p>
	 * @throws RuntimeException if a negative amount of food was requested
	 * @see #takeHoney(int)
	 * @see #takeRoyalJelly(int)
	 */
	public int takePollen(int quantity) {
		return takeFood(Food.Pollen, quantity);
	}
	

	/* Get-Food Methods */
	
	/**
	 * Get the amount of a given food in the hive's storage.
	 * 
	 * @param type - the type of food to count
	 * @return Returns the amount of food in the hive's storage
	 * 
	 * @see #storage
	 * @see #getHoney(int)
	 * @see #getRoyalJelly(int)
	 * @see #getPollen(int)
	 */
	public int getFood(Food type)
	{
		return this.storage.get(type);
	}

	/**
	 * Get the amount of {@link Food#Honey Honey} in the hive's storage. 
	 * <p> This is just a convenience method. </p>
	 * <p> Alias of {@link #getFood(Food, int) getFood(Food.Honey, int)} </p>
	 * @see #getRoyalJelly(int)
	 * @see #getPollen(int)
	 */
	public int getHoney() {
		return getFood(Food.Honey);
	}
	
	/**
	 * Get the amount of {@link Food#RoyalJelly Royal Jelly} in the hive's storage. 
	 * <p> This is just a convenience method. </p>
	 * <p> Alias of {@link #getFood(Food, int) getFood(Food.RoyalJelly, int)} </p>
	 * @see #getHoney(int)
	 * @see #getPollen(int)
	 */
	public int getRoyalJelly() {
		return getFood(Food.RoyalJelly);
	}

	/**
	 * Get the amount of {@link Food#Pollen Pollen} in the hive's storage. 
	 * <p> This is just a convenience method. </p>
	 * <p> Alias of {@link #getFood(Food, int) getFood(Food.Pollen, int)} </p>
	 * @see #getHoney(int)
	 * @see #getRoyalJelly(int)
	 */
	public int getPollen() {
		return getFood(Food.Pollen);
	}
	
	
	/* Has-Food Methods */
	
	/**
	 *  Check if there is some amount of given food in the hive storage.
	 * 
	 * @param type - the type of food to check for
	 * @return Returns the amount of food to check for
	 * 
	 * @see #storage
	 * @see #hasHoney(int)
	 * @see #hasRoyalJelly(int)
	 * @see #hasPollen(int)
	 */
	public boolean hasFood(Food type, int quantity)
	{
		return (this.storage.get(type) >= quantity); // A >= B evaluates to boolean and then gets returned
	}

	/**
	 * Check if there is some amount of {@link Food#Honey Honey} in the hive's storage. 
	 * <p> This is just a convenience method. </p>
	 * <p> Alias of {@link #hasFood(Food, int) hasFood(Food.Honey, int)} </p>
	 * @see #hasRoyalJelly(int)
	 * @see #hasPollen(int)
	 */
	public boolean hasHoney(int quantity) {
		return hasFood(Food.Honey, quantity);
	}

	/**
	 * Check if there is some amount of {@link Food#RoyalJelly Royal Jelly} in the hive's storage. 
	 * <p> This is just a convenience method. </p>
	 * <p> Alias of {@link #hasFood(Food, int) hasFood(Food.RoyalJelly, int)} </p>
	 * @see #hasHoney(int)
	 * @see #hasPollen(int)
	 */
	public boolean hasRoyalJelly(int quantity) {
		return hasFood(Food.RoyalJelly, quantity);
	}

	/**
	 * Check if there is some amount of {@link Food#Pollen Pollen} in the hive's storage. 
	 * <p> This is just a convenience method. </p>
	 * <p> Alias of {@link #hasFood(Food, int) hasFood(Food.Pollen, int)} </p>
	 * @see #hasHoney(int)
	 * @see #hasRoyalJelly(int)
	 */
	public boolean hasPollen(int quantity) {
		return hasFood(Food.Pollen, quantity);
	}
	
	
	/* Add Queen Methods */
	
	/**
	 * Appoint a new Queen.
	 * 
	 * <p> Creates a new {@link Queen} object and assigns it as this hive's queen. </p>
	 * 
	 * <p> Please see {@link #appointQueen(Queen)} for the more interesting method. </p>
	 * 
	 * @see #queen
	 * @see #appointQueen(Queen)
	 */
	public void appointQueen () {
		try {
			appointQueen (new Queen(this));
		}
		catch (QueenLeftOut e) {
			//System.out.println("\t\t" + e.getMessage());
			garden.getInterface().event("queenLeftOut", null, e);
		}
	}

	/**
	 * Try to appoint a given Queen as this hive's queen
	 * 
	 * <p> If the hive already has a queen,
	 * 		the new queen will only replace it
	 * 		if it has more health than the old queen.
	 * </p>
	 * 
	 * <p> Regardless, the losing queen will move out and try to establish a colony.
	 * 		<br /> Please see {@link #colonize(Queen)} for more details on that.
	 * </p>
	 * 
	 * @param queen - the new queen, contender for the throne
	 * @return Returns <b>true</b> if the contender queen became this hive's queen
	 * 	<br /> or <b>false</b> otherwise (regardless of whether a colony has been established).
	 * 
	 * @see #queen
	 * @see #appointQueen()
	 * @see #colonize(Queen)
	 */
	public boolean appointQueen (Queen queen) throws QueenLeftOut {
		// Imagine a fight for the "throne" has to occur :O
		// Hence the bool return type
		
		if (this.queen == null) {
			this.queen = queen;
			queen.hive = this;
			
			return true;
		}
		
		//Fight for the throne
		Queen loser;
		if (this.queen.health < queen.health) {
			loser = this.queen;

			this.queen = queen;
			queen.hive = this;
		}
		else
			loser = queen;


		try {
			colonize (loser);
		}
		catch (ColonyResourceLack e) {
			//The losing Queen failed to establish a new hive
			throw new QueenLeftOut();
		} 
		
		
		return (this.queen == queen);
	}

	
	/* Colonize - Extended Behavior */
	
	/**
	 * Try to establish a new Hive with the specified Queen as leader
	 *
	 * <p> For each of the following resources (list below) <br />
	 * 		sees how much will be needed, <br />
	 * 		calculates how much is available <br />
	 * 		and if enough, takes some amount.
	 * </p>
	 * 
	 * <ul>
	 * 		Hive resources:
	 * 		<li> {@link Food#Honey Honey} - see {@link #storage} and {@link #getHoney()} </li>
	 * 		<li> {@link Food#RoyalJelly Royal Jelly} - see {@link #storage} and {@link #getRoyalJelly()} </li>
	 * 		<li> {@link Food#Pollen Pollen} - see {@link #storage} and {@link #getPollen()} </li>
	 * 		<br /><br />
	 * 		<li> {@link Worker Workers} - see {@link Worker} and {@link #cells} </li>
	 * 		<li> {@link Drone Drones} - see {@link Drone} and {@link #cells} </li>
	 * </ul>
	 * 
	 * <p> If there was enough of all resources, <br />
	 * 		the colony takes a random amount of each <br />
	 * 		between the needed and the available. <br />
	 * 		( <code>take rand(needed, available)</code> )
	 * </p>
	 * 
	 * @param colonyQueen - the Queen that will lead the new colony
	 * @return Returns the colony's new Hive
	 * @throws ColonyResourceLack if something needed for the new hive was insufficient
	 * 
	 * @see Rand#number(int, int)
	 * @see #appointQueen(Queen)
	 * @see Hive
	 * @see default.cfg :: colony
	 */
	public Hive colonize (Queen colonyQueen) throws ColonyResourceLack {
		/* Check any requirements for establishing a new colony */
		
			//See if the colony needs any Honey
			int neededHoney = (int) Config.get("colony needed food honey");
			int allowedHoney = -1;
			
			if (neededHoney >= 0) //should be ">" for optimization
			{
				//Calculate how much Honey the colony is allowed to take
				allowedHoney = (int) ((getHoney() * ((int) Config.get("colony allowed food honey"))) / 100);
				
				//Check if that is enough
				if (allowedHoney < neededHoney)
					throw new ColonyResourceLack ("Honey");
			}
			
			
			//See if the colony needs any Royal Jelly
			int neededJelly = (int) Config.get("colony needed food royaljelly");
			int allowedJelly = -1;
			
			if (neededJelly >= 0) //should be ">" for optimization
			{
				//Calculate how much Royal Jelly the colony is allowed to take
				allowedJelly = (int) ((getRoyalJelly() * ((int) Config.get("colony allowed food royaljelly"))) / 100);
	
				//Check if that is enough
				if (allowedJelly < neededJelly)
					throw new ColonyResourceLack ("Royal Jelly");
			}
	
			
			//See if the colony needs any Pollen
			int neededPollen = (int) Config.get("colony needed food pollen");
			int allowedPollen = -1;
			
			if (neededPollen >= 0) //should be ">" for optimization
			{
				//Calculate how much Pollen the colony is allowed to take
				allowedPollen = (int) ((getPollen() * ((int) Config.get("colony allowed food pollen"))) / 100);
	
				//Check if that is enough
				if (allowedPollen < neededPollen)
					throw new ColonyResourceLack ("Pollen");
			}
	
			
			//See if the colony needs any Workers
			int neededWorkers = (int) Config.get("colony needed workers");
			int allowedWorkers = -1;
			ArrayList<Worker> workers = new ArrayList<Worker>(); //needs to be in this scope
			
			if (neededWorkers >= 0) // should be ">" for optimization
			{
				//Calculate how many Workers the colony is allowed to take				
				for (Bee bee: cells)
					if (bee instanceof Worker)
						workers.add((Worker) bee);
				
				allowedWorkers =  (int) ((workers.size() * ((int) Config.get("colony allowed workers"))) / 100); //casting to int rounds down
				
				//Check if that is enough
				if (allowedWorkers < neededWorkers)
					throw new ColonyResourceLack ("Workers");
			}
			
			
			//See if the colony needs any Drones
			int neededDrones = (int) Config.get("colony needed drones");
			int allowedDrones = -1;
			ArrayList<Drone> drones = new ArrayList<Drone>(); //needs to be in this scope
			
			if (neededDrones >= 0) //should be ">" for optimization
			{
				//Calculate how many Drones the colony is allowed to take				
				for (Bee bee: cells)
					if (bee instanceof Drone)
						drones.add((Drone) bee);
				
				allowedDrones =  (int) ((drones.size() * ((int) Config.get("colony allowed drones"))) / 100); //casting to int rounds down
				
				//Check if that is enough
				if (allowedDrones < neededDrones)
					throw new ColonyResourceLack ("Drones");
			}

			
		/* All requirements are met, organize a new Hive */
			Hive colony = new Hive (colonyQueen);
		
		
		/* Take the required */
			//Take the required food at least
			if (neededHoney > 0 && allowedHoney > 0)
				colony.addHoney (takeHoney (Rand.number(neededHoney, allowedHoney)));
			
			if (neededJelly > 0 && allowedJelly > 0)
				colony.addRoyalJelly (takeRoyalJelly (Rand.number(neededJelly, allowedJelly)));
			
			if (neededPollen > 0 && allowedPollen > 0)
				colony.addPollen (takePollen (Rand.number(neededPollen, allowedPollen)));
			
			
			//Take the required Workers at least
			if (neededWorkers > 0 && allowedWorkers > 0)
			{
				int i = 0;
				int colonyWorkers = Rand.number(neededWorkers, allowedWorkers);
				for (Worker worker: workers) {
					if (i++ >= colonyWorkers) break;
					
					try {
						colony.addBee (worker);
						cells.set(cells.indexOf(worker), null); //cells.remove(worker);
					}
					catch (HiveOverflow e) {
						//System.err.println("The colony got full! A Worker was left behind.");
						garden.getInterface().event("hiveOverflow", this, e, "The colony got full! A Worker was left behind.");
					}
				}
			}
			
			//Take the required Drones at least
			if (neededDrones > 0 && allowedDrones > 0) 
			{
				int i = 0;
				int colonyDrones = Rand.number(neededDrones, allowedDrones);
				for (Drone drone: drones) {
					if (i++ >= colonyDrones) break;
					
					try {
						colony.addBee (drone);
						cells.set(cells.indexOf(drone), null); //cells.remove(drone);
					}
					catch (HiveOverflow e) {
						//System.err.println ("The colony got full! A Drone was left behind.");
						garden.getInterface().event("hiveOverflow", this, e, "The colony got full! A Drone was left behind.");
					}
				}
			}
			
		/* Set up the new Hive in the same garden */
			garden.addHive(colony);
			garden.getInterface().event("birth", colony);
			
		return colony;
	}
	
	
	/* Manage Bees */
	
	/**
	 * Adds a new Bee into the next available cell
	 * 
	 * <p> If the bee is a Queen it is handled by {@link #appointQueen(Queen)} </p>
	 * 
	 * @param bee - the bee to add in the hive
	 * @return Returns the index of the cell the bee was added to
	 * <br /> or -1 if it was a Queen and she won the throne,
	 * <br /> or -2 if it was a Queen but she failed to win the throne
	 * @throws HiveOverflow if the hive's max size was reached
	 * 
	 * @see #appointQueen(Queen)
	 * @see #cells
	 * @see #getBee(int)
	 */
	public int addBee (Bee bee) throws HiveOverflow {
		if (bee instanceof Queen) //Handle this case in .appointQueen(q)
			try {
				return appointQueen ((Queen) bee) ? -1 : -2; //Both values should be exceptions?! Even if successful?!
			}
			catch (QueenLeftOut e) {
				//System.out.println("\t\t" + e.getMessage());
				garden.getInterface().event("queenLeftOut", bee, e);
			}
			
		
		if (this.cells.size() >= (int) Config.get("hive size max"))
			throw new HiveOverflow (this, bee);

		this.cells.add (bee);
		bee.hive = this;
		
		return this.cells.size()-1;
	}

	/**
	 * Gets the Bee in the specified cell.
	 * <p> <b>Note</b> that a dead bee is represented by <code>null</code> </p>
	 * 
	 * @param n - the index of the cell wanted
	 * @return Returns the Bee in the n-th cell
	 * <br /> or null if there was no such cell
	 *
	 * @see #cells
	 * @see #size()
	 * @see #addBee(Bee)
	 * @see #iterator()
	 * @see #beeIterator()
	 */
	public Bee getBee (int n) {
		if (n < 0 || n >= this.cells.size())
			return null;

		return this.cells.get(n);
	}

	
	/* Bee Iteration */
	
	/**
	 * Alias of {@link #beeIterator()}
	 */
	public ListIterator<Bee> iterator () {
		return this.beeIterator();
		//The aliased code is 1 line so it could be better to write it here...
		//This, though, leaves you with only one place to edit
	}

	/**
	 * Gets an iterator object for iteration over the bees in the Hive
	 * @return Returns a {@link ListIterator} of {@link #cells}
	 * @see #iterator()
	 * @see #size()
	 * @see #getBee(int)
	 */
	public ListIterator<Bee> beeIterator () {
		return this.cells.listIterator();
	}

	/**
	 * Gets the size of the hive.
	 * @return Returns the number of Bees in {@link #cells}
	 * @see #cells
	 * @see #getBee(int)
	 * @see #beeIterator()
	 * @see #iterator()
	 */
	public int size () {
		return this.cells.size();
	}

	
	/*  Recursive Convenience  */

	/**
	 * Calls anotherDay() on the {@link #queen} and all the bees in {@link #cells}
	 * 
	 * <p> Note that a dead bee is represented by <b><code>null</code></b>. </p>
	 * 
	 * Dead bees stay in {@link #cells} until the next day. <br />
	 * Then they are removed from the Hive. <br /><br />
	 * 
	 * @return the <b>Hive</b> itself
	 * <br /> or <b><code>null</code></b> if all the bees are dead.
	 * @see #cells
	 * @see #queen
	 * @see #toString()
	 */
	public Hive anotherDay () throws Death
	{
		garden.getInterface().event("newDay", this);
		
		//Store the current bee count so that if the queen adds eggs they don't get "aged" (.anotherDay() shouldn't be called)
		int beeCount = cells.size();
		
		//Long live the queen!	
		try {
			if (queen != null)
				queen.anotherDay();
		}
		catch (BeeDeath e) {
			//System.out.println ("\t\t" + e.getLocalizedMessage());
			garden.getInterface().event("death", queen, e);
			queen = null;
		}
				
		//"Age" the bees in the hive (call .anotherDay() )
		for (int i = 0; i < beeCount; i++) {
			try {
				cells.set(i, getBee(i).anotherDay());
			}
			catch (BeeDeath e) {
				//System.out.println ("\t\t" + "Cell "+i+": "+ e.getLocalizedMessage());
				garden.getInterface().event("death", cells.get(i), e);
				cells.remove(i--);
				beeCount--;
			}
		}
		
		//If the whole hive is dead / empty
		if (queen == null && cells.size() == 0)
			throw new Death ("\t" + "The Hive has perished!");

		return this; //Allows chain calls of methods
	}


	/**
	 * Provides a human-readable representation of the Hive
	 * <br />
	 * <br /> Records the amount of food in storage
	 * <br /> Calls {@link Queen#toString() .toString()} on the Queen
	 * <br /> Calls {@link Bee#toString() .toString()} on each Bee
	 * <br />
	 * <br /> Pads appropriately and returns the resulting String.
	 * 
	 * @return Returns a human-readable String representation of the Hive
	 * @see Garden#toString()
	 * @see Bee#toString() 
	 */
	public String toString () {
		String raw = "";

		raw += "\tFood:  "+storage.get(Food.Honey)+" Honey,   "+ storage.get(Food.RoyalJelly)+" RoyalJelly,   "+ storage.get(Food.Pollen)+" Pollen" + "\n";
		
		raw += "\t" + ((queen != null) ? queen.toString() : "The Queen is dead! The Hive is doomed!") + "\n";
		
		ListIterator<Bee> iBee = beeIterator();
		while (iBee.hasNext()) {
			raw += "\t" + "Cell "+iBee.nextIndex() +": ";
			
			Bee bee = iBee.next();
			raw += (bee != null) ? bee.toString() : "Dead bee!";
			
			if (iBee.hasNext())
				raw += "\n";
		}
		
		
		return raw;
	}

	
	/* Serialization */
	
	/**
	 * The padding that is applied to every Bee in the hive
	 * <br />
	 * <br /> Current padding: {@value #serializationPadding}
	 * <br /> Recommended padding: <code>"\t"</code>
	 * @see #serialize()
	 */
	public static final String serializationPadding = ""; // "\t"

	/**
	 * The spacing that is applied between every parameter of the hive (amount of honey, royal jelly, pollen)
	 * <br />
	 * <br /> Current spacing: {@value #serializationSpacing}
	 * <br /> Recommended spacing: <code>" "</code>
	 * @see #serialize()
	 */
	public static final String serializationSpacing = " ";

	/**
	 * Serialize this Hive object into a near-readable String.
	 * <br />
	 * <br /> Begins with a Hive definition containing the hive parameters. Please see {@link #serializedHivePattern}
	 * <br /> Then serializes the {@link #queen} of this hive. See {@link Queen#serialize()}
	 * <br /> Then serializes every bee in this hive's {@link #cells}. See {@link Bee#serialize()}
	 * <br />
	 * <br /> Pads appropriately and returns the resulting String.
	 * @return Returns a String describing this Hive
	 * @see #serializationPadding
	 * @see #serializationSpacing
	 * @see #serializedHivePattern
	 */
	public String serialize () {
		String stream = "";
		
		stream += "hive" + ":";
		stream +=       serializationSpacing + getHoney();
		stream += "," + serializationSpacing + getRoyalJelly();
		stream += "," + serializationSpacing + getPollen();
		stream += "\n";
		
		stream += serializationPadding + ((this.queen != null) ? this.queen.serialize() : "null") + "\n";
		
		for (Bee bee: this.cells)
			stream += serializationPadding +  ((bee != null) ? bee.serialize() : "null") + "\n";
		
		return stream;	
	}

	
	/* Deserialization */

	/**
	 * The pattern of a serialized Hive
	 * <p> Currently: {@value #serializedHivePattern} </p>
	 * @see #serialize()
	 * @see #deserialize(String)
	 */
	public static final String serializedHivePattern = "\\s*(hive)\\s*:\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+).*";

	/**
	 * Determines whether a missing parameter in the serialization is allowed.
	 * <p> Affects {@link #deserialize(String)} </p>
	 */
	public static final boolean allowDefaults = true;
	
	/**
	 * Tries to deserialize the given stream
	 * @param stream - the stream to deserialize
	 * @return Returns a {@link Hive} (if a serialized one is found)
	 * @throws NoSerializedFound if no serialized Hive was recognized
	 * @see #serializedHivePattern
	 * @see #allowDefaults
	 * @see #serialize()
	 */
	public static Hive deserialize (String stream) throws NoSerializedFound {
		Matcher matcher = Pattern.compile(serializedHivePattern).matcher(stream);
		
		if (!matcher.find(0))
			throw new NoSerializedFound("Hive");
		
		Hive hive = new Hive();
		
		//Weirdly enough, the three parameters can be grouped in a loop
		Food[] params = {Food.Honey, Food.RoyalJelly, Food.Pollen};
		for (int i = 0; i < params.length; i++)
		{
			try {
				if (matcher.group(i+2) == null)
					throw new Exception ("Parameter for amount of "+params[i].getName()+" in Hive not found!");
					
				Integer amount = new Integer (matcher.group(i+2));
					//The NumberFormatException falls in the same category as other exceptions
				
				hive.storage.put(params[i], amount);
			}
			catch (Exception e) {
				if (!allowDefaults)
					throw new NoSerializedFound("Hive", stream);
			}
		}
		
		
		//Logically, the Bees that should be in this Hive should be deserialized in here
		//Even more so because deserialize should be the exact opposite of serialize...
		//With that said, though, it is easier to handle each separate object in the top level which is Garden.deserialize()
		
		return hive;
	}
}