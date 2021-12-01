package ca.cmpt213.webappserver.controller;

import ca.cmpt213.webappserver.control.ConsumableManager;
import ca.cmpt213.webappserver.model.Consumable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/*
 * @author Kia Jalali
 */

/**
 * PostRequests handles all the POST requests sent to this server
 */
@RestController
public class PostRequests {
    @PostMapping("/addItem")
    public ArrayList<Consumable> AddItem(@RequestBody Consumable consumable) {
        ConsumableManager.AddConsumable(consumable);
        return ConsumableManager.GetAllItems();
    }

    @PostMapping("removeItem/{id}")
    public ArrayList<Consumable> RemoveItem(@PathVariable("id") int id) {
        ConsumableManager.RemoveById(id);
        return ConsumableManager.GetAllItems();
    }
}
