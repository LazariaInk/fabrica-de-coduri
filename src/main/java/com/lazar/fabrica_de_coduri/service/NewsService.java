package com.lazar.fabrica_de_coduri.service;

import com.lazar.fabrica_de_coduri.model.News;
import com.lazar.fabrica_de_coduri.model.PlatformInfo;
import com.lazar.fabrica_de_coduri.repository.NewsRepository;
import com.lazar.fabrica_de_coduri.repository.PlatformInfoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NewsService {
    private final NewsRepository newsRepository;
    private final PlatformInfoRepository platformInfoRepository;

    public NewsService(NewsRepository newsRepository, PlatformInfoRepository platformInfoRepository) {
        this.newsRepository = newsRepository;
        this.platformInfoRepository = platformInfoRepository;
    }

    public List<News> getLatestNews(Long platformInfoId) {
        return newsRepository.findTop3ByPlatformInfoIdOrderByCreatedAtDesc(platformInfoId);
    }

    public News addNews(News news) {
        PlatformInfo platformInfo = platformInfoRepository.findTopByOrderByIdAsc();
        if (platformInfo == null) {
            throw new IllegalStateException("No PlatformInfo found.");
        }

        news.setPlatformInfo(platformInfo);

        if (newsRepository.count() >= 3) {
            News oldestNews = newsRepository.findFirstByPlatformInfoIdOrderByCreatedAtAsc(platformInfo.getId());
            if (oldestNews != null) {
                newsRepository.delete(oldestNews);
            }
        }

        return newsRepository.save(news);
    }
}
