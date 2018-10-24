import java.util.ArrayList;
import java.util.HashMap;

public class Launcher
{
    public Launcher()
    {
        ArticleChecker.downloadFile();
        HashMap<Integer, ArrayList<Double>> map = PriceGuideExtractor.extract("res/base64");


        ArrayList<Double> arrayList;
        System.out.println(map.keySet().size());
        for (Integer key : map.keySet())
        {
            arrayList = map.get(key);
            if (arrayList.get(1) > 10.0 && arrayList.get(0) < arrayList.get(1)/2)
                System.out.println(key + " : " + map.get(key));
        }
    }
    public static void main(String[] args)
    {
        new Launcher();
    }
}
