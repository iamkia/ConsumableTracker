package ca.cmpt213.webappserver.controller;

import ca.cmpt213.webappserver.control.ConsumableManager;
import ca.cmpt213.webappserver.model.Consumable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
/*
 * @author Kia Jalali
 */

/**
 * GetRequests handles all the GET requests sent to this server
 */
@RestController
public class GetRequests {
    @GetMapping("/ping")
    public String PingBack() {
        return "System is up!";
    }

    @GetMapping("/listAll")
    public ArrayList<Consumable> listAllItem() {
        return ConsumableManager.GetAllItems();
    }

    @GetMapping("/listExpired")
    public ArrayList<Consumable> listExpired() {
        return ConsumableManager.GetAllExpired();
    }

    @GetMapping("/listNonExpired")
    public ArrayList<Consumable> listNonExpired() {
        return ConsumableManager.GetAllNonExpired();
    }

    @GetMapping("/listExpiringIn7Days")
    public ArrayList<Consumable> listExpiringIn7Days() {
        return ConsumableManager.GetAllExpiringIn7Days();
    }

    @GetMapping("exit")
    public String SaveCurrItems() {
        ConsumableManager.saveItems();
        return "SAVED ITEMS!";
    }
}
