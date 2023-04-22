package com.bookingwebapppApi.RepositoryPackage;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookingwebapppApi.ModelPackage.Property;
import com.bookingwebapppApi.ModelPackage.Review;

@Repository
public interface ReviewRepository extends JpaRepository <Review, Long> {

	List<Review> findByProperty(Property property);

	void deleteByProperty(Property property);

}
