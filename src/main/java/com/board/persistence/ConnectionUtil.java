package com.board.persistence;

import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ConnectionUtil {
    public static Connection getConnection () throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://127.0.0.1/board?useSSL=false", "root", "root");
    }
}
