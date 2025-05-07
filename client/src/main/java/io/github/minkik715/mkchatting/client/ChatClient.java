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

public class ChatClient {

    public static void main(String[] args) {

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


            ChannelFuture localhost = b.connect("192.168.219.102", 8080).sync();
            Channel channel = localhost.channel();

            String selectedRoomId = roomId.get();
            System.out.println("채팅을 입력하세요 >");
            String line;
            while ((line = reader.readLine()) != null) {
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
