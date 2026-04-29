import java.util.*;

class BezpecnostniSpecialista extends Zamestnanec {
    public BezpecnostniSpecialista(String jmeno, String prijmeni, int rok) { super(jmeno, prijmeni, rok); }

    @Override
    public void spustitDovednost(List<Zamestnanec> vsichni) {
        if (seznamSpolupraci.isEmpty()) {
            System.out.println("Žádná data pro výpočet rizika.");
            return;
        }
        double prumer = seznamSpolupraci.stream()
            .mapToInt(s -> s.kvalita.ordinal() + 1).average().orElse(0);
        double skore = seznamSpolupraci.size() / prumer;
        System.out.printf("Vypočtené rizikové skóre: %.2f\n", skore);
    }
}