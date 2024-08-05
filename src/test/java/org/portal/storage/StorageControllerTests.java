package org.portal.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.portal.storage.dto.FileDto;
import org.portal.storage.dto.SearchDto;
import org.portal.storage.service.HugeDiskMetadataStorageService;
import org.portal.storage.service.MetadataStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Arrays;

import static org.portal.storage.ObjectMother.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StorageControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MetadataStorageService metadataService;

    @BeforeEach
    public void cleanup() {
        try {
            FileDto dto = metadataService.readMetadataFor(compliantFileName);
            Files.deleteIfExists(Paths.get(dto.fileLocationOnDisk));
            Files.deleteIfExists(Paths.get(metadataService.metadataLocationOnDisk(dto.fileName)));
        } catch (Exception e) {
            //ignore
        }
    }

    @Test
    public void createFile_Success() throws Exception {
        String url = "/files?fileName=" + compliantFileName;
        mockMvc.perform(multipart(POST, url).file(simpleTextFile))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    public void createFile_NoFile() throws Exception {
        String url = "/files?fileName=" + compliantFileName;
        mockMvc.perform(multipart(POST, url))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Required part 'file' is not present."));
    }

    @Test
    public void createFile_NonCompliantFileName() throws Exception {
        String url = "/files?fileName=" + nonCompliantFileName;
        mockMvc.perform(multipart(POST, url).file(simpleTextFile))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid storage fileName format"));
    }

    @Test
    public void createFile_ExistingFileName() throws Exception {
        String url = "/files?fileName=" + compliantFileName;

        //create file
        mockMvc.perform(multipart(POST, url).file(simpleTextFile))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        //check already exists
        mockMvc.perform(multipart(POST, url).file(simpleTextFile))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("File already exists"));
    }

    @Test
    public void updateFile_Success() throws Exception {
        String url = "/files?fileName=" + compliantFileName;

        //create file
        mockMvc.perform(multipart(POST, url).file(simpleTextFile))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        //update
        mockMvc.perform(multipart(PUT, url).file(updatedSimpleTextFile))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    public void deleteFile_Success() throws Exception {
        String url = "/files?fileName=" + compliantFileName;

        //create file
        mockMvc.perform(multipart(POST, url).file(simpleTextFile))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        //delete
        mockMvc.perform(multipart(DELETE, url).file(simpleTextFile))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    public void readFile_Success() throws Exception {
        String url = "/files?fileName=" + compliantFileName;

        //create file
        mockMvc.perform(multipart(POST, url).file(simpleTextFile))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        //read
        mockMvc.perform(multipart(GET, url).file(simpleTextFile))
                .andExpect(status().isOk())
                .andExpect(content().string("This is a test file"));
    }

    @Test
    public void count() throws Exception {
        String url = "/files/count";

        MvcResult result = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();
        Integer count = Integer.valueOf(result.getResponse().getContentAsString());

        // Create a DecimalFormat instance with a pattern for thousands separator
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        // Format the number with thousands separator
        String formattedNumber = decimalFormat.format(count);

        System.out.println(formattedNumber + " nr of files");
    }

    @Test
    public void search() throws Exception {
        String url = "/files/search";
        SearchDto dto = new SearchDto("test", 0, 10);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(dto);

        MvcResult result = mockMvc.perform(get(url).contentType(APPLICATION_JSON_VALUE).content(jsonString))
                .andExpect(status().isOk())
                .andReturn();
        String jsonArray = result.getResponse().getContentAsString();
        String[] stringArray = objectMapper.readValue(jsonArray, new TypeReference<>() {});

        for(String s: stringArray) {
            System.out.println(s);
        }
    }

}
