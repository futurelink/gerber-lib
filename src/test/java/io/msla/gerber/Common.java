package io.msla.gerber;

import org.junit.jupiter.api.BeforeAll;

public class Common {
    @BeforeAll
    static void setUpBeforeClass() {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT | %4$s | %2$s -> %5$s%6$s%n");
    }

    protected String resourceFile(String resourceName) {
        var classLoader = getClass().getClassLoader();
        var resource = classLoader.getResource(resourceName);
        if (resource == null) throw new RuntimeException("Resource data file not found");
        return resource.getFile();
    }
}
