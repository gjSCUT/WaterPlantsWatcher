package com.gjscut.waterplantswatcher;

import com.gjscut.waterplantswatcher.model.Token;

class Constant {
    final static String clientId = "android";
    final static String clientSecret = "123456";
    static Token token;
    static String username;

    public synchronized static void putToken(Token token) {
        Constant.token = token;
    }

    public synchronized static String getToken() {
        return token.token_type + " "  + token.access_token;
    }
}



