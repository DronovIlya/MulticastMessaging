package utils;

import java.io.*;

public class Streams {

    public static byte[] readBytes(InputStream stream, int count) throws IOException {
        byte[] res = new byte[count];
        int offset = 0;
        while (offset < count) {
            int readed = stream.read(res, offset, count - offset);
            Thread.yield();
            if (readed > 0) {
                offset += readed;
            } else if (readed < 0) {
                throw new IOException();
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new IOException();
                }
            }
        }
        return res;
    }

    public static void writeBytes(byte[] data, OutputStream stream) throws IOException {
        stream.write(data);
    }

}
