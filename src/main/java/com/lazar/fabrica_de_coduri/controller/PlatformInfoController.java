package com.lazar.fabrica_de_coduri.controller;

import com.lazar.fabrica_de_coduri.model.News;
import com.lazar.fabrica_de_coduri.model.PlatformInfo;
import com.lazar.fabrica_de_coduri.model.SEOHashTag;
import com.lazar.fabrica_de_coduri.model.Sponsor;
import com.lazar.fabrica_de_coduri.service.NewsService;
import com.lazar.fabrica_de_coduri.service.PlatformInfoService;
import com.lazar.fabrica_de_coduri.service.SEOHashTagService;
import com.lazar.fabrica_de_coduri.service.SponsorService;
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
    @Autowired
    private SponsorService sponsorService;


    @GetMapping("/admin/platform-info")
    public String getPlatformInfo(Model model) {
        PlatformInfo platformInfo = platformInfoService.getPlatformInfo();
        model.addAttribute("platformInfo", platformInfo);
        model.addAttribute("hashtags", seoHashTagService.getAllHashTags(platformInfo.getId()));
        model.addAttribute("newsList", newsService.getLatestNews(platformInfo.getId()));
        model.addAttribute("newHashtag", new SEOHashTag());
        model.addAttribute("news", new News());

        // 👇 Aici adăugăm:
        model.addAttribute("newSponsor", new Sponsor());
        model.addAttribute("sponsorList", platformInfo.getSponsors());

        return "platformInfo";
    }

    @PostMapping("/admin/sponsors")
    public String addSponsor(@ModelAttribute Sponsor sponsor) {
        PlatformInfo platformInfo = platformInfoService.getPlatformInfo();
        sponsor.setPlatformInfo(platformInfo);
        sponsorService.saveSponsor(sponsor);
        return "redirect:/admin/platform-info";
    }

    @PostMapping("/admin/sponsors/delete/{id}")
    public String deleteSponsor(@PathVariable Long id) {
        sponsorService.deleteSponsor(id);
        return "redirect:/admin/platform-info";
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
