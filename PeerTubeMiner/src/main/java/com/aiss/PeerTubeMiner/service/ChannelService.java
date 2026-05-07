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
<<<<<<< HEAD:PeerTubeMiner/src/main/java/com/aiss/PeerTubeMiner/service/ChannelService.java
        String url = apiUrl + "/accounts/" + accountName;
=======
        String url = API_URL + "/accounts/" + accountName;
        // Coincide con tu clase Account.java
>>>>>>> 0505e9eb2d6f1e8834d6e01f4e580bae82b4e586:PeerTubeMiner/src/main/java/com/aiss/PeerTubeMiner/model/service/ChannelService.java
        return restTemplate.getForObject(url, Account.class);
    }
}
