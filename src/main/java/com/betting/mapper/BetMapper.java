package com.betting.mapper;

import org.mapstruct.Mapper;

import com.betting.api.model.BetResponse;
import com.betting.data.model.Bet;
import com.betting.service.model.BetDto;

@Mapper(componentModel = "spring")
public interface BetMapper {

	BetDto toDto(Bet bet);

	BetResponse toResponse(BetDto betDto);
}
