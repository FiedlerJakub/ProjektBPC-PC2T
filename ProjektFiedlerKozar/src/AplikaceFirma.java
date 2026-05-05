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
    	    String jmeno = sc.nextLine();
    	    
    	    System.out.print("Příjmení: "); 
    	    String prijmeni = sc.nextLine();
    	    
    	    System.out.print("Rok narození: "); 
    	    int rok = readNumber(sc);

    	    if (typ == 1) {
    	        db.add(new DatovyAnalytik(jmeno, prijmeni, rok));
    	    } else {
    	        db.add(new BezpecnostniSpecialista(jmeno, prijmeni, rok));
    	    }
    	    System.out.println("Zaměstnanec přidán.");
    }

    private static void pridatSpolupraci() {
        System.out.print("ID zaměstnance: "); 
        int id1 = readNumber(sc);
        
        System.out.print("ID kolegy: "); 
        int id2 = readNumber(sc);
        
        System.out.print("Kvalita (1-Spatna, 2-Prumerna, 3-Dobra): ");
        int volbaKvality = readNumber(sc);
        
        if (volbaKvality >= 1 && volbaKvality <= Kvalita.values().length) {
            Kvalita k = Kvalita.values()[volbaKvality - 1];
            
            for (Zamestnanec z : db) {
                if (z.getId() == id1) {
                    z.seznamSpolupraci.add(new Spoluprace(id2, k));
                    System.out.println("Spolupráce zaevidována.");
                    return; 
                }
            }
            System.out.println("Zaměstnanec s ID " + id1 + " nebyl nalezen.");
        } else {
            System.out.println("Neplatná volba kvality.");
        }
    }

    private static void odebratZamestnance() {
        System.out.print("ID k smazání: "); 
        int id = readNumber(sc);
        for (int i = db.size() - 1; i >= 0; i--) {
            if (db.get(i).getId() == id) {
                db.remove(i);
            }
        }

        for (Zamestnanec z : db) {
            for (int j = z.seznamSpolupraci.size() - 1; j >= 0; j--) {
                if (z.seznamSpolupraci.get(j).idKolegy == id) {
                    z.seznamSpolupraci.remove(j);
                }
            }
        }
        System.out.println("Zaměstnanec a jeho vazby odstraněny.");
    }

    private static void vyhledatZamestnance() {
        System.out.print("Zadejte ID: "); 
        int id = readNumber(sc);
        boolean nalezen = false;

        for (Zamestnanec z : db) {
            if (z.getId() == id) {
                System.out.println(z.toString());
                System.out.println("Počet vazeb: " + z.seznamSpolupraci.size());
                
                int spatne = 0, prumerne = 0, dobre = 0;
                for (Spoluprace s : z.seznamSpolupraci) {
                    if (s.kvalita == Kvalita.SPATNA) spatne++;
                    else if (s.kvalita == Kvalita.PRUMERNA) prumerne++;
                    else if (s.kvalita == Kvalita.DOBRA) dobre++;
                }
                System.out.printf("Detail vazeb: Špatné(%d), Průměrné(%d), Dobré(%d)%n", spatne, prumerne, dobre);
                
                nalezen = true;
                break;
            }
        }

        if (!nalezen) {
            System.out.println("Zaměstnanec s ID " + id + " neexistuje.");
        }
    }

    private static void vypisAbecedne() {
        Collections.sort(db);
        System.out.println("--- DATOVÍ ANALYTICI ---");
        for (Zamestnanec z : db) {
            if (z instanceof DatovyAnalytik) {
                System.out.println(z);
            }
        }

        System.out.println("--- BEZPEČNOSTNÍ SPECIALISTÉ ---");
        for (Zamestnanec z : db) {
            if (z instanceof BezpecnostniSpecialista) {
                System.out.println(z);
            }
        }
    }

    private static void statistiky() {
        Zamestnanec maxZ = null;
        int maxVazeb = -1;

        for (Zamestnanec z : db) {
            if (z.seznamSpolupraci.size() > maxVazeb) {
                maxVazeb = z.seznamSpolupraci.size();
                maxZ = z;
            }
        }
        System.out.println("Nejvíc vazeb má: " + (maxZ != null ? maxZ.getPrijmeni() : "nikdo"));

        int pocetSpatna = 0;
        int pocetPrumerna = 0;
        int pocetDobra = 0;

        for (Zamestnanec z : db) {
            for (Spoluprace s : z.seznamSpolupraci) {
                if (s.kvalita == Kvalita.SPATNA) pocetSpatna++;
                else if (s.kvalita == Kvalita.PRUMERNA) pocetPrumerna++;
                else if (s.kvalita == Kvalita.DOBRA) pocetDobra++;
            }
        }

        String prevazujici = "žádná";
        int maxPocet = Math.max(pocetSpatna, Math.max(pocetPrumerna, pocetDobra));

        if (maxPocet > 0) {
            if (maxPocet == pocetSpatna) prevazujici = "Špatná";
            else if (maxPocet == pocetPrumerna) prevazujici = "Průměrná";
            else prevazujici = "Dobrá";
        }

        System.out.println("Převažující kvalita spolupráce: " + prevazujici);
    }

    private static void poctyVeSkupinách() {
        int pocetAnalytiku = 0;
        int pocetSpecialistu = 0;

        for (Zamestnanec z : db) {
            if (z instanceof DatovyAnalytik) {
                pocetAnalytiku++;
            } else if (z instanceof BezpecnostniSpecialista) {
                pocetSpecialistu++;
            }
        }
        
        System.out.println("Analytici: " + pocetAnalytiku + ", Specialisté: " + pocetSpecialistu);
    }

    private static void dovednost() {
        System.out.print("ID zaměstnance: ");
        int id = readNumber(sc);

        for (Zamestnanec z : db) {
            if (z.getId() == id) {
                z.spustitDovednost(db);
                return;
            }
        }
        
        System.out.println("Zaměstnanec s ID " + id + " nebyl nalezen.");
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

            int maxId = 0;
            for (Zamestnanec z : db) {
                if (z.getId() > maxId) {
                    maxId = z.getId();
                }
            }
            
            Zamestnanec.nastavitPocitadlo(maxId);
            
            System.out.println("Data byla úspěšně načtena. Počet záznamů: " + db.size());
            
        } catch (FileNotFoundException e) {
            System.out.println("Soubor se zálohou nebyl nalezen, začínáme s prázdnou databází.");
        } catch (Exception e) {
            System.out.println("Chyba při načítání dat: " + e.getMessage());
        }
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