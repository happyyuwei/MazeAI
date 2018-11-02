/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reforcelearning.example.card;
import javax.swing.*;
import java.awt.*;
/**
 *
 * @author happy
 */
public class CardUI extends JFrame{
    
    final private double width_percent=0.6;
    final private double height_percent=0.4;
    
    
    /**
     * 
     */
    public CardUI(){
        super();
        super.setSize((int)(Toolkit.getDefaultToolkit().getScreenSize().width*width_percent),(int)(Toolkit.getDefaultToolkit().getScreenSize().height*height_percent));
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setLocationRelativeTo(null);
        super.setLayout(new BorderLayout());
        JPanel background_panel=new JPanel();
        background_panel.setBackground(Color.GREEN);
        
        super.add(background_panel,BorderLayout.CENTER);
        super.setVisible(true);
        
    }
    
}
