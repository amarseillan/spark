package spark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Per Wendel on 2014-05-10.
 */
public class LambdaTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(LambdaTest.class);
    public static void main(String[] args) {
        Spark spark = new Spark();
        spark.get("/hello", (request, response) -> {
            LOGGER.info("request = " + request.pathInfo());
            return "Hello World";
        });

        spark.before("/protected/*", "application/xml", (request, response) -> {
            spark.halt(401, "<xml>fuck off</xml>");
        });

        spark.before("/protected/*", "application/json", (request, response) -> {
            spark.halt(401, "{\"message\": \"Go Away!\"}");
        });

    }

}
