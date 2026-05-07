package com.aiss.DailyMotionMiner.model.videominer;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class VMVideo {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("releaseTime")
    private String releaseTime;

    @JsonProperty("user")
    private VMUser author;

    @JsonProperty("comments")
    private List<VMComment> comments;

    @JsonProperty("captions")
    private List<VMCaption> captions;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public VMUser getAuthor() {
        return author;
    }

    public void setAuthor(VMUser author) {
        this.author = author;
    }

    public List<VMComment> getComments() {
        return comments;
    }

    public void setComments(List<VMComment> comments) {
        this.comments = comments;
    }

    public List<VMCaption> getCaptions() {
        return captions;
    }

    public void setCaptions(List<VMCaption> captions) {
        this.captions = captions;
    }

    @Override
    public String toString() {
        return "Video{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", releaseTime='" + releaseTime + '\'' +
                ", author=" + author +
                ", comments=" + comments +
                ", captions=" + captions +
                '}';
    }
}
