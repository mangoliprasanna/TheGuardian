package com.example.mango.theguardian.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class Contributor {
    private String mConributorWebTitle;
    private String mContributorWebUrl;
    private String mContributorApiUrl;
    private String mContributorBio;
    private String mContributorImage;
    private String mContributorTwitter;
    private String mContributorFname;

    Contributor(JSONObject json) throws JSONException {

        // Validating JSON before extracting values.
        if(json != null && !json.isNull("tags") && json.getJSONArray("tags").length() > 0)
        {
            json = json.getJSONArray("tags").getJSONObject(0);

            // These values present for any JSON response so extracting them directly.
            this.mConributorWebTitle = json.getString("webTitle");
            this.mContributorApiUrl = json.getString("apiUrl");

            if(!json.isNull("firstName"))
                this.mContributorFname = json.getString("firstName");

            // These values may be present for any JSON response so extracting them after validating.
            // Validating if keys are exists or not if exsits extract the values.
            if(!json.isNull("bio"))
                this.mContributorBio = json.getString("bio");
            this.mContributorWebUrl = json.getString("webUrl");
            if(!json.isNull("bylineImageUrl"))
                this.mContributorImage = json.getString("bylineImageUrl");

            if(!json.isNull("twitterHandle"))
                this.mContributorTwitter = json.getString("twitterHandle");
        }
    }

    public String getContributorFname(){ return mContributorFname; }
    public String getConributorWebTitle(){ return mConributorWebTitle; }
    public String getContributorApiUrl() { return mContributorApiUrl; }
    public String getContributorBio() { return mContributorBio; }
    public String getContributorImage() { return mContributorImage; }
    public String getContributorTwitter() { return mContributorTwitter; }

}