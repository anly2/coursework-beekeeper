package coursework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

/**
 * @see #main(String[])
 */
public class Beekeeper
{
	/**
	 * The delay in miliseconds between each day in the simulation.
	 * <br /> Only relevant when the simulation is animated. See {@link #main(String[])}
	 */
	public static final int animatedSpeed = 3000;
	
	public static int day = 1;

	/**
	 * Provides a Command Line Interface for a BeeHive simulation.
	 * <br />
	 * <br /> If there is an argument provided, it is treated as a file address for a simulation save file.
	 * <br /> Also note that since a single filename is all that is expected,
	 *        if there are more than one arguments they will all get concatenated into one file name.
	 * <br />
	 * <br /> If no simulation was loaded, a garden is initialized with several sample-filling methods:
	 * <br /> {@link Garden#Garden() new Garden} , {@link Hive#Hive() new Hive} , {@link Queen#Queen(Hive) new Queen} ,
	 *        {@link Garden#addSampleFlowers() sampleFlowers} , {@link Hive#addSampleEggs() sampleEggs} , {@link Hive#initialFood() initialFood}
	 * <br />
	 * <br /> After each day the program waits for input.
	 * <br /> If the input is an <b>integer</b> then the waiting for input is skipped that many times. Meaning, <em>N</em> days get simulated instantly. 
	 * <br /> If the input is like <b><code>"\s*[sSwW].*"</code></b> a call to {@link #save(Garden)} is made.
	 * <br /> If the input is like <b><code>"\s*[lLrR].*"</code></b> a call to {@link #load()} is made.
	 * <br /> If the input is like <b><code>"\s+"</code></b> the simulation becomes animated, meaning the program will not wait for input again.
	 * <br /> If the input is <b>empty</b> (the user just pressed Enter) the program simply proceeds to the next day.
	 * <br /> If the input is anything <b>else</b>, the program stops (breaks, closes)
	 * <br />
	 * <br /> When the simulation is animated there is a delay between each day.
	 * <br /> The default delay is {@value #animatedSpeed}
	 * @param args - path to a simulation file to be loaded. All arguments get concatenated into one String.
	 */
	public static void main (String[] args)
	{
		//Initialization
		Garden garden = new Garden();
		
		if (args.length > 0)
		{
			//If we have arguments, then that is a simulation file to deserialize (read)
			String simulationFile = "";
			
			//We can assume that all of the arguments are one file name because that is all we expect
			for (String arg: args)
				simulationFile += arg;
			

			
			//Try to load the file
			try {
				garden = load (simulationFile);
			}
			catch (FileNotFoundException e) {
				System.err.println ("Simulation file not found!");
				System.exit (1);
			}
		}
		else
		{
			//Add a sample Hive
			Hive hive = new Hive(new Queen());
			garden.addHive(hive);
	
			//Sample Filling
			garden.addSampleFlowers();
			hive.addSampleEggs();
			hive.initialFood();
		}
			
		//Control variables
		Scanner scanner = new Scanner (System.in);
		boolean animated = true; //tied to stepsToSkip
		int stepsToSkip = 1; //tied to animated
		
		while (true) {
			//Control
			if (!animated) {
				String line = scanner.nextLine();
				
				try {
					stepsToSkip = (int) new Integer (line); 
					animated = true;
					continue;
				}
				catch (NumberFormatException e) {}
				
				
				if (line.matches("[sSwW].*")) {
					save (garden);
					continue;
				}
				
				if (line.matches("[lLrR].*")) {
					try {
						garden = load ();
						stepsToSkip = 1;
						animated = true;
					}
					catch (FileNotFoundException e) {
						System.err.println ("Simulation file not found!");
					}
					continue;
				}
				
				if (line.matches("\\s+")) //space
					animated = true;
				
				if (!line.equals("")) //anything but enter
					break; //Basically the same as exit()
			}
			else
			if (stepsToSkip > 0)
				if (--stepsToSkip <= 0)
					animated = false;
			
			
			//Time management
			try {
				//Clear console
				if (day > 1) {
					for (int j = 0; j < 50; j++)
						System.out.println();
				}
				
				//Print
				System.out.println ("Day " + day++);
				System.out.println (garden);
				
				//Animate
				if (animated && stepsToSkip <= 0)
					Thread.sleep(animatedSpeed);
				
				//Time goes by
				if (garden.anotherDay() == null) {
					System.out.println ("\n\n\n" + "The garden feels dead!");
					break;
				}
			}
			catch (InterruptedException e) {}
		}
		
		scanner.close();
	}
	
