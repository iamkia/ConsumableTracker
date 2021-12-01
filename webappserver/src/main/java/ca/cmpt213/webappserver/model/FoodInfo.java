package ca.cmpt213.webappserver.model;

import ca.cmpt213.webappserver.control.ConsumableManager;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static java.lang.Math.abs;

/*
 * @author Kia Jalali
 */

/**
 * Class that will be returned when the ConsumableFactory decides that item is a drink
 */
@Value
public class FoodInfo implements Consumable {
    // https://octoperf.com/blog/2018/02/01/polymorphism-with-jackson/#type-mapping
    int id;
    int choice;
    String name;
    String note;
    double price;
    int weight;
    LocalDateTime expiryDate;

    /**
     * @param choice     of the user
     * @param name       of food
     * @param note       of the food item
     * @param price      of the food
     * @param weight     of the food
     * @param expiryDate date that it will expire
     */
    public FoodInfo(@JsonProperty("choice") final int choice,
                    @JsonProperty("name") final String name,
                    @JsonProperty("note") final String note,
                    @JsonProperty("price") final double price,
                    @JsonProperty("weight") final int weight,
                    @JsonProperty("expiryDate") final LocalDateTime expiryDate) {
        this.id = ConsumableManager.GenerateId();
        this.choice = choice;
        this.name = name;
        this.note = note;
        this.price = price;
        this.weight = weight;
        this.expiryDate = expiryDate;
    }

    /**
     * @return the id of the user
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * @return the choice of the user
     */
    @Override
    public int getChoice() {
        return choice;
    }

    /**
     * @return the name of the food
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @return the name of the food
     */
    @Override
    public String getNote() {
        return note;
    }

    /**
     * @return the name of the food
     */
    @Override
    public double getPrice() {
        return price;
    }

    /**
     * @return the name of the food
     */
    @Override
    public int getAmount() {
        return weight;
    }

    /**
     * @return the expiry date of the food
     */
    @Override
    // https://stackoverflow.com/questions/29956175/json-java-8-localdatetime-format-in-spring-boot
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    /**
     * @return days left till expiry
     */
    @Override
    public int daysTillExp() {
        return (int) ChronoUnit.DAYS.between(this.expiryDate, LocalDateTime.now());
    }

    /**
     * @return whether the item is expired or not
     */
    @Override
    public boolean isExp() {
        int days = (int) ChronoUnit.DAYS.between(this.expiryDate, LocalDateTime.now());
        return days > 0;
    }

    /**
     * @return what we compare to
     */
    @Override
    public int compareTo(Consumable o) {
        return expiryDate.compareTo(o.getExpiryDate());
    }

    /**
     * @return the item in a string format, so we can see the object in a nice format
     */
    @Override
    public String toString() {
        // https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = this.expiryDate.format(formatter);

        String foodState = "";
        if (this.daysTillExp() < 0) foodState += "will expire in " + abs(this.daysTillExp()) + " day(s)\n";
        else if (this.daysTillExp() == 0) foodState += "will expire today.\n";
        else foodState += "is expired for " + this.daysTillExp() + " day(s)\n";

        DecimalFormat decimalFormat = new DecimalFormat(".00");
        String priceFormatted = decimalFormat.format(this.price);
        return "<html> Name: " + this.name + "<br>" +
                "Note: " + this.note + "<br>" +
                "Price: " + priceFormatted + "<br>" +
                "Weight: " + this.weight + "<br>" +
                "Expiry date: " + date + "<br>" +
                "This food item " + foodState;
    }
}
