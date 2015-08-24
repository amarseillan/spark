package spark;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.util.SparkTestUtil;

import java.util.concurrent.CountDownLatch;

/**
 * Created by amarseillan on 8/24/15.
 */
public class TwoServersTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwoServersTest.class);

    private static final String BODY_CONTENT1 = "1";
    private static final String BODY_CONTENT2 = "2";

    private static SparkTestUtil testUtil1;
    private static SparkTestUtil testUtil2;

    private static Integer PORT1 = 4567;
    private static Integer PORT2 = 4568;

    private static String beforeBody = null;
    private static String routeBody = null;
    private static String afterBody = null;
    private static Spark spark1;
    private static Spark spark2;

    private static CountDownLatch latch = new CountDownLatch(1);

    @AfterClass
    public static void tearDown() {
        spark1.stop();
        spark2.stop();
    }

    @BeforeClass
    public static void setup() throws InterruptedException {
        LOGGER.debug("setup()");

        spark1 = new Spark();
        spark2 = new Spark();

        spark1.port(PORT1);
        spark2.port(PORT2);

        testUtil1 = new SparkTestUtil(PORT1);
        testUtil2 = new SparkTestUtil(PORT2);

        spark1.get("/test", (req, res) -> {
            return BODY_CONTENT1;
        });
        spark1.awaitInitialization();

        spark2.get("/test", (req, res) -> {
            return BODY_CONTENT2;
        });
        spark2.awaitInitialization();
    }

    @Test
    public void testPost() {
        try {
            SparkTestUtil.UrlResponse response1 = testUtil1.doMethod("GET", "/test", "");
            LOGGER.info("response 1 is: " + response1.body);
            Assert.assertEquals(200, response1.status);
            Assert.assertTrue(response1.body.contains(BODY_CONTENT1));

            SparkTestUtil.UrlResponse response2 = testUtil2.doMethod("GET", "/test", "");
            LOGGER.info("response 2 is: " + response2.body);
            Assert.assertEquals(200, response2.status);
            Assert.assertTrue(response2.body.contains(BODY_CONTENT2));


        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
