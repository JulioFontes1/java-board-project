package com.board.service;

import com.board.persistence.dao.BoardColumnDAO;
import com.board.persistence.dao.BoardDAO;
import com.board.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;

@AllArgsConstructor
public class BoardService {
    private final Connection connection;

    public BoardEntity insert(final BoardEntity entity) throws SQLException{
        var dao = new BoardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);


        try {
            dao.insert(entity);
            var columns = entity.getBoardColumn().stream().map(c -> {
                c.setBoard(entity);
                return c;
            }).toList();

            for (var column : columns){
                boardColumnDAO.insert(column);
            }
        }catch (SQLException e){
            connection.rollback();
            e.printStackTrace();
        }

        return entity;
    }

    public boolean delete(final Long id) throws SQLException{
        var dao = new BoardDAO(connection);
        try{
            if(!dao.exists(id)){
                return false;
            };
            dao.delete(id);
            return true;
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
        return true;
    }
}
