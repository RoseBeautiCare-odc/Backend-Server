package com.rosebeauticare.rosebeauticare.Repository;

import com.rosebeauticare.rosebeauticare.Model.Measurement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeasurementRepository extends MongoRepository<Measurement, String> {
	List<Measurement> findByCustomerId(String customerId);
	List<Measurement> findByCustomerIdAndServiceType(String customerId, String serviceType);
}
