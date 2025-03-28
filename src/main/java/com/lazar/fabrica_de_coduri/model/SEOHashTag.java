package com.lazar.fabrica_de_coduri.model;

import jakarta.persistence.*;

@Entity
public class SEOHashTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String text;
    private String redirectLink;
    @ManyToOne
    @JoinColumn(name = "platform_info_id")
    private PlatformInfo platformInfo;

    public SEOHashTag() {
    }

    public SEOHashTag(long id, String text, String redirectLink, PlatformInfo platformInfo) {
        this.id = id;
        this.text = text;
        this.redirectLink = redirectLink;
        this.platformInfo = platformInfo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRedirectLink() {
        return redirectLink;
    }

    public void setRedirectLink(String redirectLink) {
        this.redirectLink = redirectLink;
    }

    public PlatformInfo getPlatformInfo() {
        return platformInfo;
    }

    public void setPlatformInfo(PlatformInfo platformInfo) {
        this.platformInfo = platformInfo;
    }
}
