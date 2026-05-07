
package com.aiss.PeerTubeMiner.model.peertube;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentSearch {

    @JsonProperty("total")
    private Integer total;
    @JsonProperty("totalNotDeletedComments")
    private Integer totalNotDeletedComments;
    @JsonProperty("data")
    private List<Comment> data;

    @JsonProperty("total")
    public Integer getTotal() {
        return total;
    }

    @JsonProperty("total")
    public void setTotal(Integer total) {
        this.total = total;
    }

    @JsonProperty("totalNotDeletedComments")
    public Integer getTotalNotDeletedComments() {
        return totalNotDeletedComments;
    }

    @JsonProperty("totalNotDeletedComments")
    public void setTotalNotDeletedComments(Integer totalNotDeletedComments) {
        this.totalNotDeletedComments = totalNotDeletedComments;
    }

    @JsonProperty("data")
    public List<Comment> getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(List<Comment> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(CommentSearch.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("total");
        sb.append('=');
        sb.append(((this.total == null)?"<null>":this.total));
        sb.append(',');
        sb.append("totalNotDeletedComments");
        sb.append('=');
        sb.append(((this.totalNotDeletedComments == null)?"<null>":this.totalNotDeletedComments));
        sb.append(',');
        sb.append("data");
        sb.append('=');
        sb.append(((this.data == null)?"<null>":this.data));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
