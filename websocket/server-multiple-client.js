const WebSocket = require("ws");
const wss = new WebSocket.Server({ port: 3000 });

console.log("WebSocket server started on port 3000");

wss.on("connection", (ws) => {
  console.log("✅ A new client has connected.");

  const welcomeMessage = JSON.stringify({
    from: "server",
    message: "Selamat datang! Kamu telah terhubung ke server chat.",
  });
  ws.send(welcomeMessage);

  ws.on("message", (rawMessage) => {
    console.log(`📩 Received raw message: ${rawMessage}`);

    let parsed;
    try {
      parsed = JSON.parse(rawMessage);
    } catch (err) {
      console.error("❌ Invalid JSON format:", err.message);
      ws.send(
        JSON.stringify({
          from: "server",
          message: "Format pesan tidak valid. Harus berupa JSON.",
        })
      );
      return;
    }

    const { from, message } = parsed;
    if (!from || !message) {
      ws.send(
        JSON.stringify({
          from: "server",
          message: "Pesan harus memiliki properti 'from' dan 'message'.",
        })
      );
      return;
    }

    console.log(`💬 ${from}: ${message}`);

    wss.clients.forEach((client) => {
      if (client.readyState === WebSocket.OPEN) {
        client.send(JSON.stringify({ from, message }));
      }
    });
  });

  ws.on("close", () => {
    console.log("⚠️ A client has disconnected.");
  });
});
