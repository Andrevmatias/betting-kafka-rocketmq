package com.betting.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.betting.data.model.Bet;

@Repository
public interface BetRepository extends JpaRepository<Bet, Long> {

	List<Bet> findByEventId(Long eventId);

}
