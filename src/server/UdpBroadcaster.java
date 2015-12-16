package server;

import proto.Packet;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedDeque;

public class UdpBroadcaster extends Thread {

    private final ConcurrentLinkedDeque<BroadcastPacket> packets = new ConcurrentLinkedDeque<>();

    private final int port;

    private DatagramSocket socket;

    public UdpBroadcaster(int port) throws SocketException {
        this.port = port;

        socket = new DatagramSocket();
    }

    public void pushPacket(String address, Packet packet) {
        packets.add(new BroadcastPacket(address, packet));
        synchronized (packets) {
            packets.notifyAll();
        }
    }

    @Override
    public void run() {

        while (true) {

            BroadcastPacket packet;
            synchronized (packets) {
                packet = packets.poll();
                if (packet == null) {
                    try {
                        packets.wait();
                    } catch (InterruptedException ignored) {
                        return;
                    }
                    packet = packets.poll();
                }
            }


            try {
                byte[] buffer = packet.packet.toByteArray();
                DatagramPacket datagramPacket = new DatagramPacket(buffer, 0, buffer.length,
                        InetAddress.getByName(packet.address), port);

                socket.send(datagramPacket);
            } catch (Exception e) {
                e.printStackTrace();
                // TODO: handle errors
            }
        }
    }

    private class BroadcastPacket {

        public final String address;
        public final Packet packet;

        private BroadcastPacket(String address, Packet packet) {
            this.address = address;
            this.packet = packet;
        }
    }
}
