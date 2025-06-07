package kmeans;

import java.util.*;

public class KMeans {

    private static final double[][] DATASET = {
            {1.0, 1.0}, {5.0, 5.0}, {8.0, 1.0},
            {0.9, 1.1}, {5.1, 4.9}, {8.1, 1.1},
            {1.1, 0.9}, {4.9, 5.1}, {7.9, 0.9},
            {1.2, 1.0}, {5.2, 5.0}, {8.2, 1.0}
    };
    private static final int[] K_VALUES = {2, 3, 4};
    private static final int MAX_ITERS = 100;
    private static final double TOLERANCE = 1e-4;
    private static double suma_odleglosci = 0;

    public static void main(String[] args) {
        for (int k : K_VALUES) {
            int[] labels = kMeans(DATASET, k, MAX_ITERS, TOLERANCE);
            selfCheck(labels, k);
        }
        System.out.println("Gratulacje – wszystkie testy zakończyły się sukcesem!");
    }

    /**
     * Zadanie: ***zaimplementuj*** algorytm k-średnich.
     * <p>
     * Wymagania:
     * <ol>
     *   <li>Inicjalizuj centroidy pierwszymi <code>k</code> punktami z wejścia.</li>
     *   <li>Przypisz każdy punkt do najbliższego centroidu (odległość euklidesowa).</li>
     *   <li>Oblicz nowe centroidy jako średnią arytmetyczną punktów w klastrze.</li>
     *   <li>Powtarzaj kroki 2-3, aż klastry przestaną się zmieniać, centroidy
     *       przesuną się mniej niż <code>tolerance</code>, lub osiągnięto
     *       <code>maxIter</code> iteracji.</li>
     *   <li>Zwróć tablicę etykiet (wartości 0…k-1) – <b>labels[i]</b> to numer
     *       klastra, do którego został przypisany <b>data[i]</b>.</li>
     * </ol>
     * <p>
     * Możesz – ale nie musisz – dopisać metody pomocnicze <em>w tej klasie</em>.
     * Nie modyfikuj sygnatury ani nazwy tej metody. Możesz korzystać z publicznej metody <b>dist()</b>.
     */
    public static int[] kMeans(double[][] data, int k, int maxIter, double tolerance) {
        int[] przypisanie = new int[data.length];
        //double[] suma_odleglosci = new double[k];
        //stworz centroidy
        System.out.println("Losowe centroidy? T/N");
        Scanner scanner = new Scanner(System.in);
        String decyzja = scanner.next();
        double[][] centroidy = new double[k][];
        if(!decyzja.toLowerCase().contains("t")) {
            for (int i = 0; i < k; i++) {
                centroidy[i] = data[i];
            }
        }else{
            List<Integer> losowane = new LinkedList<Integer>();
            for(int i = 0; i < k; i++){
                while (true){
                    int wynik = (int) (Math.random() * data.length);
                    if(!losowane.contains(wynik)){
                        centroidy[i] = data[wynik];
                        losowane.add(wynik);
                        break;
                    }
                }
            }
        }
        boolean centroids_changed = true;
        //glowna petla iteraycjna
        int iter = 0;
        while(iter++ < maxIter && centroids_changed){
            suma_odleglosci = 0;
            for(int i = 0; i < data.length; i++){
                przypisanie[i] = przydzielCentroid(centroidy,data[i]);
            }
            double[][]nowe_centroidy = aktualizujCentroidy(przypisanie,data,k);
            if(liczRoznice(centroidy,nowe_centroidy,tolerance)){
                centroids_changed = false;
            }
            System.out.println("Iteracja " + iter + ": " + suma_odleglosci);
//            System.out.println("Stare: " + Arrays.toString(centroidy[0]) + ", " + Arrays.toString(centroidy[1]));
//            System.out.println("Nowe: " + Arrays.toString(nowe_centroidy[0]) + ", " + Arrays.toString(nowe_centroidy[1]));
            centroidy = nowe_centroidy;
        }
//        wypiszGrupy(przypisanie,data,k);
        return przypisanie;
    }
    public static double log2(double x) {
        return Math.log(x) / Math.log(2);
    }
    public static double policzEntropie(Map<String,Double> mapa, int ilosc){
        double suma = 0;
        for (Map.Entry<String,Double> entry: mapa.entrySet()) {
//            System.out.println("\n"+entry.getKey() + ": " + entry.getValue());
            double ulamek = entry.getValue() / ilosc;
            double entropia = ulamek * log2(ulamek);
            suma += entropia;
        }
        if(suma != 0){
            suma*=-1;
        }
        return suma;
    }
    public static void wypiszGrupy(int[] przypisanie, List<String> data, int k){
        for(int i = 0; i < k; i++){
            System.out.println("\nGrupa: " + (i+1));
            Map<String,Double> zlicz_kategorie = new HashMap<>();
            int wszystkie = 0;
            for(int j = 0; j < przypisanie.length; j++){
                if(przypisanie[j] == i){
                    System.out.print("[" +data.get(j) +"],");
                    String[] podziel = data.get(j).split(",");
                    String klucz = podziel[podziel.length-1];
                    zlicz_kategorie.putIfAbsent(klucz,0d);
                    zlicz_kategorie.put(klucz,zlicz_kategorie.get(klucz)+1);
                    wszystkie++;
                }
            }
            System.out.println("\nEntropia: " +policzEntropie(zlicz_kategorie, wszystkie));
        }
        System.out.println("\n");
    }
    public static void wypiszGrupy(int[] przypisanie, double data[][], int k){
        for(int i = 0; i < k; i++){
            System.out.println("\nGrupa: " + (i+1));
            for(int j = 0; j < przypisanie.length; j++){
                if(przypisanie[j] == i){
                    System.out.print(Arrays.toString(data[j]) +",");
                }
            }
        }
        System.out.println("\n");
    }
    public static boolean liczRoznice(double[][] stare_centroidy, double[][] nowe_centroidy, double próg){
        boolean decision = true;
        for(int i = 0; i < stare_centroidy.length; i++){
            if(!Arrays.equals(stare_centroidy[i], nowe_centroidy[i])) decision = false;
            if(dist(stare_centroidy[i],nowe_centroidy[i]) > próg) decision = false;
        }

        return decision;
    }
    public static int przydzielCentroid(double[][] centroidy, double punkt[]){
        double odleglosc = Double.MAX_VALUE;
        int przydzial = 0;
        for(int i = 0; i < centroidy.length; i++){
            if(dist(centroidy[i],punkt) < odleglosc){
                odleglosc = dist(centroidy[i],punkt);
                przydzial = i;
            }
        }
        suma_odleglosci += odleglosc;
        return przydzial;
    }
    public static double[][] aktualizujCentroidy(int[] przypisane, double[][] data, int k){
        double[][] centroidy = new double[k][data[0].length];
        int[] zlicz = new int[k];
        for(int i = 0; i < data.length; i++){
            centroidy[przypisane[i]] = dodaj(centroidy[przypisane[i]],data[i]);
            zlicz[przypisane[i]] += 1;
        }
        //srednia
        for(int i = 0; i < k; i++){
            for(int j = 0; j < centroidy[i].length; j++){
                centroidy[i][j] /= zlicz[i];
            }
        }
        return centroidy;
    }

