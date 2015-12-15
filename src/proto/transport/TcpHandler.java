package proto.transport;

import commands.base.BaseRequest;
import commands.base.BaseResponse;
import proto.Packet;
import utils.Streams;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedDeque;

public class TcpHandler {

    private static final int CONNECTION_TIMEOUT = 5 * 1000; // 5s

    private static final boolean LOG_ITERATION = false;

    private final String ip;
    private final int port;
    private final TcpCallback callback;

    private Socket socket;
    private boolean isClosed;

    private final ReaderThread readerThread;
    private final WriterThread writerThread;

    public TcpHandler(String ip, int port, TcpCallback callback) throws IOException {
        this.ip = ip;
        this.port = port;
        this.callback = callback;

        this.socket = new Socket();
        this.socket.connect(new InetSocketAddress(ip, port), CONNECTION_TIMEOUT);
        this.socket.setKeepAlive(true);
        this.socket.setTcpNoDelay(true);

        readerThread = new ReaderThread();
        writerThread = new WriterThread();

        readerThread.start();
        writerThread.start();
    }

    public void pushRequest(BaseRequest request) {
        writerThread.pushPacket(new Packet(request));
    }

    public void close() {
        System.out.println("close socket");
        if (!isClosed) {
            isClosed = true;

            readerThread.interrupt();
            writerThread.interrupt();

            if (!socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {}
            }
        }
        callback.onTcpClosed();
    }

    private class ReaderThread extends Thread {

        public ReaderThread() {
            setName("ReaderThread");
        }

        @Override
        public void run() {
            while (!isClosed) {
                try {
                    if (socket.isClosed() || !socket.isConnected()) {
                        System.out.println("socket is closed and not connected");
                        close();
                        break;
                    }
                    if (LOG_ITERATION) {
                        System.out.println("start read");
                    }
                    byte[] header = Streams.readBytes(socket.getInputStream(), Packet.HEADER_SIZE);
                    Packet packet = new Packet(header);

                    byte[] buffer = Streams.readBytes(socket.getInputStream(), packet.getPayloadLength());
                    if (buffer.length > 0) {
                        BaseResponse response = BaseResponse.decode(packet.getOpcode(), buffer);
                        callback.onTcpReceived(response);
                    }
                    if (LOG_ITERATION) {
                        System.out.println("end read");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    close();
                }
            }
        }
    }

    private class WriterThread extends Thread {

        private final ConcurrentLinkedDeque<Packet> packets = new ConcurrentLinkedDeque<>();

        public WriterThread() {
            setName("WriterThread");
        }

        public void pushPacket(Packet packet) {
            packets.add(packet);
            synchronized (packets) {
                packets.notifyAll();
            }
        }

        @Override
        public void run() {
            while (!isClosed) {

                if (LOG_ITERATION) {
                    System.out.println("writer: start write iteration");
                }

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

                if (packet == null) {
                    if (isClosed) {
                        return;
                    } else {
                        continue;
                    }
                }

                if (LOG_ITERATION) {
                    System.out.println("retrieve packet = " + packet);
                }
                try {
                    if (LOG_ITERATION) {
                        System.out.println("start write to the socket");
                    }
                    Streams.writeBytes(packet.toByteArray(), socket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                    close();
                }

                if (LOG_ITERATION) {
                    System.out.println("writer: end iteration");
                }
            }
        }
    }
}
