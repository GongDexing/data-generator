package cn.net.communion.main;

import java.io.IOException;

import cn.net.communion.core.Generator;
import cn.net.communion.xml.Root;

public class App {
    public static void main(String[] args) {
        Root root = Root.getInstance().loadXml("jobs.xml");
        if (root != null) {
            try {
                new Generator().start(root.getJobList());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
