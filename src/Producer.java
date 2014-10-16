public class Producer extends Agent {

    private int stock;
    private int sellPrice;

    // Sale Variables
    private int production = 2;
    private int saleQuantity = 40;
    private int upperSL = 175;
    private int lowerSL = 25;
    private int reserved = 0;

    // The Producer Constructor
    public Producer(Scape controller, String food) {
        super(controller, "producer");
        this.setProduct(food);
        stock = 100;
        sellPrice = 50;
    }

    // The Producer's act function, called once per step, handling all the Producer's behavior.
    public void act() {
        increaseStocks();
        updateSellPrice();
        if (messageWaiting) {
            handleMessages();
        }
    }

    // Increasing the Producer's stocks, called once per step, to simulate production.
    private void increaseStocks() {
        if (stock > (200 - production)) {
            stock = 200;
        } else {
            stock = stock + production;
        }
    }

    // Evaluating the Producer's sellprice, called once per step, adjusting it based on the current stock.
    // "upperS9tock)L(imit)" is set to 75, lowerS(tock)L(imit)" to 25.
    private void updateSellPrice() {
        if (stock > upperSL && sellPrice > 1) {
            sellPrice--;
        }
        if (stock < lowerSL && sellPrice < 100) {
            sellPrice++;
        }
    }

    // Handling all messages received this step, then emptying the message Vector.
    private void handleMessages() {
        for (Message message : messages) {
            if (message.content() == Message.Content.CFP && getProduct().equals(message.what())) {
                if (stock - reserved >= saleQuantity) {
                    message.sender().deliverMessage(new Message(this, Message.Content.PROPOSE, getProduct(), sellPrice));
                    reserved += saleQuantity; //set apart until bought
                } else {
                    message.sender().deliverMessage(new Message(this, Message.Content.FAILURE, getProduct()));
                }
            } else if (message.content() == Message.Content.REJECT_PROPOSAL && getProduct().equals(message.what())) {
                message.sender().deliverMessage(new Message(this, Message.Content.CFP, getProduct()));
            } else if (message.content() == Message.Content.ACCEPT_PROPOSAL && getProduct().equals(message.what())) {
                reserved -= saleQuantity;
                stock -= saleQuantity;
                sellPrice++;
            } else if (message.content() == Message.Content.PROPOSE && getProduct().equals(message.what())) {
                if (message.number() < sellPrice) {
                    message.sender().deliverMessage(new Message(this, Message.Content.REJECT_PROPOSAL, message.what(), message.number()));
                    sellPrice = (int) (sellPrice - (sellPrice - message.number()) * 0.1);
                    reserved -= saleQuantity;
                } else {
                    message.sender().deliverMessage(new Message(this, Message.Content.ACCEPT_PROPOSAL, message.what(), message.number()));
                    reserved -= saleQuantity;
                    stock -= saleQuantity;
                    sellPrice++;
                }
            } else if (message.content() == Message.Content.FAILURE) {
                reserved -= saleQuantity;
            }
        }
        messages.clear();
        messageWaiting = false;
    }

    // Handling a sale, by decreasing stock and increasing the sellPrice.
    private void sell() {
        //not used, reservation of stock used instead
    }

    // Returns the Producers' stock.
    public int getStock() {
        return stock;
    }

    // Returns the Producer's sellPrice.
    public int getSellPrice() {
        return sellPrice;
    }
}
