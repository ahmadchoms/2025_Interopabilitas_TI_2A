import asyncio
import websockets

async def chat():
    uri = "ws://localhost:3000"
    async with websockets.connect(uri) as websocket:
        print("✅ Terhubung ke server WebSocket")
        while True:
            msg = input("Ketik pesan: ")
            await websocket.send(msg)
            print(f"📤 Terkirim: {msg}")
            response = await websocket.recv()
            print(f"📥 Diterima: {response}")

asyncio.run(chat())