import java.util.*;

public class Trader extends Agent {

    private static final int MAX_NUM_STEPS_IDLE = 5;
    private static final int INITIAL_BACK_AWAY_COUNTER = 7;
    // Variables for Buy & Sell Estimates
    private int estBuyFruit;
    private int estSellFruit;
    private int estBuyMeat;
    private int estSellMeat;
    private int estBuyWine;
    private int estSellWine;
    private int estBuyDairy;
    private int estSellDairy;

    private int numNegotiations = 0;
    private int numStepsIdle = 0;
    private int backAwayCounter = 0;
    private boolean isInSale = false;

    // Status Variable
    private String status;

    // A random number generator.
    static Random gen = new Random();

    // Trader Constructor
    public Trader(Scape controller) {
        super(controller, "trader");
        status = "chooseProduct";
        this.setProduct("none");
        estBuyFruit = gen.nextInt(50) + 25;
        estSellFruit = gen.nextInt(50) + 25;
        estBuyMeat = gen.nextInt(50) + 25;
        estSellMeat = gen.nextInt(50) + 25;
        estBuyWine = gen.nextInt(50) + 25;
        estSellWine = gen.nextInt(50) + 25;
        estBuyDairy = gen.nextInt(50) + 25;
        estSellDairy = gen.nextInt(50) + 25;
    }

    // The Trader's act function, called once per step, handling all the
    // Trader's behavior.
    public void act() {
        if (messageWaiting) {
            handleMessages();
        } else if (status.equals("chooseProduct")) {
            chooseProduct();
        } else if (status.equals("moveToProducer")) {
            moveToProducer();
        } else if (status.equals("buyFromProducer")) {
            buy(getProduct());
        } else if (status.equals("negotiateBuy")) {
            negotiateBuy();
        } else if (status.equals("moveToRetailer")) {
            moveToRetailer();
        } else if (status.equals("negotiateSale")) {
            negotiateSale();
        } else if (status.equals("sellToRetailer")) {
            sell();
        }
    }

    // Choosing a product to start trading in, by evaluating the expected
    // profit, based on the Trader's buy & sell estimates for each product. 
    // Switching to state "moveToProducer" afterwards.
    private void chooseProduct() {
        int fruitEstimate = estSellFruit - estBuyFruit;
        int meatEstimate = estSellMeat - estBuyMeat;
        int wineEstimate = estSellWine - estBuyWine;
        int dairyEstimate = estSellDairy - estBuyDairy;
        int[] priceEstimates = {fruitEstimate, meatEstimate, wineEstimate, dairyEstimate};

        int productNr = -1;
        int profit = -10000;

        for (int i = 0; i < priceEstimates.length; i++) {
            if (priceEstimates[i] > profit) {
                productNr = i;
                profit = priceEstimates[i];
            }
        }

        String product = getProduct(productNr);
        status = "moveToProducer";
        this.setProduct(product);
    }

