package com.wishlist.Service;

import com.wishlist.Model.User;
import com.wishlist.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void addUser(User user) {
        userRepository.addUser(user);
    }
}
