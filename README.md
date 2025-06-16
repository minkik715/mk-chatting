# 💬 Chatting Server

Netty 기반의 간단한 다중 채팅 서버입니다.

---

## 🚀 서버 실행 방법

1. **서버 IP 확인**

서버가 실행될 컴퓨터에서 아래 명령어를 통해 로컬 IP를 확인하세요.

- Windows:
  ipconfig

  

예시 출력:
IPv4 주소: 192.168.219.102


2. **서버 실행**

```bash
./gradlew build
java -cp build/libs/[생성된 JAR 파일 이름].jar io.github.minkik715.mkchatting.server.ChatServer


💻 클라이언트 실행 방법
ChatClient.java에서 서버 IP 수정


b.connect("192.168.219.102", 12000).sync(); // ← 서버 IP로 수정
클라이언트 실행


./gradlew build
java -cp build/libs/[생성된 JAR 파일 이름].jar io.github.minkik715.mkchatting.client.ChatClient

서버 포트포워딩을 통해서 포워딩 예정

```

3. **서버 실행**          
intelij로 구동시키면 편함