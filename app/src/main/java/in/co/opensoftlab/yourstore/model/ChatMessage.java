package in.co.opensoftlab.yourstore.model;

/**
 * Created by dewangankisslove on 14-01-2017.
 */

public class ChatMessage {
    private String id;
    private boolean isMine;
    private String content;
    private String date;
    private String time;

    private boolean isGroupHeader = false;

    public ChatMessage(String date, boolean mine) {
        this(null, mine, null, date, null);
        isGroupHeader = true;
    }

    public ChatMessage(String message, boolean mine, String time, String date, String id) {
        content = message;
        this.date = date;
        isMine = mine;
        this.time = time;
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isGroupHeader() {
        return isGroupHeader;
    }

    public void setGroupHeader(boolean groupHeader) {
        isGroupHeader = groupHeader;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setIsMine(boolean isMine) {
        this.isMine = isMine;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
