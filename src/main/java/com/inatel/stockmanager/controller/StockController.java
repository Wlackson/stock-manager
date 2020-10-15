package com.inatel.stockmanager.controller;

import com.inatel.stockmanager.controller.dto.StockDto;
import com.inatel.stockmanager.controller.dto.SubscriberDto;
import com.inatel.stockmanager.exception.ApiRequestException;
import com.inatel.stockmanager.response.ResponseHandler;
import com.inatel.stockmanager.service.DataManager;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wiremock.net.minidev.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping("")
public class StockController {

    private final DataManager dataManager;

    public StockController(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @GetMapping("/stock")
    public ResponseEntity<Object> getStocksInfo() throws FileNotFoundException, JSONException {

        JSONObject data = this.dataManager.getStocks();

        return new ResponseEntity<>(data, HttpStatus.OK);

    }

    @PostMapping("/stock")
    public Object registerNewStock(@RequestBody StockDto stock) throws IOException {

        if (stock == null) {

            throw new ApiRequestException("Missing required data!", new Throwable("400"));

        } else {

            //Check for the fields are present are not
            String stockId = stock.getId();
            String stockDescription = stock.getDescription();

            //if stockId is present
            if (stockId.isEmpty()) {

                throw new ApiRequestException("Missing required field: id!", new Throwable("400"));

                //if quoteValue is not present
            } else if (stockDescription.isEmpty()) {

                throw new ApiRequestException("Missing required field: quote!", new Throwable("400"));

            } else if (this.dataManager.searchStock(stockId)) {
                throw new ApiRequestException("Stock already registered!", new Throwable("400"));
            }
        }

        if (dataManager.appendStock(stock.getId(), stock.getDescription())) {

            JSONObject endpoints = this.dataManager.getSubscriptions();

            endpoints.keySet().forEach(keyStr -> {

                Object keyvalue = endpoints.get(keyStr);

                this.dataManager.notifySubscribers(keyStr, keyvalue.toString());

            });

            return new ResponseHandler().standardizedResponse("Stock successfully registered!", "201");

        }

        throw new ApiRequestException("Unable to process your request!", new Throwable("500"));

    }

    @PostMapping("/notification")
    public Object registerNotifications(@RequestBody SubscriberDto subscriber) throws FileNotFoundException {

        String host = subscriber.getHost();
        String port = subscriber.getPort();

        if( this.dataManager.searchSubscription(host)
                || this.dataManager.appendSubscription(host, port) ) {

            return new ResponseEntity<>("", HttpStatus.CREATED);

        }

        return new ResponseEntity<>("", HttpStatus.SERVICE_UNAVAILABLE);

    }

}
