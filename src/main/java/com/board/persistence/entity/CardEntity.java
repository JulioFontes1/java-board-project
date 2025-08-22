package com.board.persistence.entity;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class CardEntity {
    private long id;
    private String title;
    private String description;
}
