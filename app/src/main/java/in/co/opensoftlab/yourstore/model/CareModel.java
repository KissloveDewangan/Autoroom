package in.co.opensoftlab.yourstore.model;

/**
 * Created by dewangankisslove on 22-03-2017.
 */

public class CareModel {
    String email;
    String type;
    String msg;

    public CareModel() {
    }

    public CareModel(String email, String type, String msg) {
        this.email = email;
        this.type = type;
        this.msg = msg;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
