import ca.cmpt213.a4.client.control.ConsumableManager;
import ca.cmpt213.a4.client.model.Consumable;
import ca.cmpt213.a4.client.view.MainGUI;

import javax.swing.*;
import java.io.IOException;

/**
 * Entry to our program
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                ConsumableManager.loadItems();
                for (Consumable i : ConsumableManager.GetAllItems()) {
                    System.out.println(i.getId());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            new MainGUI();
        });
    }
}