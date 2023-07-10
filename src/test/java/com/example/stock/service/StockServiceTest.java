package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StockServiceTest {

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    void setUp() {
        stockRepository.saveAndFlush(new Stock(100L, 100L));
    }

    @AfterEach
    void tearDown() {
        stockRepository.deleteAllInBatch();
    }

    @Test
    void 재고를_감소한다() {
        //given
        Stock stock = stockRepository.findByProductId(100L);

        //when
        stockService.decrease(stock.getId(), 1L);

        //then
        Stock result = stockRepository.findById(stock.getId()).orElseThrow();
        assertThat(result.getQuantity()).isEqualTo(99L);
    }
}