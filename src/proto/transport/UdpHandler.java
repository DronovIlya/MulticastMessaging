package proto.transport;

import commands.base.BaseResponse;
import proto.Packet;

import java.io.IOException;
import java.net.*;

public class UdpHandler {

    private final static int PACKET_SIZE = 1024;

    private final String baseAddress;
    private final int port;
    private final UdpCallback callback;
    private final MulticastSocket socket;
    private final ReaderThread readerThread;

    private boolean isClosed;

    public UdpHandler(String baseAddress, int port, UdpCallback callback) throws IOException {
        this.baseAddress = baseAddress;
        this.port = port;
        this.callback = callback;

        socket = new MulticastSocket(port);
        socket.joinGroup(InetAddress.getByName(baseAddress));

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
                byte[] buffer = new byte[1024];
                try {
                    DatagramPacket datagram = new DatagramPacket(buffer, buffer.length);
                    socket.receive(datagram);
                    System.out.println("UDP: received packet, length = " + buffer.length);
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
