import java.util.ArrayList;
import java.util.HashMap;

public class Launcher
{
    public Launcher()
    {
        PriceGuideDownloader.downloadFile();
        HashMap<Integer, ArrayList<Double>> map = PriceGuideExtractor.extract("res/base64");

        ArrayList<Double> priceArray;
        ArrayList<Integer> idProductArray = new ArrayList<>();
        for (Integer key : map.keySet())
        {
            priceArray = map.get(key);
            if (priceArray.get(1) > 10.0 && priceArray.get(0) < priceArray.get(1)/2)
                idProductArray.add(key);
        }
    }
    public static void main(String[] args)
    {
        new Launcher();
    }
}
