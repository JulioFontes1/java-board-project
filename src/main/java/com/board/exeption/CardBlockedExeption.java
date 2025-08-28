package com.board.exeption;

public class CardBlockedExeption extends RuntimeException{

    public CardBlockedExeption(String message) {
        super(message);
    }
}
