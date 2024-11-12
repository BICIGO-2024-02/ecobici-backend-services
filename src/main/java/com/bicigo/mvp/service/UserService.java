package com.bicigo.mvp.service;


import com.bicigo.mvp.dto.UserUpdateDto;
import com.bicigo.mvp.model.User;

import java.util.List;

public interface UserService {
    public abstract User createUser(User user);
    public abstract User getUserById(Long user_id);
    public abstract void deleteUser(Long user_id);
    public abstract List<User> getAllUsers();
    public abstract User updateUser(Long userId, UserUpdateDto updateDTO);
}
