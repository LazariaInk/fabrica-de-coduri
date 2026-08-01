package com.lazar.fabrica_de_coduri.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class VideoStorageService {
    private final Path storageRoot;

    public VideoStorageService(@Value("${video.storage-location:secure-videos}") String storageLocation) {
        this.storageRoot = Path.of(storageLocation).toAbsolutePath().normalize();
    }

    public Resource load(String storageKey) {
        Path videoPath = storageRoot.resolve(storageKey).normalize();
        if (!videoPath.startsWith(storageRoot) || !Files.isRegularFile(videoPath)) {
            throw new IllegalArgumentException("Video file not found");
        }

        return new FileSystemResource(videoPath);
    }
}