    // Handling all messages received this step, then emptying the message
    // Vector.
    private void handleMessages() {
        for (Message message : messages) {
            if (message.content() == Message.Content.PROPOSE) {
                switch (message.sender().getType()) {
                    case "producer":
                        if (getBuyPrice(message.what()) >= message.number()) {
                            message.sender().deliverMessage(new Message(this, Message.Content.ACCEPT_PROPOSAL, message.what(), message.number()));
                            numNegotiations = 0;
                            buy(message.what());
                            setBuyPrice(message.what(), message.number());
                            isInSale = false;
                        } else {
                            message.sender().deliverMessage(new Message(this, Message.Content.REJECT_PROPOSAL, message.what(), message.number()));
                            numNegotiations++;
                            setBuyPrice(message.what(), (int) (getBuyPrice(message.what()) - (getBuyPrice(message.what()) - message.number()) * 0.1));
                        }
                        break;
                    case "retailer":
                        if (getSellPrice(message.what()) <= message.number()) {
                            message.sender().deliverMessage(new Message(this, Message.Content.ACCEPT_PROPOSAL, message.what(), message.number()));
                            numNegotiations = 0;
                            sell();
                            setSellPrice(message.what(), message.number());
                            isInSale = false;
                        } else {
                            message.sender().deliverMessage(new Message(this, Message.Content.REJECT_PROPOSAL, message.what(), message.number()));
                            numNegotiations++;
                            setSellPrice(message.what(), (int) (getSellPrice(message.what()) - (getSellPrice(message.what()) - message.number()) * 0.1));
                        }
                        break;
                }
            } else if (message.content() == Message.Content.CFP) {
                int price;
                if (numNegotiations > 5) {
                    message.sender().deliverMessage(new Message(this, Message.Content.FAILURE, message.what()));
                    this.setProduct("none");
                    status = "chooseProduct";
                    isInSale = false;
                } else {
                    switch (message.sender().getType()) {
                        case "producer":
                            price = getBuyPrice(message.what());
                            break;
                        case "retailer":
                            price = getSellPrice(message.what());
                            break;
                        default:
                            //faal
                            System.out.println("fail Trader CFP: other agent isnt a producer or retailer");
                            price = 0;
                            break;
                    }
                    message.sender().deliverMessage(new Message(this, Message.Content.PROPOSE, message.what(), price));
                }
            } else if (message.content() == Message.Content.ACCEPT_PROPOSAL) {
                int price;
                switch (message.sender().getType()) {
                    case "producer":
                        buy(message.what());
                        break;
                    case "retailer":
                        sell();
                        break;
                }
                isInSale = false;
            } else if (message.content() == Message.Content.REJECT_PROPOSAL) {
                message.sender().deliverMessage(new Message(this, Message.Content.CFP, message.what()));
            } else if (message.content() == Message.Content.FAILURE) {
                switch (message.sender().getType()) {
                    case "producer":
                        this.setProduct("none");
                        status = "chooseProduct";
                        break;
                    case "retailer":
                        break;
                }
                isInSale = false;
            }
        }
        messageWaiting = false;
        messages.clear();
    }

    // Calling a movement function to move towards the Producer selling the
    // Trader's current product. Something should happen when the Trader meets 
    // another Agent - the famous YOU WILL HAVE TO IMPLEMENT THIS YOURSELF.
    private void moveToProducer() {
        super.moveToGoal(findXLocProduct(this.getProduct()), findYLocProduct(this.getProduct()));
        Vector<Agent> agentsInRange = super.getAgentsInRange();

        for (Agent agent : agentsInRange) {
            if (agent instanceof Producer && agent.getProduct().equals(getProduct()) && backAwayCounter == 0) {
                status = "negotiateBuy";
            }
        }
    }

    // Calling a movement function to move towards the Retailer to sell the
    // Trader's current product. Something should happen when the Trader meets 
    // another Agent - the famous YOU WILL HAVE TO IMPLEMENT THIS YOURSELF.
    private void moveToRetailer() {
        super.moveToGoal((int) Math.ceil(scape.xSize / 2), (int) Math.ceil(scape.ySize / 2));
        Vector<Agent> agentsInRange = super.getAgentsInRange();

        for (Agent agent : agentsInRange) {
            if (agent instanceof Retailer && agent.getProduct().equals("none") && backAwayCounter == 0) {
                status = "negotiateSale";
            }
        }
    }

    // Negotiating a buy from a Producer.
    private void negotiateBuy() {
        if (!isInSale) {
            isInSale = true;
            numNegotiations = 0;
            for (Agent agent : getAgentsInRange()) {
                if (agent instanceof Producer) {
                    agent.deliverMessage(new Message(this, Message.Content.CFP, getProduct()));
                }
            }
        }
    }

    // Negotiating a sale to a Retailer.
    private void negotiateSale() {
        if (!isInSale) {
            isInSale = true;
            numNegotiations = 0;
            for (Agent agent : getAgentsInRange()) {
                if (agent instanceof Retailer) {
                    agent.deliverMessage(new Message(this, Message.Content.CFP, this.getProduct()));
                }
            }
        }
    }

    // Buying a product from a Producer.
    private void buy(String product) {
        this.setProduct(product);
        status = "moveToRetailer";
    }

