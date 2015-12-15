package proto.transport;

import commands.base.BaseResponse;

public interface UdpCallback {

    <T extends BaseResponse> void onUdpReceived(T result);
    void onUdpClose();
}
