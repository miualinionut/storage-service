package org.portal.storage;

import org.springframework.mock.web.MockMultipartFile;

public class ObjectMother {

    public static String compliantFileName = "abc";
    public static String nonCompliantFileName = "abc.txt";
    public static MockMultipartFile simpleTextFile = new MockMultipartFile(
            "file",
            "abc.txt",
            "text/plain",
            "This is a test file".getBytes()
    );
    public static MockMultipartFile updatedSimpleTextFile = new MockMultipartFile(
            "file",
            "abc.txt",
            "text/plain",
            "This is the updated test file".getBytes()
    );
}
