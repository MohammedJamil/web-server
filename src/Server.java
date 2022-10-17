import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;


public class Server {
    /**
     * Class attributes.
     */
    private ServerSocket serverSocket;
    private AdminServer admin;
    private ExecutorService pool;
    private Queue<Future<String>> list;
    private LineCounter counter;

    /**
     * Class constructor.
     */
    Server(int myPort, int adminPort) {
        try {
            serverSocket = new ServerSocket(myPort);
            admin = new AdminServer(adminPort, serverSocket);
            pool = Executors.newFixedThreadPool(ServerApp.MaxThread);
            list = new LinkedBlockingQueue<>();
            counter = new LineCounter();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main server loop.
     */
    public void start() {

        try {
            Future<String> res = pool.submit(admin);
            list.add(res);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int connId = 0;
        
        while(!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                connId++;
                System.out.println(String.format("%s[Main Thread] Connection received ...%s", ServerApp.ANSI_GREEN, ServerApp.ANSI_RESET));
                Connection connection = new Connection(socket, connId, counter);
                Future<String> res = pool.submit(connection);
                list.add(res);
            } catch (IOException e) {
                System.out.println(String.format("%s[Main Thread] ServerSocket closed.%s", ServerApp.ANSI_GREEN, ServerApp.ANSI_RESET));
            }
        }

        Future<String> res;

        while ((res = list.poll()) != null) {
            try {
                res.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        pool.shutdown();
        System.out.println(String.format("%s[Main Thread] Line count :%d%s", ServerApp.ANSI_GREEN, counter.getNbLines(), ServerApp.ANSI_RESET));
        System.out.println(String.format("%s[Main Thread] Main Thread terminated.%s", ServerApp.ANSI_GREEN, ServerApp.ANSI_RESET));
    }
}