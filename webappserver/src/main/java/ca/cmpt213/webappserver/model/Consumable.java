package ca.cmpt213.webappserver.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

/**
 * Consumable class pretty much defines the methods our food OR drink has
 */
@JsonTypeInfo(use = NAME, include = PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FoodInfo.class, name = "Food"),
        @JsonSubTypes.Type(value = DrinkInfo.class, name = "Drink")
})
public interface Consumable extends Comparable<Consumable> {
    // https://stackoverflow.com/questions/30362446/deserialize-json-with-jackson-into-polymorphic-types-a-complete-example-is-giv
    int getId();

    int getChoice();

    String getName();

    String getNote();

    double getPrice();

    int getAmount();

    LocalDateTime getExpiryDate();

    int daysTillExp();

    boolean isExp();
}