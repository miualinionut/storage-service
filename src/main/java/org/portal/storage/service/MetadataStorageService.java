package org.portal.storage.service;

import org.portal.storage.dto.FileDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MetadataStorageService {
    String fileDirectory();

    String metadataLocationOnDisk(String fileName);

    FileDto buildMetadataObject(String fileName, MultipartFile file);

    void deleteMetadataFor(FileDto dto) throws IOException;

    void storeMetadataFor(FileDto dto) throws IOException;

    FileDto readMetadataFor(String fileName) throws IOException;
}
