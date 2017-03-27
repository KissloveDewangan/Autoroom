package in.co.opensoftlab.yourstore.model;

/**
 * Created by dewangankisslove on 15-01-2017.
 */

public class ChatModel {
    String name;
    String url;
    String title;
    String createdAt;
    int unreadMsg;
    String block;
    String metaInfo;

    public ChatModel() {
    }

    public ChatModel(String name, String url, String title, String createdAt, int unreadMsg, String block, String metaInfo) {
        this.name = name;
        this.url = url;
        this.title = title;
        this.createdAt = createdAt;
        this.unreadMsg = unreadMsg;
        this.block = block;
        this.metaInfo = metaInfo;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getUnreadMsg() {
        return unreadMsg;
    }

    public void setUnreadMsg(int unreadMsg) {
        this.unreadMsg = unreadMsg;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getBlock() {
        return block;
    }

    public String getMetaInfo() {
        return metaInfo;
    }

    public void setMetaInfo(String metaInfo) {
        this.metaInfo = metaInfo;
    }
}
