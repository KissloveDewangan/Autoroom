package in.co.opensoftlab.yourstore.model;

/**
 * Created by dewangankisslove on 27-01-2017.
 */
public class NotificationModel {
    String requestType;
    String requestDate;
    String personName;
    String personId;
    String personUrl;
    String msg;
    String status;
    String location;

    public NotificationModel() {
    }

    public NotificationModel(String requestType, String requestDate, String personName,
                             String personId, String personUrl, String msg, String status, String location) {
        this.requestType = requestType;
        this.requestDate = requestDate;
        this.personName = personName;
        this.personId = personId;
        this.personUrl = personUrl;
        this.msg = msg;
        this.status = status;
        this.location = location;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getPersonUrl() {
        return personUrl;
    }

    public void setPersonUrl(String personUrl) {
        this.personUrl = personUrl;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}