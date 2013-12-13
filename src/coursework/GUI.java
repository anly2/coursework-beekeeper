package coursework;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class GUI implements ClientInterface {
	public static final int windowWidth = 800;
	public static final int windowHeight = 800;
	
	protected JFrame window;
	protected BackgroundPanel document;
	
	protected static HashMap<Object, JPanel> visuals = new HashMap<Object, JPanel>();

	
	
	public static void main (String[] args)
	{
		GUI intf = new GUI();
		intf.run(args);
	}

	public GUI()
    {
		JFrame window = new JFrame();

		//make sure the program exits when the frame closes
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setTitle("Example GUI");
		window.setSize(windowWidth,windowHeight);

		//This will center the JFrame in the middle of the screen
		window.setLocationRelativeTo(null);

		try {
			document = new BackgroundPanel("img/grass.jpg");
	        
			/* Sample filling to test the actual visual objects
			HivePanel hive = new HivePanel();
			document.add (hive);
			
			document.add (new FlowerPanel("rose"));
			document.add (new FlowerPanel("bluerose"));
			document.add (new FlowerPanel("daffodil"));
			document.add (new FlowerPanel("fuchsia"));
			
			BeePanel bee = new BeePanel();
			document.add (bee);
			
			 new Animation (bee, 300, 300, 1000, 2000);
			//*/
		} catch (IOException e) {
			System.err.println("Image file not found!");
			System.err.println(e);
			System.exit(1);
		}
		
		window.getContentPane().add(document);
        window.setVisible(true);
    }
	
	public int day = 1;
	public void run (String[] args)
	{
		//Initialization
		Garden garden = new Garden(this);
		
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
				garden.setInterface (this);
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
			event ("birth", hive);
	
			//Sample Filling
			garden.addSampleFlowers();
			hive.addSampleEggs();
			hive.initialFood();
		}
		
		//while?!
		try {
			while (true) {
				garden.anotherDay();
				Thread.sleep(3000);
			}
		}
		catch (InterruptedException e) {}
		catch (Death e) {
			System.exit(0);
		}
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
	public Garden load (String simulationFile) throws FileNotFoundException {
		//Read the file if possible
		BufferedReader file = new BufferedReader (new FileReader (simulationFile));
		
		//A fix for the RNG being offset by the creation of objects before the actual simulation
		String seed = null;
		
		//Look for a serialized day or random generator
		try {
			file.mark(200);
			
			String line;
			while ((line = file.readLine()) != null) {
				
				//See if this is a serialization of the day
				try {
					String reduced = line.replaceAll (Rand.serializedRandPattern, "$2");
					Rand.deserialize (reduced);
					seed = reduced; //If we still haven't jumped to the catch, then we have a valid seed
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
		Garden g = Garden.load(file);
		
		//Re-initialize Rand to avoid premature offsets
		if (seed != null) {
			try {
				Rand.deserialize (seed);
			}
			catch (NoSerializedFound e) {
				System.err.println("Failed to re-apply the clean random seed.");
			}
		}
		
		return g; //Now we can return the new Garden object
	}
	
	

	/* Event Updates */
	
	public void event (String type, Object... args) {
		if (type.equalsIgnoreCase("birth") && args[0] instanceof Hive)
			eventBirth ((Hive) args[0]);
		if (type.equalsIgnoreCase("birth") && args[0] instanceof Flower)
			eventBirth ((Flower) args[0]);
		
		if (type.equalsIgnoreCase("birth") && args[0] instanceof Queen)
			eventBirth ((Queen) args[0]);
		if (type.equalsIgnoreCase("birth") && args[0] instanceof Worker)
			eventBirth ((Worker) args[0]);
		if (type.equalsIgnoreCase("birth") && args[0] instanceof Drone)
			eventBirth ((Drone) args[0]);
		if (type.equalsIgnoreCase("birth") && args[0] instanceof Egg)
			eventBirth ((Egg) args[0]);
		if (type.equalsIgnoreCase("birth") && args[0] instanceof Larvae)
			eventBirth ((Larvae) args[0]);
		if (type.equalsIgnoreCase("birth") && args[0] instanceof Pupa)
			eventBirth ((Pupa) args[0]);
		

		if (type.equalsIgnoreCase("evolution"))
			eventEvolution (args[0], args[1]);
		
		
		if (type.equalsIgnoreCase("newday") && args[0] instanceof Garden)
			eventDay ((Garden) args[0]);
		
		if (type.equalsIgnoreCase("newday") && args[0] instanceof Hive)
			eventDay ((Hive) args[0]);
		
		if (type.equalsIgnoreCase("newday") && args[0] instanceof Flower)
			eventDay ((Flower) args[0]);
		
		if (type.equalsIgnoreCase("newday") && args[0] instanceof Bee)
			eventDay ((Bee) args[0]);
		
		
		if (type.equalsIgnoreCase("flowerTouched"))
			eventTouch ((Flower) args[0], (Worker) args[1]);
		
		
		if (type.equalsIgnoreCase("death"))
			eventDeath (args[0]);
		
		if (type.equalsIgnoreCase("outofpollen")) 
			event ((OutOfPollen) args[1]);
		
		if (type.equalsIgnoreCase("hiveoverflow")) {
			if (args.length == 2)
				event ((HiveOverflow) args[1]);
			else
				event ((HiveOverflow) args[1], (String) args[2]);
		}
		
		if (type.equalsIgnoreCase("queenleftout"))
			event ((QueenLeftOut) args[1]);
	}
	
	private void eventBirth (Hive hive) {
		document.add(new HivePanel (hive));
		document.repaint();
	}
	private void eventBirth (Flower flower) {
		document.add(new FlowerPanel (flower));
		document.repaint();
	}
	private void eventBirth (Bee bee) {		
		document.add (new BeePanel (bee));
		document.repaint();
	}
	private void eventEvolution (Object beeBefore, Object beeAfter) {
		JPanel visual1 = GUI.visuals.get(beeBefore);
		JPanel visual2 = GUI.visuals.get(beeAfter);
		
		if (visual2 != null)
			visual2.setLocation(visual1.getLocation());
		
		eventDeath (beeBefore);
	}
	
	private void eventDay (Garden garden) {
		flights = new HashMap<Worker, Point>();
	}
	private void eventDay (Hive hive) {
	}
	private void eventDay (Flower flower) {
	}
	private void eventDay (Bee bee) {
	}
	

	private HashMap<Worker, Point> flights = new HashMap<Worker, Point>();
	private void eventTouch (Flower flower, Worker worker) {
		//Worker went to extract pollen from flower
		
		JPanel visualWorker = GUI.visuals.get(worker);
		JPanel visualFlower = GUI.visuals.get(flower);
		
		if (flights.containsKey(worker)) {
			new Animation (visualWorker, visualFlower.getLocation(), 1000, 1250);
			//new Animation (visualWorker, flights.get(worker), 1000, 2000);
			//new Animation (visualWorker, GUI.visuals.get(worker.hive).getLocation(), 1000, 2000);
			return;
		}
		
		new Animation (visualWorker, visualFlower.getLocation(), 1000, 0);
		flights.put(worker, visualWorker.getLocation());
	}
	
	private void eventDeath (Object object) {
		JPanel visual = GUI.visuals.get(object);
		
		if (visual == null) return; //no visual found; no need to remove anything
		
		document.remove(visual);
		GUI.visuals.remove(object);
		
		document.repaint();
	}
	
	private void event (OutOfPollen exception) {
	}
	private void event (HiveOverflow exception) {
		System.out.println("\t" + exception.getMessage());
	}
	private void event (HiveOverflow exception, String message) {
		System.out.println("\t" + message);
	}
	private void event (QueenLeftOut exception) {
		System.out.println(exception.getMessage());
	}
}




class Animation extends Thread {
	private JPanel object;
	private int delay;
	private int duration;
	private int fromX;
	private int fromY;
	private int toX;
	private int toY;
	
	private static final int interval = 5; //ms per step

	public Animation (JPanel object, int x, int y, int duration) {
		this.object = object;
		this.duration = duration;
		this.delay = -1;
		this.toX = x;
		this.toY = y;
	}
	public Animation (JPanel object, Point dest, int duration) {
		this (object, (int)dest.getX(), (int)dest.getY(), duration);
	}
	public Animation (JPanel object, int x, int y, int duration, int delay) {
		this(object, x, y, duration);
		this.delay = delay;
		this.start();
	}
	public Animation (JPanel object, Point dest, int duration, int delay) {
		this (object, (int)dest.getX(), (int)dest.getY(), duration, delay);
	}
	
	public void run () {
		//Delay the actual effect
		if (this.delay > 0) {
			try {
				Thread.sleep(this.delay);
			}
			catch (InterruptedException e) {}
		}
		
		
		//Calculations
		this.fromX = this.object.getX();
		this.fromY = this.object.getY();
		
		int steps = this.duration/interval; //rounded
		
		double stepX = (double)(this.toX - this.fromX)/steps; //rounded
		double stepY = (double)(this.toY - this.fromY)/steps; //rounded
		
		
		//The actual movement
		for (int index = 1; index <= steps; index++) {
			try {
				Thread.sleep(interval);
			}
			catch (InterruptedException e) {}
			
			object.setLocation ((this.fromX + (int)(index*stepX)), (this.fromY + (int)(index*stepY)));
		}
		
		//exit
	}
}