package com.api.eshop.service;

import com.api.eshop.controller.DTO.UsersDTO;
import com.api.eshop.domain.Users;
import com.api.eshop.payload.LoginRequest;
import com.api.eshop.repository.UserRepository;
import com.api.eshop.service.utilities.TokenCreator;
import com.api.eshop.service.utilities.SmsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;



    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    public Map<String, String> login(LoginRequest entity) {

        if (entity.getUsername() == null || (entity.getPassword() == null && entity.getActivationCode() == null)) {
            return new HashMap<String, String>() {{
                put("status", "error");
                put("message", "input missing");
            }};
        }

        Users result = userRepository.findByUsername(entity.getUsername());


        if (result == null) {
            return new HashMap<String, String>() {{
                put("status", "error");
                put("message", "username is not valid"); //mobile number is not registered in databse
            }};
        } else if (entity.getPassword() == null) {
            if (result.isLock() == false && (result.getActivationSmsCode().equals(entity.getActivationCode()))) {


                return new HashMap<String, String>() {{
                    put("status", "success");
                    put("message", new TokenCreator().create(entity.getUsername()));
                }};
            }
        } else if (entity.getActivationCode() == null) {
            if (new BCryptPasswordEncoder().matches(entity.getPassword(), result.getPassword()) && result.isLock() == false) {

                return new HashMap<String, String>() {{
                    put("status", "success");
                    put("message", new TokenCreator().create(entity.getUsername()));
                }};
            }
        } else if (entity.getActivationCode() != null && entity.getPassword() != null) {
            if (result.isLock() == false && (result.getActivationSmsCode().equals(entity.getActivationCode()))) {
                result.setPassword(new BCryptPasswordEncoder().encode(entity.getPassword()));

                userRepository.save(result);
                return new HashMap<String, String>() {{
                    put("status", "success");
                    put("message", new TokenCreator().create(entity.getUsername()));
                }};
            }
        }

        return new HashMap<String, String>() {{
            put("status", "error");
            put("message", "data is not valid"); //user name is correct and password or activation code is not valid
        }};

    }

    public Users getById(Long id) {
        return userRepository.findById(id).get();
    }

    public Users add(Users newUsers) {
        if (newUsers.getPassword() != null) {
            newUsers.setPassword(bCryptPasswordEncoder.encode(newUsers.getPassword()));
        }

        newUsers.setActivationSmsCode(String.format("%04d", new Random().nextInt(9999)));
        Users addedUser = userRepository.save(newUsers);

        if (addedUser.getId() != 0) {
            new SmsManager().send(newUsers.getUsername(), addedUser.getActivationSmsCode());
        }
        return addedUser;
    }

    public String resendSmsActivation(String mobileNumber) {
        try {
            Users result;
            result = userRepository.findByUsername(mobileNumber);

            if (result == null) {
                return "mobile number in not correct";
            }
            result.setActivationSmsCode(String.format("%04d", new Random().nextInt(9999)));
            userRepository.save(result);
            new SmsManager().send(result.getUsername(), result.getActivationSmsCode());

            return "new code generated";
        } catch (Exception exc) {
            return "server error";
        }
    }

    public Users update(Users newUsers) {

        return userRepository.save(newUsers);
    }

    public List<Users> getAll() {
        return userRepository.findAll();
    }

    public Users enableUserById(long id) {
        Users u = userRepository.findById(id).get();
        u.setActive(true);
        return userRepository.save(u);
    }

    public Users disableUserById(long id) {
        Users u = userRepository.findById(id).get();
        u.setActive(false);
        return userRepository.save(u);
    }


    public Users checkIfMobileNumberIsRegistered(Users user) {
        return userRepository.findByUsername(user.getUsername());
    }


    public Map<String, String> submitActivationCode(Users users) {
        Users result = userRepository.findByUsername(users.getUsername());
        if (users.getActivationSmsCode().equals(result.getActivationSmsCode())) {
            result.setActive(true);
            if (users.getPassword().length() > 0)
                result.setPassword(new BCryptPasswordEncoder().encode(users.getPassword()));
            Users updatedUser = userRepository.save(result);

            if (updatedUser.isActive() == true) {
                return new HashMap<String, String>() {{
                    put("message", new TokenCreator().create(updatedUser.getUsername()));
                }};

            } else
                return new HashMap<String, String>() {{
                    put("message", "server error");
                }};
        }
        return new HashMap<String, String>() {{
            put("message", "activation code is not correct");
        }};
    }

    public String checkUsersToken(String token) {
        TokenCreator tokenCreator = new TokenCreator();
        String[] tokenArray = token.split("____");
        if (tokenCreator.create(tokenArray[1]).equals(token)) {
            return tokenArray[1];
        }
        return "token is not valid";

    }

    public String getCodeWitMobileNumber(String number) {
        return userRepository.findByUsername(number).getActivationSmsCode();
    }




    public Users getUserByToken(String token) {
        String username = token.split("____")[1];
        if (!new TokenCreator().create(username).equals(token)) {
            return null;
        }
        return userRepository.findByUsername(username);
    }


    public Users updateUser(Users user, String token) {
        String username = token.split("____")[1];
        if (!new TokenCreator().create(username).equals(token)) {
            return null;
        }

        Users oldUser = userRepository.findByUsername(username);
        if (user.getName() != null)
            oldUser.setName(user.getName());

        if (user.getLastname() != null)
            oldUser.setLastname(user.getLastname());
        Users result = userRepository.save(oldUser);
        return result;
    }



    public Users changePassword(UsersDTO user) {
        String username = user.getToken().split("____")[1];
        if (!new TokenCreator().create(username).equals(user.getToken())) {
            return null;
        }
        Users currentUser = userRepository.findByUsername(username);
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        return userRepository.save(currentUser);

    }




}