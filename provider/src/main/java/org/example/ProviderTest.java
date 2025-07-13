package org.example;/*
 * @author ddy
 * @date 2025/7/13
 * */

import example.Server.provider.ServiceProvider;
import example.Server.server.RpcServer;
import example.Server.server.impl.NettyRPCServer;
import org.example.Impl.UserServiceImpl;
import org.example.service.UserService;

public class ProviderTest {
    public static void main(String[] args){
        UserService userService=new UserServiceImpl();
        ServiceProvider serviceProvider=new ServiceProvider("127.0.0.1",9999);
        serviceProvider.providerServiceInterface(userService,true);

        RpcServer rpcServer=new NettyRPCServer(serviceProvider);
        rpcServer.start(9999);
    }
}
