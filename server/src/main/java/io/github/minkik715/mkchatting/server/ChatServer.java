package io.github.minkik715.mkchatting.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.minkik715.mkchatting.ChatCommandDTO;
import io.github.minkik715.mkchatting.ChatMessageBaseDTO;
import io.github.minkik715.mkchatting.GeneralMessageDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {

    private int port;

    public ChatServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        System.out.println("starting server...");
        try {
            ServerBootstrap b = new ServerBootstrap();
            ObjectMapper objectMapper = new ObjectMapper();
            ConcurrentHashMap<String, ConcurrentHashMap<String, Channel>> roomUserChannels = new ConcurrentHashMap<>();
            for (String s : List.of("컴퓨터 네트워크", "운영체제", "소프트웨어 공학")) {
                roomUserChannels.put(s, new ConcurrentHashMap<>());
            }

            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>(){
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new GeneralMessageDecoder<>(objectMapper, ChatMessageBaseDTO.class))
                                    .addLast(new ChatServerHandler(roomUserChannels,  objectMapper));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync();

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;

        new ChatServer(port).run();
    }
}