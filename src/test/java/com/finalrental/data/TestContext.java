package com.finalrental.data;

import java.util.Random;

public class TestContext {

    private static String registeredPhone = "1020416304";

    public static String getRegisteredPhone() {
        return registeredPhone;
    }

    public static void setRegisteredPhone(String phone) {
        registeredPhone = phone;
    }

    public static String generateIdentityNumber() {
        StringBuilder number = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            number.append(random.nextInt(10));
        }
        return number.toString();
    }
}