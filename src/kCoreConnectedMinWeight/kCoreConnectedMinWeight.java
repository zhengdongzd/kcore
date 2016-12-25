package kCoreConnectedMinWeight;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;


class NodeNeighbour{
    String nodeIndex;
    double weight;
}

class NodeBFSvisited{
    String nodeIndex;
    boolean visited;
}

public class kCoreConnectedMinWeight {
    
    private int maxDegree = -1;
    private int VertexMax = -1;
    private int[] BinInitialPos = null;
    private int[] coreTable = null;//1,...,VertexMax
    private int[] degreeTable = null;//1,...,VertexMax
    private int[] degreeTableCopy = null;//1,...,VertexMax
    private int[] degreeRemoveTable = null;//1,...,VertexMax
    private double connectedSubGraphWeightSum = -1;
    private double finalGraphWeightSum = -1;
    
    
    
    
    //degreeTable: index is vertex number, value is degree
    int[] degreeTable(HashMap<String, ArrayList<NodeNeighbour>> hm){
        
        degreeTable = new int[VertexMax+1];//1...vertexMax, not use 0;
        
        for (Map.Entry<String, ArrayList<NodeNeighbour>> entry : hm.entrySet()) {
            String key = entry.getKey();
            ArrayList<NodeNeighbour> value = entry.getValue();
            
            int vertexDegree = value.size();
            
            if(vertexDegree > maxDegree)
                maxDegree = vertexDegree;
            
            degreeTable[Integer.parseInt(key)] = vertexDegree;
            //System.out.println(key + value);
        }
        
        return degreeTable;
    }
    
    
    ArrayList<int[]> binSort(int[] degreeTable){
        
        int[] vertTable = new int[VertexMax+1];//1,...,VertexMax
        int[] posTable =  new int[VertexMax+1];
        
        int[] AllBinSize = new int[maxDegree+1];//0,....,maxDegree
        
        for(int start = 1; start < degreeTable.length; start++){
        //for(int degree : degreeTable){
            //the first zero must not be included, it's not a node, the 0 not represent for a degree 0
            //while the others 0 not the first counts.
            AllBinSize[degreeTable[start]]++;
            //AllBinSize[degree]++;
        }
        
        int[] AllBinPos = new int[maxDegree+1];//0,....,maxDegree
        
        AllBinPos[0] = 1;//will use later
        //int allBeforeBins = AllBinSize[0];
        
        for(int i=1; i < AllBinPos.length;i++){
            AllBinPos[i] = AllBinPos[i-1] + AllBinSize[i-1];
        }
        
        BinInitialPos = new int[maxDegree+1];
        //BinInitialPos = AllBinPos;
        System.arraycopy( AllBinPos, 0, BinInitialPos, 0, AllBinPos.length );
        
        for(int vertexId = 1; vertexId < degreeTable.length; vertexId++){
            vertTable[AllBinPos[degreeTable[vertexId]]] = vertexId;// sorted table
            posTable[vertexId] = AllBinPos[degreeTable[vertexId]];
            AllBinPos[degreeTable[vertexId]]++;
        }
        
        ArrayList<int[]> result = new ArrayList<int[]>();
        result.add(vertTable);
        result.add(posTable);
        
        return result;
    }
    
    
    int getMaxCore(HashMap<String, ArrayList<NodeNeighbour>> hm){
        
        coreTable = new int[VertexMax+1];
        
        int[] degreeTable = degreeTable(hm);
        
        degreeTableCopy = new int[VertexMax+1];
        System.arraycopy(degreeTable, 0, degreeTableCopy, 0, degreeTable.length );
        
        ArrayList<int[]> result = binSort(degreeTable);
        int[] vertTable = result.get(0);
        int[] posTable =  result.get(1);
        
        int maxCore = -1;
        
        for(int start = 1; start < vertTable.length; start++){
            
            coreTable[vertTable[start]] = degreeTable[vertTable[start]];
            
            if(degreeTable[vertTable[start]] > maxCore)
                maxCore = degreeTable[vertTable[start]];
            
            ArrayList<NodeNeighbour> neighbors = hm.get(Integer.toString(vertTable[start]));
            
            if(neighbors!=null){
                for(NodeNeighbour nodeNeighbor : neighbors){
                    String neighborString = nodeNeighbor.nodeIndex;
                    if(degreeTable[Integer.parseInt(neighborString)] > degreeTable[vertTable[start]]){
                        int originalDegree = degreeTable[Integer.parseInt(neighborString)];
                        degreeTable[Integer.parseInt(neighborString)]--;
                        //swap in verTable
                        int startBin = BinInitialPos[originalDegree];
                        int neighborIndex = posTable[Integer.parseInt(neighborString)]; 
                        
                        int temp = vertTable[startBin];
                        vertTable[startBin] = Integer.parseInt(neighborString);
                        vertTable[neighborIndex] = temp;
                        //swap the position
                        posTable[Integer.parseInt(neighborString)] = startBin;
                        posTable[temp]=neighborIndex;                   
                        //update the All BinInitialPos
                        BinInitialPos[originalDegree]++;
                    }
                }
            }   
            
        }
        
        return maxCore;
    }
    
    
    ArrayList<Integer> getVertexLargerCore(int queryCore){
        ArrayList<Integer> result = new ArrayList<Integer>();
        
        for(int index = 1; index < coreTable.length; index++){
            if(coreTable[index] >= queryCore)
                result.add(index);
        }
        
        return result;
    }
    
    
    ArrayList<Integer> bfsfour(int queryCore, HashMap<String, ArrayList<NodeNeighbour>> hm, String[] queryNodes){
        ArrayList<Integer> result = new ArrayList<Integer>();
        HashSet<String> hs = new HashSet<String>();
        Queue<String> bfs = new LinkedList<String>();
        
        for(String queryNode : queryNodes){
            result.add(Integer.parseInt(queryNode));//forget first, cause null all the time since the retuned list doesnt contain the querynodes 
            bfs.add(queryNode);
            hs.add(queryNode);
        }
        // 1-hop
        for(int i = 0; i < 1; i++){
            int size = bfs.size();
            while(size != 0){
                String node = bfs.poll();
                ArrayList<NodeNeighbour> al = hm.get(node);
                if(al!=null){ // if not, will have the null pointer exception
                    for(NodeNeighbour nb : al){
                        if((coreTable[Integer.parseInt(nb.nodeIndex)] >= queryCore)&&(!hs.contains(nb.nodeIndex))&&(nb.weight<=1)){
                            result.add(Integer.parseInt(nb.nodeIndex));
                            hs.add(nb.nodeIndex);
                            bfs.add(nb.nodeIndex);// forget first
                        }
                    }
                }
                size--;
            }
        }
        
        for (int i : result){
            System.out.println(i);
        }
        return result;
    }
    
    
//     public void bfs()
//      {
//          // BFS uses Queue data structure
//          Queue queue = new LinkedList();
//          queue.add(this.rootNode);
//          printNode(this.rootNode);
//          rootNode.visited = true;
//          while(!queue.isEmpty()) {
//              Node node = (Node)queue.remove();
//              Node child=null;
//              while((child=getUnvisitedChildNode(node))!=null) {
//                  child.visited=true;
//                  printNode(child);
//                  queue.add(child);
//              }
//          }
//          // Clear visited property of nodes
//          clearNodes();
//      }
    
    
    boolean BfsCheckConnected(ArrayList<Integer> connectedSubGraph, HashMap<String, ArrayList<NodeNeighbour>> hm, String startSeatchNode, HashSet<Integer> hsSub){
        
        Queue queue = new LinkedList<String>();
        queue.add(startSeatchNode);
        
        HashSet<String> hs = new HashSet<String>();
        
        while(!queue.isEmpty()) {
            String node = (String)queue.remove();
            
            ArrayList<NodeNeighbour> al = hm.get(node);
            
            if(al!=null){
                for(NodeNeighbour nb : al) {
                    String neighbourIndex = nb.nodeIndex;
                    if(hsSub.contains(Integer.parseInt(neighbourIndex))&&(!hs.contains(neighbourIndex))){
                        hs.add(neighbourIndex);
                        queue.add(neighbourIndex);
                    }
                }
            }
        }

        for(int vertex : connectedSubGraph)
            if(!hs.contains(Integer.toString(vertex)))
                return false;

        return true;
    }
    
    
    ArrayList<Integer> getConnectedSubGraph(ArrayList<Integer> vertexLargerCoreList, HashMap<String, ArrayList<NodeNeighbour>> hm, String[] queryNodes, HashSet<Integer> hsSub){
        ArrayList<Integer> newConnectedCoreTable =  new ArrayList<Integer>();
        HashSet<String> hs =  new HashSet<String>();
        
        Queue queue = new LinkedList<String>();
        queue.add(queryNodes[0]);
        hs.add(queryNodes[0]);
        
        while(!queue.isEmpty()) {
            String node = (String)queue.remove();
            
            ArrayList<NodeNeighbour> al = hm.get(node);
            if(al!=null){
                for(NodeNeighbour nb : al) {
                    String neighbourIndex = nb.nodeIndex;
                    if(hsSub.contains(Integer.parseInt(neighbourIndex))&&(!hs.contains(neighbourIndex))){
                        hs.add(neighbourIndex);
                        queue.add(neighbourIndex);
                    }
                }
            }
        }
    
        //check if the connected graph contains all the querynodes
        for(String qn : queryNodes)
            if(!hs.contains(qn))
                return null;
    
        for (String s : hs) {
            newConnectedCoreTable.add(Integer.parseInt(s));
        }
        
        return newConnectedCoreTable;
    }
    
    
    ArrayList<Integer> removeRecursive(Integer deleteNode, ArrayList<Integer> connectedSubGraph, HashMap<String, ArrayList<NodeNeighbour>> hm, String[] queryNodes, HashSet<Integer> hsSub2, int queryCore){
        ArrayList<Integer> needRemove = new  ArrayList<Integer>();

        HashSet<String> hs =  new HashSet<String>();

        Queue queue = new LinkedList<String>();
        queue.add(Integer.toString(deleteNode));
        hs.add(Integer.toString(deleteNode));

        // shouldn't change degreee table now since may go to the next largest 
        degreeRemoveTable = new int[VertexMax+1];
        //BinInitialPos = AllBinPos;
        System.arraycopy( degreeTableCopy, 0, degreeRemoveTable, 0, degreeTableCopy.length );
        
        while(!queue.isEmpty()) {
            String node = (String)queue.remove();
            
            ArrayList<NodeNeighbour> al = hm.get(node);
            if(al!=null){
                for(NodeNeighbour nb : al) {
                    String neighbourIndex = nb.nodeIndex;
                    if(hsSub2.contains(Integer.parseInt(neighbourIndex))&&(!hs.contains(neighbourIndex))){
                        degreeRemoveTable[Integer.parseInt(neighbourIndex)]--;
                        if(degreeRemoveTable[Integer.parseInt(neighbourIndex)]<queryCore){
                            hs.add(neighbourIndex);
                            queue.add(neighbourIndex);
                        }
                    }
                }
            }
        }
    
        //check if the connected graph contains all the querynodes
        for(String qn : queryNodes)
            if(hs.contains(qn))
                return null;
    
        for (String s : hs) {
            needRemove.add(Integer.parseInt(s));
        }
        
        // if return means we wont do this step
        // tricky part
        degreeTableCopy = degreeRemoveTable;
        
        return needRemove;
    }

