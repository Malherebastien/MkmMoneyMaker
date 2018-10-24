import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class PriceGuideExtractor
{
    /**
     * Extrait le fichier priceguide dans une HashMap avec pour clé son IdProduct.
     * @param targetFile le fichier contenant le priceguide en CSV (devrait s'appeler base64)
     * @return La HashMap extraite
     */
    public static HashMap<Integer, ArrayList<Double>> extract(String targetFile)
    {
        Scanner sc = null;

        HashMap<Integer, ArrayList<Double>> map = new HashMap<>();
        try
        {
           sc = new Scanner(new File(targetFile));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } finally
        {
            sc.close();
        }

        String[] ligne;
        ArrayList<Double> doubleArray = new ArrayList<>();
        while (sc.hasNext())
        {
            ligne = sc.nextLine().split(",");
            doubleArray.clear();
            // On sort si le trendPrice ou le low Price sont vides.
            if (ligne[3].equals("") || ligne[4].equals(""))
                break;

            // On ajoute dans l'arrayList le trendPrice et le low Price (colonnes 3 et 4)
            for (int i = 1; i < ligne.length; i++)
            {
                doubleArray.add(Double.parseDouble(ligne[3]));
                doubleArray.add(Double.parseDouble(ligne[4]));
            }
            map.put(Integer.parseInt(ligne[0]), doubleArray);
        }
        return map;
    }
}
