package proto.transport;

import commands.base.BaseResponse;

public interface TcpCallback {

    <T extends BaseResponse> void onTcpReceived(T result);

    void onTcpClosed();
}
