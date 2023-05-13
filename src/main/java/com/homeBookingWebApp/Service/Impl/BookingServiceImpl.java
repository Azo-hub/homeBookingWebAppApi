package com.homeBookingWebApp.Service.Impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.homeBookingWebApp.Exception.PropertyBookingExistException;
import com.homeBookingWebApp.Model.Booking;
import com.homeBookingWebApp.Model.CheckInAndOutDate;
import com.homeBookingWebApp.Model.Property;
import com.homeBookingWebApp.Model.Userr;
import com.homeBookingWebApp.Repository.BookingRepository;
import com.homeBookingWebApp.Service.BookingService;
import com.homeBookingWebApp.Service.CheckInAndOutDateService;
import com.homeBookingWebApp.Service.PropertyService;


@Service
public class BookingServiceImpl implements BookingService  {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CheckInAndOutDateService checkInAndOutDateService;

    @Autowired
    private PropertyService propertyService;


    @Override
    public Booking findBybookingEmailAddress(String email) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Booking save(Booking booking) {
        // TODO Auto-generated method stub
        return bookingRepository.save(booking);
    }

    
	@Override
    public Booking findById(Long id) {
        // TODO Auto-generated method stub
        return bookingRepository.getReferenceById(id);
    }

    @Override
    public Booking createNewBooking(String bookingFirstName, String bookingLastName, String bookingEmailAddress,
                                    String bookingPhoneNumber, String bookingHomePhoneNumber, String bookingCountry, String bookingStreet,
                                    String bookingCity, String bookingState, String bookingZipCode, Date checkInDate, Date checkOutDate,
                                    Long bookingNoOfDays, Long bookingPropertyId, Userr user) throws PropertyBookingExistException {
        // TODO Auto-generated method stub

        LocalDate checkInDateLocalDate = checkInDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate checkOutDateLocalDate = checkOutDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        List<CheckInAndOutDate> dbCheckInAndOutDateList = checkInAndOutDateService.findAll();

        for (CheckInAndOutDate dbCheckInAndOutDate : dbCheckInAndOutDateList) {
        	
        	if (dbCheckInAndOutDate.getCheckInDate() == checkInDateLocalDate && 
					dbCheckInAndOutDate.getProperty().getId() == bookingPropertyId) {

				throw new PropertyBookingExistException("Booking Already Exist!");

			}

        	
            if (dbCheckInAndOutDate.getCheckInDate() == checkInDateLocalDate
                    && dbCheckInAndOutDate.getCheckOutDate() == checkOutDateLocalDate
                    && dbCheckInAndOutDate.getProperty().getId() == bookingPropertyId) {

                throw new PropertyBookingExistException("Booking Already Exist!");

            }
        }

        Property property = propertyService.findById(bookingPropertyId);

        Booking booking = new Booking();

        booking.setBookingCheckInDate(checkInDateLocalDate);
        booking.setBookingCheckOutDate(checkOutDateLocalDate);
        booking.setProperty(property);
        booking.setLoginUser(user);
        booking.setBookingFirstName(bookingFirstName);
        booking.setBookingLastName(bookingLastName);
        booking.setBookingEmailAddress(bookingEmailAddress);
        booking.setBookingPhonehomeNumber(bookingHomePhoneNumber);
        booking.setBookingPhoneNumber(bookingPhoneNumber);
        booking.setNoOfDays(bookingNoOfDays);
        booking.setBookingCity(bookingCity);
        booking.setBookingState(bookingState);
        booking.setBookingCountry(bookingCountry);
        booking.setBookingStreet(bookingStreet);
        booking.setBookingZipCode(bookingZipCode);

        double subPrice = bookingNoOfDays * property.getPropertyPrice();

        booking.setTotalPrice(subPrice + property.getPropertyTax() +
                property.getPropertyServiceFee() + property.getPropertyCleaningFee());

        bookingRepository.save(booking);

        /*CheckInAndOutDate checkInAndOutDate = new CheckInAndOutDate();
        checkInAndOutDate.setCheckInDate(checkInDateLocalDate);
        checkInAndOutDate.setCheckOutDate(checkOutDateLocalDate);
        checkInAndOutDate.setProperty(property);
        checkInAndOutDateService.save(checkInAndOutDate);*/

        return booking;
    }

    @Override
    public void deleteBookingById(Long Id) {
        // TODO Auto-generated method stub

    }

	@Override
	public List<Booking> findAll() {
		// TODO Auto-generated method stub
		return bookingRepository.findAll();
	}

	
	@Override
	public List<Booking> findByLoginUser(Userr loginUserId){
		
		
		return bookingRepository.findByLoginUser(loginUserId);
	}
	
	
	@Override
    public List<Booking> findByBookingFirstNameContaining(String searchInput) {

		List<Booking> bookingList = bookingRepository.findByBookingFirstNameContaining(searchInput);

				
		return bookingList;

		
    }

	
}
