package com.db.cloud.school.bexevents.security.jwt;

import com.db.cloud.school.bexevents.exceptions.EmailNotFoundException;
import com.db.cloud.school.bexevents.exceptions.JWTException;
import com.db.cloud.school.bexevents.exceptions.NotLoggedInExcpetion;
import com.db.cloud.school.bexevents.models.User;
import com.db.cloud.school.bexevents.repositories.UserRepository;
import com.db.cloud.school.bexevents.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${db.cloud.school.bexevents.app.jwtSecret}")
    private String jwtSecret;

    @Value("${db.cloud.school.bexevents.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${db.cloud.school.bexevents.app.jwtCookieName}")
    private String jwtCookie;

    @Autowired
    private UserRepository userRepository;

    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);
        if (cookie != null) {
            return cookie.getValue();
        } else {
//            return null;
            throw new NotLoggedInExcpetion("You must be logged in!");
        }
    }

    public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {
        String jwt = generateTokenFromEmail(userPrincipal.getEmail());
        return ResponseCookie.from(jwtCookie, jwt).build();
    }

    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtCookie, null).path("/api").build();
    }

    public String getEmailFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
            throw new JWTException(e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            throw new JWTException(e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
            throw new JWTException(e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
            throw new JWTException(e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
            throw new JWTException(e.getMessage());
        }
    }

    public String generateTokenFromEmail(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public User getUserFromCookie(HttpServletRequest httpServletRequest) {
        String token = getJwtFromCookies(httpServletRequest);
        validateJwtToken(token);
        String email = getEmailFromJwtToken(token);
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty())
            throw new EmailNotFoundException("The logged in user is not valid!");
        return userOptional.get();
    }
}