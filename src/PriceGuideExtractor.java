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

            String lignePleine;
            String[] ligneSplit;
            //ArrayList<Double> doubleArray = new ArrayList<>();
            sc.nextLine(); // On ne traite pas la première ligne
            while (sc.hasNext())
            {
                lignePleine = sc.nextLine() + ";";
                ligneSplit = lignePleine.split(",");
                ArrayList<Double> doubleArray = new ArrayList<>();
                // On sort si le trendPrice ou le low Price sont vides.
                if (ligneSplit[2].equals("") || ligneSplit[3].equals(""))
                    continue;

                // On ajoute dans l'arrayList le trendPrice et le low Price (colonnes 3 et 4)
                doubleArray.add(Double.parseDouble(ligneSplit[2]));
                doubleArray.add(Double.parseDouble(ligneSplit[3]));
                map.put(Integer.parseInt(ligneSplit[0]), doubleArray);
            }
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } finally
        {
            sc.close();
        }

        return map;
    }
}
