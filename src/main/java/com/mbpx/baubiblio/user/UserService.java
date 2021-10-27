package com.mbpx.baubiblio.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

	private final static String USER_NOT_FOUND_MSG = "user with email %s not found";
	private final static String EXISTING_EMAIL_MSG = "email %s already has an account";

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
	}
	
	public String signUpUser (User user) {
		boolean userExists = userRepository.findByEmail(user.getEmail()).isPresent();
		if(userExists) {
			throw new IllegalStateException(String.format(EXISTING_EMAIL_MSG, user.getEmail()));
		}
		String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
		
		System.out.println(user.isEnabled());
		System.out.println(user.isLocked());
		userRepository.save(user);
		
		// TODO: Set confirmation token
		return "it works";
	}

}
