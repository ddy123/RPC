package part1.Client.rpcClient;

import part1.common.Message.RpcRequest;
import part1.common.Message.RpcResponse;

public interface RpcClient {
    //定义底层的通信方法
    RpcResponse sendRequest(RpcRequest request);
}
