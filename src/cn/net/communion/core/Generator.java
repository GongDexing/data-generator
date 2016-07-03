package cn.net.communion.core;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.net.communion.entity.JobInfo;
import cn.net.communion.entity.KeyValue;
import cn.net.communion.helper.FileHelper;

public class Generator {

    public void start(List<JobInfo> jobList) throws IOException {
        long sum = 0;
        for (JobInfo job : jobList) {
            Map<String, String> tempMap = new HashMap<String, String>();
            List<KeyValue> detail = job.getDetail();
            String filename = job.getFilename();
            if (filename == null) {
                for (int i = 0; i < job.getNum(); i++) {
                    fillTempMap(detail, tempMap);
                    FileHelper.save(null, job.getTable(), tempMap); 
                    tempMap.clear();
                }
            } else {
                FileWriter writer = new FileWriter("output/" + filename, true);
                if (filename.indexOf(".csv") > 0) {
                    for (int i = 0; i < job.getNum(); i++) {
                        fillTempMap(detail, tempMap);
                        if (i == 0) {
                            FileHelper.save(writer, tempMap.keySet());
                        }
                        FileHelper.save(writer, tempMap.values());
                        tempMap.clear();
                    }
                } else {
                    for (int i = 0; i < job.getNum(); i++) {
                        fillTempMap(detail, tempMap);
                        FileHelper.save(writer, job.getTable(), tempMap);
                        tempMap.clear();
                    }
                }
                writer.close();
            }
            System.out.println("[job-" + job.getId() + "]" + job.getNum() + " data had writen to " + (filename != null ? "file" : "console") + "\n");
            sum += job.getNum();
        }
        System.out.println("generate data successfully, total: " + sum);
    }

    private Map<String, String> fillTempMap(List<KeyValue> detail, Map<String, String> tempMap) {
        for (KeyValue kv : detail) {
            tempMap.put(kv.key, Dispatcher.checkGrammer(kv.value) ? Dispatcher.dispatch(kv.value, tempMap) : kv.value);
        }
        return tempMap;
    }
}
