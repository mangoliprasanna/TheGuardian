package com.example.mango.theguardian.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

// As Every news has Contributor News should extend Contributor.
public class News extends Contributor {
    private String mWebTitle;
    private String mSection;
    private String mPublicationDate;
    private String mApiUrl;
    private String mBody;
    private String mThumbnail;
    private String mStandfirst;
    private String mDate;
    private String mWebUrl;
    private String mSectionId;


    public News(JSONObject json) throws JSONException,ParseException {
        // calling construcotr for Contributor class.
        super(json);

        // These values present for any JSON response so extracting them directly.
        this.mSection = json.getString("sectionName");
        this.mSectionId = json.getString("sectionId");
        this.mWebTitle = json.getString("webTitle");
        this.mWebUrl = json.getString("webUrl");
        // converting 2018-10-17T16:44:17Z to 2018-10-1 T16:44:17, replacing Z and T with " " and passing in SimpleDaaFormate easy to parse.
        String tempDate = json.getString("webPublicationDate").replace("Z", "").replace("T", " ");
        this.mPublicationDate = new SimpleDateFormat("MMMM dd").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tempDate)).toString();
        this.mDate = new SimpleDateFormat("MMMM dd, yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tempDate)).toString();
        this.mApiUrl = json.getString("apiUrl");

        // These values may be present for any JSON response so extracting them after validating.
        // Validating if values present in resonce before extracting.
        if(!json.isNull("fields"))
        {
            this.mStandfirst =  json.getJSONObject("fields").has("trailText") ? json.getJSONObject("fields").getString("trailText") : null;
            this.mThumbnail = json.getJSONObject("fields").has("thumbnail") ? json.getJSONObject("fields").getString("thumbnail") : null;
            this.mBody = json.getJSONObject("fields").has("body") ? json.getJSONObject("fields").getString("body") : null;
        }

    }

    public String getSectionId() {
        return mSectionId;
    }
    public String getDate() {
        return mDate;
    }
    public String getWebUrl() {
        return mWebUrl;
    }
    public String getStandfirst() {
        return mStandfirst;
    }
    public String getThumbnail() {
        return mThumbnail;
    }
    public String getApiUrl() {
        return mApiUrl;
    }
    public String getBody() {
        return mBody;
    }
    public String getPublicationDate() {
        return mPublicationDate;
    }
    public String getSection() {
        return mSection;
    }
    public String getWebTitle() {
        return mWebTitle;
    }
}