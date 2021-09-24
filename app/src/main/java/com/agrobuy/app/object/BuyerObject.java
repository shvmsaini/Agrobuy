package com.agrobuy.app.object;

import android.os.Parcel;
import android.os.Parcelable;

public class BuyerObject implements Parcelable {
    String contactPersonName;
    String categories;
    String date;
    String rating;
    String history;
    String timePref;
    String phoneNumber;
    String personalNumber;
    String email;
    String buyerID;
    String picURL;
    String buyerName;

    protected BuyerObject(Parcel in) {
        contactPersonName = in.readString();
        categories = in.readString();
        date = in.readString();
        rating = in.readString();
        history = in.readString();
        timePref = in.readString();
        phoneNumber = in.readString();
        personalNumber = in.readString();
        email = in.readString();
        buyerID = in.readString();
        picURL = in.readString();
        buyerName = in.readString();
    }

    public static final Creator<BuyerObject> CREATOR = new Creator<BuyerObject>() {
        @Override
        public BuyerObject createFromParcel(Parcel in) {
            return new BuyerObject(in);
        }

        @Override
        public BuyerObject[] newArray(int size) {
            return new BuyerObject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(contactPersonName);
        parcel.writeString(categories);
        parcel.writeString(date);
        parcel.writeString(rating);
        parcel.writeString(history);
        parcel.writeString(timePref);
        parcel.writeString(phoneNumber);
        parcel.writeString(personalNumber);
        parcel.writeString(email);
        parcel.writeString(buyerID);
        parcel.writeString(picURL);
        parcel.writeString(buyerName);
    }

    public BuyerObject(String contactPersonName, String categories, String date, String rating,
                       String history, String timePref, String phoneNumber, String personalNumber,
                       String email,String picURL,String buyerName, String buyerID) {
        this.contactPersonName = contactPersonName;
        this.categories = categories;
        this.date = date;
        this.rating = rating;
        this.history = history;
        this.timePref = timePref;
        this.phoneNumber = phoneNumber;
        this.personalNumber = personalNumber;
        this.email = email;
        this.picURL = picURL;
        this.buyerID = buyerID;
        this.buyerName = buyerName;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        categories = categories;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        date = date;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        rating = rating;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getTimePref() {
        return timePref;
    }

    public void setTimePref(String timePref) {
        this.timePref = timePref;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }

    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBuyerID() {
        return buyerID;
    }

    public void setBuyerID(String buyerID) {
        this.buyerID = buyerID;
    }

    public String getPicURL() {
        return picURL;
    }

    public void setPicURL(String picURL) {
        this.picURL = picURL;
    }
}
