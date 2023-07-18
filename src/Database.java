import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Database {
    private static final String CREATETABLE = "CREATE TABLE IF NOT EXISTS Sessions (Player varchar(100) NOT NULL, Session int NOT NULL, Round int NOT NULL, Score int NOT NULL, CONSTRAINT PK_Sessions PRIMARY KEY (Player, Session, Round));";
    private static final String INSERT_TEMPLATE = "INSERT INTO Sessions (Player, Session, Round, Score) VALUES ('%1s', %2d, %3d, %4d);";
    private static final String SELECT_BYPLAYERANDSESSION = "SELECT Player, SUM(Score) AS Score FROM Sessions WHERE Player = '%1s' AND Session = %2d;";

    private static Random rand = new Random();

    private static int currentSession = rand.nextInt(30000);

    public static Map<Integer, Integer> sessionRoundMap = new HashMap<>();


    public static void createDatabase() {
        try {
            SqliteClient client = new SqliteClient("demodatabase.sqlite");
            if (client.tableExists("Sessions")) {
//                client.executeStatement("DROP TABLE Sessions;");
            }
            client.executeStatement(CREATETABLE);

//            client.executeStatement(String.format(INSERT_TEMPLATE, "Anita", 1, 1, 50));
//            client.executeStatement(String.format(INSERT_TEMPLATE, "Hans", 1, 1, 0));
//            client.executeStatement(String.format(INSERT_TEMPLATE, "Anita", 1, 2, 20));
//            client.executeStatement(String.format(INSERT_TEMPLATE, "Hans", 1, 2, 100));

        } catch (SQLException ex) {
            System.out.println("Ups! Something went wrong:" + ex.getMessage());
        }
    }

    public static void addRowtoDatabase(String name, int session, int round, int score) {
        SqliteClient client = null;
        try {
            client = new SqliteClient("demodatabase.sqlite");
            client.executeStatement(String.format(INSERT_TEMPLATE, name, session, round, score));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    public static int generateSessionNumber() {
        sessionRoundMap.put(currentSession, 1); // Start with round 1 for the new session

        return currentSession;
    }
}
