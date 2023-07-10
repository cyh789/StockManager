package com.example.stock.repository;

import com.example.stock.domain.Stock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockRepository extends JpaRepository<Stock, Long> {

    @Query("SELECT s FROM Stock s WHERE s.productId = :productId")
    Stock findByProductId(@Param("productId") final Long productId);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Stock s WHERE s.id = :id")
    Stock findByIdWithPessimisticLock(@Param("id") final Long id);

    @Lock(value = LockModeType.OPTIMISTIC)
    @Query("SELECT s FROM Stock s WHERE s.id = :id")
    Stock findByIdWithOptimisticLock(@Param("id") final Long id);
}
