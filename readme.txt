Please read the end to learn about the two folders
("coursework exc" and "coursework null")



To start the program please run "Beekeeper".
Beekeper.main() is commented and explained.




The extentions implemented are:

 - Bees may die
    + If a Bee gets older than its specified max age, there is a chance it may die

 - Flowers may die
    + Each Flower type has a specified max age and if a Flower gets older there is a chance it may wither and die.

 - Flowers may pollinate and reproduce
    + Each flower has a chance of pollinating another flower. The chance for a flower of a certain color to pollinate a flower of some color is described in the configuration file.
    + The case of self pollination is also considered, but is not allowed in the current configuration.

 - Dynamic choice on what Bee to spawn from a Pupa
    + Different case for when the Queen of the Hive has died
    + Different case for when the Hive is nearly full
    + Please see the configuration file for details.

 - Fully serializable, or in other words savable, simulations
    + Beekeeper.main() listens for input that matches "/[wW]|[sS]/"
    + All the properties of objects get saved. That includes:
        * health and age for all Bee types
        * health, age and nextBirth delay for the Queen
        * pollen and age for the Flower types
        * stored honey, jelly and pollen for the Hive
        * all the Bees in the Hive (including the Queen)
        * any number of Hives that are in the Garden
        * any number of Flowers that are in the Garden
        * the state of the random number generator
        * the day count for the simulation
    + Only one garden is allowed per save. It is easy to change but the simulation handles one garden anyway... so no point in doing that.

 - Fully deserializable, or in other words loadable, simulations
    + Beekeeper.main() can take a save file name as command line argument
    + beekeeper.main() also listens for input that matches "/[rR]|[lL]/"
    + All properties that are serializable are also deserializable
    + Note that if a property is not mentioned, the default behavior is to allow that and use the value that the property would normally get (by such object simply being created anew)
    + -fixed- Also note that there is a minor flaw with deserializing a Queen object, where the creation of a Queen requests a random number from the RNG and thus offsets it by +1 from the actual saved state. Most likely, though, this will not be noticed.

 - Extended Configuration
    + There are two classes defined in Config.java
    + The "static" class Config is basically a "static alias" for the default Configuration. Just for ease of access.
    + The class Configuration parses a configuration file (with custom format*) and fills an entry HashMap with values.
    + The format of the configuration file is not well explained in comments but it is fairly intuitive to use especially with indentation.
    + Running Config.main() would actually print out the loaded entries. If you are really that interested in the format you could compare the file and the actual entry HashMap generated.
    + The file "default.cfg" hopefully contains all actual values used as parameters for a simulation.

 - An added extension is having "colonies".
    + Instead of having no chance for a Queen to spawn normally, when a Queen spawns she "contends the throne" and the losing Queen tries to establish a colony.
    + If the Hive has resources and the colony is allowed to take enough, a new Hive is established and the losing Queen survives as queen of that new Hive. (Otherwise, the losing Queen is cast out and dies...)
    + The resources needed for a new colony and the resources that are allowed by the original Hive are described in "default.cfg"
    + Please see the javadoc for "Hive.appointQueen(q)" and "Hive.colonize()" as well.



Please see the javadoc comments.
If you start from Beekeeper.main() you should be able to read through all the javadoc using the links in those comments.
Hopefully, the documentation is good enough!


One last thing to note is that I deliberately chose "null" as the value representing a dead Bee and a dead Flower (and a dead Hive, and a dead Garden).
I have also provided a version where death is handled by throwing and catching exceptions.
My only argument is that normally in a simulation there is a "sample rate" at which the subject (in this case, the garden) gets examined.  Having a representation of a "corpse" allows the event of "death" to happen and <b>later</b> be observed when "a sample is taken".
This is so unrelated but I believe you would look at an actual Beehive once every day (that is the "sample rate"). So technically you are not constantly looking for things that happen in the Beehive.
The version with exceptions will immediately notify you if something has died (if any notification is made at all).
Again, this is very unrelated but, you wouldn't normally stand by a bee or a flower and notice immediately when it dies. (Even that is "sampling" but with very high "sampling rate" ....)
Regardless, I do agree that the version with exceptions is more verbose.


The two different versions are in their own folder.
	- the version which mainly uses exceptions is in "coursework exc"
	- the version that uses null for handling death is in "coursework null"