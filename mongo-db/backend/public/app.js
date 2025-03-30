const socket = io();
let currentUser = "";
let currentRoom = "";

// 사용자 이름과 채팅방 설정
function setUserAndRoom() {
  const username = document.getElementById("username").value;
  const room = document.getElementById("roomname").value;

  if (username && room) {
    currentUser = username;
    currentRoom = room;

    // 채팅방 입장
    socket.emit("joinRoom", room);

    // UI 업데이트
    document.getElementById("login-container").style.display = "none";
    document.getElementById("chat-container").style.display = "block";
    document.getElementById("room-title").textContent = `채팅방: ${room}`;
  }
}

// 메시지 전송
function sendMessage() {
  const messageInput = document.getElementById("message-input");
  const messageText = messageInput.value.trim();

  if (messageText) {
    const message = {
      text: messageText,
      user: currentUser,
      room: currentRoom,
    };

    // 서버로 메시지 전송
    socket.emit("sendMessage", message);

    // 입력 필드 초기화
    messageInput.value = "";
  }
}

// 이전 메시지 표시
socket.on("previousMessages", (messages) => {
  const chatMessages = document.getElementById("chat-messages");
  chatMessages.innerHTML = ""; // 기존 메시지 초기화

  messages.forEach((message) => {
    appendMessage(message);
  });

  // 스크롤을 최하단으로 이동
  chatMessages.scrollTop = chatMessages.scrollHeight;
});

// 새 메시지 수신 및 표시
socket.on("newMessage", (message) => {
  appendMessage(message);

  // 스크롤을 최하단으로 이동
  const chatMessages = document.getElementById("chat-messages");
  chatMessages.scrollTop = chatMessages.scrollHeight;
});

// 메시지 요소 생성 및 추가
function appendMessage(message) {
  const chatMessages = document.getElementById("chat-messages");
  const messageElement = document.createElement("div");
  messageElement.className = "message";

  // 본인 메시지와 다른 사용자의 메시지 스타일 구분
  if (message.user === currentUser) {
    messageElement.className += " own-message";
  }

  // 메시지 시간 포맷팅
  const timestamp = new Date(message.timestamp);
  const timeString = timestamp.toLocaleTimeString([], {
    hour: "2-digit",
    minute: "2-digit",
  });

  messageElement.innerHTML = `
    <span class="user">${message.user}</span>
    <span class="text">${message.text}</span>
    <span class="time">${timeString}</span>
  `;

  chatMessages.appendChild(messageElement);
}

// HTML 이벤트 리스너 설정
document.getElementById("join-btn").addEventListener("click", setUserAndRoom);
document.getElementById("send-btn").addEventListener("click", sendMessage);
document.getElementById("message-input").addEventListener("keypress", (e) => {
  if (e.key === "Enter") {
    sendMessage();
  }
});
