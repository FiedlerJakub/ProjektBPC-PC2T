import java.io.*;
import java.util.*;

public class AplikaceFirma {
    private static List<Zamestnanec> db = new ArrayList<>();
    private static Databaze sqlDb = new Databaze();
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
    	sqlDb.connect("firma_zaloha.db");
        nactiZSql(); 
        boolean bezi = true;

        while (bezi) {
            System.out.println("\n--- MENU ---");
            System.out.println("a) Přidat zaměstnance   b) Přidat spolupráci   c) Odebrat zaměstnance");
            System.out.println("d) Vyhledat dle ID      e) Spustit dovednost   f) Abecední výpis");
            System.out.println("g) Statistiky           h) Počty ve skupinách  i) Uložit do souboru");
            System.out.println("j) Načíst ze souboru    k) Ukončit a uložit do SQL");
            System.out.print("Vyberte možnost: ");
            String volba = sc.nextLine().toLowerCase();

            switch (volba) {
                case "a" -> pridatZamestnance();
                case "b" -> pridatSpolupraci();
                case "c" -> odebratZamestnance();
                case "d" -> vyhledatZamestnance();
                case "e" -> dovednost();
                case "f" -> vypisAbecedne();
                case "g" -> statistiky();
                case "h" -> poctyVeSkupinách();
                case "i" -> ulozitDoSouboru();
                case "j" -> nactiZeSouboru();
                case "k" -> { ulozitDoSql(); bezi = false; }
                default -> System.out.println("Neplatná volba.");
            }
        }
    }
    public static int readNumber(Scanner sc) {
        while (!sc.hasNextInt()) {
            System.out.println("Zadejte celé číslo"); 
            sc.next(); 
        }
        int cislo = sc.nextInt();
        sc.nextLine();
        return cislo;
    }

    private static void pridatZamestnance() {
    	    System.out.print("Skupina (1-Analytik, 2-Specialista): ");
    	    int typ = readNumber(sc);
    	    
    	    System.out.print("Jméno: "); 
    	    String j = sc.nextLine();
    	    
    	    System.out.print("Příjmení: "); 
    	    String p = sc.nextLine();
    	    
    	    System.out.print("Rok narození: "); 
    	    int r = readNumber(sc);

    	    if (typ == 1) {
    	        db.add(new DatovyAnalytik(j, p, r));
    	    } else {
    	        db.add(new BezpecnostniSpecialista(j, p, r));
    	    }
    	    System.out.println("Zaměstnanec přidán.");
    }

    private static void pridatSpolupraci() {
    	System.out.print("ID zaměstnance: "); 
        int id1 = readNumber(sc);
        
        System.out.print("ID kolegy: "); 
        int id2 = readNumber(sc);
        
        System.out.print("Kvalita (1-Spatna, 2-Prumerna, 3-Dobra): ");
        int kvalita = readNumber(sc);
        
        if (kvalita >= 1 && kvalita <= Kvalita.values().length) {
            Kvalita k = Kvalita.values()[kvalita - 1];
            db.stream()
              .filter(z -> z.id == id1)
              .findFirst()
              .ifPresent(z -> z.seznamSpolupraci.add(new Spoluprace(id2, k)));
            System.out.println("Spolupráce zaevidována.");
        } else {
            System.out.println("Neplatná volba kvality.");
        }
    }

    private static void odebratZamestnance() {
        System.out.print("ID k smazání: "); int id = Integer.parseInt(sc.nextLine());
        db.removeIf(z -> z.id == id);
        db.forEach(z -> z.seznamSpolupraci.removeIf(s -> s.idKolegy == id));
        System.out.println("Zaměstnanec a jeho vazby odstraněny.");
    }

    private static void vyhledatZamestnance() {
        System.out.print("Zadejte ID: "); int id = Integer.parseInt(sc.nextLine());
        db.stream().filter(z -> z.id == id).forEach(System.out::println);
    }

    private static void vypisAbecedne() {
        db.stream().sorted(Comparator.comparing(Zamestnanec::getPrijmeni)).forEach(System.out::println);
    }

    private static void statistiky() {
        Zamestnanec max = db.stream().max(Comparator.comparing(z -> z.seznamSpolupraci.size())).orElse(null);
        System.out.println("Nejvíc vazeb má: " + (max != null ? max.getPrijmeni() : "nikdo"));
    }

    private static void poctyVeSkupinách() {
        long analitik = db.stream().filter(z -> z instanceof DatovyAnalytik).count();
        long spec = db.stream().filter(z -> z instanceof BezpecnostniSpecialista).count();
        System.out.println("Analytici: " + analitik + ", Specialisté: " + spec);
    }

    private static void dovednost() {
        System.out.print("ID zaměstnance: ");
        int id = readNumber(sc);
        db.stream().filter(z -> z.id == id).findFirst().ifPresent(z -> z.spustitDovednost(db));
    }

    private static void ulozitDoSouboru() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data.ser"))) {
            oos.writeObject(db);
            System.out.println("Uloženo do souboru data.ser.");
        } catch (IOException e) { System.out.println("Chyba při ukládání."); }
    }

    @SuppressWarnings("unchecked")
    private static void nactiZeSouboru() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data.ser"))) {
            db = (List<Zamestnanec>) ois.readObject();
            int maxId = db.stream().mapToInt(z -> z.id).max().orElse(1);
            Zamestnanec.nastavitPocitadlo(maxId);
            System.out.println("Data načtena ze souboru.");
        } catch (Exception e) { System.out.println("Soubor nenalezen nebo je poškozen."); }
    }

    private static void ulozitDoSql() {
    	sqlDb.zalohujVse(db);
        sqlDb.disconnect();
    }

    private static void nactiZSql() {
        List<Zamestnanec> nactenaData = sqlDb.getVsechnyZamestnance();
        if (nactenaData != null && !nactenaData.isEmpty()) {
            db = nactenaData;
            int maxId = db.stream().mapToInt(Zamestnanec::getId).max().orElse(0);
            Zamestnanec.nastavitPocitadlo(maxId);
            System.out.println("[SQL] Data byla úspěšně načtena z databáze.");
        } else {
            System.out.println("[SQL] Databáze je prázdná");
        }
    }
}