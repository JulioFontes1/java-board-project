package com.board.service;

import com.board.dto.BoardColumnInfoDTO;
import com.board.exeption.CardBlockedExeption;
import com.board.exeption.EntityNotFoundExeption;
import com.board.persistence.dao.BlockDAO;
import com.board.persistence.dao.CardDAO;
import com.board.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static com.board.persistence.entity.BoardColumnKindEnum.CANCEL;
import static com.board.persistence.entity.BoardColumnKindEnum.FINAL;

@AllArgsConstructor
public class CardService {
    private final Connection connection;

    public CardEntity insert(final CardEntity entity) throws SQLException{
        try {
            var dao = new CardDAO(connection);
            dao.insert(entity);
            connection.setAutoCommit(false);
            connection.commit();
            return entity;
        }catch (SQLException ex){
            connection.rollback();
            throw ex;
        }
    }

    public void moveToNextColumn(final Long cardId, List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException{
        try {
            connection.setAutoCommit(false);
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundExeption("O card com id %s não foi encontrado".formatted(cardId))
            );
            if(dto.blocked()){
                throw new CardBlockedExeption("O card de id %s está bloqueado".formatted(cardId));
            }

            var currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board"));
            if(currentColumn.kind().equals(FINAL)){
                throw new CardBlockedExeption("O card já foi finalizado");
            }
            var nextColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.order() == currentColumn.order() + 1).findFirst()
                    .orElseThrow(() -> new IllegalStateException("O card selecionado está cancelado"));
            dao.moveToColumn(nextColumn.id(), cardId);
            connection.commit();
        }catch (SQLException ex){
                connection.rollback();
            throw ex;
        }
    }

    public void cancel(final Long cardId, final Long cancelColumnId, List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            connection.setAutoCommit(false);
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundExeption("O card com id %s não foi encontrado".formatted(cardId))
            );
            if(dto.blocked()){
                throw new CardBlockedExeption("O card de id %s está bloqueado".formatted(cardId));
            }
            var currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board"));
            if(currentColumn.kind().equals(FINAL)){
                throw new CardBlockedExeption("O card já foi finalizado");
            }
            boardColumnsInfo.stream()
                    .filter(bc -> bc.order() == currentColumn.order() + 1).findFirst()
                    .orElseThrow(() -> new IllegalStateException("O card selecionado está cancelado"));
            dao.moveToColumn(cancelColumnId, cardId);
            connection.commit();
        }catch (SQLException ex){
            connection.rollback();
            throw ex;
        }
    }


    public void block(final Long id, final String reason, List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            connection.setAutoCommit(false);

            var dao = new CardDAO(connection);
            var blockDAO = new BlockDAO(connection);
            var optional = dao.findById(id);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundExeption("O card com id %s não foi encontrado".formatted(id))
            );
            if(dto.blocked()){
                throw new CardBlockedExeption("O card de id %s já está bloqueado".formatted(id));
            }
            var currentColumn = boardColumnsInfo.stream().filter(bd -> bd.id().equals(dto.columnId())).findFirst().orElseThrow();
            if(currentColumn.kind().equals(FINAL) || currentColumn.kind().equals(CANCEL)){
                throw new IllegalStateException("O card não pode ser bloqueado, pois está na coluna %s".formatted(currentColumn.kind()));
            }
            blockDAO.block(reason, id);
            connection.commit();
        }catch (SQLException ex){
            connection.rollback();
            throw ex;
        }
    }

    public void unblock(final Long id, final String reason) throws SQLException {
        try {
            connection.setAutoCommit(false);


            var dao = new CardDAO(connection);
            var blockDAO = new BlockDAO(connection);
            var optional = dao.findById(id);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundExeption("O card com id %s não foi encontrado".formatted(id))
            );
            if(!dto.blocked()){
                throw new CardBlockedExeption("O card de id %s não está bloqueado".formatted(id));
            }

            blockDAO.unblock(reason, id);
            connection.commit();
        }catch (SQLException ex){
            connection.rollback();
            throw ex;
        }
    }
}
