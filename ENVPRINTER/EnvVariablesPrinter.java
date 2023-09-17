import java.util.Map;

/**
 *
 * @author Guido Zeuner
 */
public class EnvVariablesPrinter {
    public static void main(String[] args) {
        // Alle Umgebungsvariablen holen
        Map<String, String> env = System.getenv();

        if (args.length > 0) {
            // Wenn Argumente angegeben sind, nur die Werte für diese Schlüssel ausgeben
            for (String key : args) {
                String value = env.get(key);
                if (value != null) {
                    System.out.println(key + " = " + value);
                } else {
                    System.out.println("Kein Wert für Schlüssel: " + key);
                }
            }
        } else {
            // Alle Umgebungsvariablen ausgeben
            for (String key : env.keySet()) {
                System.out.println(key + " = " + env.get(key));
            }
        }
    }
}
