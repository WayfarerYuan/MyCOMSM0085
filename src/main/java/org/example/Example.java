package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class Example {

    public static String CS = "jdbc:mariadb://localhost:3306/elections?user=vagrant&localSocket=/var/run/mysqld/mysqld.sock";

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java -jar elections.jar <candidate_id>");
            return;
        }
        int candidateId = Integer.parseInt(args[0]);
        try (DataService ds = new DataService(CS)) {
            Candidate candidate = ds.getCandidate(candidateId);
            if (candidate == null) {
                System.out.println("Candidate #" + candidateId + " is not found");
                return;
            }
            System.out.println("Candidate #" + candidateId + " is " + candidate.name);
            System.out.println("Party: " + candidate.party.name);
            System.out.println("Ward: " + candidate.ward.name);
            System.out.println("Electorate: " + candidate.ward.electorate);
            System.out.println("Votes: " + candidate.votes);
            System.out.println("Votes %: " + (candidate.votes * 100.0 / candidate.ward.electorate));
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
