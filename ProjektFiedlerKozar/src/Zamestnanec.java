import java.io.*;
import java.util.*;

abstract class Zamestnanec implements Serializable, Comparable<Zamestnanec> {
    private static int pocitadloId = 1;
    protected int id;
    protected String jmeno;
    protected String prijmeni;
    protected int rokNarozeni;
    protected List<Spoluprace> seznamSpolupraci = new ArrayList<>();

    public Zamestnanec(String jmeno, String prijmeni, int rokNarozeni) {
        this.id = pocitadloId++;
        this.jmeno = jmeno;
        this.prijmeni = prijmeni;
        this.rokNarozeni = rokNarozeni;
    }

    public static void nastavitPocitadlo(int maxId) { 
    	pocitadloId = maxId + 1; 
    }
    public String getPrijmeni() { 
    	return prijmeni; 
    }
    public abstract void spustitDovednost(List<Zamestnanec> vsichni);
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public String getJmeno() {
        return jmeno;
    }
    public int getRokNarozeni() {
        return rokNarozeni;
    }
    public String toString() {
        return String.format("ID: %d | %s %s (%d) | Skupina: %s | Vazeb: %d", 
            id, prijmeni, jmeno, rokNarozeni, this.getClass().getSimpleName(), seznamSpolupraci.size());
    }
    public int compareTo(Zamestnanec druhy) {
        return this.prijmeni.compareTo(druhy.prijmeni);
    }
}