import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RPCServer {
    private final ServerSocket serverSocket;
    private static final Logger LOGGER = Logger.getLogger(RPCServer.class.getName());

    @SuppressWarnings("CallToThreadStartDuringObjectConstruction")
    public RPCServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        String localIP = InetAddress.getLocalHost().getHostAddress();
        System.out.println("Server is running on " + localIP + ":" + port);

        while (!serverSocket.isClosed()) {
            Socket rpcClient = serverSocket.accept();
            String address = rpcClient.getRemoteSocketAddress().toString();
            System.out.println("New client connected: " + address);

            new Thread(() -> {
                try {
                    addHook(rpcClient);
                } catch (IOException ex) {
                    LOGGER.log(Level.INFO, "Client disconnected: {0}", address);
                } finally {
                    try {
                        rpcClient.close();
                    } catch (IOException e) {
                        LOGGER.log(Level.SEVERE, "Error closing client socket", e);
                    }
                }
            }).start();
        }
    }

    private void addHook(Socket rpcClient) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(rpcClient.getInputStream()));
                PrintStream printStream = new PrintStream(rpcClient.getOutputStream(), true)) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Client request: " + line);

                String[] commands = line.split(":", 3);

                if (commands.length < 3) {
                    printStream.println("Error: Invalid command format. Use 'operation:operand1:operand2'");
                    continue;
                }

                String operation = commands[0];
                int operand1, operand2;

                try {
                    operand1 = Integer.parseInt(commands[1]);
                    operand2 = Integer.parseInt(commands[2]);
                } catch (NumberFormatException e) {
                    printStream.println("Error: Operands must be valid integers.");
                    continue;
                }

                String message;
                int result;

                switch (operation) {
                    case "add":
                        result = (operand1 + operand2);
                        message = String.format("%d + %d = %d", operand1, operand2, result);
                        break;
                    case "sub":
                        result = (operand1 - operand2);
                        message = String.format("%d - %d = %d", operand1, operand2, result);
                        break;
                    case "mul":
                        result = (operand1 * operand2);
                        message = String.format("%d * %d = %d", operand1, operand2, result);
                        break;
                    case "div":
                        if (operand2 == 0) {
                            message = "Error: Division by zero is not allowed.";
                        } else {
                            result = (operand1 / operand2);
                            message = String.format("%d / %d = %d", operand1, operand2, result);
                        }
                        break;
                    case "mod":
                        if (operand2 == 0) {
                            message = "Error: Modulo by zero is not allowed.";
                        } else {
                            result = (operand1 % operand2);
                            message = String.format("%d %% %d = %d", operand1, operand2, result);
                        }
                        break;
                    default:
                        message = "Error: Unknown operation.";
                        break;
                }
                printStream.println(message);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new RPCServer(3000);
    }
}