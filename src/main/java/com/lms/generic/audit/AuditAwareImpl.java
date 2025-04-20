package com.lms.generic.audit;


import com.lms.security.model.CustomUserDetails;
import com.lms.system.customer.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component("auditAwareImpl")
public class AuditAwareImpl implements AuditorAware<User> {

    @Override
    public Optional<User> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            log.info("Current user is not authenticated");
            return Optional.empty();
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return Optional.of(userDetails.getUser());
    }

    public User getCurrentLoggedInUser() {
        User user = null;
        Optional<User> currentUser = getCurrentAuditor();
        if (currentUser.isPresent()) {
            user = currentUser.get();
        }
        return user;
    }
}
