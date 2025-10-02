const WebSocket = require("ws");
const wss = new WebSocket.Server({ port: 3000 });

wss.on("connection", (ws) => {
  console.log("Client terhubung");
  ws.send("Selamat datang di WebSocket server!");

  ws.on("message", (message) => {
    console.log("Pesan dari client:", message.toString());

    ws.send(`Server menerima: ${message.toString()}`);
  });

  ws.on("close", () => {
    console.log("Client terputus");
  });
});

console.log("WebSocket server started on port 3000");
