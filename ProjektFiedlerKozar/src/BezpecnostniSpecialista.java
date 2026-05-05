import java.util.*;

class BezpecnostniSpecialista extends Zamestnanec {
    public BezpecnostniSpecialista(String jmeno, String prijmeni, int rok) { super(jmeno, prijmeni, rok); }

    @Override
    public void spustitDovednost(List<Zamestnanec> vsichni) {
        if (seznamSpolupraci.isEmpty()) {
            System.out.println("Žádná data pro výpočet rizika.");
            return;
        }

        double soucetKvalit = 0;
        for (Spoluprace s : seznamSpolupraci) {
            soucetKvalit += (s.kvalita.ordinal() + 1);
        }

        double prumer = soucetKvalit / seznamSpolupraci.size();

        double skore = seznamSpolupraci.size() / prumer;

        System.out.printf("Počet spolupracovníků: %d\n", seznamSpolupraci.size());
        System.out.printf("Průměrná kvalita: %.2f\n", prumer);
        System.out.printf("Vypočtené rizikové skóre: %.2f\n", skore);
    }
}