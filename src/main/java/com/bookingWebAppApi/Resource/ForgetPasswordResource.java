package com.bookingWebAppApi.Resource;

import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookingWebAppApi.Exception.UserNotFoundException;
import com.bookingWebAppApi.Model.Userr;
import com.bookingWebAppApi.Service.UserService;
import com.bookingWebAppApi.Utility.HttpCustomResponse;
import com.bookingWebAppApi.Utility.MailConstructor;
import com.bookingWebAppApi.Utility.SecurityUtility;

@RestController
public class ForgetPasswordResource {
	
	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private MailConstructor mailConstructor;

	@Autowired
	private UserService userService;


	@PostMapping("/forgetPassword")
	public ResponseEntity<HttpCustomResponse> forgetPassword(HttpServletRequest request,
			@RequestParam("email") String email) throws UserNotFoundException {

		Userr user = userService.findByEmail(email);

		if (user == null) {

			throw new UserNotFoundException("User not found");

		}

		String password = SecurityUtility.randomPassword();
		String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);
		user.setPassword(encryptedPassword);

		userService.save(user);

		String token = UUID.randomUUID().toString();
		userService.createPasswordResetTokenForUser(user, token);

		
		SimpleMailMessage newEmail = mailConstructor.constructResetTokenEmail(request.getLocale(), token, user,
				password);

		mailSender.send(newEmail);

		return response(HttpStatus.OK, "Password Reset Successfully! Check your registered email.");
	}

	private ResponseEntity<HttpCustomResponse> response(HttpStatus httpStatus, String message) {

		return new ResponseEntity<>(new HttpCustomResponse(httpStatus.value(), httpStatus,
				httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase()), httpStatus);

	}


}
