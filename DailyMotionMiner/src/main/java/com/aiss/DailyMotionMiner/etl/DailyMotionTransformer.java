package com.aiss.DailyMotionMiner.etl;


import com.aiss.DailyMotionMiner.model.dailymotion.DMOwner;
import com.aiss.DailyMotionMiner.model.dailymotion.DMSubtle;
import com.aiss.DailyMotionMiner.model.dailymotion.DMVideo;
import com.aiss.DailyMotionMiner.model.videominer.VMCaption;
import com.aiss.DailyMotionMiner.model.videominer.VMChannel;
import com.aiss.DailyMotionMiner.model.videominer.VMComment;
import com.aiss.DailyMotionMiner.model.videominer.VMUser;
import com.aiss.DailyMotionMiner.model.videominer.VMVideo;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class DailyMotionTransformer {

    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC);

    

    
    public VMChannel transformChannel(DMOwner dmChannel, List<VMVideo> videos) {
        VMChannel vmChannel = new VMChannel();
        if (dmChannel == null) return vmChannel;

        vmChannel.setId(dmChannel.getId());
        vmChannel.setName(dmChannel.getScreenname());
        vmChannel.setDescription(dmChannel.getDescription());
        vmChannel.setCreatedTime(toIsoString(dmChannel.getCreatedTime()));
        vmChannel.setVideos(videos != null ? videos : new ArrayList<VMVideo>());

        return vmChannel;
    }

    

    
    public VMVideo transformVideo(DMVideo dmVideo, List<DMSubtle> dmSubtitles) {
        return transformVideo(dmVideo, dmSubtitles, dmVideo != null ? dmVideo.getOwner() : null);
    }

    public VMVideo transformVideo(DMVideo dmVideo, List<DMSubtle> dmSubtitles, DMOwner channelOwner) {
        VMVideo vmVideo = new VMVideo();
        if (dmVideo == null) return vmVideo;

        vmVideo.setId(dmVideo.getId());
        
        vmVideo.setName(dmVideo.getTitle());
        vmVideo.setDescription(dmVideo.getDescription());
        
        vmVideo.setReleaseTime(toIsoString(dmVideo.getCreatedTime()));

        
        vmVideo.setAuthor(transformOwner(channelOwner));

        
        List<VMComment> comments = new ArrayList<>();
        if (dmVideo.getTags() != null) {
            for (String tag : dmVideo.getTags()) {
                VMComment comment = new VMComment();
                comment.setId(null);
                comment.setText(tag);
                comment.setCreatedOn(null);
                comments.add(comment);
            }
        }
        vmVideo.setComments(comments);

        
        if (dmSubtitles != null && !dmSubtitles.isEmpty()) {
            vmVideo.setCaptions(
                    dmSubtitles.stream()
                            .map(this::transformSubtitle)
                            .collect(Collectors.toList())
            );
        } else {
            vmVideo.setCaptions(Collections.emptyList());
        }

        return vmVideo;
    }

    

    
    public VMUser transformOwner(DMOwner owner) {
        VMUser vmUser = new VMUser();
        if (owner == null) return vmUser;

        vmUser.setId(owner.getId());
        vmUser.setName(owner.getScreenname());
        vmUser.setUser_link(owner.getUrl());
        vmUser.setPicture_link(owner.getAvatarUrl());

        return vmUser;
    }

    

    
    public VMCaption transformSubtitle(Object subtitle) {
        VMCaption vmCaption = new VMCaption();
        if (subtitle == null) return vmCaption;

        vmCaption.setId(readString(subtitle, "getId"));
        vmCaption.setName(readString(subtitle, "getUrl"));
        vmCaption.setLanguage(readString(subtitle, "getLanguage"));

        return vmCaption;
    }

    

    
    private String toIsoString(Long unixSeconds) {
        if (unixSeconds == null) return null;
        return ISO_FORMATTER.format(Instant.ofEpochSecond(unixSeconds));
    }

    private String readString(Object target, String methodName) {
        try {
            Object value = target.getClass().getMethod(methodName).invoke(target);
            return value != null ? value.toString() : null;
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }
}
