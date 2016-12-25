package kCoreConnectedMinWeight;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class caseStudy {
    public static void main(String[] args){
        // read the data
        HashMap<Integer, String> hm = new HashMap<Integer, String>();
        try{
    
        BufferedReader br = new BufferedReader(new FileReader("E:\\CODING FILES\\JavaCode\\dblpSax\\output\\authorString2num.txt"));//dblp min vertex = 0 which starts from 0
        //BufferedReader br = new BufferedReader(new FileReader("E:\\CODING FILES\\JavaCode\\dblpSax\\output\\running\\newIndexFruitFly.txt"));//dblp min vertex = 0 which starts from 0
        
        int count = 0;
        
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();
    
                while (line != null) {
                    
                    count++;
                    
                    // for tabs
                    //String[] a = line.split("\\t");
                    
                    //which is spetical for dblp dataset; multiple spaces
                    String[] a = line.split("#");
                    a[0]=a[0].trim();
                    hm.put(Integer.parseInt(a[1]), a[0]);
    
                    if(count % 1000000 == 0)
                        System.out.println(count);
                    
                    line = br.readLine();
                }
                
                
            } finally {
                br.close();
            }
        }catch(Exception e){
            System.out.println("Reading **.txt as bufferedReaderhappens");
            System.out.println("Maybe lack for one dimension such as the weight");
        }
        
        // construct the queryNodes
        ArrayList<String> queryNodes = new ArrayList<String>();
        try{
            
        BufferedReader br = new BufferedReader(new FileReader("E:\\CODING FILES\\JavaCode\\kCoreConnectedMinWeight\\caseStudyInput\\id.txt"));//dblp min vertex = 0 which starts from 0
        //BufferedReader br = new BufferedReader(new FileReader("E:\\CODING FILES\\JavaCode\\dblpSax\\output\\running\\newIndexFruitFly.txt"));//dblp min vertex = 0 which starts from 0
        
        int count = 0;
        
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();
    
                while (line != null) {
                    
                    count++;
                    queryNodes.add(line);
                    if(count % 1000000 == 0)
                        System.out.println(count);
                    
                    line = br.readLine();
                }
                
                
            } finally {
                br.close();
            }
        }catch(Exception e){
            System.out.println("Reading caseStudy.txt as bufferedReaderhappens");
        }
        
        
        // construct the edges
        HashMap<String, ArrayList<NodeNeighbour>> hm2 = new HashMap<String, ArrayList<NodeNeighbour>>();
        HashSet<String> hs = new HashSet<String>();
        
        try{

        BufferedReader br = new BufferedReader(new FileReader("E:\\CODING FILES\\JavaCode\\dblpSax\\output\\running\\edgesWeight.txt"));//dblp min vertex = 0 which starts from 0

        int count = 0;
        
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();
    
                while (line != null) {
                    
                    count++;
                    
                    // for tabs
                    //String[] a = line.split("\\t");
                    
                    //which is spetical for dblp dataset; multiple spaces
                    String[] a = line.split("\\s+");
                    
                    //Exist Nodes 0
                    line = a[0] + "   " + a[1];
                    if(!hs.contains(line)){
                        for(int i = 0; i < 2; i++){

                            if(hm2.containsKey(Integer.toString(Integer.parseInt(a[i])+1))){
                                NodeNeighbour nb = new NodeNeighbour();
                                nb.nodeIndex = Integer.toString(Integer.parseInt(a[1-i])+1);
                                nb.weight = 1/Double.parseDouble(a[2]);
                                
                                hm2.get(Integer.toString(Integer.parseInt(a[i])+1)).add(nb);
                                //hm.put(a[0], hm.get(a[0]));
                            }
                            else{
                                ArrayList<NodeNeighbour> al = new ArrayList<NodeNeighbour>();
                                NodeNeighbour nb = new NodeNeighbour();
                                nb.nodeIndex = Integer.toString(Integer.parseInt(a[1-i])+1);
                                nb.weight = 1/Double.parseDouble(a[2]);
                                al.add(nb);
                                hm2.put(Integer.toString(Integer.parseInt(a[i])+1), al);
                            }
                            
                        }
                    }
                    
                    hs.add(line);
                    hs.add(a[1] + " " + a[0]);
                    
//                    if(count % 10000 == 0)
//                        System.out.println(count);
                    
                    if(count % 1000000 == 0)
                        System.out.println(count);
                    
                    line = br.readLine();
                }
                
                
            } finally {
                br.close();
            }
        }catch(Exception e){
            System.out.println("Reading **.txt as bufferedReaderhappens");
            System.out.println("Maybe lack for one dimension such as the weight");
        }
        
        
        try{

            File writename1 = new File("E:\\CODING FILES\\JavaCode\\kCoreConnectedMinWeight\\output\\id.txt");
            writename1.createNewFile(); 
            BufferedWriter id = new BufferedWriter(new FileWriter(writename1));

            File writename2 = new File("E:\\CODING FILES\\JavaCode\\kCoreConnectedMinWeight\\output\\name.txt");
            writename2.createNewFile(); 
            BufferedWriter name = new BufferedWriter(new FileWriter(writename2));

            File writename3 = new File("E:\\CODING FILES\\JavaCode\\kCoreConnectedMinWeight\\output\\x.txt");
            writename3.createNewFile(); 
            BufferedWriter x = new BufferedWriter(new FileWriter(writename3));

            File writename4 = new File("E:\\CODING FILES\\JavaCode\\kCoreConnectedMinWeight\\output\\y.txt");
            writename4.createNewFile(); 
            BufferedWriter y = new BufferedWriter(new FileWriter(writename4));

            File writename5 = new File("E:\\CODING FILES\\JavaCode\\kCoreConnectedMinWeight\\output\\weight.txt");
            writename5.createNewFile(); 
            BufferedWriter weight = new BufferedWriter(new FileWriter(writename5));

            // start analysis
            // String[] queryNodes = {"16680", "16838", "76197", "76199", "76315", "76317", "6673", "8479", "8483"};

            for(String ss : queryNodes){
                id.write(ss + "\r\n");               
                id.flush();
                name.write(hm.get(Integer.parseInt(ss)-1) + "\r\n"); // dont forget minus one
                name.flush();
            }

            int len = queryNodes.size();
            for(int i = 0; i < len; i++){
                ArrayList<NodeNeighbour> al = hm2.get(queryNodes.get(i));
                HashSet<String> hashNeighbor = new HashSet<String>();
                for(NodeNeighbour nb: al){
                    hashNeighbor.add(nb.nodeIndex);
                }
                for(int j = i + 1; j < len; j++){
                    if(hashNeighbor.contains(queryNodes.get(j))){
                    x.write(queryNodes.get(i) + "\r\n");               
                    x.flush();
                    y.write(queryNodes.get(j) + "\r\n");               
                    y.flush();
                    double wei = -1;
                    for(NodeNeighbour nb: al){
                        if(nb.nodeIndex.equals(queryNodes.get(j))){
                            wei = nb.weight;
                        }
                    }
                    weight.write(Double.toString(wei) + "\r\n" );
                    weight.flush();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        // get the weight of jiawei han jianpei
        ArrayList<NodeNeighbour> jiawei = hm2.get("16680");
        for(NodeNeighbour nb: jiawei){
            if(nb.nodeIndex.equals("16838")){
                System.out.println("jiawei - jianpei    " + nb.weight);
            }
        }
}
}
