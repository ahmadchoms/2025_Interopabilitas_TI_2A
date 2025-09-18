import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class RPCClient {
    private final PrintStream printStream;

    @SuppressWarnings("CallToThreadStartDuringObjectConstruction")
    public RPCClient(String ipAddress, int port) throws IOException {
        Socket rpcClient = new Socket(ipAddress, port);

        new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(rpcClient.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("Server response : " + line);
                    System.out.print("\nCommands [add, sub, mul, div, mod, exit] : ");
                }
            } catch (IOException ex) {
                System.err.println("\nDisconnected!");
                System.exit(0);
            }
        }).start();

        this.printStream = new PrintStream(rpcClient.getOutputStream(), true);
    }

    public void sendMessage(String operation) {
        Scanner scan = new Scanner(System.in);

        System.out.print("\nEnter 1st number : ");
        int firstNumber = scan.nextInt();

        System.out.print("Enter 2nd number : ");
        int secondNumber = scan.nextInt();

        this.printStream.println(operation + ":" + firstNumber + ":" + secondNumber);
    }

    public static void main(String[] args) {
        try {
            Scanner scan = new Scanner(System.in);

            System.out.print("Enter server IP address : ");
            String ipAddress = scan.nextLine();

            System.out.print("Enter connection port : ");
            int port = scan.nextInt();
            scan.nextLine();

            RPCClient client = new RPCClient(ipAddress, port);
            System.out.println("\nConnected to server\n");
            System.out.print("Commands [add, sub, mul, div, mod, exit] : ");

            while (true) {
                String command = scan.nextLine();

                if (command.equals("exit")) {
                    System.exit(0);
                }
                client.sendMessage(command);
                System.out.print("\n");
            }
        } catch (IOException ex) {
            System.err.println("\nUnable to connect!");
        }
    }
}