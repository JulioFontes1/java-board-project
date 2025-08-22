package com.board;


import com.board.ui.MainMenu;
import org.flywaydb.core.Flyway;

import java.sql.SQLException;


public class Main {
    public static void main(String[] args) throws SQLException {
        var flyway = Flyway.configure()
                .dataSource(
                        "jdbc:mysql://127.0.0.1:3306/board?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                        "root",
                        "root")
                .load();
        flyway.migrate();
        new MainMenu().execute();
    }

}