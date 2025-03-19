package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.model.News;
import com.lazar.fabrica_de_coduri.model.SEOHashTag;
import com.lazar.fabrica_de_coduri.service.NewsService;
import com.lazar.fabrica_de_coduri.service.PlatformInfoService;
import com.lazar.fabrica_de_coduri.service.SEOHashTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PlatformInfoController {
    @Autowired
    private PlatformInfoService platformInfoService;
    @Autowired
    private SEOHashTagService seoHashTagService;
    @Autowired
    private NewsService newsService;

    @GetMapping("/admin/platform-info")
    public String getPlatformInfo(Model model) {
        model.addAttribute("platformInfo", platformInfoService.getPlatformInfo());
        model.addAttribute("hashtags", seoHashTagService.getAllHashTags(platformInfoService.getPlatformInfo().getId()));
        model.addAttribute("newsList", newsService.getLatestNews(platformInfoService.getPlatformInfo().getId()));
        model.addAttribute("newHashtag", new SEOHashTag());
        model.addAttribute("news", new News());
        return "platformInfo";
    }
}
