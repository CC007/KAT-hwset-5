import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EtchedBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class ButtonPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    Dimension preferredSize = new Dimension(330, 660);
    Scape scape;
    JTextPane info;
    JPanel buttons1, buttons2, buttons3, body;
    JLabel forwardLabel;
    JTextField forwardEpochs;
    JButton next, forward, restart, exit;
    private String newline = "\n";
    String content[] = new String[34];

    String style[] = {"bold", "bold", "regular", "regular", "regular", "regular",
        "bold", "italic", "regular", "italic", "regular",
        "bold", "italic", "regular", "italic", "regular",
        "bold", "italic", "regular", "italic", "regular",
        "bold", "italic", "regular", "italic", "regular",
        "bold", "regular", "italic", "regular", "regular", "regular", "regular", "regular",};

    public ButtonPanel(Scape controller) {
        this.scape = controller;
        this.setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        info = new JTextPane();
        info.setPreferredSize(new Dimension(270, 570));
        info.setMaximumSize(new Dimension(270, 570));
        info.setEditable(false);
        info.setOpaque(false);
        info.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0), BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), BorderFactory.createEmptyBorder(5, 5, 5, 5))));
        StyledDocument doc = info.getStyledDocument();
        addStylesToDocument(doc);
        updateInfo();
        addInfo(scape.grid[15][15]);

        buttons2 = new JPanel();
        buttons2.setLayout(new BoxLayout(buttons2, BoxLayout.LINE_AXIS));

        forwardEpochs = new JTextField("100");
        forwardEpochs.setMaximumSize(new Dimension(100, 25));

        forward = new JButton("Forward");
        forward.setActionCommand("forward");
        forward.addActionListener(this);

        next = new JButton("Next");
        next.setActionCommand("next");
        next.addActionListener(this);

        buttons2.add(next);
        buttons2.add(forwardEpochs);
        buttons2.add(Box.createRigidArea(new Dimension(5, 0)));
        buttons2.add(forward);

        buttons3 = new JPanel();
        buttons3.setLayout(new BoxLayout(buttons3, BoxLayout.LINE_AXIS));
        buttons3.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        restart = new JButton("Restart");
        restart.setActionCommand("restart");
        restart.addActionListener(this);

        exit = new JButton("Exit");
        exit.setActionCommand("exit");
        exit.addActionListener(this);

        buttons3.add(Box.createHorizontalGlue());
        buttons3.add(restart);
        buttons3.add(Box.createRigidArea(new Dimension(5, 0)));
        buttons3.add(exit);

        body = new JPanel();
        body.setLayout(new GridLayout(0, 1));

		// body.add(buttons1);
        // body.add(forwardLabel);
        body.add(buttons2);

        this.add(info, BorderLayout.NORTH);
        this.add(body, BorderLayout.CENTER);
        this.add(buttons3, BorderLayout.SOUTH);
    }

    public Dimension getPreferredSize() {
        return preferredSize;
    }

    public void actionPerformed(ActionEvent e) {
        if ("next".equals(e.getActionCommand())) {
            this.update(1);
        }

        if ("forward".equals(e.getActionCommand())) {
            Integer num = new Integer(forwardEpochs.getText());
            this.update(num.intValue());
        }

        if ("restart".equals(e.getActionCommand())) {
            scape.frame.dispose();
            Scape.startSimulation();
        }

        if ("exit".equals(e.getActionCommand())) {
            scape.frame.dispose();
        }
    }

    private void update(int cycles) {
        if (cycles < 0) {
            while (true) {
                scape.step();
                addInfo(scape.grid[scape.mainPanel.xSelected][scape.mainPanel.ySelected]);
                scape.epochs++;
            }
        } else {
            for (int c = 0; c < cycles; c++) {
                scape.step();
                addInfo(scape.grid[scape.mainPanel.xSelected][scape.mainPanel.ySelected]);
                scape.epochs++;
                scape.mainPanel.update();
            }
        }
    }

    private void addStylesToDocument(StyledDocument doc) {
        // Initialize some styles.
        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "SansSerif");
        StyleConstants.setFontSize(regular, 10);

        Style s = doc.addStyle("italic", regular);
        StyleConstants.setItalic(s, true);

        s = doc.addStyle("bold", regular);
        StyleConstants.setBold(s, true);
    }

    public void addInfo(Site s) {
        content[0] = "Scape";
        content[1] = "Step: " + scape.epochs;
        content[2] = "Tile Coordinates: (" + s.getXPosition() + ", " + s.getYPosition() + ")";
        content[3] = "Agents: " + scape.agents.size();
        content[4] = "Buying Traders: " + scape.numBuyers;
        content[5] = "Selling Traders: " + scape.numSellers;

        content[6] = newline + "Fruit Data";
        content[7] = "ProdStock\tNumTraders\tRetStock";
        content[8] = scape.fruitGrowerStock + "\t" + scape.numFruitTraders + "\t" + scape.retailerFruitStock;
        content[9] = "ProdSell$\tAvgTradBuy$\tAvgTradSell$\tRetBuy$";
        content[10] = scape.fruitGrowerPrice + "\t" + scape.avgFruitBuyPrice + "\t" + scape.avgFruitSellPrice + "\t" + scape.retailerFruitPrice;

        content[11] = newline + "Meat Data";
        content[12] = "ProdStock\tNumTraders\tRetStock";
        content[13] = scape.meatFarmerStock + "\t" + scape.numMeatTraders + "\t" + scape.retailerMeatStock;
        content[14] = "ProdSell$\tAvgTradBuy$\tAvgTradSell$\tRetBuy$";
        content[15] = scape.meatFarmerPrice + "\t" + scape.avgMeatBuyPrice + "\t" + scape.avgMeatSellPrice + "\t" + scape.retailerMeatPrice;

        content[16] = newline + "Wine Data";
        content[17] = "ProdStock\tNumTraders\tRetStock";
        content[18] = scape.wineGrowerStock + "\t" + scape.numWineTraders + "\t" + scape.retailerWineStock;
        content[19] = "ProdSell$\tAvgTradBuy$\tAvgTradSell$\tRetBuy$";
        content[20] = scape.wineGrowerPrice + "\t" + scape.avgWineBuyPrice + "\t" + scape.avgWineSellPrice + "\t" + scape.retailerWinePrice;

        content[21] = newline + "Dairy Data";
        content[22] = "ProdStock\tNumTraders\tRetStock";
        content[23] = scape.dairyFarmerStock + "\t" + scape.numDairyTraders + "\t" + scape.retailerDairyStock;
        content[24] = "ProdSell$\tAvgTradBuy$\tAvgTradSell$\tRetBuy$";
        content[25] = scape.dairyFarmerPrice + "\t" + scape.avgDairyBuyPrice + "\t" + scape.avgDairySellPrice + "\t" + scape.retailerDairyPrice;

        Agent agent = s.getAgent();

        if (agent != null && agent.getType().equals("trader")) {
            Trader trader = (Trader) agent;

            content[26] = newline + "Trader on Tile Info";
            content[27] = "Name:\t" + trader.toString();
            content[28] = "Product\tBuyprice\tEst. Sellprice";
            content[29] = "Fruit:\t" + trader.getBuyPrice("fruit") + "\t" + trader.getSellPrice("fruit");
            content[30] = "Meat:\t" + trader.getBuyPrice("meat") + "\t" + trader.getSellPrice("meat");
            content[31] = "Wine:\t" + trader.getBuyPrice("wine") + "\t" + trader.getSellPrice("wine");
            content[32] = "Dairy:\t" + trader.getBuyPrice("dairy") + "\t" + trader.getSellPrice("dairy");
            content[33] = "Agent State: " + trader.getState();
        } else if (agent != null && agent.getType().equals("producer")) {
            Producer producer = (Producer) agent;
            content[26] = newline + "Producer on Tile Info";
            content[27] = "Name:\t" + producer.toString();
            content[28] = "";
            content[29] = (producer.getProduct()).substring(0, 1).toUpperCase() + (producer.getProduct()).substring(1) + "Stock:\t" + producer.getStock();
            content[30] = (producer.getProduct()).substring(0, 1).toUpperCase() + (producer.getProduct()).substring(1) + "Sellprice:\t" + producer.getSellPrice();
            content[31] = "";
            content[32] = "";
            content[33] = "";
        } else if (agent != null && agent.getType().equals("retailer")) {
            Retailer retailer = (Retailer) agent;
            content[26] = newline + "Retailer on Tile Info";
            content[27] = "Name:\t" + retailer.toString();
            content[28] = "Product:\tBuyprice\tStock\t";
            content[29] = "Fruit:\t" + scape.retailerFruitPrice + "\t" + scape.retailerFruitStock;
            content[30] = "Meat:\t" + scape.retailerMeatPrice + "\t" + scape.retailerMeatStock;
            content[31] = "Wine:\t" + scape.retailerWinePrice + "\t" + scape.retailerWineStock;
            content[32] = "Dairy:\t" + scape.retailerDairyPrice + "\t" + scape.retailerDairyStock;
            content[33] = "";
        } else {
            content[26] = newline + "No Agent on Tile";
            content[27] = "";
            content[28] = "";
            content[29] = "";
            content[30] = "";
            content[31] = "";
            content[32] = "";
            content[33] = "";
        }

        updateInfo();
    }

    private void updateInfo() {
        StyledDocument doc = info.getStyledDocument();
        try {
            doc.remove(0, doc.getLength());
            for (int i = 0; i < content.length; i++) {
                doc.insertString(doc.getLength(), content[i] + newline, doc.getStyle(style[i]));
            }
        } catch (BadLocationException ble) {
            System.err.println("Couldn't insert text into text pane.");
        }
    }

    public String round(double value) {
        DecimalFormat df = new DecimalFormat("0.00");
        String stringValue = df.format(value);
        return stringValue;
    }
}
