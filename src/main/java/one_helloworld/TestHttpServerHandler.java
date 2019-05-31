package one_helloworld;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 自定义处理器
 */
public class TestHttpServerHandler extends SimpleChannelInboundHandler {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println("channel read0...");

        if (msg instanceof HttpRequest) {
            //通过传入对象转换为HttpRequest对象,然后得到信息
            HttpRequest request = (HttpRequest) msg;
            //断点查看客服端传入的参数,只支持get,post
            Map<String, String> parmMap = parse(request);
            //要返回的内容, Channel可以理解为连接，而连接中传输的信息要为ByteBuf
            ByteBuf content = Unpooled.copiedBuffer("Hello World", CharsetUtil.UTF_8);
            //构造响应
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
            //设置头信息的的MIME类型
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");  //内容类型
            //设置要返回的内容长度
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes()); //内容长度
            //长连接(不一定需要)
            response.headers().set(HttpHeaderNames.CONNECTION, "keep-alive");
            //将响应对象返回
            ctx.writeAndFlush(response);

        } else {
            System.out.println("没有加http转换器...");
        }
    }

    //通道注册成功
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel register...");
        super.channelRegistered(ctx);
    }

    /**
     * 自定义的Handler被添加,也就是在TestChannelnitializer的initChannel方法中，
     * pipeline.addLast("testHttpServerHandler", new TestHttpServerHandler());
     * 这行代码执行的时候，该方法被触发
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handler added...");
        super.handlerAdded(ctx);
    }

    //通道处于活动状态，即可用状态
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel active...");
        super.channelActive(ctx);
    }

    //通道处于不活动状态
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel inactive...");
        super.channelInactive(ctx);
    }

    //通道取消注册
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel unregister...");
        super.channelUnregistered(ctx);
    }


    /**
     * 处理http请求的参数,怎么取的问题
     */
    private Map<String, String> parse(HttpRequest req) {
        HttpMethod method = req.method();
        Map<String, String> parmMap = new HashMap();
        if (HttpMethod.GET == method) {
            // 是GET请求
            QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
            Map<String, List<String>> parame = decoder.parameters();
            Iterator<Map.Entry<String, List<String>>> iterator = parame.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, List<String>> next = iterator.next();
                parmMap.put(next.getKey(), next.getValue().get(0));
            }
        } else if (HttpMethod.POST == method) {

        } else {
            // 不支持其它方法
        }
        return parmMap;
    }


}