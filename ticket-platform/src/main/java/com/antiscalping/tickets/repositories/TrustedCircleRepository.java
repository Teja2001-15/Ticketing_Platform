package com.antiscalping.tickets.repositories;

import com.antiscalping.tickets.entities.TrustedCircle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrustedCircleRepository extends JpaRepository<TrustedCircle, Long> {
    
    List<TrustedCircle> findByUserId(Long userId);
    
    Optional<TrustedCircle> findByUserIdAndTrustedUserId(Long userId, Long trustedUserId);
    
    boolean existsByUserIdAndTrustedUserId(Long userId, Long trustedUserId);
}
