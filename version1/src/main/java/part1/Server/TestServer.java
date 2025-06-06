package part1.Server;

import part1.Server.provider.ServiceProvider;
import part1.Server.server.RpcServer;
import part1.Server.server.impl.SimpleRPCServer;
import part1.common.service.Impl.UserServiceImpl;
import part1.common.service.UserService;

public class TestServer {
    public static void main(String[] args){
        UserService userService=new UserServiceImpl();
        ServiceProvider serviceProvider=new ServiceProvider();
        serviceProvider.providerServiceInterface(userService);

        RpcServer rpcServer=new SimpleRPCServer(serviceProvider);
        rpcServer.start(9999);
    }
}
