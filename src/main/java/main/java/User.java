package main.java;

import java.sql.SQLException;
import java.util.List;

import Database.DatabaseHandler;
import Betting.GainFunction;
import Betting.Bet;

public class User {
    private final String id;
    private int reputation;
    private final String username;
    private final String email;
    private final String authentication;


    public User(String id, String username, int reputation, String email, String authentication) {
        this.authentication = authentication;
        this.email = email;
        this.username = username;
        this.id = id;
        this.reputation = reputation;
    }

    public User(List<String> dataFields) {
        this.id = dataFields.get(0);
        this.username = dataFields.get(1);
        this.reputation = Integer.parseInt(dataFields.get(2));
        this.email = dataFields.get(3);
        this.authentication = dataFields.get(4);
    }


    public void submitBet(int rep, String percentChange, String champion, String stat) throws SQLException {
        //add to bet database
        String betID = "";
        String IDandTime = id + System.currentTimeMillis();
        //think of a way to generate unique ids
        betID = String.valueOf(IDandTime.hashCode());
        Main.db.createNewBet(betID, id, champion, stat, percentChange, String.valueOf(rep));


    }

    /**
     * ID getter.
     *
     * @return ID of current user.
     */
    public String getID() {
        return id;
    }

    /**
     * Username getter.
     *
     * @return username of current user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the reputation
     */
    public int getReputation() {
        return reputation;
    }

    /**
     * @param reputation the reputation to set
     */
    public void setReputation(int reputation) {
        this.reputation = reputation;
    }

    public void viewHistory() {

    }
}