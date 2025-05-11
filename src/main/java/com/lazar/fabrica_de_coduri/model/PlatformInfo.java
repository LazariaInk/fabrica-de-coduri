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
    private String donateTitle;
    @OneToMany(mappedBy = "platformInfo", cascade = CascadeType.ALL)
    private List<SEOHashTag> seoHashTags;
    @OneToMany(mappedBy = "platformInfo", cascade = CascadeType.ALL)
    private List<News> news;
    private String tiktokLink;
    private String instagramLink;
    private String youTubeLink;
    private String discordLink;
    private String linkedinLink;
    private String email;

    @OneToMany(mappedBy = "platformInfo", cascade = CascadeType.ALL)
    private List<Sponsor> sponsors;


    public PlatformInfo() {
    }

    public PlatformInfo(long id, String bannerContent, String donateMessage, List<SEOHashTag> seoHashTags, List<News> news, String tiktokLink, String instagramLink, String youTubeLink, String email, String donateTile, String discordLink, String linkedinLink) {
        this.id = id;
        this.bannerContent = bannerContent;
        this.donateMessage = donateMessage;
        this.seoHashTags = seoHashTags;
        this.news = news;
        this.tiktokLink = tiktokLink;
        this.instagramLink = instagramLink;
        this.youTubeLink = youTubeLink;
        this.email = email;
        this.donateTitle = donateTile;
        this.discordLink = discordLink;
        this.linkedinLink = linkedinLink;
    }

    public List<Sponsor> getSponsors() {
        return sponsors;
    }

    public void setSponsors(List<Sponsor> sponsors) {
        this.sponsors = sponsors;
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

    public String getDonateTitle() {
        return donateTitle;
    }

    public void setDonateTitle(String donateTitle) {
        this.donateTitle = donateTitle;
    }

    public String getDiscordLink() {
        return discordLink;
    }

    public void setDiscordLink(String discordLink) {
        this.discordLink = discordLink;
    }

    public String getLinkedinLink() {
        return linkedinLink;
    }

    public void setLinkedinLink(String linkedinLink) {
        this.linkedinLink = linkedinLink;
    }
}
