package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.model.News;
import com.lazar.fabrica_de_coduri.model.PlatformInfo;
import com.lazar.fabrica_de_coduri.model.SEOHashTag;
import com.lazar.fabrica_de_coduri.service.NewsService;
import com.lazar.fabrica_de_coduri.service.PlatformInfoService;
import com.lazar.fabrica_de_coduri.service.SEOHashTagService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/platform")
public class RestPlatformInfoController {
    private final PlatformInfoService platformInfoService;
    private final SEOHashTagService seoHashTagService;
    private final NewsService newsService;

    public RestPlatformInfoController(PlatformInfoService platformInfoService, SEOHashTagService seoHashTagService, NewsService newsService) {
        this.platformInfoService = platformInfoService;
        this.seoHashTagService = seoHashTagService;
        this.newsService = newsService;
    }

    @GetMapping
    public PlatformInfo getPlatformInfo() {
        return platformInfoService.getPlatformInfo();
    }

    @PutMapping
    public PlatformInfo updatePlatformInfo(@RequestBody PlatformInfo platformInfo) {
        return platformInfoService.updatePlatformInfo(platformInfo);
    }

    @GetMapping("/hashtags")
    public List<SEOHashTag> getHashTags() {
        return seoHashTagService.getAllHashTags(platformInfoService.getPlatformInfo().getId());
    }

    @DeleteMapping("/hashtags/delete/{id}")
    public void deleteHashTag(@PathVariable Long id) {
        seoHashTagService.deleteHashTag(id);
    }

    @PostMapping("/news")
    public News addNews(@RequestBody News news) {
        return newsService.addNews(news);
    }
}
