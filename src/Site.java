class Site {
	// Site variables
    private int xPosition;
    private int yPosition;
    private Agent agent;
    
    // The empty Site constructor.
    public Site () {
    }
    
    // Site Constructor
    public Site(int x, int y) {
    		xPosition = x;
    		yPosition = y;
    }
    

    // Several self-evident public get() and set() functions, ONLY for use by the Agent itself,
    // or for visualization purposes.
    public Agent getAgent() {
    		return agent;
    }
    
    public void setAgent(Agent a) {
    		agent = a; 
    }
     
    public int getXPosition() {
    		return xPosition;
    }
    
    public int getYPosition() {
    		return yPosition;
    }
    
}