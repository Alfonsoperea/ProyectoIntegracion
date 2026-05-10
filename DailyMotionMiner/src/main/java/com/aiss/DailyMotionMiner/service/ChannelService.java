package com.aiss.DailyMotionMiner.service;

import com.aiss.DailyMotionMiner.exception.ChannelNotFoundException;
import com.aiss.DailyMotionMiner.model.dailymotion.DMOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


@Service
public class ChannelService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${dailymotionminer.baseuri}")
    private String baseUri;

    
    public DMOwner getChannel(String userId) throws ChannelNotFoundException {
        String url = baseUri + "/user/" + userId
                + "?fields=id,screenname,description,created_time,url,avatar_120_url";
        try {
            return restTemplate.getForObject(url, DMOwner.class);
        } catch (HttpClientErrorException e) {
            throw new ChannelNotFoundException();
        }
    }
}
