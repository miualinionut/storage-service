package org.portal.storage.service;

import org.portal.storage.dto.FileDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface FileStorageService {
    void create(FileDto dto, MultipartFile file) throws IOException;

    Optional<FileInputStream> read(FileDto dto) throws IOException;

    void delete(FileDto dto) throws IOException;

    List<String> search(String directory, String regex, int pageNumber, int pageSize) throws IOException;

    Integer count(String directory) throws IOException;
}
