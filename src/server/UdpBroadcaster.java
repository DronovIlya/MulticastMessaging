package server;

import proto.Packet;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedDeque;

public class UdpBroadcaster extends Thread {

    private final ConcurrentLinkedDeque<Packet> packets = new ConcurrentLinkedDeque<>();

    private final String address;
    private final int port;

    private DatagramSocket socket;

    public UdpBroadcaster(String address, int port) throws SocketException {
        this.address = address;
        this.port = port;

        socket = new DatagramSocket();
    }

    public void pushPacket(Packet packet) {
        packets.add(packet);
        synchronized (packets) {
            packets.notifyAll();
        }
    }

    @Override
    public void run() {

        while (true) {

            Packet packet;
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
                byte[] buffer = packet.toByteArray();
                DatagramPacket datagramPacket = new DatagramPacket(buffer, 0, buffer.length,
                        InetAddress.getByName(address), port);

                socket.send(datagramPacket);
            } catch (Exception e) {
                e.printStackTrace();
                // TODO: handle errors
            }
        }
    }
}
