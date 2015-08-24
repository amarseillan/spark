package spark.examples.hello;

import spark.Spark;

/**
 * You'll need to provide a JKS keystore as arg 0 and its password as arg 1.
 */
public class HelloSecureWorld {
    public static void main(String[] args) {

        Spark spark = new Spark();

        spark.secure(args[0], args[1], null, null);
        spark.get("/hello", (request, response) -> {
            return "Hello Secure World!";
        });

    }
}
