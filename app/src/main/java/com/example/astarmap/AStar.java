package com.example.astarmap;
import java.util.*;
import java.util.List;

//qh 2020-03-15
public class AStar {
    public int COST=10;
    public int row;
    public int col;
    public Node startNode;
    public Node endNode;
    public PriorityQueue<Node> openList;
    public HashSet<Node> visitedSet;
    public Node[][] NodeMap;

    public AStar(int[][] map,int[] start,int[] end){
        this.row=map.length;
        this.col=map[0].length;
        openList=new PriorityQueue<Node>(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                if(o1.getF()==o2.getF()){
                    return Integer.compare(o1.getH(),o2.getH());
                }
                return Integer.compare(o1.getF(),o2.getF());//返回-1，0，1
            }
        });
        visitedSet=new HashSet<>();
        NodeMap=new Node[row][col];
        for(int i=0;i<row;i++){
            for(int j=0;j<col;j++){
                NodeMap[i][j]=new Node(i,j);
                if(map[i][j]==0)
                    NodeMap[i][j].setBlock(false);//路径
                else
                    NodeMap[i][j].setBlock(true);
            }
        }
        this.startNode=NodeMap[start[0]][start[1]];
        this.endNode=NodeMap[end[0]][end[1]];
    }
    public boolean search(){
        if(this.endNode.isBlock()){
            System.out.println("Your can't set endpoint at block");
            return false;
        }
        if(this.startNode.isBlock()){
            System.out.println("Your can't set startpoint at block");
            return false;
        }
        openList.add(startNode);
        visitedSet.add(startNode);
        while(!openList.isEmpty()){
            Node current=openList.poll();
            visitedSet.add(current);
            List<Node> neighbors=getAdj(current);
            for(Node neighbor : neighbors){
                if(!visitedSet.contains(neighbor)){
                    int g=neighbor.calculateG(startNode);
                    int h=neighbor.calculateH(endNode);
                    int tempf=g+h;
                    if(openList.contains(neighbor)){
                        int f=neighbor.getF();
                        if(f>tempf){
                            openList.remove(neighbor);
                            neighbor.setF(tempf);
                            neighbor.setH(h);
                            neighbor.setG(g);
                            neighbor.setParent(current);
                            openList.add(neighbor);
                        }
                    }
                    else {
                        neighbor.setF(tempf);
                        neighbor.setH(h);
                        neighbor.setG(g);
                        neighbor.setParent(current);
                        openList.add(neighbor);
                    }
                }
                if(neighbor.equals(endNode))
                    return true;
            }
        }
        return false;
    }
    public Deque<Node> findpath(){
        Node current=endNode;
        Deque<Node> path=new ArrayDeque<>();
        path.add(endNode);
        System.out.println(current.toString());
        while(!current.equals(startNode)){
            current=current.getParent();
            path.add(current);
        }
        return path;
    }

    public List<Node> getAdj(Node current){
        List<Node> L=new ArrayList<>();
        int Noderow=current.getRow();
        int Nodecol=current.getColumn();
        if(Noderow>0){
            Node node=NodeMap[Noderow-1][Nodecol];
            if(!node.isBlock())
                L.add(node);
        }
        if(Nodecol>0){
            Node node=NodeMap[Noderow][Nodecol-1];
            if(!node.isBlock())
                L.add(node);
        }
        if(Noderow<row-1){
            Node node=NodeMap[Noderow+1][Nodecol];
            if(!node.isBlock())
                L.add(node);
        }
        if(Nodecol<col-1){
            Node node=NodeMap[Noderow][Nodecol+1];
            if(!node.isBlock())
                L.add(node);
        }
        return L;
    }
}