	/**
	 * Asks where to save the current simulation and saves it.
	 * <br />
	 * <br /> If the user input was empty, just prints the serialized simulation.
	 * <br /> If the user specified a file name, save the simulation there.
	 * <br /> If the file already existed, ask for confirmation to overwrite.
	 * <ul>
	 * 		The serialization itself is as follows:
	 * 		<li> Serializes the current day of the simulation. Format: <code>"day:N"</code>. Also see {@link #load(String)}</li>
	 * 		<li> Serializes the random generator. See {@link Rand#serialize()} </li>
	 * 		<li> Serializes the entire garden. See {@link Garden#serialize()} </li>
	 * </ul>
	 * @param garden - the garden object to serialize
	 * 
	 */
	public static void save (Garden garden)
	{
		//Initialization
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner (System.in);
		
		//Perform the serialization
		String serialized = Rand.serialize() + "\n";
		serialized += "day:" + day + "\n";
		serialized += garden.serialize();

		//Prompt for save destination
		System.out.print("Choose a file name: ");
		String simFile = scanner.nextLine();
		
		
		if (simFile.trim().isEmpty()) {
			System.out.println("\n" + serialized); //print for manual copying
			return;
		}
		
		try {
			//Confirm that overwriting is desired if file exists
			if (new File(simFile).exists()) {
				System.out.print("File \""+simFile+"\" already exists! Overwrite?  ");
				String shouldOverwrite = scanner.nextLine();
				
				if (!shouldOverwrite.toLowerCase().contains("y"))
					throw new AbortWriting (simFile);
			}
			
			//Do write to the file
			PrintWriter pw = new PrintWriter(simFile);
			pw.write(serialized);
			pw.close();
			
			//Notify of the successful operation
			System.out.println("Successfully saved current simulation.");
		}
		catch (FileNotFoundException e) {
			System.err.println(e);
		}
		catch (AbortWriting e) {
			//The user decided not to overwrite the file
			System.out.println("Save Writing aborted!");
		}
		
		
		// Most likely, I wont be able to read from System.in after this...
		//scanner.close();
	}
		
	/**
	 * Asks which file to load and calls {@link #load(String)}
	 * @throws FileNotFoundException
	 */
	public static Garden load () throws FileNotFoundException {
		//Initialization
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner (System.in);
		
		System.out.print ("Select simulation file to load: ");
		String simFile = scanner.nextLine();
		
		// Most likely, I wont be able to read from System.in after this...
		//scanner.close();
		
		return load (simFile);
	}

	/**
	 * Looks for a serialized <code>day</code>, <code>random generator</code> and <code>Garden</code>
	 * <br />
	 * <br /> If the first lines contain:
	 * <ul>
	 * 		<li> a serialization of a day, set the current day to that. Format: <code>"\s*(day)\s*:\s*(\d+).*"</code>. Also see {@link #save(Garden)}</li>
	 * 		<li> a random generator, deserialize it. See {@link Rand#deserialize(String)}. </li>
	 * </ul>
	 * Then deserialize the garden. See {@link Garden#load(String[])}
	 * @param simulationFile - the file to read from
	 * @return Returns a Garden loaded from the simulation save file.
	 * @throws FileNotFoundException if the simulation file could not be found.
	 */
	public static Garden load (String simulationFile) throws FileNotFoundException {
		//Read the file if possible
		BufferedReader file = new BufferedReader (new FileReader (simulationFile));
		
		//Look for a serialized day or random generator
		try {
			file.mark(200);
			
			String line;
			while ((line = file.readLine()) != null) {
				
				//See if this is a serialization of the day
				try {
					Rand.deserialize (line.replaceAll (Rand.serializedRandPattern, "$2"));
					file.mark(100);
					continue;
				}
				catch (NoSerializedFound e) {}
				
				//See if this is a serialization of the random generator
				try {
					day = new Integer (line.replaceAll ("\\s*(day)\\s*:\\s*(\\d+).*", "$2"));
					file.mark(100);
					continue;
				}
				catch (NumberFormatException e) {
					//throw new NoSerializedFound ("Day (of simulation)");
				}
				
				
				//If neither a random seed nor a day has been specified, stop looking for them
				file.reset();
				break;
			}
		}
		catch (IOException e) {}
		
		//Load the garden
		return Garden.load(file);	
	}
}