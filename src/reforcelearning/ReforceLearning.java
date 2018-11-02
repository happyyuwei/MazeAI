/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reforcelearning;
import reforcelearning.example.maze.*;
import java.util.*;
/**
 *
 * @author happy
 */
public class ReforceLearning {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        // TODO code application logic here
        //int[][] mode={{0,0,0,0,1,0},{0,0,-1,0,0,-1},{0,-1,1,0,-1,0},{0,0,0,0,1,0},{0,0,-1,1,-1,0}};
        int[][] mode={{0,0,0,1},{0,-1,0,0},{0,1,-1,0},{0,-1,0,0}};
        //int[][] mode={{0,0,-1},{0,-1,0},{1,0,1}};
        Maze maze=new Maze(mode);
        maze.start(0, 0);
        
       //qlearning instance
        QLearning q=new QLearning(mode.length,mode[0].length,4,maze);
        q.load(".\\model\\example\\maze\\model_4.txt");
        //monitor instance
        TrainMonitor train_monitor=new TrainMonitor();
        q.setMonitor(train_monitor);
        
        //start train
         q.train(10000, 0, 0);





    }
    
}
