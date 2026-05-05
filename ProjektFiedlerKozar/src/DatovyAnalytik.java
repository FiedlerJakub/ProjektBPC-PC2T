import java.util.*;

class DatovyAnalytik extends Zamestnanec {
    public DatovyAnalytik(String jmeno, String prijmeni, int rok) {
        super(jmeno, prijmeni, rok);
    }
    
    public void spustitDovednost(List<Zamestnanec> vsichni) {
        if (seznamSpolupraci.isEmpty()) {
            System.out.println("Žádní spolupracovníci – nelze provést analýzu.");
            return;
        }

        System.out.println("--- Analýza společných kontaktů pro " + jmeno + " " + prijmeni + " ---");

        int nejvetsiPrunik = -1;
        Zamestnanec nejlepsiKolega = null;

        for (Zamestnanec z : vsichni) {
            
            if (z.id == this.id || !jeToMujKolega(z.id)) {
                continue;
            }

            int pocetSpolecnych = 0;
            for (Spoluprace moje : this.seznamSpolupraci) {
                for (Spoluprace jeho : z.seznamSpolupraci) {
                    if (moje.idKolegy == jeho.idKolegy && moje.idKolegy != z.id && moje.idKolegy != this.id) {
                        pocetSpolecnych++;
                    }
                }
            }

            if (pocetSpolecnych > nejvetsiPrunik) {
                nejvetsiPrunik = pocetSpolecnych;
                nejlepsiKolega = z;
            }
        }

        if (nejlepsiKolega == null) {
            System.out.println("Žádný ze spolupracovníků nemá evidované vlastní vazby.");
        } else {
            System.out.printf("Nejvíce společných kolegů (%d) má: %s %s (ID: %d)%n",
                    nejvetsiPrunik, nejlepsiKolega.getPrijmeni(), nejlepsiKolega.getJmeno(), nejlepsiKolega.getId());
        }
    }


    private boolean jeToMujKolega(int hledaneId) {
        for (Spoluprace s : seznamSpolupraci) {
            if (s.idKolegy == hledaneId) return true;
        }
        return false;
    }
}