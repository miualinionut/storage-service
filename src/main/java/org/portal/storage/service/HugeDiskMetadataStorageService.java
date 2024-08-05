package org.portal.storage.service;

import org.portal.storage.dto.FileDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class HugeDiskMetadataStorageService implements MetadataStorageService {

    private final String fileDirectory;
    private final String metadataDirectory;
    private final String fileNameValidationRegex;

    public HugeDiskMetadataStorageService(@Value("${storage.fileDirectory}") String fileDirectory,
                                          @Value("${storage.metadataDirectory}") String metadataDirectory,
                                          @Value("${storage.fileName.validation.regex}") String fileNameValidationRegex) {
        this.fileDirectory = fileDirectory;
        this.metadataDirectory = metadataDirectory;
        this.fileNameValidationRegex = fileNameValidationRegex;
    }

    @Override
    public String fileDirectory() {
        return fileDirectory;
    }

    @Override
    public String metadataLocationOnDisk(String fileName) {
        return metadataDirectory + fileName + ".properties";
    }

    @Override
    public FileDto buildMetadataObject(String fileName, MultipartFile file) {
        if (!Pattern.matches(fileNameValidationRegex, fileName)) {
            throw new IllegalArgumentException("Invalid storage fileName format");
        }
        return new FileDto(
                fileName,
                fileDirectory + fileName + "_" + UUID.randomUUID(), //adding UUID so that for file upload/override, the new file will not clash with old one
                file.getContentType(),
                StringUtils.getFilenameExtension(file.getOriginalFilename())
        );
    }

    @Override
    public void deleteMetadataFor(FileDto dto) throws IOException {
        Path metadataPath = Paths.get(metadataLocationOnDisk(dto.fileName));
        if (Files.exists(metadataPath)) {
            Files.delete(metadataPath);
        }
    }
    @Override
    public void storeMetadataFor(FileDto dto) throws IOException {
        saveObjectToPropertiesFile(dto, dto.fileName);
    }

    @Override
    public FileDto readMetadataFor(String fileName) throws IOException {
        return loadObjectFromPropertiesFile(fileName);
    }

    private void saveObjectToPropertiesFile(FileDto obj, String fileName) throws IOException {
        Properties properties = new Properties();

        // Convert the object to key-value pairs
        for (Map.Entry<String, String> entry : toMap(obj).entrySet()) {
            properties.setProperty(entry.getKey(), entry.getValue());
        }

        // Write properties to file
        try (OutputStream output = new FileOutputStream(metadataLocationOnDisk(fileName))) {
            properties.store(output, "Object Properties");
        }
    }

    private FileDto loadObjectFromPropertiesFile(String fileName) throws IOException {
        Properties properties = new Properties();

        try (InputStream input = new FileInputStream(metadataLocationOnDisk(fileName))) {
            properties.load(input);
        }

        // Convert the properties back to an object
        return toObject(properties);
    }

    // Convert to a Map for easier handling
    private static Map<String, String> toMap(FileDto dto) {
        Map<String, String> map = new HashMap<>();
        map.put("fileName", dto.fileName);
        map.put("locationOnDisk", dto.fileLocationOnDisk);
        map.put("contentType", dto.contentType);
        map.put("fileExtension", dto.fileExtension);
        return map;
    }

    // Convert back to Object
    private static FileDto toObject(Properties properties) {
        return new FileDto(
                properties.getProperty("fileName")
                , properties.getProperty("locationOnDisk")
                , properties.getProperty("contentType")
                , properties.getProperty("fileExtension")
        );
    }
}
