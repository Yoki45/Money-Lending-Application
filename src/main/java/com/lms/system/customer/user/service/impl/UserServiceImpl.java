package com.lms.system.customer.user.service.impl;

import com.lms.generic.exception.BadRequestException;
import com.lms.generic.localization.ILocalizationService;
import com.lms.security.model.CustomUserDetails;
import com.lms.security.service.JwtService;
import com.lms.system.customer.account.service.IAccountService;
import com.lms.system.customer.user.dto.UserDTO;
import com.lms.system.customer.user.model.User;
import com.lms.system.customer.user.repository.UserRepository;
import com.lms.system.customer.user.dto.CurrentUserDTO;
import com.lms.system.customer.user.dto.LoginDTO;
import com.lms.system.customer.user.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final IAccountService accountService;

    private  final ILocalizationService localizationService;


    @Override
    public UserDTO login(LoginDTO loginDTO) {

        if (loginDTO == null) {
            throw new BadRequestException(localizationService.getMessage("message.missing.validDetails",null));
        }

        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        Authentication authenticate = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        if (!authenticate.isAuthenticated()) {
            throw new BadCredentialsException(localizationService.getMessage("message.user.Wrong.Credentials",null));
        }
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new BadRequestException(localizationService.getMessage("message.user.NotFound",null));
        }


        CustomUserDetails customerUserDetails = new CustomUserDetails(user);
        String accessToken = jwtService.generateToken(customerUserDetails);
        String refreshToken = jwtService.generateRefreshToken(customerUserDetails);
        return mapUserToUseDTO(user, accessToken, refreshToken);
    }

    @Override
    public String createUser(UserDTO userDTO) {

        if (userDTO == null) {
            throw new BadRequestException(localizationService.getMessage("message.missing.validDetails",null));
        }


        boolean userExists = userRepository.findByUsername(userDTO.getUsername()).isPresent();
        if (userExists) {
            throw new BadRequestException(localizationService.getMessage("message.user.userAlreadyExists",null));
        }

        String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
        User user = User.builder()
                .password(encryptedPassword)
                .username(userDTO.getUsername())
                .name(userDTO.getName()).build();

        user = userRepository.save(user);

        accountService.createNewAccount(user, userDTO.getAccountDetails());


        return localizationService.getMessage("message.user.userCreated.success",null);

    }


    public static CurrentUserDTO mapUserToUseDTO(User user, String accessToken, String refreshToken) {
        CurrentUserDTO currentUserDTO = new CurrentUserDTO();
        currentUserDTO.setId(user.getId());
        currentUserDTO.setUsername(user.getUsername());
        currentUserDTO.setName(user.getName());
        currentUserDTO.setAccessToken(accessToken);
        currentUserDTO.setRefreshToken(refreshToken);
        return currentUserDTO;
    }


}





