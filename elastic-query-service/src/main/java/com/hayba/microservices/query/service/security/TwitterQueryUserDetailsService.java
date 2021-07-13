package com.hayba.microservices.query.service.security;

import com.hayba.microservices.query.service.business.QueryUserService;
import com.hayba.microservices.query.service.transformer.UserPermissionsToUserDetailTransformer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TwitterQueryUserDetailsService implements UserDetailsService {

    private final QueryUserService queryUserService;
    private final UserPermissionsToUserDetailTransformer transformer;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      return queryUserService.findAllPermissionsByUsername(username)
              .map(transformer::getUserDetails)
              .orElseThrow(() -> new UsernameNotFoundException("No user found with name " + username));
    }
}
