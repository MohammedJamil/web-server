import java.util.HashMap;
import java.util.Map;

public class Parser {
    /**
     * Class attributes
     */
    private String[] requestLines;

    /**
     * Class constructor
     */
    Parser(String request) {
        this.requestLines = request.split("\r\n");

    }

    /**
     * Returns the Http method.
     */
    public String getHttpMethod() {
        String[] firstLine = requestLines[0].split(" ");
        return firstLine[0];
    }

    /**
     * Returns the Http method.
     */
    public String getProtocolVersion() {
        String[] firstLine = requestLines[0].split(" ");
        return firstLine[2];
    }

    /**
     * Returns the requested Route.
     */
    public String getRoute() {
        String[] firstLine = requestLines[0].split(" ");
        if (firstLine[1].indexOf("?") != -1) {
            return firstLine[1].split("?")[0];
        } else {
            return firstLine[1];
        }
    }

    /**
     * Returns the query arguments.
     */
    public Map<String, String> getQueryArguments() {
        String[] firstLine = requestLines[0].split(" ");
        String query;
        Map<String, String> arguments = new HashMap<String, String>();

        if (firstLine[1].indexOf("?") != -1) {
            query = firstLine[1].split("?")[1];
            String[] argStrings;

            if (query.indexOf("&") != -1) {
                argStrings = query.split("&");
            } else {
                argStrings = new String[] {query};
            }
        
            for (String argString : argStrings) {
                String[] argArr = argString.split("=");
                arguments.put(argArr[0], argArr[1]);
            }
        }

        return arguments;
    }
}