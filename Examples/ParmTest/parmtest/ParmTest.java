package parmtest;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

/**
 *
 * @author Guido Zeuner / MIT License
 */
public class ParmTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        ParmTest parmTest = new ParmTest();

        //Prüfen, ob ein Parameter übergeben wurde
        if (args.length > 0) {
            String arg = args[0].trim();
            //Alle System-Properties anzeigen, wenn Parameter = "list"
            if (arg.equalsIgnoreCase("list")) {
                String[] results = parmTest.getProperties();
                for(String result : results) {
                    System.out.println(result);
                }
            } else {
                //Gewählte System-Property anzeigen, wenn Parameter = [property.name]
                System.out.println(parmTest.getProperty(arg));
            }
        } else {
            //Mögliche Startparameter ausgeben
            System.out.println("Parameter [list] oder [property name]");
        }
    }


    // Used from RPG and Java Code
    public String getProperty(String key) {
        return String.format("%s = %s", key, System.getProperty(key));
    }

    // Used from RPG and Java Code
    public String[] getProperties() {

        ArrayList result = new ArrayList();
        Properties env = System.getProperties();
        Enumeration keys = env.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String value = env.getProperty(key);
            result.add(String.format("%s = %s", key, value));
        }

        String[] props = new String[result.size()];
        result.toArray(props);

        return props;
    }

    // Used from RPG-Code to retrieve the length an Array
    public int getArrayLength(Object[] pArray) {

        int arrayLen = 0;
        if (pArray != null) {
            arrayLen = pArray.length;
        }
        return arrayLen;
    }
}
