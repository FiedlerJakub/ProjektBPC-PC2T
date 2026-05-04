import java.util.*;
import java.util.stream.*;

class DatovyAnalytik extends Zamestnanec {
    public DatovyAnalytik(String jmeno, String prijmeni, int rok) {
        super(jmeno, prijmeni, rok);
    }

    @Override
    public void spustitDovednost(List<Zamestnanec> vsichni) {
        if (seznamSpolupraci.isEmpty()) {
            System.out.println("Žádní spolupracovníci – nelze provést analýzu.");
            return;
        }

        // Množina mých kolegů (jejich ID)
        Set<Integer> mojiKolegove = seznamSpolupraci.stream()
                .map(s -> s.idKolegy)
                .collect(Collectors.toSet());

        System.out.println("--- Analýza společných kontaktů pro " + jmeno + " " + prijmeni + " ---");

        int nejvetsiPrunik = -1;
        Zamestnanec nejlepsiKolega = null;

        for (Zamestnanec z : vsichni) {
            // Přeskočit sebe a ty, kteří nejsou moji kolegové
            if (z.id == this.id || !mojiKolegove.contains(z.id)) continue;

            // Množina kolegů tohoto spolupracovníka
            Set<Integer> jehoKolegove = z.seznamSpolupraci.stream()
                    .map(s -> s.idKolegy)
                    .collect(Collectors.toSet());

            // Průnik – společní kolegové (vyjma nás dvou navzájem)
            Set<Integer> spolecni = new HashSet<>(mojiKolegove);
            spolecni.retainAll(jehoKolegove);
            spolecni.remove(z.id);
            spolecni.remove(this.id);

            if (spolecni.size() > nejvetsiPrunik) {
                nejvetsiPrunik = spolecni.size();
                nejlepsiKolega = z;
            }
        }

        if (nejlepsiKolega == null) {
            System.out.println("Žádný ze spolupracovníků nemá evidované vlastní vazby.");
        } else {
            System.out.printf("Nejvíce společných kolegů (%d) má: %s %s (ID: %d)%n",
                    nejvetsiPrunik, nejlepsiKolega.prijmeni, nejlepsiKolega.jmeno, nejlepsiKolega.id);
        }
    }
}
