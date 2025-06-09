package example.Server.server.impl;

import example.Server.server.RpcServer;
import example.Server.provider.ServiceProvider;
import example.Server.server.work.WorkThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolRPCServer implements RpcServer {
    private final ThreadPoolExecutor threadPoolExecutor;
    private ServiceProvider serviceProvider;
    public ThreadPoolRPCServer(ServiceProvider serviceProvider){
        threadPoolExecutor=new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                1000,60, TimeUnit.SECONDS,new ArrayBlockingQueue<>(100));
        this.serviceProvider=serviceProvider;
    }
    public ThreadPoolRPCServer(ServiceProvider serviceProvider,int corePoolSize,int maxiumPoolSize,long keepAliveTime,TimeUnit unit,
                                BlockingQueue<Runnable> workQueue){
        threadPoolExecutor=new ThreadPoolExecutor(corePoolSize,maxiumPoolSize,keepAliveTime,unit,workQueue);
        this.serviceProvider=serviceProvider;

    }
    @Override
    public void start(int port) {
        System.out.println("服务起到了");
        try {
            ServerSocket serverSocket=new ServerSocket(port);
            while(true){
                Socket socket=serverSocket.accept();
                threadPoolExecutor.execute(new WorkThread(socket,serviceProvider));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {

    }
}
