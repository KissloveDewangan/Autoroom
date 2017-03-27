package in.co.opensoftlab.yourstore.model;

/**
 * Created by dewangankisslove on 15-01-2017.
 */

public class MsgItem {
    String writerId;
    String content;
    String timeStamp;
    String date;

    public MsgItem() {
    }

    public MsgItem(String writerId, String content, String timeStamp, String date) {
        this.writerId = writerId;
        this.content = content;
        this.timeStamp = timeStamp;
        this.date = date;
    }

    public String getWriterId() {
        return writerId;
    }

    public void setWriterId(String writerId) {
        this.writerId = writerId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
