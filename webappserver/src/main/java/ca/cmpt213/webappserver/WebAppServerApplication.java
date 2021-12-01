package ca.cmpt213.webappserver;

import ca.cmpt213.webappserver.control.ConsumableManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry to the server
 */
@SpringBootApplication
public class WebAppServerApplication {
    public static void main(String[] args) {
        ConsumableManager.loadItems();
        SpringApplication.run(WebAppServerApplication.class, args);
    }
}
