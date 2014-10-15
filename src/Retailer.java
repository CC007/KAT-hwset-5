public class Retailer extends Agent {

    // Stock variables
    private int fruitStock;
    private int meatStock;
    private int wineStock;
    private int dairyStock;

    // Price variables
    private int fruitPrice;
    private int meatPrice;
    private int winePrice;
    private int dairyPrice;

    // Sale variables
    private int saleQuantity = 20;
    private int stockDecrease = 5;
    private int upperSL = 75;
    private int lowerSL = 25;

    // The Retailer Constructor
    public Retailer(Scape controller) {
        super(controller, "retailer");
        this.setProduct("none");
        fruitStock = 75;
        meatStock = 75;
        wineStock = 75;
        dairyStock = 75;

        fruitPrice = 50;
        meatPrice = 50;
        winePrice = 50;
        dairyPrice = 50;
    }

    // The Retailer's act function, called once per step, handling all the Retailer's behavior.
    public void act() {
        decreaseStocks();
        updatePrices();
        if (messageWaiting) {
            handleMessages();
        }
    }

    //  Handling all messages received this step, then emptying the message Vector.
    private void handleMessages() {
        int stockChange = 0;
        int buyPrice;
        for (Message message : messages) {
            switch (message.what()) {
                case "wine":
                    buyPrice = winePrice;
                    break;
                case "meat":
                    buyPrice = meatPrice;
                    break;
                case "fruit":
                    buyPrice = fruitPrice;
                    break;
                case "dairy":
                    buyPrice = dairyPrice;
                    break;
                default:
                    //only in case of a failure, it doesnt use buyPrice
                    buyPrice = 0;
            }
            if (message.content() == Message.Content.CFP) {
                message.sender().deliverMessage(new Message(this, Message.Content.PROPOSE, getProduct(), buyPrice));
            } else if (message.content() == Message.Content.REJECT_PROPOSAL) {
                message.sender().deliverMessage(new Message(this, Message.Content.CFP, getProduct()));
                buyPrice = (int) (buyPrice - (buyPrice - message.number()) * 0.1);
            } else if (message.content() == Message.Content.ACCEPT_PROPOSAL) {
                if (message.number() > buyPrice) {
                    //the trader cheated!
                    message.sender().deliverMessage(new Message(this, Message.Content.FAILURE, message.what()));
                    stockChange -= saleQuantity; //sale was cancelled
                }
            } else if (message.content() == Message.Content.PROPOSE) {
                if (message.number() > buyPrice) {
                    message.sender().deliverMessage(new Message(this, Message.Content.REJECT_PROPOSAL, message.what(), message.number()));
                } else {
                    message.sender().deliverMessage(new Message(this, Message.Content.ACCEPT_PROPOSAL, message.what(), message.number()));
                }
            } else if (message.content() == Message.Content.FAILURE) {
                stockChange -= saleQuantity; //sale was cancelled
            }
            switch (message.what()) {
                case "wine":
                    wineStock += stockChange;
                    winePrice = buyPrice;
                    break;
                case "meat":
                    meatStock += stockChange;
                    meatPrice = buyPrice;
                    break;
                case "fruit":
                    fruitStock += stockChange;
                    fruitPrice = buyPrice;
                    break;
                case "dairy":
                    dairyStock += stockChange;
                    dairyPrice = buyPrice;
                    break;
                default:
                //only in case of a failure, it doesnt use stockChange
            }
        }
        messages.clear();
        messageWaiting = false;
    }

    // Handling a sale, by increasing stock and increasing the buyPrice.
    public void buy(String product) {
        addStock(product);
        if (getPrice(product) > 2) {
            int newPrice = getPrice(product) - 1;
            setPrice(product, newPrice);
        }
    }

    // Decreasing the Retailer's stocks, called once per step, to simulate sales.
    private void decreaseStocks() {
        if (fruitStock > stockDecrease) {
            fruitStock = fruitStock - stockDecrease;
        }
        if (meatStock > stockDecrease) {
            meatStock = meatStock - stockDecrease;
        }
        if (wineStock > stockDecrease) {
            wineStock = wineStock - stockDecrease;
        }
        if (dairyStock > stockDecrease) {
            dairyStock = dairyStock - stockDecrease;
        }
    }

    // Evaluating the Retailer's buyprices, called once per step, adjusting them based on current stock.
    private void updatePrices() {
        if (fruitStock >= upperSL && fruitPrice > 1) {
            fruitPrice--;
        }

        if (fruitStock < lowerSL && fruitPrice < 100) {
            fruitPrice++;
        }

        if (meatStock >= upperSL && meatPrice > 1) {
            meatPrice--;
        }

        if (meatStock < lowerSL && meatPrice < 100) {
            meatPrice++;
        }

        if (wineStock >= upperSL && winePrice > 1) {
            winePrice--;
        }

        if (wineStock < lowerSL && winePrice < 100) {
            winePrice++;
        }

        if (dairyStock >= upperSL && dairyPrice > 1) {
            dairyPrice--;
        }

        if (dairyStock < lowerSL && dairyPrice < 100) {
            dairyPrice++;
        }
    }

    // A public "getPrice" function, only intended for use by the Retailer itself, or for statistics purposes.
    // in Scape. NOT to be used to exchange information between agents.
    public int getPrice(String product) {
        int price = 0;

        if (product.equals("fruit")) {
            return fruitPrice;
        }

        if (product.equals("meat")) {
            return meatPrice;
        }

        if (product.equals("wine")) {
            return winePrice;
        }

        if (product.equals("dairy")) {
            return dairyPrice;
        }

        return price;
    }

    // A public "getStock" function, only intended for use by the Retailer itself, or for statistics purposes.
    // in Scape. NOT for exchanging information with other agents, and should not be called by them.
    public int getStock(String product) {
        int stock = -1000;

        if (product.equals("fruit")) {
            stock = fruitStock;
        }

        if (product.equals("meat")) {
            stock = meatStock;
        }

        if (product.equals("wine")) {
            stock = wineStock;
        }

        if (product.equals("dairy")) {
            stock = dairyStock;
        }

        return stock;
    }

    // Can be called to allow the Retailer to adjust its own buyPrices.
    private void setPrice(String product, int price) {

        if (product.equals("fruit")) {
            fruitPrice = price;
        }

        if (product.equals("meat")) {
            meatPrice = price;
        }

        if (product.equals("wine")) {
            winePrice = price;
        }

        if (product.equals("dairy")) {
            dairyPrice = price;
        }
    }

    // A function to add to the Retailer's current stock of a product;
    // for instance, after a succesful buy from a Trader.
    private void addStock(String product) {
        if (product.equals("fruit")) {
            fruitStock = fruitStock + saleQuantity;
        }

        if (product.equals("meat")) {
            meatStock = meatStock + saleQuantity;
        }

        if (product.equals("wine")) {
            wineStock = wineStock + saleQuantity;
        }

        if (product.equals("dairy")) {
            dairyStock = dairyStock + saleQuantity;
        }
    }
}
