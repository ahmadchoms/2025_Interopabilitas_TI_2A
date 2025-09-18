import org.apache.xmlrpc.WebServer;

public class Main {
    public static void main(String[] args) {
        WebServer server = new WebServer(1717);

        server.addHandler("server", new Kalkulator());

        server.start();
        System.out.println("Server berjalan pada port 1717...");
    }
}