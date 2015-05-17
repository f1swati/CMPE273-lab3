package edu.sjsu.cmpe.cache.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

//import com.csforge.ConsistentHash;


public class Client {
	
	private static final HashFunction hfunc = Hashing.murmur3_128();
	private static final Funnel<CharSequence> strFunnel = Funnels.stringFunnel();
	private static final int Servers =3;

    private static final int failedServers=1;



    private static final Map<String, AtomicInteger> nodeMap = Maps.newHashMap();
	private static final List<String> nodesList = getNodes(nodeMap);
	
    public static void main(String[] args) throws Exception {
        System.out.println("Starting Cache Client...");
        RendezvousHashTest();
        System.out.println("Existing Cache Client...");
    }

    private static final String VariableString[]={"a","b","c","d","e","f","g","h","i","j"};
    private static final int Key = VariableString.length;

	private static List<String> getNodes(Map<String, AtomicInteger> nodeMap) {
		List<String> nodes = Lists.newArrayList();
		for(int i = 0 ; i < Servers; i ++) {
			nodes.add("http://localhost:300"+i);
			nodeMap.put("http://localhost:300"+i, new AtomicInteger());
		}
		return nodes;
	}
	
	private static void RendezvousHashTest(){


				RendezvousHash<String, String> rendezvousHash = new RendezvousHash(hfunc, strFunnel, strFunnel, nodesList);

                //GET
				for(int i = 0 ; i < Key; i++) {
					String server=rendezvousHash.get(""+i);
					int serverLoad=nodeMap.get(server).incrementAndGet();
					rendezvousHash.insertData(server,i+1, VariableString[i]);
				}


                 //PUT
				for(int i = 0 ; i < Key; i++) {
					String server=rendezvousHash.get(""+i);
					rendezvousHash.getData(server,i+1);
				}
				
	}

}
