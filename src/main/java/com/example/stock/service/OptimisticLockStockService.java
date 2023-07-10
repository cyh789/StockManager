package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OptimisticLockStockService {

    private final StockRepository stockRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public void decrease(final Long id, final Long quantity) {
        Stock stock = stockRepository.findByIdWithOptimisticLock(id);
        stock.decrease(quantity);
    }
}
