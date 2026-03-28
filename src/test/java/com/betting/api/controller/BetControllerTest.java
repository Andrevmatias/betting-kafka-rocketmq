package com.betting.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.betting.data.model.Bet;
import com.betting.service.BetService;

@WebMvcTest(BetController.class)
class BetControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BetService betService;

	@Test
	void getAllBets_returnsOkWithBets() throws Exception {
		Bet bet = Bet.builder()
				.id(1L)
				.userId(1L)
				.eventId(1L)
				.eventMarketId(1L)
				.eventWinnerId(1L)
				.amount(BigDecimal.TEN)
				.build();
		when(betService.getAllBets()).thenReturn(List.of(bet));

		mockMvc.perform(get("/api/v1/bet"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1));
	}

	@Test
	void getAllBets_emptyList_returnsOkWithEmptyArray() throws Exception {
		when(betService.getAllBets()).thenReturn(List.of());

		mockMvc.perform(get("/api/v1/bet"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isEmpty());
	}
}
