package com.example.demo.domain.service;

import com.example.demo.domain.entity.role.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRoleResponseDTO {
    private UserRole userRole;
}
