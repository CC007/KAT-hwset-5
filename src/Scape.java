/**
 * @author elske
 * @author Alle Veenstra
 *
 */
import java.awt.Container;
import java.awt.FlowLayout;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;

import javax.swing.JFrame;

public class Scape {

    // Global Scape Variables
    Site[][] grid;
    int xSize = 31;
    int ySize = 31;
    int epochs = 0;
    int numProducers = 4;
    int numRetailers = 1;
    int numTraders = 40;
    Vector<Agent> agents;
    Vector<Producer> producers;
    Vector<Retailer> retailers;
    Random gen = new Random();

    // Statistics Variables
    int numBuyers = 0;
    int numSellers = 0;
    int numFruitTraders = 0;
    int numMeatTraders = 0;
    int numWineTraders = 0;
    int numDairyTraders = 0;
    int fruitSellSub;
    int fruitBuySub;
    int meatSellSub;
    int meatBuySub;
    int wineSellSub;
    int wineBuySub;
    int dairySellSub;
    int dairyBuySub;

    double avgFruitSellPrice;
    double avgMeatSellPrice;
    double avgWineSellPrice;
    double avgDairySellPrice;

    double avgFruitBuyPrice;
    double avgMeatBuyPrice;
    double avgWineBuyPrice;
    double avgDairyBuyPrice;

    int fruitGrowerStock;
    int meatFarmerStock;
    int wineGrowerStock;
    int dairyFarmerStock;

    int fruitGrowerPrice;
    int meatFarmerPrice;
    int wineGrowerPrice;
    int dairyFarmerPrice;

    int retailerFruitStock;
    int retailerMeatStock;
    int retailerWineStock;
    int retailerDairyStock;

    int retailerFruitPrice;
    int retailerMeatPrice;
    int retailerWinePrice;
    int retailerDairyPrice;

    // Visualization Variables
    JFrame frame;
    MainPanel mainPanel;
    ButtonPanel buttonPanel;

    // Function involved in showing the simulation.
    public static void main(String args[]) {
        startSimulation();
    }

    // Function involved in showing the simulation.
    public static void startSimulation() {
        Scape controller = new Scape();
        controller.epochs = 0;
        controller.initGrid();
        controller.initAgents(controller);
        controller.createAndShowGUI(controller);
    }

    // Function involved in showing the simulation.
    private void createAndShowGUI(Scape controller) {
        // Create and set up the window.
        frame = new JFrame("Scape");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        // Set up the content pane.
        controller.buildUI(frame.getContentPane());

        // Display the window.
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        buttonPanel.forwardEpochs.grabFocus();
    }

    // Function involved in showing the simulation.
    private void buildUI(Container pane) {
        pane.setLayout(new FlowLayout());
        mainPanel = new MainPanel(this);
        pane.add(mainPanel);
        buttonPanel = new ButtonPanel(this);
        pane.add(buttonPanel);
    }

