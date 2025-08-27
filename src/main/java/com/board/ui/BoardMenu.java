package com.board.ui;

import com.board.persistence.entity.BoardColumnEntity;
import com.board.persistence.entity.BoardColumnKindEnum;
import com.board.persistence.entity.BoardEntity;
import com.board.persistence.entity.CardEntity;
import com.board.service.BoardColumnQueryService;
import com.board.service.BoardQueryService;
import com.board.service.CardQueryService;
import com.board.service.CardService;
import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.util.Scanner;

import static com.board.persistence.ConnectionUtil.getConnection;
import static com.board.persistence.entity.BoardColumnKindEnum.INITIAL;

@AllArgsConstructor
public class BoardMenu {
   private final BoardEntity entity;
    private final Scanner scanner = new Scanner(System.in);

    public void execute() throws SQLException {
        System.out.printf("Bem vindo ao Board %s, selecione a opção que deseja.\n", entity.getId());

        var option = -1;
        while (option != 9) {
            System.out.println("1 - Criar um card");
            System.out.println("2 - Mover um card");
            System.out.println("3 - Bloquear um card");
            System.out.println("4 - Desloquear um card");
            System.out.println("5 - Cancelar um card");
            System.out.println("6 - Visualizar Board");
            System.out.println("7 - Visualizar colunas com card");
            System.out.println("8 - Visualizar card");
            System.out.println("9 - Voltar ao menu");
            System.out.println("10 - Sair");
            option = scanner.nextInt();
            switch (option) {
                case 1 -> createCard();
                case 2 -> moveCard();
                case 3 -> blockCard();
                case 4 -> unblockCard();
                case 5 -> cancelCard();
                case 6 -> showBoard();
                case 7 -> showColumn();
                case 8 -> showCard();
                case 9 -> System.out.println("Voltado ao menu anteriaor");
                case 10 -> System.exit(1);
                default -> System.out.println("Opção inválida!");
            }
        }
    }
    private void createCard() throws SQLException {
        var card = new CardEntity();
        System.out.println("Informe o título do card");
        card.setTitle(scanner.next());
        System.out.println("Informe a descrição do card");
        card.setDescription(scanner.next());
        var initialColumn =entity.getBoardColumn().stream()
                .filter(bc -> bc.getKind().equals(INITIAL))
                .findFirst().orElseThrow();
        card.setBoardColumn(initialColumn);
        try (var connection = getConnection()){
            new CardService(connection).insert(card);
        }
    }

    private void moveCard() {
    }

    private void blockCard() {
    }

    private void unblockCard() {
    }

    private void cancelCard() {
    }

    private void showBoard() throws SQLException {
        try(var connection = getConnection()){
            var optioal = new BoardQueryService(connection).showBoardDetails(entity.getId());
            optioal.ifPresent(b -> {
                System.out.printf("Board [%s, %s]\n", b.id(), b.name());
            });
        }
    }

    private void showColumn() throws SQLException {
        var columnsIds = entity.getBoardColumn().stream().map(BoardColumnEntity::getBoard).toList();
        var selectedColumn = -1L;
        while (!columnsIds.contains(selectedColumn)){
            System.out.printf("Escolha uma coluna do Board %s\n", entity.getName());
            entity.getBoardColumn().forEach(c -> System.out.printf("%s - %s [%s]\n", c.getId(), c.getName(), c.getKind()));
            selectedColumn = scanner.nextLong();
        }
        try(var connection = getConnection()) {
            var column = new BoardColumnQueryService(connection).findById(selectedColumn);
            column.ifPresent(co -> {
                System.out.printf("Coluna %s Tipo %s", co.getName(), co.getKind());
                            co.getCards().forEach(ca -> System.out.printf("Card: %s - %s\nDescrição: %s\n",
                                    ca.getId(), ca.getTitle(), ca.getDescription()));
            });
        }
    }

    private void showCard() throws SQLException {
        System.out.println("Digite o ID do card que deseja visualizar");
        var selectedCardId = scanner.nextLong();

        try(var connection = getConnection()) {
            new CardQueryService(connection).findById(selectedCardId)
                    .ifPresentOrElse(c -> {
                                System.out.printf("Card: %s - %s\n", c.id(), c.title());
                                System.out.printf("Descrição: %s\n", c.description());
                                System.out.println(c.blocked() ? "Está bloqueado. Motivo: " + c.blockReason() : "Não está bloqueado");
                                System.out.printf("Foi blqueado %s vezes\n", c.blockAmount());
                                System.out.printf("No momento se enconntra na coluna %s - %s\n", c.columnId(), c.columnName());
                            },
                            () -> System.out.printf("Não existe card com id %s\n", selectedCardId));
        }
    }
}

