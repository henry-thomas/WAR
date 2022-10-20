/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mypower24.smd.jcluster.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import java.util.Map;

/**
 *
 * @author henry
 */
public class MapSample {

    private final Config hzConfig = new Config();
    private final NetworkConfig hzNwConfig = new NetworkConfig();
    private final HazelcastInstance hz;
    private final HazelcastInstance hz2;

    public MapSample() {
        this.hzConfig.setClusterName("cool-cluster");
        this.hzConfig.getCPSubsystemConfig().setCPMemberCount(3);
//        this.hzConfig.getIntegrityCheckerConfig().setEnabled(true);
//        this.hzConfig.getJetConfig().setEnabled(true);
        this.hz = Hazelcast.newHazelcastInstance(hzConfig);
        this.hz2 = Hazelcast.newHazelcastInstance(hzConfig);

    }

    public void writeTestValToMap() {
        Map<String, String> map = hz.getMap("my-distributed-map");
        map.put("1", "John");
        map.put("2", "Mary");
        map.put("3", "Jane");

        System.out.println(map.get("1"));
        System.out.println(map.get("2"));
        System.out.println(map.get("3"));
    }

}
