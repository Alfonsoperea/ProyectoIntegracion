package com.aiss.PeerTubeMiner.etl;
import com.aiss.PeerTubeMiner.model.peertube.Caption;
import com.aiss.PeerTubeMiner.model.peertube.Channel;
import com.aiss.PeerTubeMiner.model.peertube.Comment;
import com.aiss.PeerTubeMiner.model.peertube.Language;
import com.aiss.PeerTubeMiner.model.peertube.Pictures;
import com.aiss.PeerTubeMiner.model.peertube.User;
import com.aiss.PeerTubeMiner.model.peertube.Video;
import com.aiss.PeerTubeMiner.model.videominer.VMCaption;
import com.aiss.PeerTubeMiner.model.videominer.VMChannel;
import com.aiss.PeerTubeMiner.model.videominer.VMComment;
import com.aiss.PeerTubeMiner.model.videominer.VMUser;
import com.aiss.PeerTubeMiner.model.videominer.VMVideo;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
class Transformer2Test {
    private final transformer2 transformer = new transformer2();
    @Test
    void transformChannel_shouldMapMainFields() {
        Channel channel = new Channel();
        channel.setId("120");
        channel.setDisplayName("framasoft");
        channel.setDescription("description");
        channel.setCreatedAt("2024-01-01T10:00:00Z");
        VMChannel vmChannel = transformer.transformChannel(channel, null);
        assertEquals("120", vmChannel.getId());
        assertEquals("framasoft", vmChannel.getName());
        assertEquals("description", vmChannel.getDescription());
        assertEquals("2024-01-01T10:00:00Z", vmChannel.getCreatedTime());
        assertNotNull(vmChannel.getVideos());
        assertEquals(0, vmChannel.getVideos().size());
    }
    @Test
    void transformVideo_shouldMapAuthorCommentsAndCaptions() {
        Video ptVideo = new Video();
        ptVideo.setId("v1");
        ptVideo.setName("Video Name");
        ptVideo.setTruncatedDescription("Video Description");
        ptVideo.setPublishedAt("2024-03-10T12:00:00Z");
        User account = new User();
        account.setId("u1");
        account.setName("Author");
        account.setUrl("https://peertube.example/u1");
        Pictures avatar = new Pictures();
        avatar.setFileUrl("https://peertube.example/avatar.png");
        account.setAvatars(List.of(avatar));
        ptVideo.setAccount(account);
        Comment comment = new Comment();
        comment.setId("c1");
        comment.setText("Great video");
        comment.setCreatedAt("2024-03-11T10:00:00Z");
        Caption caption = new Caption();
        caption.setId("cap1");
        caption.setCaptionPath("/captions/fr.vtt");
        Language language = new Language();
        language.setLabel("French");
        caption.setLanguage(language);
        VMVideo vmVideo = transformer.transformVideo(ptVideo, List.of(comment), List.of(caption));
        assertEquals("v1", vmVideo.getId());
        assertEquals("Video Name", vmVideo.getName());
        assertEquals("Video Description", vmVideo.getDescription());
        assertEquals("2024-03-10T12:00:00Z", vmVideo.getReleaseTime());
        VMUser vmUser = vmVideo.getAuthor();
        assertNotNull(vmUser);
        assertEquals("u1", vmUser.getId());
        assertEquals("Author", vmUser.getName());
        assertEquals("https://peertube.example/u1", vmUser.getUser_link());
        assertEquals("https://peertube.example/avatar.png", vmUser.getPicture_link());
        VMComment vmComment = vmVideo.getComments().get(0);
        assertEquals("c1", vmComment.getId());
        assertEquals("Great video", vmComment.getText());
        assertEquals("2024-03-11T10:00:00Z", vmComment.getCreatedOn());
        VMCaption vmCaption = vmVideo.getCaptions().get(0);
        assertEquals("cap1", vmCaption.getId());
        assertEquals("/captions/fr.vtt", vmCaption.getName());
        assertEquals("French", vmCaption.getLanguage());
    }
    @Test
    void transformCaption_shouldPreserveNullIdAsNull() {
        Caption caption = new Caption();
        caption.setId(null);
        caption.setCaptionPath("/captions/es.vtt");
        VMCaption vmCaption = transformer.transformCaption(caption);
        assertNull(vmCaption.getId());
        assertEquals("/captions/es.vtt", vmCaption.getName());
    }
}
