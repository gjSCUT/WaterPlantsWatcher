package com.gjscut.waterplantswatcher.model;


public class Token {
    public String token_type;
    public String access_token;
    public String refresh_token;
    public int expires_in;

    public Token(String token_type, String access_token, String refresh_token, int expires_in) {
        this.token_type = token_type;
        this.access_token = access_token;
        this.refresh_token = refresh_token;
        this.expires_in = expires_in;
    }
}