package dev.tim.discordbot.database;

import dev.tim.discordbot.DiscordBot;
import dev.tim.discordbot.model.Warning;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private final DiscordBot bot;

    public Database(DiscordBot bot){
        this.bot = bot;
    }

    private Connection connection;

    public Connection getConnection() throws SQLException {

        if(connection != null){
            return connection;
        }

        String url = "jdbc:mysql://" + bot.getConfig().get("DATABASE_HOST") + "/" + bot.getConfig().get("DATABASE_NAME");
        String user = bot.getConfig().get("DATABASE_USER");
        String password = bot.getConfig().get("DATABASE_PASSWORD");

        connection = DriverManager.getConnection(url, user, password);

        return connection;
    }

    public void initializeDatabase() throws SQLException {

        Statement statement = getConnection().createStatement();
        String sql = "CREATE TABLE IF NOT EXISTS warnings(id varchar(36) primary key, member_id long, member_warner_id long, reason varchar(100), date_created DATE)";
        statement.execute(sql);

        statement.close();

    }

    public void createWarning(Warning warning) throws SQLException {

        PreparedStatement statement = getConnection()
                .prepareStatement("INSERT INTO warnings(id, member_id, member_warner_id, reason, date_created) VALUES (?, ?, ?, ?, ?)");

        statement.setString(1, warning.getId());
        statement.setLong(2, warning.getMember_id());
        statement.setLong(3, warning.getMember_warner_id());
        statement.setString(4, warning.getReason());
        statement.setDate(5, new Date(warning.getDate().getTime()));

        statement.executeUpdate();
        statement.close();

    }

    public List<Warning> findWarningsByMember(long memberId) throws SQLException {

        PreparedStatement statement = getConnection()
                .prepareStatement("SELECT * FROM warnings WHERE member_id = ?");
        statement.setLong(1, memberId);

        ResultSet results = statement.executeQuery();

        List<Warning> warnings = new ArrayList<>();

        while (results.next()){

            String id = results.getString("id");
            long memberWarnerId = results.getLong("member_warner_id");
            String reason = results.getString("reason");
            Date date = results.getDate("date_created");

            Warning warning = new Warning(id, memberId, memberWarnerId, reason, date);
            warnings.add(warning);
        }

        statement.close();
        return warnings;
    }

    public Warning findWarningById(String id) throws SQLException {

        PreparedStatement statement = getConnection()
                .prepareStatement("SELECT * FROM warnings WHERE id = ?");
        statement.setString(1, id);

        ResultSet results = statement.executeQuery();

        if(results.next()) {

            long memberId = results.getLong("member_id");
            long memberWarnerId = results.getLong("member_warner_id");
            String reason = results.getString("reason");
            Date date = results.getDate("date_created");

            Warning warning = new Warning(id, memberId, memberWarnerId, reason, date);

            statement.close();
            return warning;
        }

        statement.close();
        return null;
    }

    public void deleteWarning(String id) throws SQLException {

        PreparedStatement statement = getConnection()
                .prepareStatement("DELETE FROM warnings WHERE id = ?");

        statement.setString(1, id);

        statement.executeUpdate();

        statement.close();

    }

}