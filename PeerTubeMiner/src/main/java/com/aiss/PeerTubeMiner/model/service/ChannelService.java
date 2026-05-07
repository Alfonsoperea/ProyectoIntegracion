package com.aiss.PeerTubeMiner.model.service;

import com.aiss.PeerTubeMiner.model.peertube.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChannelService {
    @Autowired
    private RestTemplate restTemplate;

    private final String API_URL = "https://peertube2.cpy.re/api/v1";

    public Account getAccount(String accountName) {
        String url = API_URL + "/accounts/" + accountName;
        // Coincide con tu clase Account.java
        return restTemplate.getForObject(url, Account.class);
    }
}
