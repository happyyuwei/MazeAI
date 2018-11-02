/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reforcelearning.example.maze;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
/**
 *
 * @author happy
 */
public class FinishUI extends JFrame{
    
    
    final private double def_width_percent=0.35;
    final private double def_height_percent=0.35;
    
    final private String drawable_path="C:\\Users\\happy\\Desktop\\ReforceLearning\\drawable\\example\\maze\\";
    
    /**
     * 
     * @param win
     * @param step
     * @throws Exception 
     */
    public FinishUI(boolean win,int step) throws Exception{
        super();
        int width=(int)(Toolkit.getDefaultToolkit().getScreenSize().width*this.def_width_percent);
        int height=(int)(Toolkit.getDefaultToolkit().getScreenSize().height*this.def_height_percent);
        
        //set size
        super.setSize(width,height);
        super.setLocationRelativeTo(null);
        super.setAlwaysOnTop(true);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //set layout
        super.setLayout(new BorderLayout());
        JLabel label=new JLabel();
        BufferedImage icon;
        if(win==true){
            icon=ImageIO.read(new File(this.drawable_path+"win.png"));
        }else{
            icon=ImageIO.read(new File(this.drawable_path+"lose.png"));
        }
        label.setIcon(new ImageIcon(icon.getScaledInstance(width, height, Image.SCALE_SMOOTH)));
        
        super.add(label);
        
        super.setVisible(true);
    }
}
