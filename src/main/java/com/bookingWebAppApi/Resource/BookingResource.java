package com.bookingWebAppApi.Resource;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookingWebAppApi.Exception.PropertyBookingExistException;
import com.bookingWebAppApi.Model.Booking;
import com.bookingWebAppApi.Model.CheckInAndOutDate;
import com.bookingWebAppApi.Model.PaymentMethod;
import com.bookingWebAppApi.Model.Property;
import com.bookingWebAppApi.Model.Userr;
import com.bookingWebAppApi.Service.BookingService;
import com.bookingWebAppApi.Service.CheckInAndOutDateService;
import com.bookingWebAppApi.Service.PaymentMethodService;
import com.bookingWebAppApi.Service.PropertyService;
import com.bookingWebAppApi.Service.UserService;
import com.bookingWebAppApi.Utility.HttpCustomResponse;
import com.bookingWebAppApi.Utility.MailConstructor;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class BookingResource {
	@Autowired
	private PropertyService propertyService;

	@Autowired
	private UserService userService;

	@Autowired
	private BookingService bookingService;

	@Autowired
	private CheckInAndOutDateService checkInAndOutDateService;

	@Autowired
	private MailConstructor mailConstructor;

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private PaymentMethodService paymentMethodService;

	@PostMapping("/checkDateAvailability")
	public ResponseEntity<HttpCustomResponse> checkDateAvailability(@RequestParam("checkInDate") String checkInDate,
			@RequestParam("checkOutDate") String checkOutDate) throws PropertyBookingExistException {

	/*	LocalDate checkInDateLocalDate = checkInDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate checkOutDateLocalDate = checkOutDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

	*/
		
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate checkInDateLocalDate = LocalDate.parse(checkInDate, format);
		LocalDate checkOutDateLocalDate = LocalDate.parse(checkOutDate, format);
		
		List<CheckInAndOutDate> dbCheckInAndOutDateList = checkInAndOutDateService.findAll();

		for (CheckInAndOutDate dbCheckInAndOutDate : dbCheckInAndOutDateList) {
			if (dbCheckInAndOutDate.getCheckInDate().equals(checkInDateLocalDate)
					&& dbCheckInAndOutDate.getCheckOutDate().equals(checkOutDateLocalDate) ) {

				throw new PropertyBookingExistException("Date Already Exist");

			}
		}


		return response(HttpStatus.OK, "Your dates are available! Check the property to rent");
	}
	
	

	@PostMapping("/checkPropertyAvailability")
	public ResponseEntity<HttpCustomResponse> checkPropertyAvailability(@RequestParam("checkInDate") String checkInDate,
			@RequestParam("checkOutDate") String checkOutDate, @RequestParam("propertyId") Long PropertyId)
			throws PropertyBookingExistException {

	/*	LocalDate checkInDateLocalDate = checkInDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate checkOutDateLocalDate = checkOutDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		
	*/
		
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate checkInDateLocalDate = LocalDate.parse(checkInDate, format);
		LocalDate checkOutDateLocalDate = LocalDate.parse(checkOutDate, format);
		
		// Property property = propertyService.findById(PropertyId);

		List<CheckInAndOutDate> dbCheckInAndOutDateList = checkInAndOutDateService.findAll();

		for (CheckInAndOutDate dbCheckInAndOutDate : dbCheckInAndOutDateList) {

			if (dbCheckInAndOutDate.getCheckInDate().equals(checkInDateLocalDate) 
					&& dbCheckInAndOutDate.getProperty().getId().equals(PropertyId)) {

				throw new PropertyBookingExistException("Booking Already Exist");

			} 

			if (dbCheckInAndOutDate.getCheckInDate().equals(checkInDateLocalDate)
						&& dbCheckInAndOutDate.getCheckOutDate().equals(checkOutDateLocalDate)
						&& dbCheckInAndOutDate.getProperty().getId().equals(PropertyId)) {

					throw new PropertyBookingExistException("Booking Already Exist");

			}
		}

		return response(HttpStatus.OK, "The dates are available!");
	}

	@PostMapping("/newBooking")
	public ResponseEntity<Booking> newBooking(HttpServletRequest request,
			@RequestParam("bookingFirstName") String bookingFirstName,
			@RequestParam("bookingLastName") String bookingLastName,
			@RequestParam("bookingEmailAddress") String bookingEmailAddress,
			@RequestParam("bookingPhoneNumber") String bookingPhoneNumber,
			@RequestParam("bookingHomePhoneNumber") String bookingHomePhoneNumber,
			@RequestParam("bookingCountry") String bookingCountry, @RequestParam("bookingStreet") String bookingStreet,
			@RequestParam("bookingCity") String bookingCity, @RequestParam("bookingState") String bookingState,
			@RequestParam("bookingZipCode") String bookingZipCode, @RequestParam("checkInDate") Date checkInDate,
			@RequestParam("checkOutDate") Date checkOutDate, @RequestParam("bookingNoOfDays") Long bookingNoOfDays,
			@RequestParam("bookingPropertyId") Long bookingPropertyId, Principal principal,
			@RequestParam("noOfGuest") String noOfGuest, @RequestParam("noOfChildren") String noOfChildren,
			@RequestParam("pets") String pets, @RequestParam("bookingPaymentMethodId") Long bookingPaymentMethodId ) throws PropertyBookingExistException {

		Userr loginUser = userService.findByUsername(principal.getName());
		
		PaymentMethod paymentCard = paymentMethodService.getById(bookingPaymentMethodId);

		Booking booking = bookingService.createNewBooking(bookingFirstName, bookingLastName, bookingEmailAddress,
				bookingPhoneNumber, bookingHomePhoneNumber, bookingCountry, bookingStreet, bookingCity, bookingState,
				bookingZipCode, checkInDate, checkOutDate, bookingNoOfDays, bookingPropertyId, loginUser, noOfGuest, 
				noOfChildren, pets, paymentCard);

		SimpleMailMessage loginUserEmail = mailConstructor
				.constructNewBookingEmailTravellerLoginUser(request.getLocale(), booking);
		mailSender.send(loginUserEmail);

		SimpleMailMessage BookingUserEmail = mailConstructor
				.constructNewBookingEmailTravellerBookingUser(request.getLocale(), booking);
		mailSender.send(BookingUserEmail);

		SimpleMailMessage OwnerEmail = mailConstructor.constructNewBookingEmailOwner(request.getLocale(), booking);
		mailSender.send(OwnerEmail);

		return new ResponseEntity<>(booking, HttpStatus.OK);

	}

	@PostMapping("/addDates")
	public ResponseEntity<HttpCustomResponse> addCheckInAndOutDates(
			@RequestParam("checkinDateAdmin") String checkInDate,
			@RequestParam("checkoutDateAdmin") String checkOutDate, @RequestParam("propertyId") Long PropertyId) {

		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		LocalDate checkInDateLocalDate = LocalDate.parse(checkInDate, format);
		LocalDate checkOutDateLocalDate = LocalDate.parse(checkOutDate, format);

		Property property = propertyService.findById(PropertyId);

		CheckInAndOutDate checkInAndOutDate = new CheckInAndOutDate();
		checkInAndOutDate.setCheckInDate(checkInDateLocalDate);
		checkInAndOutDate.setCheckOutDate(checkOutDateLocalDate);
		checkInAndOutDate.setProperty(property);
		checkInAndOutDateService.save(checkInAndOutDate);

		return response(HttpStatus.OK, "Dates added successsfully!");
	}

	@PostMapping("/getBookingById")
	public ResponseEntity<Booking> getBookingById(@RequestParam("bookingId") Long bookingId) {

		Booking booking = bookingService.findById(bookingId);

		return new ResponseEntity<>(booking, HttpStatus.OK);

	}

	@GetMapping("/allBookingByUser")
	public ResponseEntity<List<Booking>> getAllBookingByUser(Principal principal) {

		Userr loginUser = userService.findByUsername(principal.getName());

		List<Booking> bookingList = bookingService.findByLoginUser(loginUser);

		return new ResponseEntity<List<Booking>>(bookingList, HttpStatus.OK);

	}

	@GetMapping("/allBooking")
	public ResponseEntity<List<Booking>> getAllBooking() {

		List<Booking> bookingList = bookingService.findAll();

		return new ResponseEntity<List<Booking>>(bookingList, HttpStatus.OK);

	}

	@PreAuthorize("hasAnyAuthority('user:delete')")
	@PostMapping("/searchAllBooking")
	public ResponseEntity<List<Booking>> searchBooking(@RequestParam("searchInput") String searchInput) {

		List<Booking> searchBooking = new ArrayList<>();
		List<Booking> bookingById = bookingService.findByBookingFirstNameContaining(searchInput);

		searchBooking.addAll(bookingById);

		return new ResponseEntity<>(searchBooking, HttpStatus.OK);

	}

	private ResponseEntity<HttpCustomResponse> response(HttpStatus httpStatus, String message) {

		return new ResponseEntity<>(new HttpCustomResponse(httpStatus.value(), httpStatus,
				httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase()), httpStatus);

	}

}
