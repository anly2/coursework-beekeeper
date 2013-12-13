package coursework;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


@SuppressWarnings("serial")
class BackgroundPanel extends JPanel {
    private Image image;
    
    BackgroundPanel(String imagePath) throws IOException {
		this (ImageIO.read(new File(imagePath)));
    }

    BackgroundPanel(Image image) {
        this.image = image;
        setLayout(null);
    };

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int iw = image.getWidth(this);
        int ih = image.getHeight(this);
        
        if (iw > 0 && ih > 0) {
        	for (int x = 0; x < getWidth(); x += iw) {
        		for (int y = 0; y < getHeight(); y += ih) {
        			g.drawImage(image, x, y, iw, ih, this);
        		}
        	}
        }
    }
    

    public Component add (HivePanel hive) {
		hive.setLocation (nextPos());
		
    	return super.add (hive);
    }

    public Component add (FlowerPanel flower) {
		flower.setLocation (nextPos());
		
    	return super.add (flower);
    }

    public Component add (BeePanel bee) {
		bee.setLocation (nextPos());
		
    	return super.add (bee);
    }
        
    
    private int prevX = 0;
    private int prevY = 100;
    private static final int stepX = 100;
    private static final int stepY = 100;    
    public Point nextPos (boolean old) {
    	int x = prevX + stepX;
    	int y = prevY;
    	
    	if (x > GUI.windowWidth - stepX) {
    		x = 0;
    		y += stepY;
    	}

    	prevX = x;
    	prevY = y;
    	
    	return new Point (x, y);
    }

    private static Random rng = new Random();
    public Point nextPos () {
    	int x = 100 + rng.nextInt (Math.max(1, this.getWidth()-200));
    	int y = 50  + rng.nextInt (Math.max(1, this.getHeight()-150));
    	
    	return new Point (x, y);
    }
}


@SuppressWarnings("serial")
class HivePanel extends JPanel {
	private Image image;
	
	public static final String hiveImagePath = "img/hive.png";
	
	public HivePanel () {
		try {
			this.image = ImageIO.read(new File(hiveImagePath));
		}
		catch (IOException e) {
			System.err.println("Hive image not found!");
			System.exit(1);
		}
		
        this.setOpaque(false);
        this.setSize(image.getWidth(this), image.getHeight(this));
	}
	
	public HivePanel (Hive hive) {
		this ();
		
		GUI.visuals.put (hive, this); //replaces any old associated visuals
		
		//Honey label
		//Jelly label
	}
	
	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int iw = image.getWidth(this);
        int ih = image.getHeight(this);
        
        if (iw > 0 && ih > 0)
        	g.drawImage(image, 0, 0, iw, ih, this);
    }
}

@SuppressWarnings("serial")
class BeePanel extends JPanel {
	private Image image;
	
	public static final String eggImagePath = "img/egg.png";
	public static final String larvaeImagePath = "img/larvae.png";
	public static final String beeImagePath = "img/bee.png";
	
	public BeePanel () {
		this (beeImagePath);
	}
 	public BeePanel (String path) {
		try {
			this.image = ImageIO.read(new File(path));
		}
		catch (IOException e) {
			System.err.println("Egg image not found!");
			System.exit(1);
		}
        this.setOpaque(false);
        this.setSize(image.getWidth(this), image.getHeight(this));
	}
	public BeePanel (Bee bee) {
		this ( ((bee instanceof Egg) ? eggImagePath : ((bee instanceof Larvae || bee instanceof Pupa) ? larvaeImagePath : beeImagePath )) );
		GUI.visuals.put(bee, this);
	}
	
	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int iw = image.getWidth(this);
        int ih = image.getHeight(this);
        
        if (iw > 0 && ih > 0)
        	g.drawImage(image, 0, 0, iw, ih, this);
    }
}

@SuppressWarnings("serial")
class FlowerPanel extends JPanel { 
	private Image image;
	
	public FlowerPanel (String type){
		String flowerImagePath = "img/"+type+".png";
		//type should be "rose" , "bluerose", "daffodil", "fuchsia" (case sensitive)
		
		try {
			this.image = ImageIO.read(new File(flowerImagePath));
		}
		catch (IOException e) {
			System.err.println("Flower image not found!");
			System.exit(1);
		}
		
        this.setOpaque(false);
        this.setSize(image.getWidth(this), image.getHeight(this));
	}
	public FlowerPanel (Flower flower) {
		this (flower instanceof BlueRose? "bluerose" : flower.getType("as string"));
		GUI.visuals.put(flower, this);
	}
	
	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int iw = image.getWidth(this);
        int ih = image.getHeight(this);
        
        if (iw > 0 && ih > 0)
        	g.drawImage(image, 0, 0, iw, ih, this);
    }
}