package com.example.demo.domain.controller;

import com.example.demo.domain.service.RefreshTokenService;
import com.example.demo.domain.service.UserRoleRequestDTO;
import com.example.demo.domain.service.UserService;
import com.example.demo.security.jwt.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequiredArgsConstructor
public class TestController {
    public final UserService userService;
    public final RefreshTokenService refreshTokenService;

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @ResponseBody
    @PostMapping("/user/role")
    public ResponseEntity<?> saveUserRole(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            UserRoleRequestDTO userRoleRequestDTO,
            HttpServletResponse response
    ){
        return ResponseEntity.ok().body(userService.saveUserRole(userDetails.getUserId(), userRoleRequestDTO, response));
    }

    @ResponseBody
    @GetMapping("/refresh")
    public ResponseEntity<?> updateJwtToken(HttpServletRequest request, HttpServletResponse response){
        return ResponseEntity.ok().body(refreshTokenService.updateJwtToken(request.getHeader("Refresh-Token"), response));
    }
}
