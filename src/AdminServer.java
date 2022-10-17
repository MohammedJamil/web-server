import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;


public class AdminServer implements Callable<String>{
    /**
     * Class attributes
     */
    private ServerSocket adminSocket;
    private ServerSocket serverSocket;
    private String id = "Admin";

    /**
     * Class constructor
     */
    AdminServer (int adminPort, ServerSocket serverSocket) {
        try {
            this.adminSocket = new ServerSocket(adminPort);
            this.serverSocket = serverSocket;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the socket and returns the http request.
     */
    public String readSocket(Socket socket) {
        String request = "";
        try {
            InputStream in = socket.getInputStream();
            byte[] buffer = new byte[1024];
            while(request.indexOf("\r\n\r\n") == -1) {
                in.read(buffer);
                request = request + new String(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return request;
    }

    /**
     * Writes in the socket.
     */
    public void writeSocket(Socket socket, String msg) {
        
        msg = msg + "\r\n\r\n";

        try (OutputStream out = socket.getOutputStream()) {
            out.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * Closes the server.
     */
    public void closeServerSocket(){
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     */
    @Override
    public String call() {
        try (Socket socket = adminSocket.accept()) {
            String request = readSocket(socket);
            Parser parser = new Parser(request);
            String route = parser.getRoute();

            switch (route) {
                case "/admin/quit" :
                    closeServerSocket();
                    writeSocket(socket, "Server Successfully Closed.");
                    break;
                default:
                    assert false : "UnknownRoute Execption.";
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(String.format("%s[Admin Thread] Thread %s terminated.%s", ServerApp.ANSI_GREEN, id, ServerApp.ANSI_RESET));
        return id;
    }
}