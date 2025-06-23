package com.rosebeauticare.rosebeauticare.Controller;

import com.rosebeauticare.rosebeauticare.DTO.LoginDTO;
import com.rosebeauticare.rosebeauticare.Model.Staff;
import com.rosebeauticare.rosebeauticare.Security.JwtUtil;
import com.rosebeauticare.rosebeauticare.Service.StaffService;
import com.rosebeauticare.rosebeauticare.Service.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://192.168.234.184:8080")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StaffService staffService;

    public static class LoginRequest {
        @NotBlank(message = "Username is required")
        private String username;
        @NotBlank(message = "Security pin is required")
        private String securitypin;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getSecuritypin() {
            return securitypin;
        }

        public void setSecuritypin(String securitypin) {
            this.securitypin = securitypin;
        }
    }

    public static class LoginResponse {
        private String token;
        private LoginDTO staff;

        public LoginResponse(String token, LoginDTO staff) {
            this.token = token;
            this.staff = staff;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public LoginDTO getStaff() {
            return staff;
        }

        public void setStaff(LoginDTO staff) {
            this.staff = staff;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        String normalizedUsername = loginRequest.getUsername().toLowerCase();
        logger.debug("Login attempt for username: {}", normalizedUsername);
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(normalizedUsername);

            if (!passwordEncoder.matches(loginRequest.getSecuritypin(), userDetails.getPassword())) {
                logger.warn("Invalid security pin for username: {}", normalizedUsername);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid security pin");
            }

            Staff staff = staffService.getStaffByUsername(userDetails.getUsername().toLowerCase());
            if (staff == null) {
                logger.warn("Staff not found for username: {}", normalizedUsername);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            String role = userDetails.getAuthorities().stream()
                    .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                    .findFirst()
                    .orElseThrow(() -> {
                        logger.error("No role found for user: {}", normalizedUsername);
                        return new RuntimeException("No role found for user");
                    });

            String token = jwtUtil.generateToken(userDetails.getUsername(), role);
            LoginDTO loginDTO = new LoginDTO(staff.getId(), staff.getUsername(), staff.getRole());
            logger.info("Login successful for username: {}", normalizedUsername);
            return ResponseEntity.ok(new LoginResponse(token, loginDTO));
        } catch (UsernameNotFoundException e) {
            logger.warn("User not found: {}", normalizedUsername);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username");
        } catch (Exception e) {
            logger.error("Login failed for username: {}", normalizedUsername, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed due to server error");
        }
    }
}