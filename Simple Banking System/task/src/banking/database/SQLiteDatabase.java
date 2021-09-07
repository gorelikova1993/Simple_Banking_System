package banking.database;

import banking.Account;
import banking.bankcard.Card;
import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.logging.Logger;

public class SQLiteDatabase implements InterfaceDB {
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS card(" +
                    "id INTEGER PRIMARY KEY," +
                    "number TEXT," +
                    "pin TEXT," +
                    "balance INTEGER DEFAULT 0)";
    private static final String SQL_ADD_ACCOUNT = "INSERT INTO card (number, pin) VALUES (?, ?)";
    private static final String SQL_LOGIN_ACCOUNT = "SELECT number, pin, balance FROM card WHERE number = ? AND pin = ?";
    private static final String SQL_FIND_ACCOUNT = "SELECT number, pin, balance FROM card WHERE number = ?";
    private static final String SQL_UPDATE_ACCOUNT = "UPDATE card SET balance = balance + ? WHERE number = ?";
    private static final String SQL_DELETE_ACCOUNT = "DELETE FROM card WHERE number = ?";

    private static final Logger logger = Logger.getLogger(SQLiteDatabase.class.getPackageName());

    private SQLiteDataSource dataSource;
    private Connection con;
    private final String DATABASE_URL;

    public SQLiteDatabase(String databaseName) {
        dataSource = new SQLiteDataSource();
        DATABASE_URL = "jdbc:sqlite:" + databaseName;
        dataSource.setUrl(DATABASE_URL);
        try {
            con = dataSource.getConnection();
            logger.info("connection DB");
        } catch (SQLException throwables) {
            logger.severe("Connection error!");
        }
        createTable();
    }

    private void createTable() {
        try (Statement statement = con.createStatement()) {
            statement.executeUpdate(SQL_CREATE_TABLE);
            logger.info("create SQL table");
        } catch (SQLException e) {
            logger.severe("Creation table error!");
        }
    }

    @Override
    public void createAccount(Account userAccount) {
        try (PreparedStatement ps = con.prepareStatement(SQL_ADD_ACCOUNT)) {
            ps.setString(1, userAccount.getCard().getNumber());
            ps.setString(2, userAccount.getCard().getPin());
            ps.executeUpdate();
            logger.info("create account");
        } catch (SQLException e) {
            logger.severe("Create account error!");
        }
    }

    @Override
    public Account loginAccount(Card userCard) {
        Account resultAccount = null;
        try (PreparedStatement ps = con.prepareStatement(SQL_LOGIN_ACCOUNT)) {
            ps.setString(1, userCard.getNumber());
            ps.setString(2, userCard.getPin());
            ResultSet rs = ps.executeQuery();
            int balance = rs.getInt("balance");
            resultAccount = new Account(balance, userCard);
            logger.info("login in account");
            if (rs.next()) {
                return resultAccount;
            }
        } catch (SQLException e) {
            logger.severe("Login account error!");
        }
        return resultAccount;
    }

    @Override
    public boolean findAccount(String cardNumber) {
        try (PreparedStatement ps = con.prepareStatement(SQL_FIND_ACCOUNT)) {
            ps.setString(1, cardNumber);
            ResultSet rs = ps.executeQuery();
            logger.info("find account");
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            logger.severe("Find account error!");
        }
        return false;
    }

    @Override
    public void updateAccount(String cardNumber, int changeBalance) {
        try (PreparedStatement ps = con.prepareStatement(SQL_UPDATE_ACCOUNT)) {
            ps.setString(1, Integer.toString(changeBalance));
            ps.setString(2, cardNumber);
            ps.executeUpdate();
            logger.info("update account");
        } catch (SQLException e) {
            logger.severe("Update account error!");
        }
    }

    @Override
    public void deleteAccount(Account userAccount) {
        try (PreparedStatement ps = con.prepareStatement(SQL_DELETE_ACCOUNT)) {
            ps.setString(1, userAccount.getCard().getNumber());
            ps.executeUpdate();
            logger.info("delete account");
        } catch (SQLException e) {
            logger.severe("Delete account error!");
        }
    }

    @Override
    public void close() {
        try {
            con.close();
            logger.info("close db");
        } catch (SQLException throwables) {
            logger.severe("Closing error!");
        }
    }

}
