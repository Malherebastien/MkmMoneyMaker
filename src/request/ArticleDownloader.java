package request;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;
import org.api.mkm.modele.Article;
import org.api.mkm.modele.Link;
import org.api.mkm.modele.Product;
import org.api.mkm.modele.Response;
import request.MkmRequester;

import java.util.List;

public class ArticleDownloader
{
    public static String downloadArticle(Integer idProduct)
    {
        String mkmAppToken = "tMg9qBysWquAh3Vq";
        String mkmAppSecret = "VQdhR0qOblARZb4IxQVI0bTNRPPHEhh9";

        MkmRequester app = new MkmRequester(mkmAppToken, mkmAppSecret);

        app.setDebug(false);

        String requestURL = "https://api.cardmarket.com/ws/v2.0/articles/" + idProduct;

        if (app.request(requestURL))
        {
            XStream xstream = new XStream(new StaxDriver());

            XStream.setupDefaultSecurity(xstream);
            xstream.addPermission(AnyTypePermission.ANY);
            xstream.alias("response", Response.class);
            xstream.addImplicitCollection(Response.class, "links", Link.class);
            xstream.addImplicitCollection(Response.class, "article", Article.class);
            //xstream.addImplicitCollection(Response.class, "product", Product.class);
            xstream.ignoreUnknownElements();

            String xml = app.responseContent();
            Response res = (Response) xstream.fromXML(xml);

            List<Article> article = res.getArticle();
            Product product = article.get(0).getProduct();

            if (article.get(0).getCondition().equals("EX")
                    || article.get(0).getCondition().equals("NM")
                    || article.get(0).getCondition().equals("MT"))
            {
                if (article.get(1).getPrice() > article.get(0).getPrice() * 1.2)
                    return product.getEnName() + " : " + product.getExpansionName();
            }
        }

        return null;
    }
}
