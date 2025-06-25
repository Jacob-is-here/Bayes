import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        List<Piwo> piwka = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("/Users/jakub/Desktop/Bayes/piwo.csv"))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] dane = line.split(",");
                piwka.add(new Piwo(dane[0], dane[1], dane[2], dane[3]));
            }

            Scanner scanner = new Scanner(System.in);

            System.out.println("Podaj barwę piwa: ");
            String barwa = scanner.nextLine();

            System.out.println("Podaj chmiel: ");
            String chmiel = scanner.nextLine();

            System.out.println("Podaj goryczkę: ");
            String goryczka = scanner.nextLine();

            String wynik = decyzja(piwka, barwa, chmiel, goryczka);
            System.out.println("Przewidywany styl piwa: " + wynik);

        } catch (IOException e) {
            System.out.println("Błąd odczytu pliku: " + e.getMessage());
        }
    }

    static double prawdopodobienstwo(List<Piwo> piwka, String atrybut, String wartoscAtrybutu, String styl) {
        long licznikPasujacych = piwka.stream()
                .filter(p -> p.styl.equals(styl))
                .filter(p -> switch (atrybut) {
                    case "barwa" -> p.barwa.equals(wartoscAtrybutu);
                    case "chmiel" -> p.chmiel.equals(wartoscAtrybutu);
                    case "goryczka" -> p.goryczka.equals(wartoscAtrybutu);
                    default -> false;
                })
                .count();

        long mianownik = piwka.stream()
                .filter(p -> p.styl.equals(styl))
                .count();

        return (double) licznikPasujacych / mianownik;
    }

    static double wygladzanie(List<Piwo> piwka, String atrybut, String wartoscAtrybutu, String styl, double m) {
        long n = piwka.stream().filter(p -> p.styl.equals(styl)).count();
        long v = piwka.stream()
                .map(p -> switch (atrybut) {
                    case "barwa" -> p.barwa;
                    case "chmiel" -> p.chmiel;
                    case "goryczka" -> p.goryczka;
                    default -> "";
                })
                .distinct()
                .count();

        return (prawdopodobienstwo(piwka, atrybut, wartoscAtrybutu, styl) * n + m) / (n + m * v);
    }

    static String decyzja(List<Piwo> piwka, String barwa, String chmiel, String goryczka) {
        List<String> style = piwka.stream().map(p -> p.styl).distinct().toList();
        double maxP = -5;
        String najlepszy = "";

        for (String styl : style) {
            double p = wygladzanie(piwka, "barwa", barwa, styl, 1) *
                    wygladzanie(piwka, "chmiel", chmiel, styl, 1) *
                    wygladzanie(piwka, "goryczka", goryczka, styl, 1);

            if (p > maxP) {
                maxP = p;
                najlepszy = styl;
            }
        }

        return najlepszy;
    }
}

class Piwo{
    String barwa,chmiel,goryczka,styl ;

    public Piwo(String barwa, String chmiel, String goryczka, String styl) {
        this.barwa = barwa;
        this.chmiel = chmiel;
        this.goryczka = goryczka;
        this.styl = styl;
    }


}