    // Selling a product to a Retailer.
    private void sell() {
        this.setProduct("none");
        status = "chooseProduct";
    }

    // A utility function to find the "XLocation" of a Producer specified by its
    // product.
    private int findXLocProduct(String product) {
        if (product.equals("fruit") || product.equals("wine")) {
            return 0;
        }

        if (product.equals("meat") || product.equals("dairy")) {
            return scape.xSize - 1;
        }

        return -1000;
    }

    // A utility function to find the "YLocation" of a Producer specified by its
    // product.
    private int findYLocProduct(String product) {
        if (product.equals("fruit") || product.equals("meat")) {
            return 0;
        }

        if (product.equals("wine") || product.equals("dairy")) {
            return scape.ySize - 1;
        }

        return -1000;
    }

    // A utility function linking the products to numbers, used in
    // chooseProduct().
    private String getProduct(int i) {
        switch (i) {
            case 0:
                return "fruit";
            case 1:
                return "meat";
            case 2:
                return "wine";
            case 3:
                return "dairy";
        }
        return null;
    }

    // Allowing the Trader to set its own buyPrices.
    private void setBuyPrice(String product, int price) {
        if (product.equals("fruit")) {
            estBuyFruit = price;
        }
        if (product.equals("meat")) {
            estBuyMeat = price;
        }
        if (product.equals("wine")) {
            estBuyWine = price;
        }
        if (product.equals("dairy")) {
            estBuyDairy = price;
        }
    }

    // A public "getBuyPrice" function, only intended for use by the Trader
    // itself, or for statistics purposes.
    // NOT for exchanging information with other agents, and should not be
    // called by them.
    public int getBuyPrice(String product) {
        int price = 0;
        if (product.equals("fruit")) {
            price = estBuyFruit;
        }
        if (product.equals("meat")) {
            price = estBuyMeat;
        }
        if (product.equals("wine")) {
            price = estBuyWine;
        }
        if (product.equals("dairy")) {
            price = estBuyDairy;
        }
        return price;
    }

    // A public "getSellPrice" function, only intended for use by the Trader
    // itself, or for statistics purposes.
    // in Scape. NOT for exchanging information with other agents, and should
    // not be called by them.
    public int getSellPrice(String product) {
        int price = 0;
        if (product.equals("fruit")) {
            price = estSellFruit;
        }
        if (product.equals("meat")) {
            price = estSellMeat;
        }
        if (product.equals("wine")) {
            price = estSellWine;
        }
        if (product.equals("dairy")) {
            price = estSellDairy;
        }
        return price;
    }

    // Allowing the Trader to set its own sellPrices.
    private void setSellPrice(String product, int price) {
        if (product.equals("fruit")) {
            estSellFruit = price;
        }
        if (product.equals("meat")) {
            estSellMeat = price;
        }
        if (product.equals("wine")) {
            estSellWine = price;
        }
        if (product.equals("dairy")) {
            estSellDairy = price;
        }
    }

    // A public functioning returning the Agent's state. Only for statistics and
    // visualisation purposes in Scape,
    // MainPanel and ButtonPanel. NOT for exchanging information with other
    // agents, and should not be called by them.
    public String getState() {
        return status;
    }

    @Override
    public void move(Site newS) {
        if (newS.getAgent() != null && backAwayCounter == 0) {
            numStepsIdle++;
        } else {
            numStepsIdle = 0;
        }
        if (numStepsIdle > MAX_NUM_STEPS_IDLE && backAwayCounter == 0) {
            numStepsIdle = 0;
            backAwayCounter = INITIAL_BACK_AWAY_COUNTER;
            if (this.getState().equals("moveToProducer")) {
                status = "moveToRetailer";

            } else {
                status = "moveToProducer";
            }
        } else if (backAwayCounter == 1) {
            if (this.getState().equals("moveToProducer")) {
                status = "moveToRetailer";

            } else {
                status = "moveToProducer";
            }
            backAwayCounter--;
        } else {
            super.move(newS);
            backAwayCounter -= (backAwayCounter == 0 ? 0 : 1);
        }
    }

}
