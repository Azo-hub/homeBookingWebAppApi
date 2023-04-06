package com.bookingwebapppApi.UtilityPackage;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import com.bookingwebapppApi.ModelPackage.Booking;
import com.bookingwebapppApi.ModelPackage.Userr;
import com.bookingwebapppApi.ServicePackage.UserService;

@Component
public class MailConstructor {
    @Autowired
    private Environment env;

    @Autowired
    private UserService userService;

    public SimpleMailMessage constructContactEmail(Locale locale, String Email, String Name, String Phone,
                                                   String Subject, String Content) {

        String message = "Email:" + " " + Email + "\n" + "Name:" + " " + Name + "\n" + "Phone Number:" + " " + Phone
                + "\n" + "Content:" + " " + Content;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo("devreadone@gmail.com");
        email.setSubject(Subject);
        email.setText(message);
        email.setFrom(env.getProperty("devreadone@gmail.com"));
        return email;

    }

    public SimpleMailMessage constructResetTokenEmail(Locale locale, String token, Userr user,
                                                      String password) {

        

        String message = "\nHi "+user.getFirstname()+","+"\nYour new password is: \n"
                + password;
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject("Valence Direct Booking Rental - User Password Reset Email");
        email.setText(message);
        email.setFrom(env.getProperty("devreadone@gmail.com"));
        return email;

    }

    public SimpleMailMessage constructNewUserEmail(Locale locale, String token, Userr user,
                                                   String password) {


        String message = "\nThank you for signing up " + user.getUsername() +", Your password is: \n"
                + password;
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject("Valence Direct Booking Rental - New User Email Verification");
        email.setText(message);
        email.setFrom(env.getProperty("devreadone@gmail.com"));
        return email;

    }

    public SimpleMailMessage constructNewBookingEmailTravellerLoginUser(Locale locale, Booking booking) {
        // TODO Auto-generated method stub
        String message = "\nThank you " + booking.getLoginUser().getUsername() + " for booking a property with us \n"
                + "Your Booking details are as List below:\n\n" +
                "Booking ID: " + booking.getId() + "\n\n" +
                "Property Name: " + booking.getProperty().getName() + "\n\n" +
                "Booking Guest Name: " + booking.getBookingFirstName() + " " + booking.getBookingLastName() + "\n\n"
                + "Email Address: " + booking.getBookingEmailAddress() + "\n\n"+
                "Home Phone Number: " + booking.getBookingPhonehomeNumber() + "\n\n" +
                "Cell Phone Number: " + booking.getBookingPhoneNumber() + "\n\n" +
                "Check-in Date: " + booking.getBookingCheckInDate() + "\n\n" +
                "Check-out Date: " + booking.getBookingCheckOutDate() + "\n\n" +
                "Total: " + booking.getTotalPrice();

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(booking.getLoginUser().getEmail());
        email.setSubject("Valence Direct Booking Rental - New Booking Notification");
        email.setText(message);
        email.setFrom(env.getProperty("devreadone@gmail.com"));
        return email;
    }

    public SimpleMailMessage constructNewBookingEmailTravellerBookingUser(Locale locale, Booking booking) {
        // TODO Auto-generated method stub
        String message = "\nThank you " + booking.getLoginUser().getUsername() + " for booking a property with us \n"
                + "Your Booking details are as List below:\n\n" +
                "Booking ID: " + booking.getId() + "\n\n" +
                "Property Name: " + booking.getProperty().getName() + "\n\n" +
                "Booking Guest Name: " + booking.getBookingFirstName() + " " + booking.getBookingLastName() + "\n\n"
                + "Email Address: " + booking.getBookingEmailAddress() + "\n\n"+
                "Home Phone Number: " + booking.getBookingPhonehomeNumber() + "\n\n" +
                "Cell Phone Number: " + booking.getBookingPhoneNumber() + "\n\n" +
                "Check-in Date: " + booking.getBookingCheckInDate() + "\n\n" +
                "Check-out Date: " + booking.getBookingCheckOutDate() + "\n\n" +
                "Total: " + booking.getTotalPrice();

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(booking.getBookingEmailAddress());
        email.setSubject("Valence Direct Booking Rental - New Booking Notification");
        email.setText(message);
        email.setFrom(env.getProperty("devreadone@gmail.com"));
        return email;
    }


    public SimpleMailMessage constructNewBookingEmailOwner(Locale locale, Booking booking) {
        // TODO Auto-generated method stub
        String message = "\nA user with username: " + booking.getLoginUser().getUsername() + " just book a property with us \n"
                + "Your Booking details are as List below:\n\n" +
                "Booking ID: " + booking.getId() + "\n\n" +
                "Property Name: " + booking.getProperty().getName() + "\n\n" +
                "Booking Guest Name: " + booking.getBookingFirstName() + " " + booking.getBookingLastName() + "\n\n"
                + "Email Address: " + booking.getBookingEmailAddress() + "\n\n"+
                "Home Phone Number: " + booking.getBookingPhonehomeNumber() + "\n\n" +
                "Cell Phone Number: " + booking.getBookingPhoneNumber() + "\n\n" +
                "Check-in Date: " + booking.getBookingCheckInDate() + "\n\n" +
                "Check-out Date: " + booking.getBookingCheckOutDate() + "\n\n" +
                "Total: " + booking.getTotalPrice();


        Userr propertyOwner = userService.findByUsername(booking.getProperty().getCreatedBy());

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(propertyOwner.getEmail());
        email.setSubject("Valence Direct Booking Rental - New Booking Notification");
        email.setText(message);
        email.setFrom(env.getProperty("devreadone@gmail.com"));
        return email;

    }

}
