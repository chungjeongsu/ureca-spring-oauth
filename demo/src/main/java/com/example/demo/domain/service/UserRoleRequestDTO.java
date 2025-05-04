package com.example.demo.domain.service;

import com.example.demo.domain.entity.role.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserRoleRequestDTO {
    private UserRole role;
}
