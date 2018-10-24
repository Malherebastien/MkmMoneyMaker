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
import java.util.HashMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class ArticleChecker {

    private String _mkmAppToken;
    private String _mkmAppSecret;
    private String _mkmAccessToken;
    private String _mkmAccessTokenSecret;

    private Throwable _lastError;
    private int _lastCode;
    private String _lastContent;
    private boolean _debug;

    /**
     * Constructor. Fill parameters according to given MKM profile app parameters.
     *
     * @param appToken
     * @param appSecret
     */
    public ArticleChecker(String appToken, String appSecret) {
        _mkmAppToken = appToken;
        _mkmAppSecret = appSecret;
        _mkmAccessToken = "";
        _mkmAccessTokenSecret = "";

        _lastError = null;
        _debug = false;
    }

    /**
     * Activates the console debug messages
     * @param flag true if you want to enable console messages; false to disable any notification.
     */
    public void setDebug(boolean flag) {
        _debug = flag;
    }

    /**
     * Encoding function. To avoid deprecated version, the encoding used is UTF-8.
     * @param str
     * @return
     * @throws UnsupportedEncodingException
     */
    private String rawurlencode(String str) throws UnsupportedEncodingException {
        return URLEncoder.encode(str, "UTF-8");
    }

    private void _debug(String msg) {
        if (_debug) {
            System.out.print(GregorianCalendar.getInstance().getTime());
            System.out.print(" > ");
            System.out.println(msg);
        }
    }

    /**
     * Get last Error exception.
     * @return null if no errors; instead the raised exception.
     */
    public Throwable lastError() {
        return _lastError;
    }

    /**
     * Perform the request to given url with OAuth 1.0a API.
     *
     * @param requestURL url to be requested. Ex. https://api.cardmarket.com/ws/v1.1/products/island/1/1/false
     * @return true if request was successfully executed. You can retrieve the content with responseContent();
     */
    public boolean request(String requestURL) {
        _lastError = null;
        _lastCode = 0;
        _lastContent = "";
        try {

            _debug("Requesting "+requestURL);

            String realm = requestURL ;
            String oauth_version = "1.0" ;
            String oauth_consumer_key = _mkmAppToken ;
            String oauth_token = _mkmAccessToken ;
            String oauth_signature_method = "HMAC-SHA1" ;
            // String oauth_timestamp = "" + (System.currentTimeMillis()/1000) ;
            String oauth_timestamp = "1407917892" ;
            // String oauth_nonce = "" + System.currentTimeMillis() ;
            String oauth_nonce = "53eb1f44909d6" ;


            String encodedRequestURL = rawurlencode(requestURL) ;

            String baseString = "GET&" + encodedRequestURL + "&" ;

            String paramString = "oauth_consumer_key=" + rawurlencode(oauth_consumer_key) + "&" +
                    "oauth_nonce=" + rawurlencode(oauth_nonce) + "&" +
                    "oauth_signature_method=" + rawurlencode(oauth_signature_method) + "&" +
                    "oauth_timestamp=" + rawurlencode(oauth_timestamp) + "&" +
                    "oauth_token=" + rawurlencode(oauth_token) + "&" +
                    "oauth_version=" + rawurlencode(oauth_version) ;

            baseString = baseString + rawurlencode(paramString) ;

            String signingKey = rawurlencode(_mkmAppSecret) +
                    "&" +
                    rawurlencode(_mkmAccessTokenSecret) ;

            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec secret = new SecretKeySpec(signingKey.getBytes(), mac.getAlgorithm());
            mac.init(secret);
            byte[] digest = mac.doFinal(baseString.getBytes());
            String oauth_signature = DatatypeConverter.printBase64Binary(digest);    //Base64.encode(digest) ;

            String authorizationProperty =
                    "OAuth " +
                            "realm=\"" + realm + "\", " +
                            "oauth_version=\"" + oauth_version + "\", " +
                            "oauth_timestamp=\"" + oauth_timestamp + "\", " +
                            "oauth_nonce=\"" + oauth_nonce + "\", " +
                            "oauth_consumer_key=\"" + oauth_consumer_key + "\", " +
                            "oauth_token=\"" + oauth_token + "\", " +
                            "oauth_signature_method=\"" + oauth_signature_method + "\", " +
                            "oauth_signature=\"" + oauth_signature + "\"" ;

            HttpURLConnection connection = (HttpURLConnection) new URL(requestURL).openConnection();
            connection.addRequestProperty("Authorization", authorizationProperty) ;
            connection.connect() ;

            // from here standard actions...
            // read response code... read input stream.... close connection...

            _lastCode = connection.getResponseCode();

            _debug("Response Code is "+_lastCode);

            if (200 == _lastCode || 401 == _lastCode || 404 == _lastCode) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(_lastCode==200?connection.getInputStream():connection.getErrorStream()));
                StringBuffer sb = new StringBuffer();
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
                _lastContent = sb.toString();
                _debug("Response Content is \n"+_lastContent);
            }

            return (_lastCode == 200);

        } catch (Exception e) {
            _debug("(!) Error while requesting "+requestURL);
            _lastError = e;
        }
        return false;
    }

    /**
     * Get response code from last request.
     * @return
     */
    public int responseCode() {
        return _lastCode;
    }

    /**
     * Get response content from last request.
     * @return
     */
    public String responseContent() {
        return _lastContent;
    }

    public static void downloadFile()
    {
        // USAGE EXAMPLE

        String mkmAppToken = "tMg9qBysWquAh3Vq";
        String mkmAppSecret = "VQdhR0qOblARZb4IxQVI0bTNRPPHEhh9";


        ArticleChecker app = new ArticleChecker(mkmAppToken, mkmAppSecret);

        app.setDebug(true);
        /*if (app.request("https://api.cardmarket.com/ws/v2.0/priceguide"))
        {
            //if (app.request("https://api.cardmarket.com/ws/v2.0/articles/22510")) {
            System.out.println(app.responseContent());
        }*/

        String requestURL = "https://api.cardmarket.com/ws/v2.0/priceguide";

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