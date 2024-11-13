package org.uvhnael.chatserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.uvhnael.chatserver.dto.AuthRequest;
import org.uvhnael.chatserver.dto.AuthResponse;
import org.uvhnael.chatserver.dto.ErrorResponse;
import org.uvhnael.chatserver.model.User;
import org.uvhnael.chatserver.service.UserService;

@Controller
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody AuthRequest auth) {
        try{
            AuthResponse user = userService.signIn(auth);
            if (user != null) {
                return ResponseEntity.status(HttpStatus.OK).body(user);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("Unexpected error", e.getMessage()));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody User user) {
        try {
            AuthResponse newUser = userService.signUp(user);
            if (newUser != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("Unexpected error", e.getMessage()));
        }
    }
    private Object createErrorResponse(String error, String message) {
        return new ErrorResponse(error, message);
    }

}
