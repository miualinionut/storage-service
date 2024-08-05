package org.portal.storage.dto;

public class FileDto {
    public String fileName;
    public String fileLocationOnDisk;
    public String contentType;
    public String fileExtension;

    public FileDto(String fileName, String fileLocationOnDisk, String contentType, String fileExtension) {
        this.fileName = fileName;
        this.fileLocationOnDisk = fileLocationOnDisk;
        this.contentType = contentType;
        this.fileExtension = fileExtension;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileLocationOnDisk() {
        return fileLocationOnDisk;
    }

    public void setFileLocationOnDisk(String fileLocationOnDisk) {
        this.fileLocationOnDisk = fileLocationOnDisk;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

}
