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
    private int saleQuantity = 40;
    private int stockDecrease = 1;
    private int upperSL = 50;
    private int lowerSL = 2;

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
        for (Message message : messages) {
            if (message.content() == Message.Content.CFP) {
                message.sender().deliverMessage(new Message(this, Message.Content.PROPOSE, message.what(), getPrice(message.what())));
            } else if (message.content() == Message.Content.REJECT_PROPOSAL) {
                message.sender().deliverMessage(new Message(this, Message.Content.CFP, getProduct()));
            } else if (message.content() == Message.Content.ACCEPT_PROPOSAL) {
                buy(message.what());
            } else if (message.content() == Message.Content.PROPOSE) {
                if (message.number() > getPrice(message.what())) {
                    message.sender().deliverMessage(new Message(this, Message.Content.REJECT_PROPOSAL, message.what(), message.number()));
                    setPrice(message.what(), (int) (getPrice(message.what()) - (getPrice(message.what()) - message.number()) * 0.1));
                } else {
                    message.sender().deliverMessage(new Message(this, Message.Content.ACCEPT_PROPOSAL, message.what(), message.number()));
                    buy(message.what());
                }
            } else if (message.content() == Message.Content.FAILURE) {
                //Do nothing
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
