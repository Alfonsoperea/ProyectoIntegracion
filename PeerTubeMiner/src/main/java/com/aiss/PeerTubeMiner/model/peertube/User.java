package com.aiss.PeerTubeMiner.model.peertube;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("user_link")
    private String url;
    
    @JsonProperty("avatars")
    private List<Pictures> avatars;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Pictures> getAvatars() {
        return avatars;
    }

    public void setAvatars(List<Pictures> avatars) {
        this.avatars = avatars;
    }
}
