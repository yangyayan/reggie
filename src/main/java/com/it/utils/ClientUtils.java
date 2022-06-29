package com.it.utils;

public class ClientUtils {
    public static void sendMessage(String phoneNumbers,String code){

        Client client = new Client();
        client.setAppId("hw_11229");     //开发者ID，在【设置】-【开发设置】中获取
        client.setSecretKey("fe14ad08d257367296afbd6375105ce7");    //开发者密钥，在【设置】-【开发设置】中获取
        client.setVersion("1.0");

        /**
         *   json格式可在 bejson.com 进行校验
         */
        String singnstr = "闪速码";
        Client.Request request = new Client.Request();
        request.setMethod("sms.message.send");
        request.setBizContent("{\"mobile\":[\""+phoneNumbers+"\"],\"type\":0,\"template_id\":\"ST_2020101100000007\",\"sign\":\"" + singnstr +"\",\"send_time\":\"\",\"params\":{\"code\":"+code+"}}");  // 这里是json字符串，send_time 为空时可以为null, params 为空时可以为null,短信签名填写审核后的签名本身，不需要填写签名id
        System.out.println( client.execute(request) );
    }
}
