package com.example.stock.facade;

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
class LettuceLockStockFacadeTest {

    @Autowired
    LettuceLockStockFacade lettuceLockStockFacade;

    @Autowired
    StockRepository stockRepository;

    @AfterEach
    void tearDown() {
        stockRepository.deleteAllInBatch();
    }

    @Test
    void LettuceLock_재고를_감소한다() {
        //given
        stockRepository.saveAndFlush(new Stock(600L, 100L));
        Stock stock = stockRepository.findByProductId(600L);

        //when
        try {
            lettuceLockStockFacade.decrease(stock.getId(), 1L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //then
        Stock result = stockRepository.findById(stock.getId()).orElseThrow();
        assertThat(result.getQuantity()).isEqualTo(99L);
    }

    @Test
    void LettuceLock_동시에_100건을_요청한다() throws InterruptedException {
        //given
        stockRepository.saveAndFlush(new Stock(601L, 100L));
        Stock stock = stockRepository.findByProductId(601L);

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        //when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    lettuceLockStockFacade.decrease(stock.getId(), 1L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
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