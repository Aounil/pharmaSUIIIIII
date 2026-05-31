package com.pharmacy.shared.storage;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;

@Service
public class FileStorageService {

    private final Path storageRootPath;

    public FileStorageService(@Value("${app.storage.root:/uploads}") String storageRoot) {
        this.storageRootPath = Paths.get(storageRoot).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(storageRootPath);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not create storage directory", ex);
        }
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        if (originalFilename.isBlank()) {
            throw new IllegalArgumentException("File name must not be blank");
        }

        String extension = getExtension(originalFilename);
        String hashedName = hashFilename(originalFilename + Instant.now().toEpochMilli()) + extension;
        Path target = storageRootPath.resolve(hashedName);

        try {
            file.transferTo(target);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to store file", ex);
        }

        return hashedName;
    }

    public Resource load(String relativePath) {
        try {
            Path file = storageRootPath.resolve(relativePath).normalize();
            if (!Files.exists(file) || !Files.isReadable(file)) {
                throw new StorageFileNotFoundException("File not found: " + relativePath);
            }
            return new UrlResource(file.toUri());
        } catch (MalformedURLException ex) {
            throw new StorageFileNotFoundException("File path is invalid: " + relativePath, ex);
        }
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex >= 0 ? filename.substring(dotIndex) : "";
    }

    private String hashFilename(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Unable to hash file name", ex);
        }
    }
}
