package com.example.stock.service;

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
class SynchronizedStockServiceTest {

    @Autowired
    private SynchronizedStockService synchronizedStockService;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    void setUp() {
        stockRepository.saveAndFlush(new Stock(200L, 100L));
    }

    @AfterEach
    void tearDown() {
        stockRepository.deleteAllInBatch();
    }

    @Test
    void synchronized_동시에_100건을_요청한다() throws InterruptedException {
        //given
        Stock stock = stockRepository.findByProductId(200L);

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        //when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    synchronizedStockService.decrease(stock.getId(), 1L);
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