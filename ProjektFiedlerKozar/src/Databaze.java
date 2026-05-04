import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Databaze {
    private Connection conn;

    public boolean connect(String dbName) {
    	try {
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);
            Statement st = conn.createStatement();
            st.execute("CREATE TABLE IF NOT EXISTS zaloha (id INTEGER, jmeno TEXT, prijmeni TEXT, rok INTEGER, typ TEXT)");
            st.execute("CREATE TABLE IF NOT EXISTS vazby (id_zamestnance INTEGER, id_kolegy INTEGER, kvalita TEXT)");
            return true;
        } catch (SQLException e) { 
            System.out.println(e.getMessage());
            return false;
        }
    }
    
    public List<Zamestnanec> getVsechnyZamestnance() {
        List<Zamestnanec> list = new ArrayList<>();
        String sql = "SELECT * FROM zaloha";
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
               
               while (rs.next()) {
                   int id = rs.getInt("id");
                   String typ = rs.getString("typ");
                   Zamestnanec z;
                   
                   if (typ.equals("Analytik")) {
                       z = new DatovyAnalytik(rs.getString("jmeno"), rs.getString("prijmeni"), rs.getInt("rok"));
                   } else {
                       z = new BezpecnostniSpecialista(rs.getString("jmeno"), rs.getString("prijmeni"), rs.getInt("rok"));
                   }
                   z.setId(id); 

                   String sqlVazby = "SELECT * FROM vazby WHERE id_zamestnance = " + id;
                   try (Statement stmtVazby = conn.createStatement();
                        ResultSet rsV = stmtVazby.executeQuery(sqlVazby)) {
                       while (rsV.next()) {
                           int idKolegy = rsV.getInt("id_kolegy");
                           Kvalita kv = Kvalita.valueOf(rsV.getString("kvalita"));
                           z.seznamSpolupraci.add(new Spoluprace(idKolegy, kv));
                       }
                   }
                   list.add(z);
               }
           } catch (SQLException e) {
               System.out.println("Chyba při načítání: " + e.getMessage());
           }
        return list;
    }

    public void zalohujVse(List<Zamestnanec> db) {
    	try {
            Statement st = conn.createStatement();
            st.execute("DELETE FROM zaloha");
            st.execute("DELETE FROM vazby"); 

            String sqlZam = "INSERT INTO zaloha VALUES(?,?,?,?,?)";
            PreparedStatement psZam = conn.prepareStatement(sqlZam);
            
            String sqlVazba = "INSERT INTO vazby VALUES(?,?,?)";
            PreparedStatement psVazba = conn.prepareStatement(sqlVazba);

            for (Zamestnanec z : db) {
                psZam.setInt(1, z.getId());
                psZam.setString(2, z.getJmeno());
                psZam.setString(3, z.getPrijmeni());
                psZam.setInt(4, z.getRokNarozeni());
                psZam.setString(5, z instanceof DatovyAnalytik ? "Analytik" : "Specialista");
                psZam.executeUpdate();

                for (Spoluprace s : z.seznamSpolupraci) {
                    psVazba.setInt(1, z.getId()); 
                    psVazba.setInt(2, s.idKolegy); 
                    psVazba.setString(3, s.kvalita.toString()); 
                    psVazba.executeUpdate();
                }
            }
            System.out.println("[SQL] Všechna data i vazby byly zálohovány.");
        } catch (SQLException e) {
            System.out.println("Chyba při záloze: " + e.getMessage());
        }
    }

    public void disconnect() {
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
}