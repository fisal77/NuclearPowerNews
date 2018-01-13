package com.fisal.nuclearpowernews;

/**
 * Created by fisal on 12/01/2018.
 *
 * sectionName
 * webTitle
 * webPublicationDate
 * webUrl
 */

class NuclearPower {

    private String mSectionName;
    private String mWebTitle;
    private String mWebPublicationDate;
    private String mWebUrl;

    public NuclearPower(String sectionName, String webTitle, String webPublicationDate, String webUrl) {
        mSectionName = sectionName;
        mWebTitle = webTitle;
        mWebPublicationDate = webPublicationDate;
        mWebUrl = webUrl;
    }

    public String getSectionName() {
        return mSectionName;
    }

    public String getWebTitle() {
        return mWebTitle;
    }

    public String getWebPublicationDate() {
        return mWebPublicationDate;
    }

    public String getWebUrl() {
        return mWebUrl;
    }

}
