package com.example.cluvrnotifications.global.jwt;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.cluvrnotifications.domain.user.entity.User;
import com.example.cluvrnotifications.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<User> optionalUser = userRepository.findByEmail(email);
		if (optionalUser.isEmpty()) {
			throw new UsernameNotFoundException("등록된 사용자가 없습니다. email=" + email);
		}
		User user = optionalUser.get();
		return new CustomUserDetails(user);
	}
}
