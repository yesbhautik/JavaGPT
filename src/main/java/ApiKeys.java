import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApiKeys {
    public static final String OPENAI_API_KEY;

    static {
        String apiKey = "demo"; // default or fallback value
        try (InputStream input = ApiKeys.class.getClassLoader().getResourceAsStream("api.properties")) {
            Properties prop = new Properties();
            if (input != null) {
                prop.load(input);
                apiKey = prop.getProperty("openai.api.key", apiKey);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        OPENAI_API_KEY = apiKey;
    }
}