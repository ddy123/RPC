package example.Client.rpcClient;

import example.common.Message.RpcRequest;
import example.common.Message.RpcResponse;

public interface RpcClient {
    //定义底层的通信方法
    RpcResponse sendRequest(RpcRequest request);
}
