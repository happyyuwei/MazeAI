/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reforcelearning.example.card;

import java.util.Arrays;

/**
 *
 * @author happy
 */
public class CardGambo {
    
    final private int card_num;
    final private double[] proxibility_list;
    
    /**
     * constructor.
     * @param card_num
     */
    public CardGambo(int card_num){
        this.card_num=card_num;
        //create posibility
        this.proxibility_list=this.createPosibility(card_num);
       // System.out.println(Arrays.toString(this.proxibility_list));
    }
    
    /**
     * 
     * @param num
     * @return 
     */
    public boolean gambo(int num){
        //out of bound
        if(num>=card_num){
            return false;
        }
        //random
        double rand=Math.random();
        //return
        return rand<this.proxibility_list[num];
    }
    
    /**
     * 
     * @param num
     * @return 
     */
    private double[] createPosibility(int num){
        double[] p=new double[num];
        for(int i=0;i<p.length;i++){
            //down 0.2
            p[i]=Math.random();
            //remove less zero
            if(p[i]<0){
                p[i]=0;
            }
        }
        return p;
    }
    
    
}
