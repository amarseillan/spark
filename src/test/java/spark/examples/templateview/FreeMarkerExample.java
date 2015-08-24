package spark.examples.templateview;

import java.util.HashMap;
import java.util.Map;

import spark.ModelAndView;
import spark.Spark;


public class FreeMarkerExample {

    public static void main(String args[]) {

        Spark spark = new Spark();

        spark.get("/hello", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("message", "Hello FreeMarker World");

            // The hello.ftl file is located in directory:
            // src/test/resources/spark/examples/templateview/freemarker
            return new ModelAndView(attributes, "hello.ftl");
        }, new FreeMarkerTemplateEngine());

    }

}