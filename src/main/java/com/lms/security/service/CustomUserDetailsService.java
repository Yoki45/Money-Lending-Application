package com.lms.security.service;

import com.lms.generic.localization.ILocalizationService;
import com.lms.security.model.CustomUserDetails;
import com.lms.system.customer.user.model.User;
import com.lms.system.customer.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ILocalizationService localizationService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            throw new UsernameNotFoundException(localizationService.getMessage("message.user.NotFound",null));
        }
        User user = userOptional.get();

        return new CustomUserDetails(user);
    }
}
