package com.board.dto;

import com.board.persistence.entity.BoardColumnEntity;

import java.util.List;

public record BoardDetailsDTO(long id, String name, List<BoardColumnEntity> columns) {
}
