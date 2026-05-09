package com.aiss.DailyMotionMiner.etl;
import com.aiss.DailyMotionMiner.model.dailymotion.DMOwner;
import com.aiss.DailyMotionMiner.model.dailymotion.DMSubtle;
import com.aiss.DailyMotionMiner.model.dailymotion.DMVideo;
import com.aiss.DailyMotionMiner.model.videominer.VMCaption;
import com.aiss.DailyMotionMiner.model.videominer.VMChannel;
import com.aiss.DailyMotionMiner.model.videominer.VMComment;
import com.aiss.DailyMotionMiner.model.videominer.VMUser;
import com.aiss.DailyMotionMiner.model.videominer.VMVideo;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
class DailyMotionTransformerTest {
    private final DailyMotionTransformer transformer = new DailyMotionTransformer();
    @Test
    void transformChannel_shouldMapOwnerFields() {
        DMOwner owner = new DMOwner();
        owner.setId("x123");
        owner.setScreenname("creator");
        owner.setDescription("channel description");
        owner.setCreatedTime(1712345678L);
        VMChannel vmChannel = transformer.transformChannel(owner, List.of());
        assertEquals("x123", vmChannel.getId());
        assertEquals("creator", vmChannel.getName());
        assertEquals("channel description", vmChannel.getDescription());
        assertEquals("2024-04-05T19:34:38Z", vmChannel.getCreatedTime());
        assertNotNull(vmChannel.getVideos());
    }
    @Test
    void transformVideo_shouldMapVideoAuthorTagsAndSubtitles() {
        DMVideo dmVideo = new DMVideo();
        dmVideo.setId("xv1");
        dmVideo.setTitle("Video title");
        dmVideo.setDescription("Video description");
        dmVideo.setCreatedTime(1712400000L);
        dmVideo.setTags(List.of("tag1", "tag2"));
        DMOwner owner = new DMOwner();
        owner.setId("xowner");
        owner.setScreenname("Owner Name");
        owner.setUrl("https://dailymotion.com/owner");
        owner.setAvatarUrl("https://img/avatar.png");
        DMSubtle subtitle = new DMSubtle();
        subtitle.setId("sub1");
        subtitle.setLanguage("fr");
        subtitle.setUrl("https://subs/video.vtt");
        VMVideo vmVideo = transformer.transformVideo(dmVideo, List.of(subtitle), owner);
        assertEquals("xv1", vmVideo.getId());
        assertEquals("Video title", vmVideo.getName());
        assertEquals("Video description", vmVideo.getDescription());
        assertEquals("2024-04-06T10:40:00Z", vmVideo.getReleaseTime());
        VMUser author = vmVideo.getAuthor();
        assertNotNull(author);
        assertEquals("xowner", author.getId());
        assertEquals("Owner Name", author.getName());
        assertEquals("https://dailymotion.com/owner", author.getUser_link());
        assertEquals("https://img/avatar.png", author.getPicture_link());
        VMComment firstTagAsComment = vmVideo.getComments().get(0);
        assertNull(firstTagAsComment.getId());
        assertEquals("tag1", firstTagAsComment.getText());
        VMCaption vmCaption = vmVideo.getCaptions().get(0);
        assertEquals("sub1", vmCaption.getId());
        assertEquals("https://subs/video.vtt", vmCaption.getName());
        assertEquals("fr", vmCaption.getLanguage());
    }
    @Test
    void transformSubtitle_shouldReturnEmptyCaptionWhenNull() {
        VMCaption vmCaption = transformer.transformSubtitle(null);
        assertNull(vmCaption.getId());
        assertNull(vmCaption.getName());
        assertNull(vmCaption.getLanguage());
    }
}

