package io.github.minkik715.mkchatting.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.minkik715.mkchatting.ChatCommandDTO;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Scanner;
import java.util.UUID;

public class ChatClient {


    public static void main(String[] args) {

        EventLoopGroup worker = new NioEventLoopGroup();
        String userId = args.length > 0 ? args[0] : UUID.randomUUID().toString();
        String roomId = "1";
        try {
            Bootstrap b = new Bootstrap();
            ObjectMapper objectMapper = new ObjectMapper();
            b.group(worker)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ChatMessageDecoder(objectMapper));
                            ch.pipeline().addLast(new ChatClientHandler(userId, objectMapper));
                        }
                    });

            ChannelFuture localhost = b.connect("192.168.219.102", 8080).sync();
            Channel channel = localhost.channel();
            Scanner scanner = new Scanner(System.in);

            System.out.println("채팅을 입력하세요:");
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                ChatCommandDTO dto = new ChatCommandDTO(userId, roomId, ChatCommandDTO.ChatCommandType.SEND, line);
                send(channel, objectMapper, dto);
            }

            channel.closeFuture().sync();

            channel.closeFuture().sync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            worker.shutdownGracefully();
        }
    }

    private static void send(Channel channel, ObjectMapper objectMapper, ChatCommandDTO dto) throws Exception {
        byte[] bytes = objectMapper.writeValueAsBytes(dto);
        ByteBuf buf = Unpooled.wrappedBuffer(bytes);
        channel.writeAndFlush(buf);
    }
}