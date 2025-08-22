package com.board.persistence.entity;

import lombok.Data;

@Data
public class BoardColumnEntity {
    private long id;
    private String name;
    private int order;
    private BoardColumnKindEnum kind;
    private String description;
    private BoardEntity board = new BoardEntity();
}
