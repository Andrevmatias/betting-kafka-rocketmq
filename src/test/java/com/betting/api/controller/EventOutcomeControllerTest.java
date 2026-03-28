package com.betting.api.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.betting.service.EventOutcomeService;

@WebMvcTest(EventOutcomeController.class)
class EventOutcomeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private EventOutcomeService eventOutcomeService;

	@Test
	void publishOutcome_validRequest_returns202() throws Exception {
		mockMvc.perform(post("/api/v1/eventOutcomes")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
									"eventId": 1,
									"eventName": "Match A vs B",
									"eventWinnerId": 2
								}
								"""))
				.andExpect(status().isAccepted());

		verify(eventOutcomeService).publishOutcome(1L, 2L);
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"""
		{"eventId": 1, "eventName": "", "eventWinnerId": 2}
		""",
		"""
		{"eventId": -1, "eventName": "Match A vs B", "eventWinnerId": 2}
		""",
		"""
		{"eventId": 1, "eventName": "Match A vs B", "eventWinnerId": -1}
		""",
		"{ invalid json }",
		"",
		"""
		{"eventId": "not-a-number", "eventName": "Match A vs B", "eventWinnerId": 2}
		"""
	})
	void publishOutcome_invalidRequest_returns400(String body) throws Exception {
		mockMvc.perform(post("/api/v1/eventOutcomes")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(eventOutcomeService);
	}
}
