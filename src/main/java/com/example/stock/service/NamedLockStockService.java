package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NamedLockStockService {

    private final StockRepository stockRepository;

    //부모의 트랜잭션과 별도로 실행되어야 함
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decrease(final Long id, final Long quantity) {
        Stock stock = stockRepository.findById(id).orElseThrow();
        stock.decrease(quantity);
        stockRepository.saveAndFlush(stock);
    }
}
