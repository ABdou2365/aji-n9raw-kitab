package com.abde.aji_n9raw_kitab_api.config;

import com.abde.aji_n9raw_kitab_api.user.User;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.data.auditing.config.AuditingConfiguration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class ApplicationAuditAware implements AuditorAware<Integer> {
    @Override
    public Optional<Integer> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if( auth == null ||
        !auth.isAuthenticated()
        || auth instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        User user = (User) auth.getPrincipal();
        return Optional.ofNullable(user.getId());
    }
}
