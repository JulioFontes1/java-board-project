package com.board.dto;

import com.board.persistence.entity.BoardColumnKindEnum;

public record BoardColumnDTO(Long id,
                             String name,
                             int order,
                             BoardColumnKindEnum kind,
                             int cardsAmount) {

}
