package org.example;


//DataService implements AutoCloseable.
// It has one public constructor that takes a connection string and creates a Connection using this string,
// which it stores in a private field. The close() method closes the connection.
//    A method public List<Party> getParties() that returns a list of all parties in the database,
//      by using JDBC on the provided connection.
//    A method public Party getParty(int id) that returns the party for this id,
//      if there is one, otherwise null.

import java.sql.*;
import java.util.*;


public class DataService implements AutoCloseable {
    private Connection connection;

    public DataService(String connectionString) throws SQLException {
        try {
            connection = DriverManager.getConnection(connectionString);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Party> getParties() throws SQLException {
        List<Party> parties = new ArrayList<>();
        String SQL = "SELECT id, name FROM Party";
        try (PreparedStatement s = connection.prepareStatement(SQL)) {
            ResultSet r = s.executeQuery();
            while (r.next()) {
                int id = r.getInt("id");
                String name = r.getString("name");
                parties.add(new Party(id, name));
            }
        }
        return parties;
    }

    public Party getParty(int id) throws SQLException {
        String SQL = "SELECT name FROM Party WHERE id = ?";
        try (PreparedStatement s = connection.prepareStatement(SQL)) {
            s.setInt(1, id);
            ResultSet r = s.executeQuery();
            if (!r.next()) {
                System.out.println("Party #" + id + " is not found");
                return null;
            }
            String name = r.getString("name");
            return new Party(id, name);
        }
    }

    public Ward getWard(int id) throws SQLException {
        String SQL = "SELECT name, electorate FROM Ward WHERE id = ?";
        try (PreparedStatement s = connection.prepareStatement(SQL)) {
            s.setInt(1, id);
            ResultSet r = s.executeQuery();
            if (!r.next()) {
                System.out.println("Ward #" + id + " is not found");
                return null;
            }
            String name = r.getString("name");
            int electorate = r.getInt("electorate");
            return new Ward(id, name, electorate);
        }
    }

    public Candidate getCandidate(int id) throws SQLException {
        //String SQL = "SELECT name, party, ward, votes FROM Candidate WHERE id = ?";
        //SELECT Candidate.id AS candiadte_id, Candidate.name AS name, Party.name AS party_name, Ward.name AS ward_name, Candidate.votes AS votes FROM Candidate INNER JOIN Party ON Candidate.party = Party.id INNER JOIN Ward ON Candidate.ward = Ward.id ORDER BY Candidate.id;
        String SQL = "SELECT Candidate.id AS id, Candidate.name AS name, Party.name AS party, Party.id AS party_id, Ward.id AS ward_id, Ward.name AS ward, Candidate.votes AS votes " +
                "FROM Candidate " +
                "LEFT JOIN Party ON Candidate.party = Party.id " +
                "LEFT JOIN Ward ON Candidate.ward = Ward.id " +
                "WHERE Candidate.id = ?";
        try (PreparedStatement s = connection.prepareStatement(SQL)) {
            s.setInt(1, id);
            ResultSet r = s.executeQuery();
            if (!r.next()) {
                System.out.println("Candidate #" + id + " is not found");
                return null;
            }
            String name = r.getString("name");
            Party party = r.getInt("party_id") == 0 ? null : getParty(r.getInt("party_id"));
            Ward ward = r.getInt("ward_id") == 0 ? null : getWard(r.getInt("ward_id"));
            int votes = r.getInt("votes");
            //int id, String name, Party party, Ward ward, int votes
            return new Candidate(id, name, party, ward, votes);
        }
    }


    @Override
    public void close() throws SQLException {
        connection.close();
    }
}
