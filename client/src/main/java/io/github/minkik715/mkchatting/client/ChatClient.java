package io.github.minkik715.mkchatting.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.minkik715.mkchatting.ChatCommandDTO;
import io.github.minkik715.mkchatting.ChatHelper;
import io.github.minkik715.mkchatting.ChatMessageBaseDTO;
import io.github.minkik715.mkchatting.GeneralMessageDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

public class ChatClient {

    public static void main(String[] args) {

        EventLoopGroup worker = new NioEventLoopGroup();
        String userId = args.length > 0 ? args[0] : UUID.randomUUID().toString();
        CompletableFuture<String> roomId = new CompletableFuture<>();
        try {
            Bootstrap b = new Bootstrap();
            ObjectMapper objectMapper = new ObjectMapper();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            b.group(worker)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new GeneralMessageDecoder<>(objectMapper, ChatMessageBaseDTO.class))
                                    .addLast(new ChatClientHandler(userId, objectMapper, roomId, reader));
                        }
                    });

            ChannelFuture localhost = b.connect("192.168.219.102", 8080).sync();
            Channel channel = localhost.channel();

            String selectedRoomId = roomId.get();
            System.out.println("채팅을 입력하세요:");
            String line;
            while ((line = reader.readLine()) != null) {
                ChatCommandDTO dto = new ChatCommandDTO(userId, selectedRoomId, ChatCommandDTO.ChatCommandType.SEND, line);
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
