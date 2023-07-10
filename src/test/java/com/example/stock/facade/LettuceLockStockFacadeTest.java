package com.example.stock.facade;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LettuceLockStockFacadeTest {

    @Autowired
    LettuceLockStockFacade lettuceLockStockFacade;

    @Autowired
    StockRepository stockRepository;

    @BeforeEach
    void setUp() {
        Stock stock = new Stock(1L, 100L);
        stockRepository.saveAndFlush(stock);
    }

    @AfterEach
    void tearDown() {
        stockRepository.deleteAllInBatch();
    }

    @Test
    void LettuceLock_재고를_감소한다() {
        //given
        //when
        try {
            lettuceLockStockFacade.decrease(1L, 1L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //then
        Stock stock = stockRepository.findById(1L).orElseThrow();
        assertThat(stock.getQuantity()).isEqualTo(99L);
    }

    @Test
    void LettuceLock_동시에_100건을_요청한다() throws InterruptedException {
        //given
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        //when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    lettuceLockStockFacade.decrease(1L, 1L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        //then
        Stock stock = stockRepository.findById(1L).orElseThrow();
        assertThat(stock.getQuantity()).isEqualTo(0L);
    }
}