
package com.aiss.DailyMotionMiner.model.dailymotion;

import java.util.List;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "title",
        "description",
        "created_time",
        "owner.id",
        "owner.screenname",
        "owner.url",
        "owner.avatar_120_url",
        "tags"
})
@Generated("jsonschema2pojo")
public class DMVideo {

    @JsonProperty("id")
    private String id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("description")
    private String description;
    @JsonProperty("created_time")
    private Long createdTime;
    @JsonProperty("owner.id")
    private String ownerId;
    @JsonProperty("owner.screenname")
    private String ownerScreenname;
    @JsonProperty("owner.url")
    private String ownerUrl;
    @JsonProperty("owner.avatar_120_url")
    private String ownerAvatarUrl;
    @JsonProperty("tags")
    private List<String> tags;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("created_time")
    public Long getCreatedTime() {
        return createdTime;
    }

    @JsonProperty("created_time")
    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    @JsonIgnore
    public DMOwner getOwner() {
        if (ownerId == null && ownerScreenname == null && ownerUrl == null && ownerAvatarUrl == null) {
            return null;
        }
        DMOwner owner = new DMOwner();
        owner.setId(ownerId);
        owner.setScreenname(ownerScreenname);
        owner.setUrl(ownerUrl);
        owner.setAvatarUrl(ownerAvatarUrl);
        return owner;
    }

    @JsonProperty("owner.id")
    public String getOwnerId() {
        return ownerId;
    }

    @JsonProperty("owner.id")
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @JsonProperty("owner.screenname")
    public String getOwnerScreenname() {
        return ownerScreenname;
    }

    @JsonProperty("owner.screenname")
    public void setOwnerScreenname(String ownerScreenname) {
        this.ownerScreenname = ownerScreenname;
    }

    @JsonProperty("owner.url")
    public String getOwnerUrl() {
        return ownerUrl;
    }

    @JsonProperty("owner.url")
    public void setOwnerUrl(String ownerUrl) {
        this.ownerUrl = ownerUrl;
    }

    @JsonProperty("owner.avatar_120_url")
    public String getOwnerAvatarUrl() {
        return ownerAvatarUrl;
    }

    @JsonProperty("owner.avatar_120_url")
    public void setOwnerAvatarUrl(String ownerAvatarUrl) {
        this.ownerAvatarUrl = ownerAvatarUrl;
    }

    @JsonProperty("tags")
    public List<String> getTags() {
        return tags;
    }

    @JsonProperty("tags")
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(DMVideo.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null)?"<null>":this.id));
        sb.append(',');
        sb.append("title");
        sb.append('=');
        sb.append(((this.title == null)?"<null>":this.title));
        sb.append(',');
        sb.append("description");
        sb.append('=');
        sb.append(((this.description == null)?"<null>":this.description));
        sb.append(',');
        sb.append("createdTime");
        sb.append('=');
        sb.append(((this.createdTime == null)?"<null>":this.createdTime));
        sb.append(',');
        sb.append("ownerId");
        sb.append('=');
        sb.append(((this.ownerId == null)?"<null>":this.ownerId));
        sb.append(',');
        sb.append("tags");
        sb.append('=');
        sb.append(((this.tags == null)?"<null>":this.tags));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