    double getGraphWeightSum(ArrayList<Integer> subGraph, HashSet<Integer> hs, HashMap<String, ArrayList<NodeNeighbour>> hm){
        double result = 0;
        
        if(subGraph != null){
            for(int node : subGraph){
                ArrayList<NodeNeighbour> al = hm.get(Integer.toString(node));
                if(al != null){
                    for(NodeNeighbour nb : al){
                        if(hs.contains(Integer.parseInt(nb.nodeIndex))){
                            result += nb.weight;
                        }
                    }
                }
            }
        }

        return result/2;
    }
    
    ArrayList<Integer> getSubGraph(ArrayList<Integer> vertexLargerCoreList, HashMap<String, ArrayList<NodeNeighbour>> hm, String[] queryNodes, int queryCore){
    
        int initialVertexSize = vertexLargerCoreList.size();
        HashSet<Integer> hsSub = new HashSet<Integer>();
        for(int node : vertexLargerCoreList)
            hsSub.add(node);
        
        // bug 1 not stop, is set #core = 10 while the max is 6
        for(String qn : queryNodes)
            if(!hsSub.contains(Integer.parseInt(qn)))
                return null;
        
        ArrayList<Integer> connectedSubGraph = getConnectedSubGraph(vertexLargerCoreList, hm, queryNodes, hsSub);
        //which means the initial connected graph couldnt contain all the query nodes
        if(connectedSubGraph == null)
            return null;
        
        int initialConnectedVertexSize = connectedSubGraph.size();
        
        System.out.println("Finish finding the connected larger than core graph");
        System.out.println("the connected larger than core graph size: " + initialConnectedVertexSize);
        
        System.out.println("========================");
        
        HashSet<Integer> hsSub2 = new HashSet<Integer>();
        for(int node : connectedSubGraph)
            hsSub2.add(node);
        
        connectedSubGraphWeightSum = getGraphWeightSum(connectedSubGraph, hsSub2, hm);
        
        //upadate new graph degree
        for(Integer nodeConnected : connectedSubGraph){
            ArrayList<NodeNeighbour> al = hm.get(Integer.toString(nodeConnected));
            if(al!=null){
                for(NodeNeighbour nb : al) {
                    String neighbourIndex = nb.nodeIndex;
                    if(!hsSub2.contains(Integer.parseInt(neighbourIndex))){
                        degreeTableCopy[nodeConnected]--;
                        // ************************ new added Dec 21 11pm
//                        if(degreeTableCopy[nodeConnected] < queryCore){
//                              connectedSubGraph = removeRecursive(nodeConnected, connectedSubGraph, hm, queryNodes, hsSub2, queryCore);
//                            connectedSubGraph.remove(nodeConnected);
//                            hsSub2.remove(nodeConnected);
//                            break;
//                        }
                        // ************************ new added Dec 21 11pm
                    }
                }
            }
        }
        
        // Dec 23 added
        System.out.println("*****************************");
        System.out.println("connectedSubGraphWeightSum: " + connectedSubGraphWeightSum);
        for(Integer nodeConnected : connectedSubGraph){
            System.out.println(nodeConnected);
        }
        System.out.println("*****************************");
        // Dec 23 added
        
        HashSet<String> hs =  new HashSet<String>();
        for(String qn : queryNodes)
            hs.add(qn);
        
        //start to iterate delete the nodes
        int flag = 0;
        String startSarchNode = queryNodes[0];
        
        int iterationTimesForOutput = 1;
        
        boolean transferLinear = false;
        HashSet<Integer> hsCheckedNodesCouldntDelete = new HashSet<Integer>();
        
        while(flag != -1){
            flag = -1;
            double maxWeight  = -1;
            Integer removeNode = -1;
            
            int len = connectedSubGraph.size();
            len = len - queryNodes.length;
            double[][] weightsumSort = new double[len][2];//remember to int for the node index
            
            int indexSort = 0;
            
            for(Integer node : connectedSubGraph){
                if(!hs.contains(Integer.toString(node))){//detect if query nodes or not
                  
                  ArrayList<NodeNeighbour> al = hm.get(Integer.toString(node));
                  double add = 0;
                  // double count = 0;//new added
                  if(al!=null){
                      for(NodeNeighbour nb : al){
                          if(hsSub2.contains(Integer.parseInt(nb.nodeIndex))){
                              add += nb.weight;
                              // count++;// new added
                          }
                      }
                  }
                  weightsumSort[indexSort][0] = add;// new added
                  weightsumSort[indexSort][1] = (double)node;
                  indexSort++;
//                    //connectedSubGraph.remove(node);
//                    ArrayList<Integer> RemovedOneconnectedSubGraph =  new ArrayList<Integer>(connectedSubGraph);
//                    RemovedOneconnectedSubGraph.remove(node);
//                    hsSub2.remove(node);
//                    if(BfsCheckConnected(RemovedOneconnectedSubGraph, hm, startSeatchNode, hsSub2)){
//                        //must contain the querynodes we dont need to check since we only remove one node once.
//                        flag = 0;
//                        ArrayList<NodeNeighbour> al = hm.get(Integer.toString(node));
//                        double add = 0;
//                        for(NodeNeighbour nb : al){
//                            if(hsSub2.contains(Integer.parseInt(nb.nodeIndex))){
//                                add += nb.weight;
//                            }
//                        }
//                        if(add>maxWeight){
//                            maxWeight = add;
//                            removeNode = node;
//                        }
//                    }
//                    //connectedSubGraph.add(node);
//                    hsSub2.add(node);
//                }
                    
            }
//            if(removeNode != -1){
//                connectedSubGraph.remove(removeNode);
//                hsSub2.remove(removeNode);
//            }
        }

        Arrays.sort(weightsumSort, Comparator.comparing((double[] arr) -> arr[0]).reversed());
        
        ArrayList<Integer> removeGraph = null;
        
        boolean haveSthToRemove = false;
        
        //transfer only one need to change
        int batchsize = weightsumSort.length/10;
        //int batchsize = 1;
        
        if((batchsize > 10)&&(!transferLinear)){
            int deleteCountNodes = 0;
            for(int i = 0; i < batchsize; i++){
                Integer deleteNode = (Integer)(int)weightsumSort[i][1];
                ArrayList<Integer> removeBatchCollectGraph = removeRecursive(deleteNode, connectedSubGraph, hm, queryNodes, hsSub2, queryCore);
                if(removeBatchCollectGraph!=null){
                    for(Integer removeNodeIterate : removeBatchCollectGraph){
                        connectedSubGraph.remove(removeNodeIterate);
                        hsSub2.remove(removeNodeIterate);
                    }
                    haveSthToRemove = true;
                    deleteCountNodes++;
                }
                if(i%1000 == 0){
                    System.out.print("#" + i);
                }
            }
            if(deleteCountNodes <= batchsize/10){
                transferLinear = true;
                haveSthToRemove = true;//avoid the case all the first 10% nodes couldnt delete, then we set true here to avoid end the delete progress and give it the chance to linear delete
            }
        }

        if((batchsize <= 10)||(transferLinear)){
            for(int i = 0; i < weightsumSort.length; i++){
                Integer deleteNode = (Integer)(int)weightsumSort[i][1];
                if(!hsCheckedNodesCouldntDelete.contains(deleteNode)){
                    removeGraph = removeRecursive(deleteNode, connectedSubGraph, hm, queryNodes, hsSub2, queryCore);
                    if(removeGraph!=null){
                        for(Integer removeNodeIterate : removeGraph){
                            connectedSubGraph.remove(removeNodeIterate);
                            hsSub2.remove(removeNodeIterate);
                        }
                        haveSthToRemove = true;
                        break;
                    }
                    else{
                        hsCheckedNodesCouldntDelete.add(deleteNode);
                    }
                }
            }
        }

//        if(removeGraph!=null){
//            if(removeGraph.size()!=0){
        if(haveSthToRemove == true){
                flag = 0;
                System.out.println("Finish the " + iterationTimesForOutput + "th time remove iteration");
                System.out.println("Finish the " + iterationTimesForOutput + "th core graph size: " + connectedSubGraph.size());
                System.out.println("========================");
                iterationTimesForOutput++;
                
                // Dec 23 added
                if(connectedSubGraph.size()<40){
                    System.out.println("connectedSubGraph.size(): " + connectedSubGraph.size());
                    for (Integer i : connectedSubGraph){
                        System.out.println(i);
                    }
                }
             // Dec 23 added
                
//for the second test to get the 10 nodes
//                if(connectedSubGraph.size()<12){
//                    return connectedSubGraph;
//                }
            }
//        }

    }

        finalGraphWeightSum = getGraphWeightSum(connectedSubGraph, hsSub2, hm);

        return connectedSubGraph;
    }
//  ArrayList<Integer> decoupleCores(HashMap<String, ArrayList<String>> hm){
//      
//      ArrayList<Integer> vertexCore = new ArrayList<Integer>();
//      
//      int[] degreeTable = degreeTable(hm);
//      
//      ArrayList<ArrayList<Integer>> twoTables = binSort(degreeTable);
//      
//      ArrayList<Integer> vertTable = twoTables.get(0);
//      ArrayList<Integer> posTable = twoTables.get(1);
//      
//      //int maxcore = 
//      
//      for(int vertexId : vertTable){
//          
//      }
//          
//      
//      
//      return vertexCore;
//      
//  }
    
    
    
