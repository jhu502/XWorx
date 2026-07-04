package com.flame.rpc;

public interface ISender {
    void sendResponse(FlameResult result);

    void shutdown();
}
