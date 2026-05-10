package com.aiss.DailyMotionMiner.controller;

import com.aiss.DailyMotionMiner.etl.DailyMotionTransformer;
import com.aiss.DailyMotionMiner.exception.CaptionNotFoundException;
import com.aiss.DailyMotionMiner.exception.ChannelNotFoundException;
import com.aiss.DailyMotionMiner.exception.VideoNotFoundException;
import com.aiss.DailyMotionMiner.model.dailymotion.DMOwner;
import com.aiss.DailyMotionMiner.model.dailymotion.DMSubtle;
import com.aiss.DailyMotionMiner.model.dailymotion.DMSubtleSearch;
import com.aiss.DailyMotionMiner.model.dailymotion.DMVideo;
import com.aiss.DailyMotionMiner.model.dailymotion.DMVideoSearch;
import com.aiss.DailyMotionMiner.model.videominer.VMChannel;
import com.aiss.DailyMotionMiner.model.videominer.VMVideo;
import com.aiss.DailyMotionMiner.service.ChannelService;
import com.aiss.DailyMotionMiner.service.SubtitleService;
import com.aiss.DailyMotionMiner.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/dailymotion")
public class ChannelController {

    @Autowired
    private ChannelService channelService;

    @Autowired
    private VideoService videoService;

    @Autowired
    private SubtitleService subtitleService;

    @Autowired
    private DailyMotionTransformer transformer;

    @Autowired
    private RestTemplate restTemplate;

    
    @Value("${videominer.uri}")
    private String videoMinerUri;

    
    @Value("${dailymotionminer.maxVideos}")
    private int defaultMaxVideos;

    
    @Value("${dailymotionminer.maxPages}")
    private int defaultMaxPages;

    

    
    @GetMapping("/{userId}")
    public VMChannel getChannel(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int maxVideos,
            @RequestParam(defaultValue = "0") int maxPages)
            throws ChannelNotFoundException, VideoNotFoundException, CaptionNotFoundException {

        int limit = maxVideos > 0 ? maxVideos : defaultMaxVideos;
        int pages = maxPages > 0 ? maxPages : defaultMaxPages;
        return fetchAndTransform(userId, limit, pages);
    }

    

    
    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public VMChannel createChannel(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int maxVideos,
            @RequestParam(defaultValue = "0") int maxPages)
            throws ChannelNotFoundException, VideoNotFoundException, CaptionNotFoundException {

        int limit = maxVideos > 0 ? maxVideos : defaultMaxVideos;
        int pages = maxPages > 0 ? maxPages : defaultMaxPages;

        
        VMChannel vmChannel = fetchAndTransform(userId, limit, pages);

        
        return restTemplate.postForObject(videoMinerUri, vmChannel, VMChannel.class);
    }

    

    
    private VMChannel fetchAndTransform(String userId, int maxVideos, int maxPages)
            throws ChannelNotFoundException, VideoNotFoundException, CaptionNotFoundException {

        

        
        DMOwner dmChannel = channelService.getChannel(userId);

        
        DMVideoSearch videoSearch = videoService.getVideos(userId, maxVideos, maxPages);
        List<DMVideo> dmVideos = videoSearch.getList();

        

        
        List<VMVideo> vmVideos = new ArrayList<>();
        if (dmVideos != null) {
            for (DMVideo dmVideo : dmVideos) {
                
                DMSubtleSearch subtitleSearch = subtitleService.getSubtitles(dmVideo.getId());
                List<DMSubtle> subtitles =
                        subtitleSearch.getList();

                
                VMVideo vmVideo = transformer.transformVideo(dmVideo, subtitles, dmChannel);
                vmVideos.add(vmVideo);
            }
        }

        
        return transformer.transformChannel(dmChannel, vmVideos);
    }
}