    public static void main(String[] args){
        
        kCoreConnectedMinWeight run = new kCoreConnectedMinWeight();
        HashMap<String, ArrayList<NodeNeighbour>> hm = new HashMap<String, ArrayList<NodeNeighbour>>();
        HashSet<String> hs = new HashSet<String>();
        
        try{
        //BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\ZhengD\\Desktop\\Amazon0601.txt"));
        //BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\ZhengD\\Desktop\\Amazon0601.txt"));
        //BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\ZhengD\\Desktop\\TestXinWeight0.txt"));
        BufferedReader br = new BufferedReader(new FileReader("E:\\CODING FILES\\JavaCode\\dblpSax\\output\\running\\edgesWeight.txt"));//dblp min vertex = 0 which starts from 0
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
                    String[] a = line.split("\\s+");
                    
                    
                    //System.out.println(line);
                    
                    //No nodes 0
//                  for(int i = 0; i < 2; i++){
//                      
//                      if(Integer.parseInt(a[i])>run.VertexMax)
//                          run.VertexMax = Integer.parseInt(a[i]);
//                      
//                      if(hm.containsKey(a[i])){
//                          hm.get(a[i]).add(a[1-i]);
//                          //hm.put(a[0], hm.get(a[0]));
//                      }
//                      else{
//                          ArrayList<String> al = new ArrayList<String>();
//                          al.add(a[1-i]);
//                          hm.put(a[i], al);
//                      }
//                      
//                  }
                    
                    //Exist Nodes 0
                    line = a[0] + "   " + a[1];
                    if(!hs.contains(line)){
                        for(int i = 0; i < 2; i++){
                            
                            if( Integer.parseInt(a[i])+1 > run.VertexMax)
                                run.VertexMax = Integer.parseInt(a[i]) + 1;
                            
                            if(hm.containsKey(Integer.toString(Integer.parseInt(a[i])+1))){
                                NodeNeighbour nb = new NodeNeighbour();
                                nb.nodeIndex = Integer.toString(Integer.parseInt(a[1-i])+1);
                                nb.weight = 1/Double.parseDouble(a[2]);
                                
                                hm.get(Integer.toString(Integer.parseInt(a[i])+1)).add(nb);
                                //hm.put(a[0], hm.get(a[0]));
                            }
                            else{
                                ArrayList<NodeNeighbour> al = new ArrayList<NodeNeighbour>();
                                NodeNeighbour nb = new NodeNeighbour();
                                nb.nodeIndex = Integer.toString(Integer.parseInt(a[1-i])+1);
                                nb.weight = 1/Double.parseDouble(a[2]);
                                al.add(nb);
                                hm.put(Integer.toString(Integer.parseInt(a[i])+1), al);
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


        System.out.println("Finish construt the edges hashmap");
        System.out.println("Vertices: " + hm.size());
        //we could get the edges from the input txt file which is super easier
        System.out.println("for dblp should shpw up 4 numbers : 1,000,000...");
        System.out.println("========================");

//never used
//get the index of max core
//        **********************
//        int resultCore = run.getMaxCore(hm);
//        System.out.println("maxCore: " + resultCore);
//        int[] out = run.coreTable;
//        for(int index = 1; index < out.length; index++){
//            if(out[index] >= 3){
//                System.out.println(index + "    " + out[index]);
//            }
//        }



//==========================================================================
//==========================================================================
        //***********************Fruit***************************//
//==========================================================================
//==========================================================================
//**********************
        //dont forget to transfer between batch/linear
        //test 1 -- vary k
        //1910
        //2944
        //1698
        //3660
        //1900
//        String[] queryNodes = {"1698", "2944"};//fix k =2
//        int queryCore = 4;//vary
//        long startTime   = System.currentTimeMillis();
//        for(int time = 0; time < 10; time++){
//            int resultCore = run.getMaxCore(hm);
//            System.out.println("MaxCore: " + resultCore);
//            System.out.println("Finish finding the max -core, not connected larger than core  graph");
//            ArrayList<Integer> vertexLargerCoreList = run.getVertexLargerCore(queryCore);
//            System.out.println("not connected larger than core  graph size: " + vertexLargerCoreList.size());
//            System.out.println("========================");
//              System.out.println("The subgraph nodes: ");
//              ArrayList<Integer> result = run.getSubGraph(vertexLargerCoreList, hm, queryNodes, queryCore);
//              if(result == null){
//                  System.out.println("The final grap is null");
//              }
//              else{
//                  for(int vertex : result){
//                      System.out.println(vertex);
//                  }
//              }
//        }
//        long endTime   = System.currentTimeMillis();
//        long time = (endTime - startTime)/10;// /10/1000 get seconds
//        System.out.println(queryCore + " : " + time);
//        System.out.println("connectedGraphWeightSum : " + run.connectedSubGraphWeightSum);
//        System.out.println("finalGraphWeightSum : " + run.finalGraphWeightSum);
        //**********************
        
        
        //**********************
        //test 2 -- vary |Q|
        //"1698"
        //"1698", "3660", "2130"
        //"1698", "3660", "2130", "894", "696"
        //"1698", "3660", "2130", "894", "696", "3196", "1900"
        //"1698", "3660", "2130", "894", "696", "3196", "1900", "1910", "2944"
//        String[] queryNodes = {"1698"};//should be after transfer
//        int queryCore = 2;
//        long startTime   = System.currentTimeMillis();
//        for(int time = 0; time < 10; time++){
//            int resultCore = run.getMaxCore(hm);
//            System.out.println("MaxCore: " + resultCore);
//            System.out.println("Finish finding the max -core, not connected larger than core  graph");
//            ArrayList<Integer> vertexLargerCoreList = run.getVertexLargerCore(queryCore);
//            System.out.println("not connected larger than core  graph size: " + vertexLargerCoreList.size());
//            System.out.println("========================");
//              System.out.println("The subgraph nodes: ");
//              ArrayList<Integer> result = run.getSubGraph(vertexLargerCoreList, hm, queryNodes, queryCore);
//              if(result == null){
//                  System.out.println("The final grap is null");
//              }
//              else{
//                  for(int vertex : result){
//                      System.out.println(vertex);
//                  }
//              }
//        }
//        long endTime   = System.currentTimeMillis();
//        long time = (endTime - startTime)/10;// /10/1000 get seconds
//        System.out.println(queryCore + " : " + time);
//**********************
//==========================================================================
//==========================================================================
//***********************Fruit***************************//
//==========================================================================
//==========================================================================



//==========================================================================
//==========================================================================
//***********************dblp***************************//
//==========================================================================
//==========================================================================
//dont forget to transfer between batch/linear
        //test 1 -- vary k = 2,3,4,5,6
        //"16680", "16838"
        //
        
        //(473283 - zechao Shang)
        
        
        //core = 9 i genetate
        //Jure Leskovec
        //195480
        //52271
        //544256
        //544252
        //721693
        //558880
        //558881
        //558879
        //196626
        //558877
        //499969
        //676070
        //499963
        //499968
        //294716
        
        //"16680", "16838" -- original
        //"32240", "397106"
        // "18211"
        
        // "16680" "18211" -- Jiawei Yizhou
        // "16730" "18210" Haixun wang; Wei fan
        // "16681" "1201845" -- Laks; Hosagrahar Visvesvaraya Jagadish
        //  "1768" -- Jeffrey Xu Yu  "16876"?
        // "17086" -- Divesh Srivastava
        // plus one not minus one
        String[] queryNodes = {"16680", "16838", "16631", "17678", "16713"};//fix k =2
        //String[] queryNodes = {"32240"};//fix k =2
        int queryCore = 4;//vary
        long startTime   = System.currentTimeMillis();
        for(int time = 0; time < 1; time++){
            int resultCore = run.getMaxCore(hm);
            System.out.println("MaxCore: " + resultCore);
            System.out.println("Finish finding the max core, not connected larger than core graph");
            // ArrayList<Integer> vertexLargerCoreList = run.getVertexLargerCore(queryCore);
            ArrayList<Integer> vertexLargerCoreList = run.bfsfour(queryCore, hm, queryNodes);

            System.out.println("not connected larger than core graph size: " + vertexLargerCoreList.size());
            System.out.println("========================");
              ArrayList<Integer> result = run.getSubGraph(vertexLargerCoreList, hm, queryNodes, queryCore);
              System.out.println("The subgraph nodes: ");
              if(result == null){
                  System.out.println("The final grap is null");
              }
              else{
                  for(int vertex : result){
                      System.out.println(vertex);
                  }
              }
        }
        long endTime   = System.currentTimeMillis();
        long time = (endTime - startTime);// /10/1000 get seconds
        System.out.println(queryCore + " : " + time);
        System.out.println("connectedGraphWeightSum : " + run.connectedSubGraphWeightSum);
        System.out.println("finalGraphWeightSum : " + run.finalGraphWeightSum);
        //**********************
      //dblp 
      // fix  k = 5
      //test 2 -- vary |Q| = 1,3,5,7,9
      //"16680", "16838", "76197", "76199", "76315", "76317", "6673", "8479", "8483"
//      String[] queryNodes = {"16680", "16838", "76197", "76199", "76315", "76317", "6673", "8479", "8483"};//fix k =2
//      int queryCore = 5;//vary
//      long startTime   = System.currentTimeMillis();
//      for(int time = 0; time < 1; time++){
//          int resultCore = run.getMaxCore(hm);
//          System.out.println("MaxCore: " + resultCore);
//          System.out.println("Finish finding the max -core, not connected larger than core  graph");
//          ArrayList<Integer> vertexLargerCoreList = run.getVertexLargerCore(queryCore);
//          System.out.println("not connected larger than core  graph size: " + vertexLargerCoreList.size());
//          System.out.println("========================");
//            ArrayList<Integer> result = run.getSubGraph(vertexLargerCoreList, hm, queryNodes, queryCore);
//            System.out.println("The subgraph nodes: ");
//            if(result == null){
//                System.out.println("The final grap is null");
//            }
//            else{
//                for(int vertex : result){
//                    System.out.println(vertex);
//                }
//            }
//      }
//      long endTime   = System.currentTimeMillis();
//      long time = (endTime - startTime);// /10/1000 get seconds
//      System.out.println(queryCore + " : " + time);

      //**********************
          System.out.println("========================");
    }
}

