package com.bookingWebAppApi.Service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookingWebAppApi.Model.PaymentMethod;
import com.bookingWebAppApi.Repository.PaymentMethodRepository;
import com.bookingWebAppApi.Service.PaymentMethodService;

@Service
public class PaymentMethodServiceImpl implements PaymentMethodService {

	@Autowired
	private PaymentMethodRepository paymentMethodRepository;
	
	@Override
	public void save(PaymentMethod paymentMethod) {
		// TODO Auto-generated method stub
		paymentMethodRepository.save(paymentMethod);
	}
	
	@Override
	public PaymentMethod getById(Long id) {
		// TODO Auto-generated method stub
		return paymentMethodRepository.getReferenceById(id);
	}
	
	@Override
	public List<PaymentMethod> getAllPaymentMethod() {
		// TODO Auto-generated method stub
		return paymentMethodRepository.findAll();
	}
	
	@Override
	public void removeCard(Long id) {
		
		paymentMethodRepository.deleteById(id);
	}

}
