package com.lazar.fabrica_de_coduri.model;

import jakarta.persistence.*;

@Entity
public class Sponsor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String instagramHandle;
    private String instagramUrl;

    @ManyToOne
    @JoinColumn(name = "platform_info_id")
    private PlatformInfo platformInfo;

    public Sponsor() {
    }

    public Sponsor(String name, String instagramHandle, String instagramUrl, PlatformInfo platformInfo) {
        this.name = name;
        this.instagramHandle = instagramHandle;
        this.instagramUrl = instagramUrl;
        this.platformInfo = platformInfo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstagramHandle() {
        return instagramHandle;
    }

    public void setInstagramHandle(String instagramHandle) {
        this.instagramHandle = instagramHandle;
    }

    public String getInstagramUrl() {
        return instagramUrl;
    }

    public void setInstagramUrl(String instagramUrl) {
        this.instagramUrl = instagramUrl;
    }

    public PlatformInfo getPlatformInfo() {
        return platformInfo;
    }

    public void setPlatformInfo(PlatformInfo platformInfo) {
        this.platformInfo = platformInfo;
    }
}
