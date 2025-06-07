import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        try {
            List<String> test = Files.readAllLines(Path.of("iris.data"));
            double[][] data = new double[test.size()][];
            int k = 3;
            int indeks = 0;
            for(String linijka : test){
                String[] podziel = linijka.split(",");
                double[] wartosci = new double[podziel.length-1];
                for(int i = 0; i < wartosci.length; i++){
                    wartosci[i] = Double.parseDouble(podziel[i]);
                }
                data[indeks++] = wartosci;
            }
            int[] przypisane = kmeans.KMeans.kMeans(data,k,1000,1e-4);
            kmeans.KMeans.wypiszGrupy(przypisane,test,k);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}