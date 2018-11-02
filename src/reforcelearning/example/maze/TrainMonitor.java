/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reforcelearning.example.maze;

/**
 *
 * @author happy
 */
public class TrainMonitor implements Monitor{
    
    final private String model_path=".\\model\\example\\maze\\model_4.txt";
    
    private int win_count_period_round=0;
    
    @Override
    public void epochTrained(QLearning instance,MazeState current_feedback){
        //print win round
        System.out.println("win round="+instance.getWin_round());
        
        //count win round each period
        if(current_feedback.getState().equals(MazeState.STATE_WIN)){
            this.win_count_period_round=this.win_count_period_round+1;
        }
        
        //save each 20 rounds
        int round=instance.getCurrent_round();
        if(round%20==0){
            
            //save
            instance.save(model_path);
            System.out.println(new java.util.Date()+" model saved, current round="+round);
            this.win_count_period_round=0;
            //show rate
             System.out.println("period win rate="+this.win_count_period_round/20.0);
        }
    }
}
