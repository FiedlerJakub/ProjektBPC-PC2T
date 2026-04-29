import java.util.*;

class DatovyAnalytik extends Zamestnanec {
    public DatovyAnalytik(String jmeno, String prijmeni, int rok) { super(jmeno, prijmeni, rok); }

    @Override
    public void spustitDovednost(List<Zamestnanec> vsichni) {
        System.out.println("--- Analýza společných kontaktů pro " + jmeno + " ---");
        System.out.println("Výpočet dokončen. Nejvíce společných kolegů má ID: 5 (Simulace)");
    }
}