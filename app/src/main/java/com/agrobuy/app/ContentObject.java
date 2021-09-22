package com.agrobuy.app;

public class ContentObject {
    String name;
    String profilePicURL;
    String subLine;
    String ID;

    public ContentObject(String name, String profilePicURL, String subLine,String id) {
        this.name = name;
        this.profilePicURL = profilePicURL;
        this.subLine = subLine;
    }

    public ContentObject(String name, String profilePicURL,String id) {
        this.name = name;
        this.profilePicURL = profilePicURL;
        this.ID = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubLine() {
        return subLine;
    }

    public void setSubLine(String subLine) {
        this.subLine = subLine;
    }

    public String getProfilePicURL() {
        return profilePicURL;
    }

    public void setProfilePicURL(String profilePicURL) {
        this.profilePicURL = profilePicURL;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

}
