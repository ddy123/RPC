package example.Server.ratelimit.impl;/*
 * @author ddy
 * @date 2025/6/11
 * */

import example.Server.ratelimit.RateLimit;

public class TokenBucketRateLimitImpl implements RateLimit {
    //令牌产生速率(单位为ms)
    private static int RATE;
    //桶容量
    private static int CAPACITY;
    //当前桶容量
    private volatile int curCapacity;
    //时间戳
    private volatile long timeStamp=System.currentTimeMillis();
    public TokenBucketRateLimitImpl(int rate,int capacity){
        RATE=rate;
        CAPACITY=capacity;
        curCapacity=capacity;
    }
    @Override
    public synchronized boolean getToken() {
        if(curCapacity>0){
            curCapacity--;
            return true;
        }
        //如果桶无剩余
        long current=System.currentTimeMillis();
        //如果距离上一次的请求时间大于RATE的时间，表示需要生成新的令牌
        if(current-timeStamp>=RATE){
            //计算这段时间间隔中生成的令牌，如果>2,桶容量加上（计算的令牌-1）
            //如果==1，就不做操作（因为这一次操作要消耗一个令牌）
            if((current-timeStamp)/RATE>=2){
                curCapacity+=(int) (current-timeStamp)/RATE-1;
            }
            //保证桶内令牌容量不会超过最大容量
            if(curCapacity>CAPACITY) curCapacity=CAPACITY;
            //刷新时间戳为本次请求
            timeStamp=current;
            return true;
        }
        return false;
    }
}
