package in.co.opensoftlab.yourstore.model;

import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dewangankisslove on 08-12-2016.
 */

public class Users {
    String emailId;
    String name;
    String photoUrl;
    String location;
    String createdAt;
    boolean verifiedUser;
    String aboutUser;
    String phoneNo;
    String gender;
    String languages;

    public Users() {
    }

    public Users(String emailId, String name, String photoUrl, String location, String createdAt, boolean verifiedUser, String aboutUser, String phoneNo, String gender, String languages) {
        this.emailId = emailId;
        this.name = name;
        this.photoUrl = photoUrl;
        this.location = location;
        this.createdAt = createdAt;
        this.verifiedUser = verifiedUser;
        this.aboutUser = aboutUser;
        this.phoneNo = phoneNo;
        this.gender = gender;
        this.languages = languages;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isVerifiedUser() {
        return verifiedUser;
    }

    public void setVerifiedUser(boolean verifiedUser) {
        this.verifiedUser = verifiedUser;
    }

    public String getAboutUser() {
        return aboutUser;
    }

    public void setAboutUser(String aboutUser) {
        this.aboutUser = aboutUser;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
