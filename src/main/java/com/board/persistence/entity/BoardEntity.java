package com.board.persistence.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import static com.board.persistence.entity.BoardColumnKindEnum.CANCEL;

@Data
public class BoardEntity {
    private long id;
    private String name;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<BoardColumnEntity> boardColumn = new ArrayList<>();

    public BoardColumnEntity getCancelColumn(){
        return boardColumn.stream().filter(bc -> bc.getKind().equals(CANCEL)).findFirst().orElseThrow();
    }
}
