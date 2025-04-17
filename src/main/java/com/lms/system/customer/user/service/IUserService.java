package com.lms.system.customer.user.service;

import com.lms.system.customer.user.dto.UserDTO;
import com.lms.system.customer.user.dto.LoginDTO;

public interface IUserService {

    UserDTO login(LoginDTO loginDTO);

    String createUser(UserDTO userDTO);
}
