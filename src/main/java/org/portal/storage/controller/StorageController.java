package org.portal.storage.controller;

import org.portal.storage.dto.FileDto;
import org.portal.storage.dto.SearchDto;
import org.portal.storage.service.MetadataStorageService;
import org.portal.storage.service.FileStorageService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
public class StorageController {

    private final FileStorageService fileStorageService;
    private final MetadataStorageService metadataService;

    public StorageController(FileStorageService fileStorageService,
                             MetadataStorageService metadataService) {
        this.fileStorageService = fileStorageService;
        this.metadataService = metadataService;
    }

    @PostMapping("/files")
    public void create(@RequestParam String fileName, @RequestParam("file") MultipartFile file) throws IOException {
        Path metadataPath = Paths.get(metadataService.metadataLocationOnDisk(fileName));
        if (Files.exists(metadataPath)) {
            throw new IllegalArgumentException("File already exists");
        }
        FileDto dto = metadataService.buildMetadataObject(fileName, file);
        fileStorageService.create(dto, file);
        metadataService.storeMetadataFor(dto);
    }

    @GetMapping("/files")
    public ResponseEntity<InputStreamResource> read(@RequestParam String fileName) throws IOException {
        FileDto dto = metadataService.readMetadataFor(fileName);
        Optional<FileInputStream> inputStream = fileStorageService.read(dto);

        if (inputStream.isPresent()) {
            InputStreamResource resource = new InputStreamResource(inputStream.get());
            HttpHeaders headers = new HttpHeaders();
            headers.add(CONTENT_DISPOSITION, "attachment; filename=" + dto.getFileName());
            headers.add(CONTENT_TYPE, dto.contentType);
            return ResponseEntity.ok().headers(headers).body(resource);
        } else {
            return ResponseEntity.status(NOT_FOUND).body(null);
        }
    }

    @PutMapping("/files")
    public void update(@RequestParam String fileName, @RequestParam("file") MultipartFile file) throws IOException {
        FileDto oldDto = metadataService.readMetadataFor(fileName);
        FileDto newDto = metadataService.buildMetadataObject(fileName, file);
        fileStorageService.create(newDto, file); //upload the new file having unique UUID identifier
        metadataService.storeMetadataFor(newDto); //atomic operation - switch between path of old file and new file
        fileStorageService.delete(oldDto);
    }

    @DeleteMapping("/files")
    public void delete(@RequestParam String fileName) throws IOException {
        FileDto dto = metadataService.readMetadataFor(fileName);
        fileStorageService.delete(dto);
        metadataService.deleteMetadataFor(dto);
    }

    @GetMapping("files/search")
    public List<String> search(@RequestBody SearchDto dto) throws IOException {
        return fileStorageService.search(metadataService.fileDirectory(), dto.getRegex(), dto.getPageNumber(), dto.getPageSize());
    }

    @GetMapping("files/count")
    public Integer count() throws IOException {
        return fileStorageService.count(metadataService.fileDirectory());
    }
}
