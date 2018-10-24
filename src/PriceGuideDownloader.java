import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;
import org.apache.commons.codec.binary.Base64;
import org.api.mkm.modele.Article;
import org.api.mkm.modele.Link;
import org.api.mkm.modele.Response;
import org.api.mkm.tools.Tools;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.GregorianCalendar;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class PriceGuideDownloader
{

    public static void downloadFile()
    {
        // USAGE EXAMPLE

        String mkmAppToken = "tMg9qBysWquAh3Vq";
        String mkmAppSecret = "VQdhR0qOblARZb4IxQVI0bTNRPPHEhh9";


        MkmRequester app = new MkmRequester(mkmAppToken, mkmAppSecret);

        app.setDebug(true);
        /*if (app.request("https://api.cardmarket.com/ws/v2.0/priceguide"))
        {
            //if (app.request("https://api.cardmarket.com/ws/v2.0/articles/22510")) {
            System.out.println(app.responseContent());
        }*/

        String requestURL = "https://api.cardmarket.com/ws/v2.0/priceguide";
        //String requestURL = "https://api.cardmarket.com/ws/v2.0/articles/21369";

        if (app.request(requestURL))
        {
            XStream xstream = new XStream(new StaxDriver());

            XStream.setupDefaultSecurity(xstream);
            xstream.addPermission(AnyTypePermission.ANY);
            xstream.alias("response", Response.class);
            xstream.addImplicitCollection(Response.class, "links", Link.class);
            xstream.addImplicitCollection(Response.class, "article", Article.class);
            xstream.ignoreUnknownElements();

            String xml = app.responseContent();
            Response res = (Response) xstream.fromXML(xml);

            xml = res.getPriceguidefile();

            try
            {
                FileWriter fileWriter = new FileWriter("res/encodedString.txt");
                fileWriter.write(xml);
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            try
            {
                decode("res/encodedString.txt", "res/base64.gz");
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            String targetFile = "res/base64";
            try
            {
                Tools.unzip(new File("res/base64.gz"), new File(targetFile));
            } catch (IOException e)
            {
                e.printStackTrace();
            }

        }
    }

    public static void decode(String sourceFile, String targetFile) throws Exception {

        byte[] decodedBytes = Base64.decodeBase64(loadFileAsBytesArray(sourceFile));

        writeByteArraysToFile(targetFile, decodedBytes);
    }

    public static void writeByteArraysToFile(String fileName, byte[] content) throws IOException {

        File file = new File(fileName);
        BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(file));
        writer.write(content);
        writer.flush();
        writer.close();

    }

    public static byte[] loadFileAsBytesArray(String fileName) throws Exception {

        File file = new File(fileName);
        int length = (int) file.length();
        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
        byte[] bytes = new byte[length];
        reader.read(bytes, 0, length);
        reader.close();
        return bytes;

    }
}