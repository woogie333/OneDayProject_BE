package com.knuaf.oneday.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    private String userId;
    private String password;
    private Long studentId;
    private String name;
    private String major;
}
