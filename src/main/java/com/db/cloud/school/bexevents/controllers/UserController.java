package com.db.cloud.school.bexevents.controllers;

import com.db.cloud.school.bexevents.exceptions.EmailNotFoundException;
import com.db.cloud.school.bexevents.models.LoginRequest;
import com.db.cloud.school.bexevents.models.User;
import com.db.cloud.school.bexevents.models.UserInfoResponse;
import com.db.cloud.school.bexevents.payload.UserSignupRequest;
import com.db.cloud.school.bexevents.repositories.UserRepository;
import com.db.cloud.school.bexevents.security.jwt.JwtUtils;
import com.db.cloud.school.bexevents.security.services.UserDetailsImpl;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        return ResponseEntity.ok().header(HttpHeaders.COOKIE, jwtCookie.toString())
                .body(new UserInfoResponse(userDetails.getId(),
                        userDetails.getFirstName(),
                        userDetails.getLastName(),
                        userDetails.getEmail()));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("You've been signed out!");
    }

    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers(HttpServletRequest httpServletRequest) {
        String token = jwtUtils.getJwtFromCookies(httpServletRequest);
        jwtUtils.validateJwtToken(token);
        String email = jwtUtils.getEmailFromJwtToken(token);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty())
            throw new EmailNotFoundException("The logged in user is not valid!");

        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserSignupRequest signUpRequest) {

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        // Create new user's account
        User user = new User(signUpRequest.getFirstName(),
                signUpRequest.getLastName(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    @GetMapping("/test")
    public String homePage(HttpServletRequest httpServletRequest) {
        String token = jwtUtils.getJwtFromCookies(httpServletRequest);
        System.out.println(token);
        jwtUtils.validateJwtToken(token);
        System.out.println(jwtUtils.getEmailFromJwtToken(token));
        return null;
    }
}
