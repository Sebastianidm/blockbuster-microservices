package com.blockbuster.transactions.repository;

import com.blockbuster.transactions.model.entity.RentalDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalDetailRepository extends JpaRepository<RentalDetail, Long> {
}