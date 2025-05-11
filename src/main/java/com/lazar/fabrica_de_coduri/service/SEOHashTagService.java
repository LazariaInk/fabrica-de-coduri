package com.lazar.fabrica_de_coduri.service;

import com.lazar.fabrica_de_coduri.model.SEOHashTag;
import com.lazar.fabrica_de_coduri.repository.SEOHashTagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SEOHashTagService {
    private final SEOHashTagRepository seoHashTagRepository;

    public SEOHashTagService(SEOHashTagRepository seoHashTagRepository) {
        this.seoHashTagRepository = seoHashTagRepository;
    }

    public List<SEOHashTag> getAllHashTags(Long platformInfoId) {
        return seoHashTagRepository.findByPlatformInfoId(platformInfoId);
    }

    public SEOHashTag addHashTag(SEOHashTag tag) {
        return seoHashTagRepository.save(tag);
    }

    public SEOHashTag updateHashTag(Long id, SEOHashTag newTag) {
        return seoHashTagRepository.findById(id).map(tag -> {
            tag.setText(newTag.getText());
            tag.setRedirectLink(newTag.getRedirectLink());
            return seoHashTagRepository.save(tag);
        }).orElse(null);
    }

    public void deleteHashTag(Long id) {
        seoHashTagRepository.deleteById(id);
    }
}
