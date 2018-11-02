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
public class MazeState {
    
    //state code
    final public static String STATE_WIN="win";
    final public static String STATE_LOSE="lose";
    final public static String STATE_CONTINUE="cuntinue";
    final public static String STATE_START="start";
    
    
    //current state arguments
    final private String state;
    final private int current_x;
    final private int current_y;
    final private int this_action_reward;
    final private int current_reward;
    final private int total_reward;    
    
    /**
     * 
     * @param state
     * @param current_x
     * @param current_y
     * @param this_action_reward
     * @param current_reward
     * @param total_reward
     */
    public MazeState(String state, int current_x, int current_y,int this_action_reward,  int current_reward,int total_reward){
        this.state=state;
        this.current_x=current_x;
        this.current_y=current_y;
        this.this_action_reward=this_action_reward;
        this.current_reward=current_reward;
        this.total_reward=total_reward;
    }
    
    @Override
    public String toString(){
        return String.format("state=%s, current location=(%s,%s), this step reward=%s, current reward=%s, total reward=%s", this.state,this.current_x,this.current_y,this.this_action_reward,this.current_reward,this.total_reward);
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @return the current_x
     */
    public int getCurrent_x() {
        return current_x;
    }

    /**
     * @return the current_y
     */
    public int getCurrent_y() {
        return current_y;
    }
    
    /**
     * 
     * @return 
     */
    public int getThis_action_reward(){
        return this.this_action_reward;
    }

    /**
     * @return the current_reward
     */
    public int getCurrent_reward() {
        return current_reward;
    }

    /**
     * @return the total_reward
     */
    public int getTotal_reward() {
        return total_reward;
    }

    
    
}
