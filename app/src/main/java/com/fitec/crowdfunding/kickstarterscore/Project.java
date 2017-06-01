package com.fitec.crowdfunding.kickstarterscore;

class Project {

    private double mScore;
    private String mName;
    private String mUrl;

    public Project(String name, double score, String url) {
        mName = name;
        mScore = score;
        mUrl = url;
    }

    public double getScore() {
        return mScore;
    }

    public String getName() {
        return mName;
    }

    public String getUrl() {
        return mUrl;
    }
}

