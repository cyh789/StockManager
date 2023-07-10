package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PessimisticLockStockServiceTest {

    @Autowired
    private PessimisticLockStockService pessimisticLockStockService;

    @Autowired
    private StockRepository stockRepository;

    @AfterEach
    void tearDown() {
        stockRepository.deleteAllInBatch();
    }

    @Test
    void PessimisticLock_재고를_감소한다() {
        //given
        stockRepository.saveAndFlush(new Stock(300L, 100L));
        Stock stock = stockRepository.findByProductId(300L);

        //when
        pessimisticLockStockService.decrease(stock.getId(), 1L);

        //then
        Stock result = stockRepository.findById(stock.getId()).orElseThrow();
        assertThat(result.getQuantity()).isEqualTo(99L);
    }

    @Test
    void PessimisticLock_동시에_100건을_요청한다() throws InterruptedException {
        //given
        stockRepository.saveAndFlush(new Stock(301L, 100L));
        Stock stock = stockRepository.findByProductId(301L);

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        //when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pessimisticLockStockService.decrease(stock.getId(), 1L);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        //then
        Stock result = stockRepository.findById(stock.getId()).orElseThrow();
        assertThat(result.getQuantity()).isEqualTo(0L);
    }
}