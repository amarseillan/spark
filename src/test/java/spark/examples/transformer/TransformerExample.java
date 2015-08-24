package spark.examples.transformer;


import spark.Spark;

public class TransformerExample {

    public static void main(String args[]) {
        Spark spark = new Spark();

        spark.get("/hello", "application/json", (request, response) -> {
            return new MyMessage("Hello World");
        }, new JsonTransformer());
    }

}
