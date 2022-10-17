public class ServerApp {

    /* Global Variables */
    private static int myPort = 2048;
    private static int adminPort = 3072;
    protected static int MaxThread = 10;
    protected static String ressourcePath = "./public";

    /* Colors */
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    /* ServerApp main */
    public static void main(String[] args)  {
        Server server = new Server(myPort, adminPort);
        server.start();
    }
}