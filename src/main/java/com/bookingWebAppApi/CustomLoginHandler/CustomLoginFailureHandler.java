package com.bookingWebAppApi.CustomLoginHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import com.bookingWebAppApi.Model.Userr;
import com.bookingWebAppApi.Service.UserService;
import com.bookingWebAppApi.Service.Impl.UserServiceImpl;

@Component
public class CustomLoginFailureHandler {
    @Autowired
    private UserService userService;

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) throws LockedException {

        Object principal = event.getAuthentication().getPrincipal();

        if (principal instanceof String) {
            String username = (String) event.getAuthentication().getPrincipal();
            Userr user = userService.findByUsername(username);

            if (user != null) {
                if (user.isAccountEnabled() && user.isAccountNonLocked()) {
                    if (user.getFailedAttempt() < UserServiceImpl.MAX_FAILED_ATTEMPTS - 1) {
                        userService.increaseFailedAttempt(user);
                    }

                    else {
                        userService.lock(user);
                        throw new LockedException(
                                "Your account has been locked due to 3 failed attempts. It will be unlocked after 24 hours.");
                    }

                }

            }
        }

    }
}
