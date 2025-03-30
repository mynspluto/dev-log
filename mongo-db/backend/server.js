import express from "express";
import { MongoClient } from "mongodb";
import http from "http";
import { Server } from "socket.io";
import path from "path";
import { fileURLToPath } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const app = express();
const server = http.createServer(app);
const io = new Server(server, {
  cors: {
    origin: "*",
    methods: ["GET", "POST"],
  },
});

app.use(express.static(path.join(__dirname, "public")));

const uri = "mongodb://localhost:27017";
const client = new MongoClient(uri);
const dbName = "chatApp";
const collectionName = "messages";

async function startServer() {
  try {
    await client.connect();
    console.log("MongoDB에 연결되었습니다");

    const db = client.db(dbName);
    const messagesCollection = db.collection(collectionName);

    io.on("connection", (socket) => {
      console.log("새로운 클라이언트가 연결되었습니다:", socket.id);

      socket.on("sendMessage", async (message) => {
        const messageDoc = {
          text: message.text,
          user: message.user,
          room: message.room,
          timestamp: new Date(),
        };

        await messagesCollection.insertOne(messageDoc);
      });

      // 특정 채팅방 입장
      socket.on("joinRoom", (room) => {
        socket.join(room);
        console.log(`클라이언트 ${socket.id}가 ${room} 방에 입장했습니다`);

        // 해당 방의 이전 메시지 가져오기
        messagesCollection
          .find({ room: room })
          .sort({ timestamp: -1 })
          .limit(50)
          .toArray()
          .then((messages) => {
            socket.emit("previousMessages", messages.reverse());
          });

        // Change Stream 설정 - 특정 채팅방의 메시지만 필터링
        const changeStream = messagesCollection.watch(
          [
            {
              $match: {
                "fullDocument.room": room,
              },
            },
          ],
          { fullDocument: "updateLookup" }
        );

        // 변경 사항(새 메시지) 감지 시 해당 채팅방에 브로드캐스트
        changeStream.on("change", (change) => {
          if (change.operationType === "insert") {
            io.to(room).emit("newMessage", change.fullDocument);
          }
        });

        // 클라이언트 연결 해제 시 Change Stream 닫기
        socket.on("disconnect", () => {
          changeStream.close();
          console.log(`클라이언트 ${socket.id}가 연결을 해제했습니다`);
        });
      });
    });

    // 서버 시작
    const PORT = process.env.PORT || 3000;
    server.listen(PORT, () => {
      console.log(`서버가 포트 ${PORT}에서 실행 중입니다`);
    });
  } catch (err) {
    console.error("서버 시작 중 오류 발생:", err);
  }
}

startServer();
