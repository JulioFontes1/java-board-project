package com.board.exeption;

public class EntityNotFoundExeption extends RuntimeException{

    public EntityNotFoundExeption(String message) {
        super(message);
    }
}
