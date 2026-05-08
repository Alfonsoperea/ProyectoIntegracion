package com.aiss.PeerTubeMiner.model.peertube;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Caption {

    @JsonProperty("id")
    private String id;

    @JsonProperty("captionPath")
    private String captionPath;

    @JsonProperty("fileUrl")
    private String fileUrl;

    @JsonProperty("language")
    private Language language;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCaptionPath() {
        return captionPath;
    }

    public void setCaptionPath(String captionPath) {
        this.captionPath = captionPath;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}
