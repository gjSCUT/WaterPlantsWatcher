package com.gjscut.waterplantswatcher;

/**
 * Created by Administrator on 2017/4/18.
 */
public class Constant {
    public final static String clientId = "admin";
    public final static String clientSecret = "123456";
    public static Token token;
    public static String username;

    class Token {
        public String token_type;
        public String access_token;
        public String refresh_token;
        public int expires_in;
    }
}



