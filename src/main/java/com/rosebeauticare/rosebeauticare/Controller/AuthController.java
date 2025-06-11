package com.rosebeauticare.rosebeauticare.Controller;

import com.rosebeauticare.rosebeauticare.Model.Staff;
import com.rosebeauticare.rosebeauticare.Security.JwtUtil;
import com.rosebeauticare.rosebeauticare.Service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Staff staff) {
        if (staff.getEmail() == null || staff.getSecuritypin() == null) {
            throw new RuntimeException("Email or security pin is missing");
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(staff.getEmail());
        if (!passwordEncoder.matches(staff.getSecuritypin(), userDetails.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        String role = userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No role found for user"));
        String token = jwtUtil.generateToken(userDetails.getUsername(), role);
        return ResponseEntity.ok(token);
    }
}