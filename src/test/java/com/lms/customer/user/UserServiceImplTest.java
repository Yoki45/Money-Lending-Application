package com.lms.customer.user;

import com.lms.generic.exception.BadRequestException;
import com.lms.generic.localization.ILocalizationService;
import com.lms.security.service.JwtService;
import com.lms.system.customer.account.enums.AccountType;
import com.lms.system.customer.account.model.Account;
import com.lms.system.customer.account.repository.AccountRepository;
import com.lms.system.customer.account.service.IAccountService;
import com.lms.system.customer.user.dto.CurrentUserDTO;
import com.lms.system.customer.user.dto.LoginDTO;
import com.lms.system.customer.user.dto.UserDTO;
import com.lms.system.customer.user.model.User;
import com.lms.system.customer.user.repository.UserRepository;
import com.lms.system.customer.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private ILocalizationService localizationService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IAccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void login_ShouldThrowBadRequest_WhenLoginDtoIsNull() {
        when(localizationService.getMessage("message.missing.validDetails", null))
                .thenReturn("Missing valid details");

        assertThrows(BadRequestException.class, () -> userService.login(null));
    }

    @Test
    void login_ShouldThrowBadCredentialsException_WhenAuthenticationFails() {
        LoginDTO loginDTO = new LoginDTO("user@example.com", "wrongPassword");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> userService.login(loginDTO));
    }

    @Test
    void login_ShouldThrowBadRequest_WhenUserNotFoundAfterAuthentication() {
        LoginDTO loginDTO = new LoginDTO("user@example.com", "password");
        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any()))
                .thenReturn(auth);
        when(auth.isAuthenticated()).thenReturn(true);
        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.empty());
        when(localizationService.getMessage("message.user.NotFound", null))
                .thenReturn("User not found");

        assertThrows(BadRequestException.class, () -> userService.login(loginDTO));
    }

    @Test
    void login_ShouldReturnCurrentUserDTO_WhenCredentialsAreCorrect() {
        LoginDTO loginDTO = new LoginDTO("user@example.com", "password");
        User user = User.builder()
                .id(1L)
                .username("user@example.com")
                .password("encodedPass")
                .name("John Doe")
                .build();

        Account account = Account.builder()
                .accountNumber(123456789L)
                .accountType(AccountType.SAVINGS)
                .customer(user)
                .build();

        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any()))
                .thenReturn(auth);
        when(auth.isAuthenticated()).thenReturn(true);
        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(user));
        when(accountRepository.findAccountByCustomer(user))
                .thenReturn(account);
        when(jwtService.generateToken(any()))
                .thenReturn("access-token");
        when(jwtService.generateRefreshToken(any()))
                .thenReturn("refresh-token");

        CurrentUserDTO userDTO = (CurrentUserDTO) userService.login(loginDTO);

        assertEquals("user@example.com", userDTO.getUsername());
        assertEquals("access-token", userDTO.getAccessToken());
        assertEquals("refresh-token", userDTO.getRefreshToken());
        assertEquals(123456789L, userDTO.getAccountNumber());
        assertEquals(AccountType.SAVINGS, userDTO.getAccountType());
    }

    @Test
    void createUser_ShouldThrowBadRequest_WhenUserDTOIsNull() {
        when(localizationService.getMessage("message.missing.validDetails", null))
                .thenReturn("Missing valid details");

        assertThrows(BadRequestException.class, () -> userService.createUser(null));
    }

    @Test
    void createUser_ShouldThrowBadRequest_WhenUserAlreadyExists() {
        UserDTO userDTO = UserDTO.builder()
                .username("user@example.com")
                .password("password")
                .role("USER")
                .name("John Doe")
                .accountDetails(null)
                .build();

        when(userRepository.findByUsername(userDTO.getUsername()))
                .thenReturn(Optional.of(new User()));
        when(localizationService.getMessage("message.user.userAlreadyExists", null))
                .thenReturn("User already exists");

        assertThrows(BadRequestException.class, () -> userService.createUser(userDTO));
    }

    @Test
    void createUser_ShouldReturnSuccessMessage_WhenUserIsCreatedSuccessfully() {
        UserDTO userDTO = UserDTO.builder()
                .username("user@example.com")
                .password("password")
                .role("USER")
                .name("John Doe")
                .accountDetails(null)
                .build();

        when(userRepository.findByUsername(userDTO.getUsername()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDTO.getPassword()))
                .thenReturn("encodedPassword");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(localizationService.getMessage("message.user.userCreated.success", null))
                .thenReturn("User created successfully");

        String result = userService.createUser(userDTO);

        assertEquals("User created successfully", result);
        verify(accountService).createNewAccount(any(User.class), eq(userDTO.getAccountDetails()));
    }

}
