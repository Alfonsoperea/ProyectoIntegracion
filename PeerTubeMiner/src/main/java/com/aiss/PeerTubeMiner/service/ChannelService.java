package com.aiss.PeerTubeMiner.service;

import com.aiss.PeerTubeMiner.model.peertube.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChannelService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${peertubeminer.baseuri}")
    private String apiUrl;

    public Account getAccount(String accountName) {
        String url = apiUrl + "/accounts/" + accountName;
        return restTemplate.getForObject(url, Account.class);
    }
}
