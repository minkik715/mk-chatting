package io.github.minkik715.mkchatting.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

public class UdpHeartbeatClient {
    private final String serverHost;
    private final int serverPort;
    private final String userId;

    public UdpHeartbeatClient(String serverHost, int serverPort, String userId) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.userId = userId;
    }

    public void start() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChannelInitializer<DatagramChannel>() {
                        @Override
                        protected void initChannel(DatagramChannel ch) {
                            // UDP는 클라이언트에서 수신할 필요 없음
                        }
                    });

            Channel ch = bootstrap.bind(0).sync().channel();

            Thread heartbeatThread = new Thread(() -> {
                while (!Thread.interrupted()) {
                    String msg = "heartbeat:" + userId;
                    DatagramPacket packet = new DatagramPacket(
                            Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8),
                            new InetSocketAddress(serverHost, serverPort)
                    );
                    ch.writeAndFlush(packet);
                    try {
                        Thread.sleep(5000); // 5초 간격
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            });

            heartbeatThread.setDaemon(true);
            heartbeatThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}