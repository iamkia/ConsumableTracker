package ca.cmpt213.a4.client.view;


import ca.cmpt213.a4.client.control.ConsumableManager;
import ca.cmpt213.a4.client.model.Consumable;
import ca.cmpt213.a4.client.model.ConsumableFactory;
import com.github.lgooddatepicker.components.DateTimePicker;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

import static ca.cmpt213.a4.client.view.MainGUI.refreshList;


/**
 * When the user wants to add an item this is the class that will be called
 * its responsible for handling calling the right logic methods and give a good user experience
 */
public class AddGUI extends JDialog {
    public AddGUI() {
        // https://stackoverflow.com/questions/25513711/how-to-make-my-textfield-bigger-for-gui-in-java
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel mainPanel = new JPanel();
        // panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        TitledBorder title = BorderFactory.createTitledBorder("Consumable Tracker");
        mainPanel.setBorder(title);

        final JPanel typesPanel = new JPanel();

        typesPanel.add(new JLabel("Type: "));
        String[] listOfItems = {"Food", "Drink"};

        JComboBox itemDropDown = new JComboBox(listOfItems);
        itemDropDown.setSelectedIndex(-1);
        typesPanel.add(itemDropDown);

        mainPanel.add(typesPanel);


        JPanel namePanel = new JPanel();
        JPanel notesPanel = new JPanel();
        JPanel pricePanel = new JPanel();
        JPanel weightPanel = new JPanel();

        namePanel.add(new JLabel("Name: "));
        JTextField nameTextField = new JTextField(25);
        namePanel.add(nameTextField);
        mainPanel.add(namePanel);

        notesPanel.add(new JLabel("Notes: "));
        JTextField notesTextField = new JTextField(25);
        notesPanel.add(notesTextField);
        mainPanel.add(notesPanel);


        pricePanel.add(new JLabel("Price: "));
        JTextField priceTextField = new JTextField(25);
        pricePanel.add(priceTextField);
        mainPanel.add(pricePanel);

        weightPanel.add(new JLabel("Weight: "));
        JTextField weightTextField = new JTextField(25);
        weightPanel.add(weightTextField);
        mainPanel.add(weightPanel);

        final JPanel dateTimePanel = new JPanel();
        dateTimePanel.add(new JLabel("Date: "));
        DateTimePicker dateTimePicker = new DateTimePicker();
        dateTimePanel.add(dateTimePicker);


        mainPanel.add(dateTimePanel);

        JButton createButton = new JButton("Create");
        createButton.addActionListener(e -> {
            if (itemDropDown.getSelectedIndex() != -1 &&
                    nameTextField.getText() != null &&
                    !priceTextField.getText().isEmpty() &&
                    !weightTextField.getText().isEmpty() &&
                    dateTimePicker.getDateTimeStrict() != null) {
                ConsumableFactory factory = new ConsumableFactory();

                Consumable newConsumable = factory.getInstance(1, itemDropDown.getSelectedIndex() + 1,
                        nameTextField.getText(), notesTextField.getText(),
                        Double.parseDouble(priceTextField.getText()), Integer.parseInt(weightTextField.getText()), dateTimePicker.getDateTimeStrict());
                ConsumableManager.AddConsumable(newConsumable);
                ConsumableManager.AddConsumableToServer(newConsumable);
                ConsumableManager.RefreshList();
                refreshList();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Complete Form");
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        mainPanel.add(createButton);
        mainPanel.add(cancelButton);

        getContentPane().add(BorderLayout.CENTER, mainPanel);
        setSize(300, 300);
        setLocationByPlatform(true);
        setVisible(true);
        setResizable(false);
    }
}
