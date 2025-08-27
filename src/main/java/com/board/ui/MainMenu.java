package com.board.ui;

import com.board.persistence.entity.BoardColumnEntity;
import com.board.persistence.entity.BoardColumnKindEnum;
import com.board.persistence.entity.BoardEntity;
import com.board.service.BoardQueryService;
import com.board.service.BoardService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.board.persistence.ConnectionUtil.getConnection;
import static com.board.persistence.entity.BoardColumnKindEnum.*;

public class MainMenu {

    private final Scanner scanner = new Scanner(System.in);

    public void execute() throws SQLException {
        System.out.println("Seja bem-vindo ao gerenciador de boads, selecione a opção que deseja.");
        var option = -1;
        while (true){
            System.out.println("1 - Criar novo Board");
            System.out.println("2 - Selecionar Board");
            System.out.println("3 - Excluir um Board");
            System.out.println("4 - Sair");
            option = scanner.nextInt();
            switch (option){
                case 1 -> createBoard();
                case 2 -> selectBoard();
                case 3 -> deleteBoard();
                case 4 -> System.exit(1);
                default -> System.out.println("Opção inválida!");
            }
        }
    }

    private void createBoard() throws SQLException {
        var entity = new BoardEntity();
        System.out.println("Insira o nome do seu Board");
        entity.setName(scanner.next());

        System.out.println("Seu board terá colunas além das 3 padrões? Se sim informe quantas, se não digite 0");
        var additionalColumns = scanner.nextInt();
        if(additionalColumns == 0){
            additionalColumns = 1;
        }
        List<BoardColumnEntity> columns = new ArrayList<>();

        System.out.println("Informe o nome da coluna inicial");
        var initialColumnName = scanner.next();
        var initialColumn = createColumn(initialColumnName, INITIAL, 0);
        columns.add(initialColumn);

        for (int i = 0; i < additionalColumns; i++) {
            System.out.println("Informe o nome da coluna de tarefa pendente");
            var pendingColumnName = scanner.next();
            var pendingColumn = createColumn(pendingColumnName, PENDING, i + 1);
            columns.add(pendingColumn);
        }

        System.out.println("Informe o nome da coluna final");
        var finalColumnName = scanner.next();
        var finalColumn = createColumn(finalColumnName, FINAL, additionalColumns + 1);
        columns.add(finalColumn);

        System.out.println("Informe o nome da coluna de cancelamento");
        var cancelColumnName = scanner.next();
        var cancelColumn = createColumn(cancelColumnName, CANCEL, additionalColumns + 2);
        columns.add(cancelColumn);

        entity.setBoardColumn(columns);
        try(var connection = getConnection()) {
            var service = new BoardService(connection);
            service.insert(entity);
        }
    }

    private void selectBoard() throws SQLException {
        System.out.println("Digite o ID do board que deseja selecionar.");
        var id = scanner.nextLong();
        try(var connection = getConnection()){
           var queryService = new BoardQueryService(connection);
           var optional = queryService.findById(id);
           optional.ifPresentOrElse(b -> {
               try {
                   new BoardMenu(b).execute();
               } catch (SQLException e) {
                   throw new RuntimeException(e);
               }
           }, () -> System.out.printf("O board %s não foi encontrado\n", id));
        }
    }

    private void deleteBoard() throws SQLException {
        System.out.println("Digite o ID do board que deseja deletar.");
        var id = scanner.nextLong();

        try(var connection = getConnection()) {
            var service = new BoardService(connection);
            if(service.delete(id)){
                System.out.printf("O board %s foi deletado\n", id);
            }else {
                System.out.printf("O board %s não foi encontrado\n", id);
            }
        }
    }

    private BoardColumnEntity createColumn(final String name, final BoardColumnKindEnum kind, final int order){
        var boardColumn = new BoardColumnEntity();
        boardColumn.setName(name);
        boardColumn.setKind(kind);
        boardColumn.setOrder(order);
        return boardColumn;
    }
}
