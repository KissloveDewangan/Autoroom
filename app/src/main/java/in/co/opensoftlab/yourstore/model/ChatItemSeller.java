package in.co.opensoftlab.yourstore.model;

/**
 * Created by dewangankisslove on 03-01-2017.
 */
public class ChatItemSeller {
    private String chatId;
    private String personId;
    private String personName;
    private String personUrl;
    private String title;
    private int unreads;

    public ChatItemSeller() {
    }

    public ChatItemSeller(String chatId, String personId, String personName, String personUrl, String title, int unreads) {
        this.chatId = chatId;
        this.personId = personId;
        this.personName = personName;
        this.personUrl = personUrl;
        this.title = title;
        this.unreads = unreads;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonUrl() {
        return personUrl;
    }

    public void setPersonUrl(String personUrl) {
        this.personUrl = personUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUnreads() {
        return unreads;
    }

    public void setUnreads(int unreads) {
        this.unreads = unreads;
    }
}
