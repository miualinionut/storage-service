package org.portal.storage;

import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GenerateTestData {

    @Autowired
    private MockMvc mockMvc;

    private static final int NUM_REQUESTS = 10_000_000;
    private static final int BATCH_SIZE = 10_000; // Adjust based on system capacity
    private static final int THREAD_POOL_SIZE = 20; // Adjust based on system capacity

    @BeforeAll
    public static void setup() {
        // Initialize any setup if needed
    }

    @Test
    @Disabled("Temporarily disabled")
    public void generateTestFiles() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        for (int i = 0; i < NUM_REQUESTS; i += BATCH_SIZE) {
            final int start = i;
            final int end = Math.min(i + BATCH_SIZE, NUM_REQUESTS);
            executor.submit(() -> {
                try {
                    for (int j = start; j < end; j++) {
                        String fileName = generateRandomAlphanumeric(64);
                        String url = "/files?fileName=" + fileName;

                        // Create MockMultipartFile instance
                        MockMultipartFile file = new MockMultipartFile(
                                "file",
                                fileName+".txt",
                                "text/plain",
                                ("This is a test file for filename "+fileName).getBytes()
                        );

                        ResultActions result = mockMvc.perform(multipart(url).file(file))
                                .andExpect(status().isOk())
                                .andExpect(content().string(""));
                        // Optionally handle result or log output
                        // result.andDo(print()); // Uncomment to print response for debugging
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.HOURS); // Adjust timeout as necessary
    }

    /**
     * Generates a random alphanumeric string of the specified length.
     *
     * @param length The length of the generated string.
     * @return A random alphanumeric string.
     */
    private String generateRandomAlphanumeric(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }
}
