package spark.servlet;

import spark.Spark;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MyApp implements SparkApplication {

    @Override
    public void init() {
        Spark spark = new Spark();
        try {
            spark.externalStaticFileLocation(System.getProperty("java.io.tmpdir"));
            spark.staticFileLocation("/public");

            File tmpExternalFile = new File(System.getProperty("java.io.tmpdir"), "externalFile.html");
            FileWriter writer = new FileWriter(tmpExternalFile);
            writer.write("Content of external file");
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        spark.before("/protected/*", (request, response) -> {
            spark.halt(401, "Go Away!");
        });

        spark.get("/hi", (request, response) -> {
            return "Hello World!";
        });

        spark.get("/:param", (request, response) -> {
            return "echo: " + request.params(":param");
        });

        spark.get("/", (request, response) -> {
            return "Hello Root!";
        });

        spark.post("/poster", (request, response) -> {
            String body = request.body();
            response.status(201); // created
            return "Body was: " + body;
        });

        spark.after("/hi", (request, response) -> {
            response.header("after", "foobar");
        });

        try {
            Thread.sleep(500);
        } catch (Exception e) {
        }
    }

}
