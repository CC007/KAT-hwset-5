
import java.util.Vector;
import java.util.Random;

class Agent {

	// Agent variables
    private int xPosition;
    private int yPosition;
    private int range = 1;
    private String type;
    private String product;
    public Scape scape;
    public Site[][] grid;
    public Vector<Agent> agents;
    static Random ran = new Random();
    
	// Communication Variables
	protected Vector<Message> messages;
	protected boolean messageWaiting;

    public Agent (Scape controller) {
    }
    
    public Agent (Scape controller, String job) {
		scape = controller;
		type = job;
		this.grid = scape.grid;
		this.agents = scape.agents;
		messageWaiting = false;
		messages = new Vector<Message>();
    }
    
    // A non-deterministic movement function, gradually moving towards the Tile with location (x,y).
    // The idea:
    /* If (a, b) is the vector to be traveled from the trader's
    current position to his current goal, the odds of moving along either axis are
    |a|/|a+b| and |b|/|a+b|, respectively.
    This will have the effect that the vector (a, b) will tend to a diagonal direction 
    (i.e., a vector of the vorm (c,c)).*/   
    public void moveToGoal(int x, int y) {		
    		int newX = xPosition;
    		int newY = yPosition;
    	
    		int xDir = Math.abs(x - xPosition);
    		int yDir = Math.abs(y - yPosition);
    	
    		int distance = Math.abs(xDir + yDir);
    		
    		if (ran.nextInt(distance) <= xDir) {
    			if (x > xPosition)	{
    				if (newX < scape.xSize) {
    					newX++;
    				}
    			}
    			else {
    				if (newX > 0) {
    					newX--;
    				}
    			}   		
    		}
    	
    		if (ran.nextInt(distance) <= yDir) {
    			if (y > yPosition)	{
    				if (newY < scape.ySize) {
    					newY++;
    				}
    			}
    			else {
    				if (newY > 0) {
    					newY--;
    				}
    			}
    		}
    		/*
    		 System.out.println("CurrentXPos: " + xPosition + " NewXPos: " + newX + " DestXPos: " + x);
    		 System.out.println("CurrentYPos: " + yPosition + " NewYPos: " + newY + " DestYPos: " + y);
   		*/    		
    		move(grid[newX][newY]);
    }

    // An empty act() function, to be overwritten by the subclasses for various Agent types.
    public void act() {
    }
    
    // When an Agent moves, tell its old Site it has left, its new Site it has arrived, and itself that it has moved.
    public void move(Site newS) {
		if (newS.getAgent() == null) {
    		grid[xPosition][yPosition].setAgent(null);
    		newS.setAgent(this);
    		xPosition = newS.getXPosition();
    		yPosition = newS.getYPosition();
    	}
    }	

    // Returns a Vector with all other agents in range; range specified above.
    public Vector<Agent> getAgentsInRange() {
		Vector<Agent> agentsInRange = new Vector<Agent>();
		for(int m = -range; m <= range; m++) {
			for(int n = -range; n <= range; n++) {
				Site site;
				int x = xPosition + m;
				int y = yPosition + n;
				if(x >= 0 && x < scape.xSize && y >= 0 && y < scape.ySize) {
					site = grid[x][y];
					if(site.getAgent() != null  && !site.getAgent().equals(this))
						agentsInRange.add(site.getAgent());
				}
			}
		}		
		return agentsInRange;
    }
 
    // A public function allowing others to deliver messages to this Agent.
    public void deliverMessage(Message message){
    	messages.add(message);
    	messageWaiting = true;
    }
    
    // Several self-evident public get() and set() functions, ONLY for use by either the Agent itself,
    // or for statistics and visualisation purposes in Scape, ButtonPanel and MainPanel.
    // NOT to be used to exchange information between agents, and should not be called by them.
    
    public String getProduct() {
    		return product;
    }
    
    public void setProduct(String food) {
    		product = food;
    }
    
    public String getType()	{
    		return type;
    }
    
    public void setPosition(int x, int y) {
    		xPosition = x;
    		yPosition = y;
    }
}