package mbpx.baubiblio.registration;

import java.time.LocalDateTime;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import mbpx.baubiblio.email.EmailBuilder;
import mbpx.baubiblio.email.EmailSender;
import mbpx.baubiblio.registration.token.ConfirmationToken;
import mbpx.baubiblio.registration.token.ConfirmationTokenService;
import mbpx.baubiblio.user.User;
import mbpx.baubiblio.user.UserRole;
import mbpx.baubiblio.user.UserService;

@Service
@AllArgsConstructor
public class RegistrationService {

	private static final String INVALID_EMAIL_MSG = "email %s is not valid";
	private static final String TOKEN_NOT_FOUND_MSG = "token not found";
	private static final String TOKEN_EXPIRED_MSG = "token expired";
	private static final String ALREADY_CONFIRMED_MSG = "email already confirmed";
	private static final String TOKEN_CONFIRMED_MSG = "confirmed";
	private static final String CONFIRMATION_EMAIL_SUBJECT = "Baubiblio - Confirm your email";
	private static final String CONFIRMATION_LINK = "http://localhost:8080/api/v1/registration/confirm?token=%s";

	private final UserService userService;
	private final EmailValidator emailValidator;
	private final ConfirmationTokenService confirmationTokenService;
	private final EmailSender emailSender;

	public String register(RegistrationRequest request) {
		boolean isValidEmail = emailValidator.test(request.getEmail());
		if (!isValidEmail) {
			throw new IllegalStateException(String.format(INVALID_EMAIL_MSG, request.getEmail()));
		}
		String token = userService.signUpUser(new User(request.getName(), request.getLastName(), request.getEmail(),
				request.getPassword(), UserRole.USER));
		String emailContent = EmailBuilder.buildEmail(request.getName(), String.format(CONFIRMATION_LINK, token));
		emailSender.send(request.getEmail(), CONFIRMATION_EMAIL_SUBJECT, emailContent);
		return token;
	}

	@Transactional
	public String confirmToken(String token) {
		ConfirmationToken confirmationToken = confirmationTokenService.getToken(token)
				.orElseThrow(() -> new IllegalStateException(TOKEN_NOT_FOUND_MSG));

		if (confirmationToken.getConfirmedAt() != null) {
			throw new IllegalStateException(ALREADY_CONFIRMED_MSG);
		}

		LocalDateTime expiredAt = confirmationToken.getExpiresAt();

		if (expiredAt.isBefore(LocalDateTime.now())) {
			throw new IllegalStateException(TOKEN_EXPIRED_MSG);
		}

		confirmationTokenService.setConfirmedAt(token);
		userService.enableUser(confirmationToken.getUser().getEmail());
		return TOKEN_CONFIRMED_MSG;
	}

}
