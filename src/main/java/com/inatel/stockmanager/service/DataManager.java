package com.inatel.stockmanager.service;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import wiremock.net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Scanner;

@Service
@EnableAsync
public class DataManager {

    private static final String STOCKS_FILE = "src/main/java/com/inatel/stockmanager/data/stocksb";
    private static final String SUBSCRIPTIONS_FILE = "src/main/java/com/inatel/stockmanager/data/subscriptions";

    public boolean appendStock(String id, String description) {

        try {

            FileWriter writer = new FileWriter(STOCKS_FILE, true);
            writer.write(id + "|" + description);
            writer.write("\r\n");
            writer.close();
            return true;
        } catch (IOException e) {

            e.printStackTrace();
        }

        return false;
    }

    public boolean searchStock(String id) throws FileNotFoundException {

        Scanner scanner = new Scanner(new File(STOCKS_FILE));

        boolean found = false;
        String line;

        while (scanner.hasNextLine()) {

            line = scanner.nextLine();
            String[] splitted = line.split("\\|");
            line = splitted[0].substring(0, 5);

            if (id.equals(line)) {
                found = true;
                break;
            }

        }

        return found;
    }

    public boolean searchSubscription(String host) throws FileNotFoundException {

        Scanner scanner = new Scanner(new File(SUBSCRIPTIONS_FILE ));

        boolean found = false;
        String line;

        while (scanner.hasNextLine()) {

            line = scanner.nextLine();
            String[] splitted = line.split("\\|");
            int length = splitted[0].length();
            line = splitted[0].substring(0, length);

            if (host.equals(line)) {
                found = true;
                break;
            }

        }

        return found;
    }

    public boolean appendSubscription(String host, String port) {

        try {

            FileWriter writer = new FileWriter(SUBSCRIPTIONS_FILE, true);
            writer.write(host+"|"+port);
            writer.write("\r\n");
            writer.close();
            return true;

        } catch (IOException e) {

            e.printStackTrace();
        }

        return false;
    }

    public JSONObject getStocks() throws FileNotFoundException {

        Scanner scanner = new Scanner(new File(STOCKS_FILE));
        JSONObject stock = new JSONObject();

        String line;

        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            line = line.replace("\n", "").replace("\r", "");

            String[] splitted = line.split("\\|");

            stock.put(splitted[0], splitted[1]);

        }

        return stock;

    }

    public JSONObject getSubscriptions() throws FileNotFoundException {

        Scanner scanner = new Scanner(new File(SUBSCRIPTIONS_FILE ));
        JSONObject subscriptions = new JSONObject();

        String line;

        while (scanner.hasNextLine()) {

            line = scanner.nextLine();
            line = line.replace("\n", "").replace("\r", "");
            String[] splitted = line.split("\\|");
            int hostLength = splitted[0].length();
            int portLength = splitted[1].length();

            String host = splitted[0].substring(0, hostLength);
            String port = splitted[1].substring(0, portLength);

            String[] frag = line.split("\\/");
            int fragLength = frag[0].length();

            String url = host.substring(0, fragLength);
            String uri = host.substring(fragLength+1, hostLength);

            subscriptions.put(url+":"+port+uri, port);

        }

        return subscriptions;

    }

    @Async
    public void notifySubscribers(String host, String port) {

        RestTemplate restTemplate = new RestTemplate();
        String subscriberEndpoint = "http://"+host+"/stockcache";
        ResponseEntity<String> response = restTemplate.getForEntity(subscriberEndpoint, String.class);

        System.out.println(response.getStatusCode());

    }

}
