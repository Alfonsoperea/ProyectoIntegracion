package com.aiss.DailyMotionMiner.model.dailymotion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class DMOwner {

    
    @JsonProperty("id")
    private String id;

    
    @JsonProperty("screenname")
    private String screenname;

    
    @JsonProperty("description")
    private String description;

    
    @JsonProperty("created_time")
    private Long createdTime;

    
    @JsonProperty("url")
    private String url;

    
    @JsonProperty("avatar_120_url")
    private String avatarUrl;

    

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getScreenname() { return screenname; }
    public void setScreenname(String screenname) { this.screenname = screenname; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getCreatedTime() { return createdTime; }
    public void setCreatedTime(Long createdTime) { this.createdTime = createdTime; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    @Override
    public String toString() {
        return "DMChannel{id='" + id + "', screenname='" + screenname + "'}";
    }
}
