package com.api.eshop.controller;

import com.api.eshop.controller.DTO.UsersDTO;
import com.api.eshop.domain.Users;
import com.api.eshop.payload.LoginRequest;
import com.api.eshop.service.utilities.ErrorsMaps;
import com.api.eshop.service.utilities.FileStorageService;
import com.api.eshop.security.JwtTokenProvider;
import com.api.eshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private ErrorsMaps errorsMaps;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("token/{token}")
    @CrossOrigin("*")
    public ResponseEntity getUserByToken(@PathVariable String token) {

        Users result = service.getUserByToken(token);

        if (result == null) {
            return new ResponseEntity(new HashMap() {{
                put("message", "token is not correct");
            }}, HttpStatus.OK);
        }

        return new ResponseEntity(new HashMap() {{
            put("message", "success");
            put("user", result);
        }}, HttpStatus.OK);

    }

    @PostMapping("register")
    @CrossOrigin("*")
    public ResponseEntity add(@Valid @RequestBody Users users, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return errorsMaps.getMap(bindingResult);
        }
        try {
            Map<String, String> activationResult = service.submitActivationCode(users);

            return new ResponseEntity(activationResult, HttpStatus.CREATED);

        } catch (Exception exc) {
            return new ResponseEntity(new HashMap<String, String>() {{
                put("message", "server error");
            }}, HttpStatus.BAD_REQUEST);

            // throw new ApiRequestException(ApiExceptionMessageParser.getErrorReasonByExceptionMessage(exc.getMessage()));
        }
    }

    @PutMapping("changePassword")
    @CrossOrigin("*")
    public ResponseEntity changePassword(@RequestBody UsersDTO user) {
        if (!user.getPassword().equals(user.getConfirmPassword()))
            return new ResponseEntity(new HashMap<String, String>() {{
                put("message", "passwords does not matched");
            }}, HttpStatus.OK);
        else {
            Users result = service.changePassword(user);
            if (result == null)
                return new ResponseEntity(new HashMap<String, String>() {{
                    put("message", "token is not correct");
                }}, HttpStatus.OK);
            else
                return new ResponseEntity(new HashMap<String, String>() {{
                    put("message", "success");
                }}, HttpStatus.OK);

        }
    }


    @PostMapping("login")
    @CrossOrigin("*")
    public ResponseEntity authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult, HttpServletRequest request) {

        return new ResponseEntity(service.login(loginRequest), HttpStatus.OK);

    }

    @PostMapping("isMobileNumberRegistered")
    @CrossOrigin("*")
    public ResponseEntity checkIfMobileNumberIsRegistered(@RequestBody Users user) // check received mobile number is my user
    {

        Users result = service.checkIfMobileNumberIsRegistered(user);

        if (result != null) {
            result.setActivationSmsCode("****");
            return new ResponseEntity(result, HttpStatus.OK);
        }
        result = service.add(user);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @PostMapping("resendSmsActivation")
    @CrossOrigin("*")
    public ResponseEntity resendSmsActivationCode(@RequestBody Map<String, String> mobileNumber) {
        return new ResponseEntity(service.resendSmsActivation(mobileNumber.get("mobile")), HttpStatus.OK);
    }

    @PostMapping("isTokenValid")
    @CrossOrigin("*")
    public ResponseEntity isTokenValid(@RequestBody Map<String, String> token) {
        return new ResponseEntity(service.checkUsersToken(token.get("token")), HttpStatus.OK);
    }

    @GetMapping
    @RequestMapping("m/{number}")
    public String getCodeWitMobileNumber(@PathVariable String number) {
        return service.getCodeWitMobileNumber(number);
    }



    @PutMapping(value = "update/{token}" , consumes = "multipart/form-data")
    @CrossOrigin("*")
    public ResponseEntity<Users> updateUser(@RequestParam("user") Users user , @RequestParam("file") MultipartFile file, @PathVariable String token) {

        fileStorageService.storeFile(file);
        Users result = service.updateUser(user, token);
        if (result == null) {
            return new ResponseEntity(new HashMap<String, String>() {{
                put("message", "token is not correct");
            }}, HttpStatus.OK);
        }

        return new ResponseEntity(new HashMap<String, Object>() {{
            put("message", "ok");
            put("user", result);
        }}, HttpStatus.OK);
    }






}
