package ca.cmpt213.webappserver.control;

import ca.cmpt213.webappserver.model.Consumable;
import ca.cmpt213.webappserver.model.ConsumableFactory;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * @author Kia Jalali
 */

/**
 * Consumable manager is the middle man between control and view (in this case it's called controllers)
 */
public class ConsumableManager {
    private static final String JSON_FILE_NAME = "listOfItems.json";
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger();
    private static final ArrayList<Consumable> CONSUMABLE_ARRAY_LIST = new ArrayList<>();

    public static int GenerateId() {
        return ID_GENERATOR.incrementAndGet();
    }

    public static void AddConsumable(Consumable consumable) {
        CONSUMABLE_ARRAY_LIST.add(consumable);
    }

    public static void RemoveById(int id) {
        CONSUMABLE_ARRAY_LIST.removeIf(i -> Objects.equals(i.getId(), id));
    }

    public static ArrayList<Consumable> GetAllItems() {
        Collections.sort(CONSUMABLE_ARRAY_LIST);
        return CONSUMABLE_ARRAY_LIST;
    }

    public static ArrayList<Consumable> GetAllExpired() {
        ArrayList<Consumable> expiredFoods = new ArrayList<>();
        if (CONSUMABLE_ARRAY_LIST.size() == 0) return expiredFoods;
        Collections.sort(CONSUMABLE_ARRAY_LIST);
        for (Consumable i : CONSUMABLE_ARRAY_LIST) if (i.isExp()) expiredFoods.add(i);
        return expiredFoods;
    }

    public static ArrayList<Consumable> GetAllNonExpired() {
        ArrayList<Consumable> noneExpiredFoods = new ArrayList<>();
        if (CONSUMABLE_ARRAY_LIST.size() == 0) return noneExpiredFoods;
        Collections.sort(CONSUMABLE_ARRAY_LIST);
        for (Consumable i : CONSUMABLE_ARRAY_LIST) if (!i.isExp()) noneExpiredFoods.add(i);
        return noneExpiredFoods;
    }

    public static ArrayList<Consumable> GetAllExpiringIn7Days() {
        ArrayList<Consumable> soonToExpire = new ArrayList<>();
        if (CONSUMABLE_ARRAY_LIST.size() == 0) return soonToExpire;
        Collections.sort(CONSUMABLE_ARRAY_LIST);
        for (Consumable i : CONSUMABLE_ARRAY_LIST)
            if (!i.isExp() && i.daysTillExp() >= 0 && i.daysTillExp() <= 7) soonToExpire.add(i);
        return soonToExpire;
    }

    /**
     * loads all the items from a json file
     */
    public static void loadItems() {
        File file = new File(JSON_FILE_NAME);
        try {
            ConsumableFactory factory = new ConsumableFactory();
            JsonElement fileElement = JsonParser.parseReader(new FileReader(file));
            JsonArray jsonArray = fileElement.getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject itemObj = jsonArray.get(i).getAsJsonObject();
                String date = itemObj.get("expiryDate").getAsString();
                // https://stackoverflow.com/questions/19085073/java-how-to-convert-string-with-month-name-to-datetime
                DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .appendPattern("yyyy-MM-dd'T'HH:mm")
                        .toFormatter(Locale.CANADA);

                String[] WeightOrVolume = {"weight", "volume"};
                String getWeight = WeightOrVolume[itemObj.get("choice").getAsInt() - 1];
                LocalDateTime formattedDate = LocalDateTime.parse(date, formatter);
                Consumable newFood = factory.getInstance(itemObj.get("choice").getAsInt(),
                        itemObj.get("name").getAsString(), itemObj.get("note").getAsString(),
                        itemObj.get("price").getAsDouble(), itemObj.get(getWeight).getAsInt(), formattedDate);
                CONSUMABLE_ARRAY_LIST.add(newFood);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Nothing to load yet.");
        }
    }

    /**
     * saves all the items to a json file
     */
    public static void saveItems() {
        Gson myGson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
                new TypeAdapter<LocalDateTime>() {
                    @Override
                    public void write(JsonWriter jsonWriter,
                                      LocalDateTime localDateTime) throws IOException {
                        jsonWriter.value(localDateTime.toString());
                    }

                    @Override
                    public LocalDateTime read(JsonReader jsonReader) throws IOException {
                        return LocalDateTime.parse(jsonReader.nextString());
                    }
                    // https://sites.google.com/site/gson/gson-user-guide#TOC-Compact-Vs.-Pretty-Printing-for-JSON-Output-Format
                }).setPrettyPrinting().create();
        try {
            Writer writer = new FileWriter(JSON_FILE_NAME);
            myGson.toJson(CONSUMABLE_ARRAY_LIST, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
