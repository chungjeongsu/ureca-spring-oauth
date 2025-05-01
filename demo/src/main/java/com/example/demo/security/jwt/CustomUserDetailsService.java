package com.example.demo.security.jwt;

import com.example.demo.domain.entity.User;
import com.example.demo.domain.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String providerWithProviderId) throws UsernameNotFoundException {
        String provider = providerWithProviderId.split("\\s")[0];
        String providerId = providerWithProviderId.split("\\s")[1];
        Optional<User> user = userRepository.findByProviderAndProviderId(provider, providerId);
        return user.map(CustomUserDetails::new).orElse(null);
    }
}
