package coursework;

/** Enumerate the food types */
public enum Food
{
   /* Enumerate the food types and set names */
   Honey("honey", 40),
   Pollen("pollen", 10),
   RoyalJelly("royaljelly", 20);

   /* Private variables */
   private String name; //Hold the name as a string
   private int initialDefault;

   /* Constructors */
      /** Simply store the name of the food type */
      Food (String nm, int initialDefault) {
         this.name = nm;
         this.initialDefault = initialDefault;
      }
   /* End of Constructors */

   /* Accessors */
      /** Return the name of the food type object */
      public String getName () {
         return this.name;
      }
   /* End of Accessors */

   /* Auxiliary */
      /** Alias of byName */
      public static Food getFood (String name) {
         return Food.byName(name);
      }

      /** Returns the food type object which has the specified name (case-INsensitive) */
      public static Food byName (String name) {
         for (Food f: Food.values())
            if (f.getName().equalsIgnoreCase(name))
               return f;
         return null;
      }

      /** Returns an initial default value for a food type reserve */
      public int initDefault () {
         return this.initialDefault;
      }

   /* End of Auxiliary */
}