package example.Server;

import example.Server.provider.ServiceProvider;
import example.Server.server.RpcServer;
import example.Server.server.impl.NettyRPCServer;
import example.common.service.Impl.UserServiceImpl;
import example.common.service.UserService;

public class TestServer {
    public static void main(String[] args){
        UserService userService=new UserServiceImpl();
        ServiceProvider serviceProvider=new ServiceProvider("127.0.0.1",9999);
        serviceProvider.providerServiceInterface(userService);

        RpcServer rpcServer=new NettyRPCServer(serviceProvider);
        rpcServer.start(9999);
    }
}
