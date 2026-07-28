package com.talan.tradeguard.repository;

import com.talan.tradeguard.model.TradeOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TradeOrderRepository extends JpaRepository<TradeOrder, Long> {
    
    @Query("SELECT t FROM TradeOrder t WHERE t.traderName = :trader")
    List<TradeOrder> findByTraderName(@Param("trader") String trader);
}