    /**
     * Odległość euklidesowa w 2D.
     */
    public static double dist(double[] p, double[] q) {
        double suma = 0;
        for(int i = 0; i < p.length; i++){
            suma += Math.pow((p[i] - q[i]),2);
        }
        return Math.sqrt(suma);
    }
    public static double[] dodaj(double[] p, double[] q){
        for(int i = 0; i < p.length; i++){
            p[i] += q[i];
        }
        return p;
    }

    private static void selfCheck(int[] labels, int k) {
        assert labels != null : "Metoda kMeans zwróciła null (k=" + k + ")";
        assert labels.length == KMeans.DATASET.length : "Nieprawidłowa długość tablicy etykiet (k=" + k + ")";

        final double[][] expected = switch (k) {
            case 2 -> new double[][]{
                    {1.0500, 1.0000},
                    {6.5500, 3.0000}
            };
            case 3 -> new double[][]{
                    {1.0500, 1.0000},
                    {5.0500, 5.0000},
                    {8.0500, 1.0000}
            };
            case 4 -> new double[][]{
                    {1.1000, 0.9667},
                    {5.0500, 5.0000},
                    {8.0500, 1.0000},
                    {0.9000, 1.1000}
            };
            default -> throw new IllegalArgumentException("Brak zdefiniowanego testu dla k=" + k);
        };

        double[][] found = centroidsFromLabels(labels, k);

        if (!matches(expected, found)) {
            throw new IllegalStateException("Wynik algorytmu odbiega od oczekiwanego – sprawdź implementację (k=" + k + ").");
        }
    }

    private static double[][] centroidsFromLabels(int[] labels, int k) {
        double[][] centroids = new double[k][KMeans.DATASET[0].length];
        int[] counts = new int[k];
        for (int i = 0; i < KMeans.DATASET.length; i++) {
            int c = labels[i];
            if (c < 0 || c >= k) {
                throw new IllegalStateException("Etykieta spoza zakresu 0…" + (k - 1));
            }
            counts[c]++;
            centroids[c][0] += KMeans.DATASET[i][0];
            centroids[c][1] += KMeans.DATASET[i][1];
        }
        for (int c = 0; c < k; c++) {
            if (counts[c] == 0) {
                throw new IllegalStateException("Pusty klaster (k=" + k + ", klaster=" + c + ") – upewnij się, że inicjalizacja jest poprawna.");
            }
            centroids[c][0] /= counts[c];
            centroids[c][1] /= counts[c];
        }
        return centroids;
    }

    private static boolean matches(double[][] exp, double[][] found) {
        if (exp.length != found.length) return false;
        boolean[] used = new boolean[found.length];
        for (double[] e : exp) {
            boolean ok = false;
            for (int i = 0; i < found.length; i++) {
                if (!used[i] && dist(e, found[i]) < 0.01) {
                    used[i] = true;
                    ok = true;
                    break;
                }
            }
            if (!ok) return false;
        }
        return true;
    }
}
//
