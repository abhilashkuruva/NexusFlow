package com.nexusflow.repository;

import com.nexusflow.entity.DemandHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemandHistoryRepository extends JpaRepository<DemandHistory, Long> {
    List<DemandHistory> findTop3ByProductIdOrderByMonthDateDesc(Long productId);
}