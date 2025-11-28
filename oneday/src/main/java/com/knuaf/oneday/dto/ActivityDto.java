package com.knuaf.oneday.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ActivityDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request{
        private String category;
        private String title;
        private String detail;
        private String year;
    }

    @Data
    @AllArgsConstructor
    public static class Response{
        private Long id;
        private String category;
        private String title;
        private String detail;
        private String year;
    }
}
