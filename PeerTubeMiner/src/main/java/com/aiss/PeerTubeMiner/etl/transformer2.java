package com.aiss.PeerTubeMiner.etl;

import com.aiss.PeerTubeMiner.model.peertube.Caption;
import com.aiss.PeerTubeMiner.model.peertube.Channel;
import com.aiss.PeerTubeMiner.model.peertube.Comment;
import com.aiss.PeerTubeMiner.model.peertube.Pictures;
import com.aiss.PeerTubeMiner.model.peertube.User;
import com.aiss.PeerTubeMiner.model.peertube.Video;
import com.aiss.PeerTubeMiner.model.videominer.VMCaption;
import com.aiss.PeerTubeMiner.model.videominer.VMChannel;
import com.aiss.PeerTubeMiner.model.videominer.VMComment;
import com.aiss.PeerTubeMiner.model.videominer.VMUser;
import com.aiss.PeerTubeMiner.model.videominer.VMVideo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class transformer2 {

    public VMChannel transformChannel(Channel ptChannel, List<Video> ptVideos) {
        VMChannel channel = new VMChannel();
        if (ptChannel == null) {
            return channel;
        }

        channel.setId(String.valueOf(ptChannel.getId()));
        channel.setName(ptChannel.getDisplayName());
        channel.setDescription(ptChannel.getDescription() != null ? ptChannel.getDescription() : "");
        channel.setCreatedTime(ptChannel.getCreatedAt());

        List<VMVideo> commonVideos = (ptVideos == null ? Collections.<Video>emptyList() : ptVideos)
                .stream()
                .map(this::transformVideo)
                .collect(Collectors.toList());
        channel.setVideos(commonVideos);

        return channel;
    }

    public VMVideo transformVideo(Video ptVideo) {
        return transformVideo(ptVideo, null, null);
    }

    public VMVideo transformVideo(Video ptVideo, List<Comment> ptComments, List<Caption> ptCaptions) {
        VMVideo video = new VMVideo();
        if (ptVideo == null) {
            return video;
        }

        video.setId(String.valueOf(ptVideo.getId()));
        video.setName(ptVideo.getName());
        video.setDescription(ptVideo.getTruncatedDescription());
        video.setReleaseTime(ptVideo.getPublishedAt());
        video.setAuthor(transformUser(ptVideo.getAccount()));

        if (ptComments != null) {
            video.setComments(ptComments.stream().map(this::transformComment).collect(Collectors.toList()));
        }

        if (ptCaptions != null) {
            video.setCaptions(ptCaptions.stream().map(this::transformCaption).collect(Collectors.toList()));
        }

        return video;
    }

    public VMUser transformUser(User ptAccount) {
        VMUser user = new VMUser();
        if (ptAccount == null) {
            return user;
        }

        user.setId(ptAccount.getId());
        user.setName(ptAccount.getName());
        user.setUser_link(ptAccount.getUrl());

        List<Pictures> avatars = ptAccount.getAvatars();
        if (avatars != null && !avatars.isEmpty() && avatars.get(0) != null) {
            user.setPicture_link(avatars.get(0).getFileUrl());
        }

        return user;
    }

    public VMComment transformComment(Comment ptComment) {
        VMComment comment = new VMComment();
        if (ptComment == null) {
            return comment;
        }

        comment.setId(String.valueOf(ptComment.getId()));
        comment.setText(ptComment.getText());
        comment.setCreatedOn(ptComment.getCreatedAt());
        return comment;
    }

    public VMCaption transformCaption(Caption ptCaption) {
        VMCaption caption = new VMCaption();
        if (ptCaption == null) {
            return caption;
        }

        caption.setId(ptCaption.getId());
        caption.setName(ptCaption.getFileUrl() != null ? ptCaption.getFileUrl() : ptCaption.getCaptionPath());
        if (ptCaption.getLanguage() != null) {
            caption.setLanguage(ptCaption.getLanguage().getLabel());
        }
        return caption;
    }
}
