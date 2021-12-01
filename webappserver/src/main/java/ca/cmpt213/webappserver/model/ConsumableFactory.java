package ca.cmpt213.webappserver.model;

import java.time.LocalDateTime;

/*
 * @author Kia Jalali
 */

/**
 * This class helps us decide what will our consumable will be (food or drink)
 * It also follows the singleton design pattern to make sure there's only one instance of this class
 */
public class ConsumableFactory {
    /**
     * @param choice     of user
     * @param name       of user
     * @param note       of user
     * @param price      of user
     * @param amount     of user
     * @param expiryDate of user
     * @return what class type of class should be returned
     */
    public Consumable getInstance(int choice, String name, String note, double price, int amount, LocalDateTime expiryDate) {
        if (choice == 1) {
            return new FoodInfo(choice, name, note, price, amount, expiryDate);
        } else if (choice == 2) {
            return new DrinkInfo(choice, name, note, price, amount, expiryDate);
        }
        return null;
    }
}