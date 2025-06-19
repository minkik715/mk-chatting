package io.github.minkik715.mkchatting.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.minkik715.mkchatting.ChatCommandDTO;
import io.github.minkik715.mkchatting.ChatHelper;
import io.github.minkik715.mkchatting.ChatMessageBaseDTO;
import io.github.minkik715.mkchatting.GeneralMessageDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static io.github.minkik715.mkchatting.ChatHelper.encrypt;

public class ChatClient {

    public static void main(String[] args) {
        String serverIp = "10.10.10.10";
        int tcpPort = 12000;
        int udpPort = 12000;
        boolean stress = false;
        encrypt = true;
        // 명령행 인자로부터 IP 및 포트 설정
        if (args.length >= 1) serverIp = args[0];
        if (args.length >= 2) stress = Boolean.valueOf(args[1]);
        if (args.length >= 3) encrypt = Boolean.valueOf(args[2]);

        System.out.println(encrypt);
        EventLoopGroup worker = new NioEventLoopGroup();
        CompletableFuture<String> roomId = new CompletableFuture<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("사용할 닉네임을 설정해주세요: ");
            String userId = UUID.randomUUID().toString();
            String nickname = reader.readLine();
            Bootstrap b = new Bootstrap();

            ObjectMapper objectMapper = new ObjectMapper();
            b.group(worker)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new GeneralMessageDecoder<>(objectMapper, ChatMessageBaseDTO.class))
                                    .addLast(new ChatClientHandler(userId, nickname, objectMapper, roomId, reader));
                        }
                    });


            ChannelFuture localhost = b.connect(serverIp, tcpPort).sync();
            Channel channel = localhost.channel();
            UdpHeartbeatClient heartbeatClient = new UdpHeartbeatClient(serverIp, udpPort, userId);
            heartbeatClient.start();
            String selectedRoomId = roomId.get();
            System.out.println("채팅을 입력하세요 >");
            String line;
            while ((line = reader.readLine()) != null) {

                if(stress){
                    int i =0;
                    ChatCommandDTO dto = new ChatCommandDTO(userId, nickname, selectedRoomId, ChatCommandDTO.ChatCommandType.SEND, line);
                    while(i <= 10){
                        Thread.sleep(100);
                        ChatHelper.sendMessage(channel, dto);
                        i++;
                    }
                    continue;
                }
                if (line.equalsIgnoreCase("/exit")) {
                    line = nickname + "님이" + "채팅방에서 나갔습니다.";
                    ChatCommandDTO dto = new ChatCommandDTO(userId, nickname, selectedRoomId, ChatCommandDTO.ChatCommandType.SEND, line);
                    ChatHelper.sendMessage(channel, dto);
                    System.out.println("채팅을 종료합니다.");
                    channel.close().sync();  // 채널 연결 종료
                    worker.shutdownGracefully().sync(); // EventLoopGroup 종료 대기
                    System.exit(0);
                }
                ChatCommandDTO dto = new ChatCommandDTO(userId, nickname, selectedRoomId, ChatCommandDTO.ChatCommandType.SEND, line);
                ChatHelper.sendMessage(channel, dto);
            }

            channel.closeFuture().sync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            worker.shutdownGracefully();
        }
    }

}
