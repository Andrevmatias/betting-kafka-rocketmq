package com.betting.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.betting.data.model.Bet;
import com.betting.data.model.BetStatus;

@Repository
public interface BetRepository extends JpaRepository<Bet, Long> {

	List<Bet> findByEventIdAndStatus(Long eventId, BetStatus status);

	@Modifying
	@Query("UPDATE Bet b SET b.status = :newStatus WHERE b.id = :betId AND b.status = 'PENDING'")
	int settleIfPending(Long betId, BetStatus newStatus);

}
