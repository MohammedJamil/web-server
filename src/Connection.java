import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;


public class Connection implements Callable<String> {

    /**
     * Class attributes
     */
    private String id;
    private Socket socket;
    private LineCounter counter;

    /**
     * Class Constructor
     */
    Connection(Socket socket, int id, LineCounter counter) {
        this.socket = socket;
        this.id = String.valueOf(id);
        this.counter = counter;
    }

    /**
     * Counts the number of lines in a http request.
     */
    public int countLines(String request) {
        char lineBreakChar = '\n';
        int count = 0;
 
        for (int i = 0; i < request.length(); i++) {
            if (request.charAt(i) == lineBreakChar) {
                count++;
            }
        }

        return (count == 0) ? count : count - 1;
    }

    /**
     * Reads the socket, counts the number of lines and returns the http request.
     */
    public String readSocket() {
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

        int nbLines = countLines(request);
        counter.incrementByNbLines(nbLines);
        
        return request;
    }

    /**
     * Cherche the wanted file and load transform it into an array of bytes
     */
    public String convertFileToString(String path) {
        File file = new File(path);
        byte[] fileBytes = new byte[(int) file.length()];

        try (FileInputStream in = new FileInputStream(file)) {
            in.read(fileBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return new String(fileBytes);
    }

    /**
     * Writes in the socket.
     */
    public void writeSocket(String res) {
        String endMarker = "\r\n\r\n";
        res = res + endMarker;

        try (OutputStream out = socket.getOutputStream()) {
            out.write(res.getBytes());
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    /**
     * Processes the request.
     */
    public void processRequest(String request) {
        Parser parser = new Parser(request);
        String path = ServerApp.ressourcePath + parser.getRoute();
        File file =  new File(path);

        if(!file.exists()) {
            String notFoundString = "<!DOCTYPE html><html><head><title>404 Not Found</title></head><body><h1>404 Not Found</h1></body></html>";
            String res = "HTTP/1.1 404 Not Found\r\n\r\n";
            writeSocket(res + notFoundString);
        } else {
            String res = "HTTP/1.1 200 OK\r\n\r\n" + convertFileToString(path);
            writeSocket(res);
        }
    }

    /**
     * 
     */
    @Override
    public String call() throws Exception {
        String request = readSocket();
        System.out.println(String.format("%s[Thread %s] Client request:\n%s%s", ServerApp.ANSI_YELLOW, id, ServerApp.ANSI_RESET, request));
        //Thread.sleep(10*1000);
        processRequest(request);
        System.out.println(String.format("%s[Thread %s] Thread terminated.%s", ServerApp.ANSI_YELLOW, id, ServerApp.ANSI_RESET));
        
        return id;
    }
}