    // Initializing the Scape with Sites.
    private void initGrid() {
        grid = new Site[xSize][ySize];
        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                grid[x][y] = new Site(x, y);
            }
        }
    }

    // Initializing the Scape with Agents - generating the vectors the different
    // Agent types are stored in,
    // then calling the functions which will seed them in the Scape.
    private void initAgents(Scape controller) {
        agents = new Vector<Agent>();
        producers = new Vector<Producer>();
        retailers = new Vector<Retailer>();
        seedProducers(controller);
        seedRetailers(controller);
        seedTraders(controller);
    }

    // Inititializing the Producers.
    private void seedProducers(Scape controller) {
        Producer fruitGrower = new Producer(controller, "fruit");
        producers.add(fruitGrower);
        fruitGrower.setPosition(0, 0);
        grid[0][0].setAgent(fruitGrower);

        Producer meatFarmer = new Producer(controller, "meat");
        producers.add(meatFarmer);
        fruitGrower.setPosition(xSize - 1, 0);
        grid[xSize - 1][0].setAgent(meatFarmer);

        Producer wineGrower = new Producer(controller, "wine");
        producers.add(wineGrower);
        fruitGrower.setPosition(0, ySize - 1);
        grid[0][ySize - 1].setAgent(wineGrower);

        Producer dairyFarmer = new Producer(controller, "dairy");
        producers.add(dairyFarmer);
        fruitGrower.setPosition(xSize - 1, ySize - 1);
        grid[xSize - 1][ySize - 1].setAgent(dairyFarmer);
    }

    // Initializing the Retailers (or in this case, Lone Retailer).
    private void seedRetailers(Scape controller) {
        Retailer retailer = new Retailer(controller);
        retailers.add(retailer);
        retailer.setPosition((int) (Math.ceil(xSize / 2)), (int) (Math.ceil(ySize / 2)));
        grid[(int) Math.ceil(xSize / 2)][(int) Math.ceil(ySize / 2)].setAgent(retailer);
    }

    // Initializing the Traders.
    private void seedTraders(Scape controller) {
        for (int a = 0; a < numTraders; a++) {
            agents.add(new Trader(controller));
        }

        for (int a = 0; a < agents.size(); a++) {
            int x = 0;
            int y = 0;
            boolean free = false;
            while (!free) {
                x = gen.nextInt(xSize);
                y = gen.nextInt(ySize);
                if (grid[x][y].getAgent() == null) {
                    free = true;
                }
            }

            Agent agent = (Agent) agents.elementAt(a);
            agent.setPosition(x, y);
            grid[x][y].setAgent(agent);
        }
    }

    // The step function.
    public void step() {
        initStats();

        for (int a = 0; a < producers.size(); a++) {
            Producer producer = (Producer) producers.elementAt(a);
            producer.act();
            updateProducerStats((Producer) producer);
        }

        Collections.shuffle(agents);

        for (int a = 0; a < agents.size(); a++) {
            Trader trader = (Trader) agents.elementAt(a);
            trader.act();
            addStats((Trader) trader);
        }

        for (int a = 0; a < retailers.size(); a++) {
            Retailer retailer = (Retailer) retailers.elementAt(a);
            retailer.act();
            updateRetailerStats((Retailer) retailer);
        }

        updateStats();

        for (int x = 0; x < 2; x++) {
            System.out.println("");
        }
    }

    // Initializing various stats.
    private void initStats() {
        fruitSellSub = 0;
        fruitBuySub = 0;
        meatSellSub = 0;
        meatBuySub = 0;
        wineSellSub = 0;
        wineBuySub = 0;
        dairySellSub = 0;
        dairyBuySub = 0;

        avgFruitSellPrice = 0;
        avgMeatSellPrice = 0;
        avgWineSellPrice = 0;
        avgDairySellPrice = 0;

        avgFruitBuyPrice = 0;
        avgMeatBuyPrice = 0;
        avgWineBuyPrice = 0;
        avgDairyBuyPrice = 0;

        numBuyers = 0;
        numSellers = 0;

        numFruitTraders = 0;
        numMeatTraders = 0;
        numWineTraders = 0;
        numDairyTraders = 0;
    }

    // Initializing various stats.
    private void addStats(Trader trader) {
        if (trader.getState().equals("moveToProducer") || trader.getState().equals("negotiateBuy") || trader.getState().equals("buyFromProducer")) {
            numBuyers++;
        } else {
            numSellers++;
        }

        if (trader.getProduct().equals("fruit")) {
            numFruitTraders++;
        }

        if (trader.getProduct().equals("meat")) {
            numMeatTraders++;
        }

        if (trader.getProduct().equals("wine")) {
            numWineTraders++;
        }

        if (trader.getProduct().equals("dairy")) {
            numDairyTraders++;
        }

        fruitSellSub += trader.getSellPrice("fruit");
        fruitBuySub += trader.getBuyPrice("fruit");
        meatSellSub += trader.getSellPrice("meat");
        meatBuySub += trader.getBuyPrice("meat");
        wineSellSub += trader.getSellPrice("wine");
        wineBuySub += trader.getBuyPrice("wine");
        dairySellSub += trader.getSellPrice("dairy");
        dairyBuySub += trader.getBuyPrice("dairy");
    }

    // Updating various stats.
    private void updateStats() {
        avgFruitSellPrice = fruitSellSub / numTraders;
        avgMeatSellPrice = meatSellSub / numTraders;
        avgWineSellPrice = wineSellSub / numTraders;
        avgDairySellPrice = dairySellSub / numTraders;

        avgFruitBuyPrice = fruitBuySub / numTraders;
        avgMeatBuyPrice = meatBuySub / numTraders;
        avgWineBuyPrice = wineBuySub / numTraders;
        avgDairyBuyPrice = dairyBuySub / numTraders;
    }

    // Updating Producer stats.
    private void updateProducerStats(Producer producer) {
        String product = producer.getProduct();
        int stock = producer.getStock();
        int price = producer.getSellPrice();

        if (product.equals("fruit")) {
            fruitGrowerStock = stock;
            fruitGrowerPrice = price;
        }

        if (product.equals("meat")) {
            meatFarmerStock = stock;
            meatFarmerPrice = price;
        }

        if (product.equals("wine")) {
            wineGrowerStock = stock;
            wineGrowerPrice = price;
        }

        if (product.equals("dairy")) {
            dairyFarmerStock = stock;
            dairyFarmerPrice = price;
        }
    }

    // Updating Retailer stats.
    private void updateRetailerStats(Retailer retailer) {
        retailerFruitStock = retailer.getStock("fruit");
        retailerMeatStock = retailer.getStock("meat");
        retailerWineStock = retailer.getStock("wine");
        retailerDairyStock = retailer.getStock("dairy");

        retailerFruitPrice = retailer.getPrice("fruit");
        retailerMeatPrice = retailer.getPrice("meat");
        retailerWinePrice = retailer.getPrice("wine");
        retailerDairyPrice = retailer.getPrice("dairy");
    }
}
