package com.example.stock.facade;

import com.example.stock.repository.LockRepository;
import com.example.stock.service.NamedLockStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class NamedLockStockFacade {

    private final LockRepository lockRepository;
    private final NamedLockStockService namedLockStockService;

    public void decrease(final Long id, final Long quantity) {
        try {
            lockRepository.getLock(id.toString());
            namedLockStockService.decrease(id, quantity);
        } finally {
            lockRepository.releaseLock(id.toString());
        }
    }

}
