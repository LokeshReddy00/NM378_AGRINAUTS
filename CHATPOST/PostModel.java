package com.example.ioc.CHATPOST;

class PostModel {

    String pImage, pTitle, pDescription, uEmail;

    public PostModel(){}

    public PostModel(final String pImage, final String pTitle, final String pDescription, final String uEmail) {
        this.pImage = pImage;
        this.pTitle = pTitle;
        this.pDescription = pDescription;
        this.uEmail = uEmail;
    }

    public String getpImage() {
        return this.pImage;
    }

    public void setpImage(final String pImage) {
        this.pImage = pImage;
    }

    public String getpTitle() {
        return this.pTitle;
    }

    public void setpTitle(final String pTitle) {
        this.pTitle = pTitle;
    }

    public String getpDescription() {
        return this.pDescription;
    }

    public void setpDescription(final String pDescription) {
        this.pDescription = pDescription;
    }

    public String getuEmail() {
        return this.uEmail;
    }

    public void setuEmail(final String uEmail) {
        this.uEmail = uEmail;
    }
}
