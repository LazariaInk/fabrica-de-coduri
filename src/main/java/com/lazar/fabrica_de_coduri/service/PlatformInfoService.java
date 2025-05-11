package com.lazar.fabrica_de_coduri.service;

import com.lazar.fabrica_de_coduri.model.PlatformInfo;
import com.lazar.fabrica_de_coduri.repository.PlatformInfoRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class PlatformInfoService {
    private final PlatformInfoRepository platformInfoRepository;

    public PlatformInfoService(PlatformInfoRepository platformInfoRepository) {
        this.platformInfoRepository = platformInfoRepository;
    }

    public PlatformInfo getPlatformInfo() {
        return platformInfoRepository.findTopByOrderByIdAsc();
    }

    public PlatformInfo updatePlatformInfo(PlatformInfo updatedInfo) {
        PlatformInfo platformInfo = getPlatformInfo();
        if (platformInfo != null) {
            platformInfo.setBannerContent(updatedInfo.getBannerContent());
            platformInfo.setDonateMessage(updatedInfo.getDonateMessage());
            platformInfo.setTiktokLink(updatedInfo.getTiktokLink());
            platformInfo.setInstagramLink(updatedInfo.getInstagramLink());
            platformInfo.setYouTubeLink(updatedInfo.getYouTubeLink());
            platformInfo.setEmail(updatedInfo.getEmail());
            platformInfo.setLinkedinLink(updatedInfo.getLinkedinLink());
            platformInfo.setDiscordLink(updatedInfo.getDiscordLink());
            platformInfo.setDonateTitle(updatedInfo.getDonateTitle());
            return platformInfoRepository.save(platformInfo);
        }
        return null;
    }

    @PostConstruct
    public void ensurePlatformInfoExists() {
        if (platformInfoRepository.count() == 0) {
            PlatformInfo defaultPlatform = new PlatformInfo();
            defaultPlatform.setBannerContent("Welcome to our platform!");
            defaultPlatform.setDonateMessage("Support us!");
            defaultPlatform.setTiktokLink("https://www.tiktok.com/@fabricadecoduri");
            defaultPlatform.setInstagramLink("https://www.instagram.com/fabricadecoduri/");
            defaultPlatform.setLinkedinLink("https://www.linkedin.com/company/106583635/admin/dashboard/");
            defaultPlatform.setYouTubeLink("https://youtube.com");
            defaultPlatform.setEmail("contact@fabricadecoduri.com");
            defaultPlatform.setDiscordLink("");
            defaultPlatform.setDonateTitle("Haida sa crestem impreuna");

            platformInfoRepository.save(defaultPlatform);
        }
    }
}
