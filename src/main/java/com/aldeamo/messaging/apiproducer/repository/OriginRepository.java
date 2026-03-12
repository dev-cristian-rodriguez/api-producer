package com.aldeamo.messaging.apiproducer.repository;

import com.aldeamo.messaging.apiproducer.entity.Origin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OriginRepository extends JpaRepository<Origin, Long> {

    Optional<Origin> findByPhoneNumberAndActiveTrue(String phoneNumber);

    boolean existsByPhoneNumberAndActiveTrue(String phoneNumber);
}
