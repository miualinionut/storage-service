package org.portal.storage.service;

import org.portal.storage.dto.FileDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class HugeDiskFileFileStorageService implements FileStorageService {

    @Override
    public void create(FileDto dto, MultipartFile file) throws IOException {
        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(dto.fileLocationOnDisk));
             InputStream inputStream = file.getInputStream()) {
            byte[] buffer = new byte[8192]; // 8KB buffer
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) { // Stream file data to disk
                stream.write(buffer, 0, bytesRead);
            }
            stream.flush(); // Ensure all data is written out
        }
    }

    @Override
    public Optional<FileInputStream> read(FileDto dto) throws IOException {
        Path filePath = Paths.get(dto.fileLocationOnDisk);
        if (Files.exists(filePath)) {
            return Optional.of(new FileInputStream(filePath.toFile()));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void delete(FileDto dto) throws IOException {
        Path filePath = Paths.get(dto.fileLocationOnDisk);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }

    @Override
    public List<String> search(String directory, String regex, int pageNumber, int pageSize) throws IOException {
        Path directoryPath = Paths.get(directory);

        try {
            Pattern pattern = Pattern.compile(regex);
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath)) {

                // Convert DirectoryStream to Stream<Path> for enabling stream operations like filter, skip and limit
                Stream<Path> pathStream = StreamSupport.stream(stream.spliterator(), false);

                return pathStream.sorted() //needed for pagination
                        .map((Path entry) -> {
                            String fileName = entry.getFileName().toString();
                            return fileName.substring(0, fileName.length() - 37); //removing the UUID
                        })
                        .filter((String fileName) -> pattern.matcher(fileName).matches())
                        .skip(pageNumber * pageSize) // Skip files for the previous pages
                        .limit(pageSize) // Limit to the current page size
                        .collect(Collectors.toList());
            }
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Invalid regex pattern", e);
        }
    }

    @Override
    public Integer count(String directory) throws IOException {
        AtomicInteger count = new AtomicInteger();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Path.of(directory))) {
            stream.forEach(entry -> count.incrementAndGet());
        }

        return count.get();
    }
}
