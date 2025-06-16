package io.github.minkik715.mkchatting.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

import java.util.Map;

public class UdpHeartbeatServer {

    private final int port;
    private final Map<String, Long> lastSeenMap; // userId → timestamp

    public UdpHeartbeatServer(int port, Map<String, Long> lastSeenMap) {
        this.port = port;
        this.lastSeenMap = lastSeenMap;
    }

    public void run() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new SimpleChannelInboundHandler<DatagramPacket>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) {
                        String msg = packet.content().toString(CharsetUtil.UTF_8);
                        if (msg.startsWith("heartbeat:")) {
                            String userId = msg.split(":")[1];
                            lastSeenMap.put(userId, System.currentTimeMillis());
                            System.out.println("[UDP] Heartbeat received from " + userId);
                        }
                    }
                });

        b.bind(port).sync().channel().closeFuture().await();
    }
}