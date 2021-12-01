package ca.cmpt213.a4.client.control;

import ca.cmpt213.a4.client.model.Consumable;
import ca.cmpt213.a4.client.model.ConsumableFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import org.json.JSONObject;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
/**
 * Consumable Manger is the middle man between our view and server
 */
public class ConsumableManager {
    private static final ArrayList<Consumable> CONSUMABLE_ARRAY_LIST = new ArrayList<>();

    public static void AddConsumable(Consumable consumable) {
        CONSUMABLE_ARRAY_LIST.add(consumable);
    }

    public static void AddConsumableToServer(Consumable consumable) {
        String TypeOfConsumable = consumable.getChoice() == 1 ? "Food" : "Drink";
        String TypeOfWeight = consumable.getChoice() == 1 ? "weight" : "volume";

        // https://stackoverflow.com/questions/8876089/how-to-fluently-build-json-in-java
        String payLoad = new JSONObject()
                .put("@type", TypeOfConsumable)
                .put("choice", consumable.getChoice())
                .put("name", consumable.getName())
                .put("note", consumable.getNote())
                .put("price", consumable.getPrice())
                .put(TypeOfWeight, consumable.getAmount())
                .put("expiryDate", consumable.getExpiryDate())
                .toString();
        System.out.println(payLoad);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/addItem"))
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payLoad))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response);
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public static int getItemById(String name) {
        for (Consumable i : CONSUMABLE_ARRAY_LIST) {
            if (Objects.equals(name, i.getName())) {
                return i.getId();
            }
        }
        return -1;
    }

    public static Consumable FindItemById(int id) {
        for (Consumable i : CONSUMABLE_ARRAY_LIST) {
            if (i.getId() == id) {
                return i;
            }
        }
        return null;
    }

    public static void RefreshList() {
        CONSUMABLE_ARRAY_LIST.clear();
        // https://www.baeldung.com/java-http-request
        URL url = null;
        try {
            url = new URL("http://localhost:8080/listAll");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection conn = null;
        try {
            assert url != null;
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            assert conn != null;
            conn.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        try {
            conn.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int responseCode = 0;
        try {
            responseCode = conn.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        } else {
            StringBuilder inline = new StringBuilder();
            Scanner scanner = null;
            try {
                scanner = new Scanner(url.openStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (true) {
                assert scanner != null;
                if (!scanner.hasNext()) break;
                inline.append(scanner.nextLine());
            }
            scanner.close();

            ConsumableFactory factory = new ConsumableFactory();
            JsonElement fileElement = JsonParser.parseString(inline.toString());
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
                Consumable newFood = factory.getInstance(itemObj.get("id").getAsInt(), itemObj.get("choice").getAsInt(),
                        itemObj.get("name").getAsString(), itemObj.get("note").getAsString(),
                        itemObj.get("price").getAsDouble(), itemObj.get(getWeight).getAsInt(), formattedDate);
                CONSUMABLE_ARRAY_LIST.add(newFood);
            }
        }
    }

    public static void RemoveByName(String name) {
        int id = getItemById(name);
        Consumable consumable = FindItemById(id);
        if (consumable != null) {
            String TypeOfConsumable = consumable.getChoice() == 1 ? "Food" : "Drink";
            String TypeOfWeight = consumable.getChoice() == 1 ? "weight" : "volume";
            String payLoad = new JSONObject()
                    .put("@type", TypeOfConsumable)
                    .put("choice", consumable.getChoice())
                    .put("name", consumable.getName())
                    .put("note", consumable.getNote())
                    .put("price", consumable.getPrice())
                    .put(TypeOfWeight, consumable.getAmount())
                    .put("expiryDate", consumable.getExpiryDate())
                    .toString();
            System.out.println(payLoad);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/removeItem/" + id))
                    .header("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payLoad))
                    .build();
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println(response);
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        RefreshList();
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
    @SneakyThrows
    public static void loadItems() throws MalformedURLException, ProtocolException {
        // https://www.baeldung.com/java-http-request
        URL url = new URL("http://localhost:8080/listAll");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        int responseCode = conn.getResponseCode();

        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        } else {
            StringBuilder inline = new StringBuilder();
            Scanner scanner = new Scanner(url.openStream());

            while (scanner.hasNext()) {
                inline.append(scanner.nextLine());
            }
            scanner.close();

            ConsumableFactory factory = new ConsumableFactory();
            JsonElement fileElement = JsonParser.parseString(inline.toString());
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
                Consumable newFood = factory.getInstance(itemObj.get("id").getAsInt(), itemObj.get("choice").getAsInt(),
                        itemObj.get("name").getAsString(), itemObj.get("note").getAsString(),
                        itemObj.get("price").getAsDouble(), itemObj.get(getWeight).getAsInt(), formattedDate);
                CONSUMABLE_ARRAY_LIST.add(newFood);
            }
        }
    }

    /**
     * saves all the items to a json file
     */
    public static void saveItems() throws IOException {
        URL url = new URL("http://localhost:8080/exit");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        } else {
            System.out.println("Saved Items!");
        }
    }
}
