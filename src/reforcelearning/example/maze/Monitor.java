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
public interface Monitor {
  
    /**
     * 
     * @param learning_instance 
     * @param current_feedback 
     */
    public void epochTrained(QLearning learning_instance, MazeState current_feedback);
   
}
