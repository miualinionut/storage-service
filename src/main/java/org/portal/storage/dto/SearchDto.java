package org.portal.storage.dto;

public class SearchDto {
    public String regex;
    public int pageNumber;
    public int pageSize;

    public SearchDto() {
    }

    public SearchDto(String regex, int pageNumber, int pageSize) {
        this.regex = regex;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
