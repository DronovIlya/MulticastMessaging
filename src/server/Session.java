package server;

import commands.base.BaseRequest;
import commands.base.BaseResponse;
import proto.Packet;
import utils.Streams;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Session extends Thread {

    private static final boolean LOG_ITERATION = false;

    private final int sessionId;
    private final Socket socket;
    private final ServerCallback callback;

    private boolean isClosed;

    private final SessionReaderThread readerThread;
    private final SessionWriterThread writerThread;

    public Session(int sessionId, Socket socket, ServerCallback callback) {
        this.sessionId = sessionId;
        this.socket = socket;
        this.callback = callback;

        readerThread = new SessionReaderThread();
        writerThread = new SessionWriterThread();

        readerThread.start();
        writerThread.start();
    }

    public void sendResponse(BaseResponse response) {
        Packet packet = new Packet(response);
        writerThread.pushPacket(packet);
    }

    public void close() {
        if (!isClosed) {
            isClosed = true;
            try {
                socket.close();
            } catch (IOException ignored) {}
        }

        callback.onSessionClosed(sessionId);
    }

    private class SessionReaderThread extends Thread {

        @Override
        public void run() {
            while (!isClosed) {

                try {
                    if (LOG_ITERATION) {
                        System.out.println("reader: start iteration");
                    }

                    byte[] header = Streams.readBytes(socket.getInputStream(), Packet.HEADER_SIZE);
                    Packet packet = new Packet(header);
                    if (LOG_ITERATION) {
                        System.out.println("received header, opcode = " + packet.getOpcode() + ", length = " + packet.getPayloadLength());
                    }

                    byte[] payload = Streams.readBytes(socket.getInputStream(), packet.getPayloadLength());
                    if (payload.length > 0) {
                        BaseRequest request = BaseRequest.decode(packet.getOpcode(), payload);
                        callback.onRequest(sessionId, request);
                    }
                } catch (IOException e) {
                    System.out.println("reader: close session");
                    close();
                }

                if (LOG_ITERATION) {
                    System.out.println("reader: end iteration");
                }
            }
        }
    }

    private class SessionWriterThread extends Thread {

        private final ConcurrentLinkedDeque<Packet> packets = new ConcurrentLinkedDeque<>();

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
                    System.out.println("writer: write iteration");
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

                try {
                    if (LOG_ITERATION) {
                        System.out.println("start write to the socket");
                    }
                    Streams.writeBytes(packet.toByteArray(), socket.getOutputStream());
                } catch (IOException e) {
                    System.out.println("exception in WriterThread");
                    close();
                }

                if (LOG_ITERATION) {
                    System.out.println("writer: end iteration");
                }
            }
        }
    }

}
