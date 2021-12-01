package ca.cmpt213.a4.client.view;

import ca.cmpt213.a4.client.control.ConsumableManager;
import ca.cmpt213.a4.client.model.Consumable;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;


/**
 * The first gui class the user sees when they start the program
 * It's responsible for create the programs frame and show all the list items in our arraylist
 */
public class MainGUI {
    private static JTabbedPane tabbedPane;
    private final JFrame mainFrame;
    private final JPanel mainPanel;

    public MainGUI() {
        mainFrame = new JFrame("My Consumable Tracker");
        mainFrame.pack();
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // https://stackoverflow.com/questions/6084039/create-custom-operation-for-setdefaultcloseoperation
        WindowListener exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showOptionDialog(
                        null, "Close Application?",
                        "Exit Confirmation", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == 0) {
                    try {
                        ConsumableManager.saveItems();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    System.exit(0);
                }
            }
        };
        mainFrame.addWindowListener(exitListener);


        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mainPanel = new JPanel(new BorderLayout());
        TitledBorder title = BorderFactory.createTitledBorder("Consumable Tracker");
        mainPanel.setBorder(title);

        tabbedPane = new JTabbedPane();
        refreshList();

        JButton addButton = new JButton("Add item");
        addButton.addActionListener(e -> new AddGUI());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainFrame.getContentPane().add(mainPanel, BoxLayout.X_AXIS);
        mainFrame.getContentPane().add(addButton, BorderLayout.SOUTH);
        mainFrame.setMinimumSize(new Dimension(350, 500));
        mainFrame.setResizable(false);
        mainFrame.setVisible(true);
    }

    public static void refreshList() {
        tabbedPane.removeAll();
        AllItemsTab allTab = new AllItemsTab();
        ExpiredTab expiredTab = new ExpiredTab();
        NonExpiredTab notExpiredTab = new NonExpiredTab();
        SoonToExpiredTab soonToExpire = new SoonToExpiredTab();

        JScrollPane allScrollPane = new JScrollPane(allTab, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollPane expiredScrollPane = new JScrollPane(expiredTab, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollPane notExpiredScrollPane = new JScrollPane(notExpiredTab, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollPane soonToExpireScrollPane = new JScrollPane(soonToExpire, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


        // https://stackoverflow.com/questions/5583495/how-do-i-speed-up-the-scroll-speed-in-a-jscrollpane-when-using-the-mouse-wheel
        allScrollPane.getVerticalScrollBar().setUnitIncrement(20);
        expiredScrollPane.getVerticalScrollBar().setUnitIncrement(20);
        notExpiredScrollPane.getVerticalScrollBar().setUnitIncrement(20);
        soonToExpireScrollPane.getVerticalScrollBar().setUnitIncrement(20);


        tabbedPane.addTab("All", allScrollPane);
        tabbedPane.addTab("Expired", expiredScrollPane);
        tabbedPane.addTab("Not Expired", notExpiredScrollPane);
        tabbedPane.addTab("Expires in 7 Days", soonToExpireScrollPane);
    }

    // https://stackoverflow.com/questions/41307943/how-can-i-add-jscrollpane-inside-a-jtabbedpanes-tab
    private static class AllItemsTab extends JPanel {
        AllItemsTab() {
            ArrayList<Consumable> allItems = ConsumableManager.GetAllItems();

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            if (allItems.isEmpty()) {
                JPanel emptyPanel = new JPanel();
                emptyPanel.add(new JLabel("NO ITEMS TO SHOW."));
                add(emptyPanel);
                return;
            }

            final JPanel[] items = new JPanel[allItems.size()];
            int itemCounter = 1;
            for (int i = 0; i < items.length; i++) {
                items[i] = new JPanel();

                items[i].add(new JLabel(allItems.get(i).toString()));
                String nameOfItem = allItems.get(i).getName();
                String titleString = allItems.get(i).getChoice() == 1 ?
                        "Food Item " + itemCounter++ : "Drink Item " + itemCounter++;
                items[i].setBorder(BorderFactory.createTitledBorder(titleString));

                JButton removeButton = new JButton("Remove");
                items[i].add(removeButton);

                int finalI = i;
                removeButton.addActionListener(e -> {
                    ConsumableManager.RemoveByName(nameOfItem);
                    refreshList();
                });

                add(items[i]);
            }
        }
    }

    private static class ExpiredTab extends JPanel {
        ExpiredTab() {
            ArrayList<Consumable> allItems = ConsumableManager.GetAllExpired();

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            if (allItems.isEmpty()) {
                JPanel emptyPanel = new JPanel();
                emptyPanel.add(new JLabel("NO EXPIRED ITEMS."));
                add(emptyPanel);
                return;
            }

            final JPanel[] items = new JPanel[allItems.size()];
            int itemCounter = 1;
            for (int i = 0; i < items.length; i++) {
                items[i] = new JPanel();

                items[i].add(new JLabel(allItems.get(i).toString()));
                String nameOfItem = allItems.get(i).getName();
                String titleString = allItems.get(i).getChoice() == 1 ?
                        "Food Item " + itemCounter++ : "Drink Item " + itemCounter++;
                items[i].setBorder(BorderFactory.createTitledBorder(titleString));

                JButton removeButton = new JButton("Remove");
                items[i].add(removeButton);

                int finalI = i;
                removeButton.addActionListener(e -> {
                    ConsumableManager.RemoveByName(nameOfItem);
                    refreshList();
                });

                add(items[i]);
            }
        }
    }

    private static class NonExpiredTab extends JPanel {
        NonExpiredTab() {
            ArrayList<Consumable> allItems = ConsumableManager.GetAllNonExpired();

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            if (allItems.isEmpty()) {
                JPanel emptyPanel = new JPanel();
                emptyPanel.add(new JLabel("NO NON-EXPIRED ITEMS."));
                add(emptyPanel);
                return;
            }

            final JPanel[] items = new JPanel[allItems.size()];
            int itemCounter = 1;
            for (int i = 0; i < items.length; i++) {
                items[i] = new JPanel();

                items[i].add(new JLabel(allItems.get(i).toString()));
                String nameOfItem = allItems.get(i).getName();
                String titleString = allItems.get(i).getChoice() == 1 ?
                        "Food Item " + itemCounter++ : "Drink Item " + itemCounter++;
                items[i].setBorder(BorderFactory.createTitledBorder(titleString));

                JButton removeButton = new JButton("Remove");
                items[i].add(removeButton);

                int finalI = i;
                removeButton.addActionListener(e -> {
                    ConsumableManager.RemoveByName(nameOfItem);
                    refreshList();
                });

                add(items[i]);
            }
        }
    }

    private static class SoonToExpiredTab extends JPanel {
        SoonToExpiredTab() {
            ArrayList<Consumable> allItems = ConsumableManager.GetAllExpiringIn7Days();
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            if (allItems.isEmpty()) {
                JPanel emptyPanel = new JPanel();
                emptyPanel.add(new JLabel("NO ITEM WILL EXPIRE SOON."));
                add(emptyPanel);
                return;
            }

            final JPanel[] items = new JPanel[allItems.size()];
            int itemCounter = 1;
            for (int i = 0; i < items.length; i++) {
                items[i] = new JPanel();

                items[i].add(new JLabel(allItems.get(i).toString()));
                String nameOfItem = allItems.get(i).getName();
                String titleString = allItems.get(i).getChoice() == 1 ?
                        "Food Item " + itemCounter++ : "Drink Item " + itemCounter++;
                items[i].setBorder(BorderFactory.createTitledBorder(titleString));

                JButton removeButton = new JButton("Remove");
                items[i].add(removeButton);

                int finalI = i;
                removeButton.addActionListener(e -> {
                    ConsumableManager.RemoveByName(nameOfItem);
                    refreshList();
                });

                add(items[i]);
            }
        }
    }
}
