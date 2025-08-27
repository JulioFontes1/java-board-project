package com.board.persistence.dao;

import com.board.dto.BoardColumnDTO;
import com.board.persistence.entity.BoardColumnEntity;
import com.board.persistence.entity.CardEntity;
import com.mysql.cj.jdbc.StatementImpl;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.board.persistence.entity.BoardColumnKindEnum.findByName;
import static java.util.Objects.isNull;

@AllArgsConstructor
public class BoardColumnDAO {

    private final Connection connection;
    public BoardColumnEntity insert(final BoardColumnEntity entity) throws SQLException {
        connection.setAutoCommit(false);
        var sql = "INSERT INTO BOARDS_COLUMNS (name, `order`, kind, board_id) VALUES (?, ?, ?, ?)";
        try(var statement = connection.prepareStatement(sql)){
            var i = 1;
            statement.setString(i++, entity.getName());
            statement.setInt(i++, entity.getOrder());
            statement.setString(i++, entity.getKind().name());
            statement.setLong(i++, entity.getBoard().getId());
            statement.executeUpdate();
            connection.commit();
            if(statement instanceof StatementImpl impl){
                entity.setId(impl.getLastInsertID());
            }
            return entity;
        }
    }

    public List<BoardColumnEntity> findByBoardId(long boardId) throws SQLException {
        List<BoardColumnEntity> entities = new ArrayList<>();
        connection.setAutoCommit(false);
        var sql = "SELECT id, name, `order`, kind FROM BOARDS_COLUMNS WHERE board_id = ? ORDER BY `order`";

        try (var statement = connection.prepareStatement(sql)){
            statement.setLong(1, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            while (resultSet.next()){
                var entity = new BoardColumnEntity();
                entity.setId(resultSet.getLong("id"));
                entity.setName(resultSet.getString("name"));
                entity.setOrder(resultSet.getInt("order"));
                entity.setKind(findByName(resultSet.getString("kind")));
                entities.add(entity);
                connection.commit();
            }
            return entities;
        }
    }


    public List<BoardColumnDTO> findByBoardIdWithDetails(long boardId) throws SQLException {
        List<BoardColumnDTO> dtos = new ArrayList<>();
        connection.setAutoCommit(false);
        var sql = "SELECT bc.id, bc.name, bc.`order`, bc.kind, (SELECT COUNT(c.id) FROM CARDS c WHERE c.board_column_id = bc.id) cards_amount FROM BOARDS_COLUMNS bc WHERE board_id = ? ORDER BY `order`;";

        try (var statement = connection.prepareStatement(sql)){
            statement.setLong(1, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            while (resultSet.next()){
                var dto = new BoardColumnDTO(
                        resultSet.getLong("bc.id"),
                        resultSet.getString("bc.name"),
                        resultSet.getInt("bc.order"),
                        findByName(resultSet.getString("bc.kind")),
                        resultSet.getInt("cards_amount")
                );

                dtos.add(dto);
                connection.commit();
            }
            return dtos;
        }
    }


    public Optional<BoardColumnEntity> findById(long boardId) throws SQLException {
        List<BoardColumnEntity> entities = new ArrayList<>();
        connection.setAutoCommit(false);
        var sql = "SELECT bc.name, bc.kind, c.id, c.title, c.description FROM BOARDS_COLUMNS bc LEFT JOIN CARDS c ON c.board_column_id = bc.id WHERE bc.id = ?`";

        try (var statement = connection.prepareStatement(sql)){
            statement.setLong(1, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if(resultSet.next()){
                var entity = new BoardColumnEntity();
                entity.setName(resultSet.getString("bc.name"));
                entity.setKind(findByName(resultSet.getString("bc.kind")));
                do {
                    if(isNull(resultSet.getString("c.title"))){
                        break;
                    }
                    var card = new CardEntity();
                    card.setId(resultSet.getLong("c.id"));
                    card.setTitle(resultSet.getString("c.title"));
                    card.setDescription(resultSet.getString("c.description"));
                    entity.getCards().add(card);
                }while (resultSet.next());
                return Optional.of(entity);
            }

            return Optional.empty();
        }
    }
}
