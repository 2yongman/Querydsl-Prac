package com.codestates.order.repository;

import com.codestates.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> { // 수정된 부분

}
