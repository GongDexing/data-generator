package cn.net.communion.core;

import java.util.Map;

public class Dispatcher {
    static private Dispatcher instance;

    static public String dispatch(String value, Map<String, String> map) {
        return Parser.getInstance(value, map).execute();
    }

    static public boolean checkGrammer(String value) {
        return Parser.checkGrammar(value);
    }
}
