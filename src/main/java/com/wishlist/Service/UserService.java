package com.wishlist.Service;

import com.wishlist.Model.User;
import com.wishlist.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.wishlist.Exceptions.EntityDoesNotExistException;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void addUser(User user) {
        userRepository.addUser(user);
    }


    public User getUserByID (int ID) throws EntityDoesNotExistException {
        User user =  userRepository.getUserByID(ID);

        if (user == null) {
            throw new EntityDoesNotExistException("User does not exist.");
        }

        return user;
    }

    public void updateUser (User user) {
        userRepository.updateUser(user);
    }

    public void deleteUserByID (int ID) {
        userRepository.deleteUserByID(ID);
    }

}
