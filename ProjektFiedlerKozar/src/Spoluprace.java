import java.io.*;

enum Kvalita { SPATNA, PRUMERNA, DOBRA }

class Spoluprace implements Serializable {
    int idKolegy;
    Kvalita kvalita;

    public Spoluprace(int idKolegy, Kvalita kvalita) {
        this.idKolegy = idKolegy;
        this.kvalita = kvalita;
    }
}