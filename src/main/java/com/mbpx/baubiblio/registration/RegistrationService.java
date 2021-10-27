package com.mbpx.baubiblio.registration;

import org.springframework.stereotype.Service;

import com.mbpx.baubiblio.user.User;
import com.mbpx.baubiblio.user.UserRole;
import com.mbpx.baubiblio.user.UserService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RegistrationService {

	private static final String INVALID_EMAIL_MSG = "email %s is not valid";

	private final UserService userService;
	private final EmailValidator emailValidator;

	public String register(RegistrationRequest request) {
		boolean isValidEmail = emailValidator.test(request.getEmail());
		if (!isValidEmail) {
			throw new IllegalStateException(String.format(INVALID_EMAIL_MSG, request.getEmail()));
		}
		return userService.signUpUser(new User(request.getName(), request.getLastName(), request.getEmail(),
				request.getPassword(), UserRole.USER));
	}

}
