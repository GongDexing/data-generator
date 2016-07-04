package cn.net.communion.helper;

import java.util.Random;

public class RandomData {
    static private Random rand = new Random();

    static public String getRuleData(String content, int len) {
        int strLen = content.length();
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < len; i++) {
            result.append(content.charAt(rand.nextInt(strLen)));
        }
        return result.toString();
    }

    static public String getPoolData(String[] arr) {
        return arr[rand.nextInt(arr.length)];
    }
}
