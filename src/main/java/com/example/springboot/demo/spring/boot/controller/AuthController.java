package com.example.springboot.demo.spring.boot.controller;

import com.example.springboot.demo.spring.boot.dto.BearerToken;
import com.example.springboot.demo.spring.boot.dto.LoginDto;
import com.example.springboot.demo.spring.boot.dto.RegisterDto;
import com.example.springboot.demo.spring.boot.model.RoleName;
import com.example.springboot.demo.spring.boot.model.User;
import com.example.springboot.demo.spring.boot.repository.IRoleRepository;
import com.example.springboot.demo.spring.boot.repository.IUserRepository;
import com.example.springboot.demo.spring.boot.security.JwtUtilities;
import com.example.springboot.demo.spring.boot.service.StudentService;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Transactional
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth Controller", description = "Operations pertaining to authentication in the system")
public class AuthController {

        private final AuthenticationManager authenticationManager ;
        private final StudentService studentService;
        private final IUserRepository iUserRepository ;
        private final IRoleRepository iRoleRepository ;
        private final PasswordEncoder passwordEncoder ;
        private final JwtUtilities jwtUtilities ;
        private final RateLimiter loginRateLimiter;

        public AuthController(IUserRepository iUserRepository, PasswordEncoder passwordEncoder,
                              AuthenticationManager authenticationManager, JwtUtilities jwtUtilities,
                              RateLimiterRegistry rateLimiterRegistry, StudentService studentService, IRoleRepository iRoleRepository) {
            this.iUserRepository = iUserRepository;
            this.passwordEncoder = passwordEncoder;
            this.authenticationManager = authenticationManager;
            this.jwtUtilities = jwtUtilities;
            this.studentService = studentService;
            this.iRoleRepository = iRoleRepository;
            this.loginRateLimiter = rateLimiterRegistry.rateLimiter("loginRateLimiter");
    }

        @PostMapping("/register")
        public ResponseEntity<?> register(RegisterDto registerDto) {
            if(iUserRepository.existsByEmail(registerDto.getEmail()))
            { return  new ResponseEntity<>("email is already taken !", HttpStatus.SEE_OTHER); }
            else
            { User user = new User();
                user.setEmail(registerDto.getEmail());
                user.setFirstName(registerDto.getFirstName());
                user.setLastName(registerDto.getLastName());
                user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
                user.setRoles(Collections.singletonList(iRoleRepository.findByRoleName(RoleName.USER)));
                iUserRepository.save(user);
                return ResponseEntity.ok("User registered successfully");
            }
        }

        @PostMapping("/login")
        public ResponseEntity<BearerToken> login(LoginDto loginDto) {
            try {
                User user = iUserRepository.findByEmail(loginDto.getEmail())
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
                    if (!loginRateLimiter.acquirePermission()) {
                        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
                    }
                    throw new BadCredentialsException("Invalid password");
                }

                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginDto.getEmail(),
                                loginDto.getPassword()
                        )
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Xử lý lấy danh sách role và sinh token
                List<String> rolesNames = new ArrayList<>();
                user.getRoles().forEach(r -> rolesNames.add(r.getRoleName()));
                String token = jwtUtilities.generateToken(user.getUsername(), rolesNames);
                BearerToken bearerToken = new BearerToken(token, "Bearer");
                return ResponseEntity.ok(bearerToken);
            } catch (UsernameNotFoundException ex) {
                // Xử lý khi không tìm thấy người dùng
                return ResponseEntity.notFound().build();
            } catch (BadCredentialsException ex) {
                // Xử lý khi mật khẩu không đúng
                if (loginRateLimiter.getMetrics().getAvailablePermissions() == 0) {
                    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
                }
            }
        }
}
