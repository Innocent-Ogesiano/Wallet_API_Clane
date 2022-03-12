package com.wallet_api_clane.global_constants;

public class Constants {
    public static final String BASE_URL = "http://localhost:8000/";
    public static final long JWT_TOKEN_VALIDITY= 50L*60*60;
    public static final long JWT_TOKEN_EXPIRATION_DATE = JWT_TOKEN_VALIDITY*120000;
    public static final String USER_NOT_FOUND = "User with this email was not found";
    public static final String RESOURCE_ALREADY_EXIST = " already exist";
    public static final String INVALID_AMOUNT = "Invalid Amount, kindly enter a valid amount";


    private Constants() {
    }
}
