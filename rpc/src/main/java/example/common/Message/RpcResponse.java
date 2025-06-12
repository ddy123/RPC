package example.common.Message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse implements Serializable {
    //状态信息
    private int code;
    private String message;
    //具体数据
    private Object data;
    //更新：加入传输数据的类型，以便在自定义序列化器中解析
    private Class<?> dataType;
    //构造成功信息,新增dataType，json反序列化需要显示注明类型
    public static RpcResponse success(Object data){return  RpcResponse.builder().code(200).dataType(data.getClass()).data(data).build();}
    //构造失败信息
    public static RpcResponse fail(){return RpcResponse.builder().code(500).message("服务器发生错误").build();}

}
