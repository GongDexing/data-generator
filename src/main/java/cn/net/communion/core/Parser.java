package cn.net.communion.core;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.net.communion.helper.FileHelper;
import cn.net.communion.helper.PropsReader;
import cn.net.communion.helper.RandomData;

public class Parser {
    private String value;
    private Map<String, String> map;
    private Pattern varPattern;
    private Pattern rulePattern;
    private Pattern poolPattern;
    private Pattern datePattern;
    private PropsReader props;
    private Map<String, String> poolFileMap = new HashMap<String, String>();
    static private Parser instance = null;
    private Random rand = new Random();

    private Parser() {
        varPattern = Pattern.compile("\\$var\\{(\\w+)\\}");
        rulePattern = Pattern.compile("\\$rule\\{([0-9a-zA-Z,]+)\\}");
        poolPattern = Pattern.compile("\\$pool\\{([0-9a-zA-Z.]+)\\}");
        datePattern = Pattern.compile("\\$date\\{([^\\s,]+),([^\\s,]+),([^\\s,]+)\\}");
        props = PropsReader.getInstance();

    }

    static Parser getInstance(String value, Map<String, String> map) {
        if (instance == null) {
            instance = new Parser();
        }
        instance.setValue(value);
        instance.setMap(map);
        return instance;
    }

    static public boolean checkGrammar(String value) {
        return value.contains("$var") || value.contains("$rule") || value.contains("$pool")
                || value.contains("$date");
    }

    public String execute() {
        parseVar().parseRule().parsePool().parseDate();
        return value;
    }

    private Parser parseVar() {
        Matcher m = varPattern.matcher(value);
        if (m.find()) {
            String name = m.group(1);
            String propValue = props.getProperty("var." + name);
            value = value.replace(m.group(0), propValue != null ? propValue : this.map.get(name));
        }
        return this;
    }

    private Parser parseRule() {
        Matcher m = rulePattern.matcher(value);
        if (m.find()) {
            value = value.replace(m.group(0), getRuleData(m.group(1).split(",")));
        }
        return this;
    }

    private String getRuleData(String[] arr) {
        String content = props.getProperty("rule." + arr[0]);
        if (content != null) {
            return RandomData.getRuleData(content, arr.length < 2 ? 6 : Integer.parseInt(arr[1]));
        }
        return null;
    }

    private Parser parsePool() {
        Matcher m = poolPattern.matcher(value);
        if (m.find()) {
            value = value.replace(m.group(0), getPoolData(m.group(1)));
        }
        return this;
    }

    private String getPoolData(String name) {
        String content = props.getProperty("pool." + name);
        if (content != null) {
            return RandomData.getPoolData(content.split(","));
        } else {
            String poolContent = poolFileMap.get(name);
            if (poolContent == null) {
                poolContent = FileHelper.read("pool/" + name);
                if (poolContent == null) {
                    return null;
                }
                poolFileMap.put(name, poolContent);
            }
            return RandomData.getPoolData(poolContent.split(","));
        }
    }

    private Parser parseDate() {
        Matcher m = datePattern.matcher(value);
        if (m.find()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(m.group(3));
            LocalDate startDate = m.group(1).trim().equals("now") ? LocalDate.now()
                    : LocalDate.parse(m.group(1).trim());
            LocalDate endDate = m.group(2).trim().equals("now") ? LocalDate.now()
                    : LocalDate.parse(m.group(2).trim());
            int length = (int) startDate.until(endDate, ChronoUnit.DAYS);
            LocalDate randDate = length > 0 ? startDate.plusDays(rand.nextInt(length + 1))
                    : startDate.minusDays(rand.nextInt(1 - length));
            value = value.replace(m.group(), randDate.format(formatter));
        }
        return this;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    // public static void main(String [] args){
    // Pattern datePattern = Pattern.compile("\\$date\\{([^\\s,]+),([^\\s,]+),([^\\s,]+)\\}");
    // Matcher m = datePattern.matcher("$date{2017-02-01,now,yyyy/MM/dd}");
    //
    // if(m.find()){
    // System.out.println(m.group(3));
    // DateTimeFormatter formatter = DateTimeFormatter.ofPattern(m.group(3));
    // String start = m.group(1).trim();
    // String end = m.group(2).trim();
    // LocalDate startDate = start.equals("now") ? LocalDate.now() : LocalDate.parse(start);
    // LocalDate endDate = end.equals("now") ? LocalDate.now() : LocalDate.parse(end);
    // int length = (int) startDate.until(endDate, ChronoUnit.DAYS);
    // System.out.println(startDate.plusDays(rand.nextInt(length)).format(formatter));
    // }
    // }
}
