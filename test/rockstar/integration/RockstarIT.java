package rockstar.integration;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import rockstar.test.RockstarTest;

/**
 *
 * @author Gabor
 */
public class RockstarIT {

    public static String TEST_PATH = "programs/tests";

    @Test
    public void itTest() {
        // given
        Map<String, String> options = new HashMap<>();
        options.put("-v", "-v");

        System.out.println("IT testttt!");

        // when
        boolean success = new RockstarTest(options).execute(TEST_PATH);

        // then
        Assertions.assertTrue(success, "All tests in " + TEST_PATH + " must pass");
    }

}
