package com.lazar.fabrica_de_coduri.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class PlatformInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String bannerContent;
    private String donateMessage;
    @OneToMany(mappedBy = "platformInfo", cascade = CascadeType.ALL)
    private List<SEOHashTag> seoHashTags;
    @OneToMany(mappedBy = "platformInfo", cascade = CascadeType.ALL)
    private List<News> news;
    private String tiktokLink;
    private String instagramLink;
    private String youTubeLink;
    private String email;

    public PlatformInfo() {
    }

    public PlatformInfo(long id, String bannerContent, String donateMessage, List<SEOHashTag> seoHashTags, List<News> news, String tiktokLink, String instagramLink, String youTubeLink, String email) {
        this.id = id;
        this.bannerContent = bannerContent;
        this.donateMessage = donateMessage;
        this.seoHashTags = seoHashTags;
        this.news = news;
        this.tiktokLink = tiktokLink;
        this.instagramLink = instagramLink;
        this.youTubeLink = youTubeLink;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBannerContent() {
        return bannerContent;
    }

    public void setBannerContent(String bannerContent) {
        this.bannerContent = bannerContent;
    }

    public String getDonateMessage() {
        return donateMessage;
    }

    public void setDonateMessage(String donateMessage) {
        this.donateMessage = donateMessage;
    }

    public List<SEOHashTag> getSeoHashTags() {
        return seoHashTags;
    }

    public void setSeoHashTags(List<SEOHashTag> seoHashTags) {
        this.seoHashTags = seoHashTags;
    }

    public List<News> getNews() {
        return news;
    }

    public void setNews(List<News> news) {
        this.news = news;
    }

    public String getTiktokLink() {
        return tiktokLink;
    }

    public void setTiktokLink(String tiktokLink) {
        this.tiktokLink = tiktokLink;
    }

    public String getInstagramLink() {
        return instagramLink;
    }

    public void setInstagramLink(String instagramLink) {
        this.instagramLink = instagramLink;
    }

    public String getYouTubeLink() {
        return youTubeLink;
    }

    public void setYouTubeLink(String youTubeLink) {
        this.youTubeLink = youTubeLink;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
