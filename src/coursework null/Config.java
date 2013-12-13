package coursework;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * An object that contains key-value pairs
 * and provides several methods for adding and accessing them.
 * 
 * <ul>
 * 		<b>Constants:</b>
 * 		<li> {@link #COMMENT_START} - {@value #COMMENT_START} - the sequence which denotes the start of a comment. </li>
 * 		<li> {@link #COMMENT_END} - {@value #COMMENT_END} - the sequence which denotes the end of a comment. </li>
 * 		<li> {@link #SPACE} - {@value #SPACE} - the sign (sequence) which would be used to separate prefix words. This is the delimiter of groups. </li>
 * 		<li> {@link #EQUALS} - {@value #EQUALS} - the sign (sequence) which identifies the previous word as key and the next as value. This identifies an entry. </li>
 * 		<li> {@link #BACKSPACE} - {@value #BACKSPACE} - signifies to remove the last word from the prefix. Basically steps up one group. </li>
 * 		<li> {@link #RESET} - {@value #RESET} - signifies to reset the prefix. Basically cleans the stack of groups accumulated for the next values. </li>
 * 		<br /> <b>Please note</b> that there is a hardcoded alias for {@link #RESET} which is &nbsp; <b><code>"\n\s*?\n"</code></b> 
 * 		<br /> <b>Also</b>, be careful with one sequence being contained in another!
 * </ul>
 * 
 * @see #Configuration()
 * @see #entries
 * @see #parse(String)
 * @see #get(String)
 * @see #getGroup(String)
 */
class Configuration {
	/* Constants */
	
	// Be careful with one sequence being contained in another!
	public static final String COMMENT_START = "#"; 	  //The sequence which denotes the start of a comment 
	public static final String COMMENT_END = "\\s*?\\n";  //The sequence which denotes the end of a comment
	public static final String SPACE = " ";				  //The sign which would be used to separate prefix words -> delimiter of groups
	public static final String EQUALS = "=";              //The sign which identifies the previous word as key and the next as value -> makes an entry
	public static final String BACKSPACE = ";";           //Removes the last word from the prefix -> steps up one group
	public static final String RESET = "::";              //Resets the prefix -> removes the groups that the next value(s) would belong to 
	//There is a hardcoded alias for RESET which is "\n\s*?\n"
	

	/* Main method */ 
	
	/**
	 * A main method just to print a warning that this is simply a helper class
	 * @see Config#main(String[])
	 */
	public static void main (String[] args) {
		//Warning
		System.out.println ("This is a helper Class!");
		System.exit(0);
	}
	
	
	/* Properties */
	
	/**
	 * The source code of the loaded configuration.
	 * <p> This refers to the loaded String. It is checked against in {@link #parse(String)} to see if there is anything new </p>
	 * @see #parse(String)
	 * @see #Configuration(String)
	 */
	protected String source;
	
	/**
	 * A HashMap of all the entries loaded in this configuration.
	 * @see #parse(String)
	 * @see #parseValue(String)
	 * @see #get(String)
	 * @see #getGroup(String)
	 * @see #print()
	 * @see #print(String)
	 */
	protected HashMap<String, Object> entries;

	
	/* Evaluated Properties */
	
	/**
	 * The number of variables defined.
	 * <p> Not implemented because it is not actually used. It is just an idea. </p>
	 * @deprecated
	 */
	public int length () {return -1;}
	
	/**
	 * The number of groups in which the deepest variable is.
	 * <p> Not implemented because it is not actually used. It is just an idea. </p>
	 * @deprecated
	 */
	public int depth () {return -1;}
	
	/**
	 * The number of groups the variable specified by key is in.
	 * <p> Not implemented because it is not actually used. It is just an idea. </p>
	 * @deprecated
	 */
	public int depth (String key) {return -1;}
	
	
	/* Constructors */
	
	/**
	 * Load the default configuration
	 * @see Config#Config()
	 * @see Config#loadDefault()
	 */
	public Configuration () {
		this (Config.loadDefault());
	}
	
	/**
	 * Parse the given String and merge the configuration with the default one
	 * <p> The String given is parsed using {@link #parse(String)}
	 * <p> Merging ensures the values in default will not be "undefined" </p>
	 * <p> Also, stores a reference to the parsed String to not load it again unnecessarily.
	 * @param conf - the String to parse and load
	 * @see #Configuration()
	 * @see #source
	 */
	public Configuration (String conf) {
		entries = new HashMap<>();
		this.parse (conf);
		source = conf;
	}

	
	/* Parsing */
	
	/**
	 * Parse a string into a HashMap of configuration entries
	 * <br />
	 * <br /> For details of how it actually happens, please look the actual code.
	 * <br /> Mainly, though, it recognizes three type of words and acts accordingly.
	 * <ul>
	 * 		Recognized type of words:
	 * 		<li> Keywords which hold some special meaning. For these please the {@linkplain Configuration <b>constants</b>} </li>
	 * 		<li> A term definition. A term definition basically contains the {@link #EQUALS} sequence ({@value #EQUALS})
	 * 			 <br /> with a key on the left side and a value on the right </li>
	 * 		<li> A simple word. Such words are treated as group identifiers and stack together to form the prefix of subsequent terms. </li>
	 * </ul>
	 * Adds all the the entries to the {@link #entries} instance variable
	 * @param conf - the String to parse and load from
	 * @see #parseValue(String)
	 * @see #get(String)
	 * @see #getGroup(String)
	 */
	public void parse (String conf) {
		//Initialize the current map
		HashMap<String, Object> map = new HashMap<>();
			/* In theory we could easily add the results immediately to this.entries
			 * instead of putting them in this local object first.
			 * But this way we allow for easier future modifications */
		
		//Check if this configuration is the already loaded one
		if (conf.equals(this.source))
			return; //no need to continue
		
		
		//Get the string into a proper format
		String raw = conf;
		raw = raw.replaceAll(COMMENT_START+".*?"+COMMENT_END, SPACE); //Remove all the comments
		raw = raw.replaceAll("\\n\\s*?\\n", RESET); //Make "\n\n" alias of ":;"
		raw = raw.replaceAll(RESET, SPACE+RESET+SPACE); //Separate the RESET sequence from any word it might have stuck to
		raw = raw.replaceAll(BACKSPACE, SPACE+BACKSPACE+SPACE); //Separate the BACKSPACE sequence from any word it might have stuck to
		raw = raw.replaceAll("\\s+", SPACE); //Reduce multiple whitespaces to a single SPACE sequence
		raw = raw.replaceAll(SPACE+"?"+ EQUALS +SPACE+"?", EQUALS); //Remove the SPACEs around the EQUALS sequence and have it stick to both adjacent words
		
		//A couple of definition
		String[] words = raw.split(" "); //Get the words in an array
		ArrayList<String> prefix = new ArrayList<String>(); //Initialize the prefix object
		boolean hadDefinition = false; //flag initialization
		
		//Cycle through words
		for (int i = 0; i < words.length; i++)
		{
			//If it is an actual term definition (key=value pair)
			if (words[i].contains(EQUALS)) {
				//Get the correct key with the appropriate prefix
				String key = words[i].substring(0, words[i].indexOf(EQUALS));
				key = join (prefix, SPACE) + SPACE + key; //add the current prefix
				
				//Get the value specified
				Object value = parseValue (words[i].substring(words[i].indexOf(EQUALS)+1));
				
				//Put the values in the current entry list
				map.put(key, value);
				
				//Notify that we got through a definition
				hadDefinition = true;
			}
			
			//If the word contains the RESET sequence (";;" by default)
			if (words[i].contains(RESET)) {
				//Reset the prefix
				prefix.removeAll(prefix);
				
				//Reset the hadDefinition flag
				hadDefinition = false;
				
				//Clean up the RESET sequence as we already handled it
				words[i] = words[i].replaceAll(RESET, "");
			}
			
			//If the word contains the BACKSPACE sequence (";" by default)
			if (words[i].contains(BACKSPACE)) {
				//Remove the last group/word/part of the prefix
				if (prefix.size() > 0)
					prefix.remove(prefix.size()-1);

				//Reset the hadDefinition flag
				hadDefinition = false;
				
				//Clean up the BACKSPACE sequence as we already handled it
				words[i] = words[i].replaceAll(BACKSPACE, "");
			}
			
			//If it is not a term definition then this word is a group identifier
			if (!words[i].contains(EQUALS)) {
				//If we had a previous definition go back once (remove the last group from the prefix)
				if (hadDefinition) {
					//If the prefix is already empty we'll get errors
					if (prefix.size() > 0) 
						prefix.remove(prefix.size()-1);
					
					//Reset the hadDefinition flag because we already handled it
					hadDefinition = false;
				}
				
				//Add this word to the prefix (add it as a group identifier) but only if it is not empty 
				if (words[i].trim().length() > 0)
					prefix.add(words[i].trim());
			}
		}
		
		//Store the result
		this.entries.putAll(map);
	}
	
	/**
	 * Parse a given string representing a value into the proper object
	 * <br />
	 * <br /> Tries to parse into an integer.
	 * <br /> Tries to parse into a double.
	 * <br /> Tries to parse into a boolean.
	 * <br /> If none of those was successful, just store the String of the value itself
	 * @param value - the string representing the value
	 * @return Returns a boxed object of a particular type depending on the value.
	 * @see #parse(String)
	 * @see #get(String)
	 * @see #getGroup(String)
	 */
	public static Object parseValue (String value) {
		Object box;
		
		try {
			box = new Integer (value);
			return box;
		}
		catch (NumberFormatException e) {}
		
		
		try {
			box = new Double (value);
			return box;
		}
		catch (NumberFormatException e) {}
		
		
		if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes"))
			return true;
		if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("no"))
		    return false;
		
		/* A bit useless
		try {
			if (value.toLowerCase().contains("rand")) {
				String[] bounds = value.replaceAll("(.*rand\\s*\\(\\s*|\\s*\\).*)", "").split("\\s*,?\\s*");
				return Rand.number(new Integer(bounds[0]),  new Integer(bounds[1]));
			}
		}
		catch (Exception e) {}
		*/
		
		return value;
	}

	
	/* Helper Methods */
	
	/**
	 * Joins the elements of the String array into a single String using the given delimiter
	 * <p> Simply concatenates all the elements of the String array with the given delimiter in between them, separating them. </p>
	 * @param array - the String array to concatenate the elements of
	 * @param delimiter - the String that will separate the elements of the array
	 * @return Returns a concatenation of all the elements in <code>array</code>
	 * @see #join(ArrayList, String)
	 * @see #parse(String) 
	 */
	public static String join (String[] array, String delimiter) {
		String result = "";
		
		for (int i = 0; i < array.length; i++) 
			result += array[i] + ((i < array.length-1) ? delimiter : "");
		
		return result;
	}
	
	/**
	 * Alias of {@link #join(String[], String)}
	 * @see #join(String[], String)
	 */
	public static String join (ArrayList<String> array, String delimiter) {
		return join (array.toArray(new String[0]), delimiter);
	}
	

	/* Access Methods */
	
	/**
	 * Gets the value identified by the given key.
	 * @param key - the key that identifies the value saught in {@link #entries}
	 * @return Returns the value identified by the given key in the loaded configuration
	 * @throws ConfigKeyNotFound if the given key was not found in {@link #entries}
	 * @see #entries
	 * @see #parse(String)
	 * @see #parseValue(String)
	 */
	public Object get (String key) throws ConfigKeyNotFound {
		if (!this.entries.containsKey(key)) 
			throw new ConfigKeyNotFound (key);
		
		return this.entries.get(key);	
	}
	
	/**
	 * Gets an array of values identified by the given prefix
	 * <br /> If no values were found the array will be empty.
	 * @param prefix - a part of the key of values that are to be returned
	 * @return Returns an ArrayList of objects whose key begins with the given prefix
	 * @see #entries
	 * @see #get(String)
	 * @see #parse(String)
	 * @see #parseValue(String)
	 */
	public ArrayList<Object> getGroup (String prefix) {
		ArrayList<Object> list = new ArrayList<>();

		SortedSet<String> keys = new TreeSet<String>(entries.keySet());
		for (String key : keys) {
	        if (key.indexOf(prefix) == 0) //.contains(filter)
	        	list.add(entries.get(key));
		}
		
		return list;
	}
	
	
	/* Print Methods */
	
	/**
	 * Alias of {@link #print(String) print("")}
	 */
	public void print () {
		this.print("");
	}

	/**
	 * Print some of the entries of this configuration
	 * <br />
	 * <br /> Basically goes through all of {@link #entries}
	 * <br /> and prints an entry if its key begins with the given <code>filter</code>
	 * @param filter - the String with which the entry keys must begin to get printed
	 * @see #entries 
	 */
	public void print (String filter) {
		SortedSet<String> keys = new TreeSet<String>(entries.keySet());
		for (String key : keys) {
	        if (key.indexOf(filter) == 0) //.contains(filter)
	        	System.out.println (key +EQUALS+ entries.get(key));
		}
	}
}


/**
 * Static Alias for the Configuration object with the default configuration loaded
 * @see #currentConfiguration
 * @see #loadDefault()
 * @see Configuration
 * @see #print(String)
 */
public class Config {
	/* Default Configuration */
	
	/**
	 * The address of the default configuration file
	 * <p> Current address: {@value #defaultFile}
	 * @see #loadDefault()
	 * @see #default_
	 */
	public static final String defaultFile = "default.cfg";

	/**
	 * Gets the default configuration as string
	 * <p> The default configuration file to read is {@value #defaultFile}
	 * @return Returns the contents of the {@link #defaultFile default configuration file} 
	 * @throws RuntimeException if the file was not found or if there was an IO error
	 * @see #defaultFile
	 * @see #default_
	 */
	protected static String loadDefault () throws RuntimeException {		
		try {
			BufferedReader reader = new BufferedReader (new FileReader (defaultFile));
			StringBuilder sb = new StringBuilder();
        
        	String ln;
	        while ((ln = reader.readLine()) != null)
	            sb.append (ln + "\n");
	        
	        reader.close();
	        return sb.toString();
		}
		catch (FileNotFoundException e) { 
			throw new RuntimeException ("Default Configuration File could not be found!");
		}
		catch (IOException e) {
			throw new RuntimeException (e);
		}
	}
	
	/**
	 * The actual default configuration object
	 * 
	 * <p> This is basically a new {@link Configuration} object
	 * with the {@linkplain #loadDefault() default configuration loaded} </p>
	 * 
	 * @see #loadDefault()
	 * @see #defaultFile
	 */
	protected static Configuration currentConfiguration = new Configuration (loadDefault());
	
	
	/* Decorator Methods */
	
	/**
	 * Static Alias for the parse method of the default configuration object
	 * @see Configuration#parse(String)
	 * @see #currentConfiguration
	 */
	public static HashMap<String, Object> parse (String conf) {
		currentConfiguration.parse(conf);
		return currentConfiguration.entries;
	}
	
	/**
	 * Static Alias for getting the current configuration's entries
	 * @return Returns {@link Configuration#entries}
	 * @see Configuration#entries
	 * @see #currentConfiguration
	 * @deprecated doesn't really make sense 
	 */
	public static HashMap<String, Object> parse () {
		return currentConfiguration.entries;
	}

	
	/* Access Methods */
	
	/**
	 * Static Alias for getting a value from the currently loaded configuration
	 * @see Configuration#get(String)
	 * @see #currentConfiguration
	 */
	public static Object get (String key) throws ConfigKeyNotFound {
		return currentConfiguration.get(key);
	}

	/**
	 * Static Alias for getting a group of values from the currently loaded configuration
	 * @see Configuration#getGroup(String)
	 * @see #currentConfiguration
	 */
	public static ArrayList<Object> getGroup (String prefix) {
		return currentConfiguration.getGroup(prefix);
	}

	
	/* Print Methods */
	
	/**
	 * Static Alias for printing the default configuration
	 * @see Configuration#print()
	 * @see #currentConfiguration
	 */
	public static void print () {
		currentConfiguration.print();
	}
	
	/**
	 * Static Alias for printing the default configuration applying a given filter
	 * @see Configuration#print(String)
	 * @see #currentConfiguration
	 */
	public static void print (String filter) {
		currentConfiguration.print(filter);
	}
	

	/* Main Method */
	
	/**
	 * A main method print out the entries of the default configuration.
	 * <p> Also warns that this is just a helper class. </p>
	 * @see #print()
	 * @see #defaultFile
	 */
	public static void main (String[] args) {
		//Warning
		System.out.println ("This is a helper Class!");
		//System.exit(0);
		
		System.out.println (); //beautifier
		
		//Print the default configuration
		System.out.println("Default configuration:");
		Config.print();
	}
}