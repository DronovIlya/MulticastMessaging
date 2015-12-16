package client.transport;

import commands.base.BaseResponse;
import commands.entity.Chat;
import proto.Packet;

import java.io.IOException;
import java.net.*;
import java.util.List;

public class UdpHandler {

    private final static boolean LOG_OPERATION = false;

    private final static int MAX_PACKET_SIZE = 1024;

    private final int port;
    private final UdpCallback callback;
    private final MulticastSocket socket;
    private final ReaderThread readerThread;

    private boolean isClosed;

    public UdpHandler(List<Chat> subscribedChats, int port, UdpCallback callback) throws IOException {
        this.port = port;
        this.callback = callback;

        socket = new MulticastSocket(port);

        StringBuilder builder = new StringBuilder();
        for (Chat chat : subscribedChats) {
            socket.joinGroup(InetAddress.getByName(chat.address));
            builder.append(chat.title + ", chat address = " + chat.address + "\n");
        }

        if (LOG_OPERATION) {
            System.out.println("UdpHandler: start listening : " + builder.toString());
        }

        readerThread = new ReaderThread();
        readerThread.start();
    }

    public void close() {
        if (!isClosed) {
            isClosed = true;
            readerThread.interrupt();

            if (!socket.isClosed()) {
                socket.close();
            }
        }
        callback.onUdpClose();
    }

    private class ReaderThread extends Thread {

        public ReaderThread() {
            setName("ReaderThread");
        }

        @Override
        public void run() {
            while (!isClosed) {
                // TODO: don't use specific packet size. It's bad, man :(
                byte[] buffer = new byte[MAX_PACKET_SIZE];
                try {
                    DatagramPacket datagram = new DatagramPacket(buffer, buffer.length);
                    socket.receive(datagram);
                    if (LOG_OPERATION) {
                        System.out.println("UDP: received packet, length = " + buffer.length);
                    }
                    Packet packet = new Packet(datagram.getData());

                    byte[] payload = new byte[packet.getPayloadLength()];
                    System.arraycopy(buffer, Packet.HEADER_SIZE, payload, 0, payload.length);

                    if (datagram.getData().length > 0) {
                        BaseResponse response = BaseResponse.decode(packet.getOpcode(), payload);
                        callback.onUdpReceived(response);
                    }
                } catch (Exception e) {
                    close();
                }
            }
        }
    }
}
