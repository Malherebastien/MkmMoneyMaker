import request.ArticleDownloader;
import request.PriceGuideExtractor;

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
            {
                idProductArray.add(key);
                String article = ArticleDownloader.downloadArticle(key);
                if (article != null)
                    System.out.println("trend : " + priceArray.get(1) + "\tLow : " + priceArray.get(0) + "\t" + article);
            }

        }
    }
    public static void main(String[] args)
    {
        new Launcher();
    }
}
