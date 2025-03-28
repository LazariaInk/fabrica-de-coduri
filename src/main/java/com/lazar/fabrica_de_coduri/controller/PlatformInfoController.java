package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.model.News;
import com.lazar.fabrica_de_coduri.model.PlatformInfo;
import com.lazar.fabrica_de_coduri.model.SEOHashTag;
import com.lazar.fabrica_de_coduri.service.NewsService;
import com.lazar.fabrica_de_coduri.service.PlatformInfoService;
import com.lazar.fabrica_de_coduri.service.SEOHashTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/admin/hashtags")
    public String addHashTag(@ModelAttribute SEOHashTag newTag) {
        PlatformInfo platformInfo = platformInfoService.getPlatformInfo();
        if (platformInfo == null) {
            throw new IllegalStateException("No PlatformInfo found.");
        }
        newTag.setPlatformInfo(platformInfo);
        seoHashTagService.addHashTag(newTag);

        return "redirect:/admin/platform-info";
    }

    @PostMapping("/admin/hashtags/delete/{id}")
    @RequestMapping(value = "/admin/hashtags/delete/{id}", method = RequestMethod.POST)
    public String deleteHashTag(@PathVariable Long id) {
        seoHashTagService.deleteHashTag(id);
        return "redirect:/admin/platform-info";
    }

    @PostMapping("/admin/news")
    public String addNews(@ModelAttribute News news) {
        newsService.addNews(news);
        return "redirect:/admin/platform-info";
    }

    @PostMapping("/admin/platform")
    @RequestMapping(value = "/admin/platform", method = RequestMethod.POST)
    public String updatePlatformInfo(@ModelAttribute PlatformInfo platformInfo) {
        platformInfoService.updatePlatformInfo(platformInfo);
        return "redirect:/admin/platform-info";
    }
}
