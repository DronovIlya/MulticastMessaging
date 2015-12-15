package server;

import commands.base.BaseRequest;

public interface ServerCallback {

    void onRequest(int sessionId, BaseRequest request);
    void onSessionClosed(int sessionId);
}
