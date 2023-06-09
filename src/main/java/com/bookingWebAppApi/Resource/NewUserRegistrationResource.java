package com.bookingWebAppApi.Resource;

import java.util.Locale;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookingWebAppApi.Exception.EmailExistException;
import com.bookingWebAppApi.Exception.InvalidTokenException;
import com.bookingWebAppApi.Exception.UserNotFoundException;
import com.bookingWebAppApi.Exception.UsernameExistException;
import com.bookingWebAppApi.Model.Userr;
import com.bookingWebAppApi.Service.UserSecurityService;
import com.bookingWebAppApi.Service.UserService;
import com.bookingWebAppApi.Utility.MailConstructor;
import com.bookingWebAppApi.Utility.PasswordResetToken;
import com.bookingWebAppApi.Utility.SecurityUtility;

@RestController
public class NewUserRegistrationResource {
	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private MailConstructor mailConstructor;

	@Autowired
	private UserService userService;

	@Autowired
	private UserSecurityService userSecurityService;

	@GetMapping("/newUser")
	public ResponseEntity<Userr> newUser(Locale locale, @RequestParam("token") String token)
			throws InvalidTokenException {
		PasswordResetToken passToken = userService.getPasswordResetToken(token);

		if (passToken == null) {
			throw new InvalidTokenException("Invalid Request");
		}

		Userr user = passToken.getUser();
		String username = user.getUsername();
		UserDetails userDetails = userSecurityService.loadUserByUsername(username);

		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
				userDetails.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authentication);

		Userr loginUser = userService.findByUsername(authentication.getName());

		return new ResponseEntity<>(loginUser, HttpStatus.OK);
	}

	@PostMapping("/newUser")
	public ResponseEntity<Userr> newUserPost(HttpServletRequest request,@RequestBody Userr user)
			throws UsernameExistException, UserNotFoundException, EmailExistException {

		Userr newUser = userService.createUser(user.getEmail(), user.getUsername(), user.getRole());

		String password = SecurityUtility.randomPassword();
		String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);
		newUser.setPassword(encryptedPassword);
		
		
		/* Capitalizing first letter of the first name */

		if (!user.getFirstname().trim().isEmpty()) {

			char FirstName_FirstChar = user.getFirstname().trim().charAt(0);

			String UpperCase_FirstName_FirstChar = Character.toString(FirstName_FirstChar).toUpperCase();

			String FirstName_RemainingChar = user.getFirstname().trim().substring(1).toLowerCase();

			String FirstName = UpperCase_FirstName_FirstChar.concat(FirstName_RemainingChar);

			newUser.setFirstname(FirstName);

		}

		/* Capitalizing first letter of the last name */

		if (!user.getLastname().trim().isEmpty()) {

			char LastName_FirstChar = user.getLastname().trim().charAt(0);

			String UpperCase_LastName_FirstChar = Character.toString(LastName_FirstChar).toUpperCase();

			String LastName_RemainingChar = user.getLastname().trim().substring(1).toLowerCase();

			String LastName = UpperCase_LastName_FirstChar.concat(LastName_RemainingChar);

			newUser.setLastname(LastName);

		}

		
		userService.save(newUser);

		String token = UUID.randomUUID().toString();
		userService.createPasswordResetTokenForUser(newUser, token);


		
		mailSender.send(mailConstructor.constructNewUserEmail(Locale.ENGLISH, token, newUser,
				password)); 
		
		

		return new ResponseEntity<>(newUser, HttpStatus.OK);

	}


}
