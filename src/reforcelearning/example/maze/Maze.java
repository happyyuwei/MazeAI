/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reforcelearning.example.maze;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.*;

/**
 *
 * @author happy
 */
public class Maze extends JFrame {

    //the size of each grid
    final private double def_grid_width_percent = 0.08;

    //drawable location
    final private String drawable = ".\\drawable\\example\\maze\\";
    final private String adv_drawable = drawable + "adv.png";
    final private String award_drawable = drawable + "award.png";
    final private String devil_drawable = drawable + "devil.png";
    //icon instance
    final private ImageIcon adv_icon;
    final private ImageIcon devil_icon;
    final private ImageIcon award_icon;

    //maze mode, 0 is nothing, 1 is award, -1 is devil
    final private int[][] init_mode;
    final private JLabel[][] maze_label;
    final private int total_reward;

    //game state
    private int init_x;
    private int init_y;
    private int current_x;
    private int current_y;
    private int current_award = 0;
    private int[][] mode;

    //state listener
    private MazeListener maze_listener;

    /**
     *
     * @param mode
     * @throws java.lang.Exception
     */
    public Maze(int[][] mode) throws Exception {
        super("迷宫");
        //store mode
        this.init_mode = mode;
        this.mode = this.copy(this.init_mode);
        //grad width
        int grid_width = (int) (Toolkit.getDefaultToolkit().getScreenSize().width * this.def_grid_width_percent);
        //calculate width
        int width = mode[0].length * grid_width;
        int height = mode.length * grid_width;
        super.setSize(width, height);
        //System.out.println(this.def_grid_width_percent+" "+grid_width+" "+width+" "+height);
        //basic config
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setLocationRelativeTo(null);
        super.setLayout(new BorderLayout());

        //background
        JPanel background_panel = new JPanel();
        background_panel.setBackground(Color.WHITE);
        background_panel.setLayout(new GridLayout(mode.length, mode[0].length));
        //initial label
        this.maze_label = new JLabel[mode.length][mode[0].length];
        int image_width = (int) (grid_width * 0.8);
        //initial icon
        this.adv_icon = new ImageIcon(this.loadImage(this.adv_drawable, image_width, image_width));
        this.award_icon = new ImageIcon(this.loadImage(this.award_drawable, image_width, image_width));
        this.devil_icon = new ImageIcon(this.loadImage(this.devil_drawable, image_width, image_width));
        //for each initial label
        int total_reward_count = 0;
        for (int i = 0; i < this.maze_label.length; i++) {
            for (int j = 0; j < this.maze_label[0].length; j++) {
                this.maze_label[i][j] = new JLabel("", JLabel.CENTER);
                //border
                this.maze_label[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                //add to panel
                background_panel.add(this.maze_label[i][j]);
                if (this.mode[i][j] > 0) {
                    total_reward_count = total_reward_count + this.mode[i][j];
                }
            }
        }
        //save total reward
        this.total_reward = total_reward_count;

        //add to panel
        super.add(background_panel);
        super.setVisible(true);

        //keyboard listener
        super.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                // System.out.println(e.getKeyChar());
                switch (e.getKeyChar()) {
                    case 'w':
                        Maze.this.moveNorth();
                        break;
                    case 's':
                        Maze.this.moveSouth();
                        break;
                    case 'd':
                        Maze.this.moveEast();
                        break;
                    case 'a':
                        Maze.this.moveWest();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     *
     * @param x
     * @param y
     * @return
     */
    public MazeState start(int x, int y) {
        //start the game
        //save init state
        this.init_x = x;
        this.init_y = y;
        //invoke resume
        return this.resume();
    }

    /**
     * resume the maze game
     *
     * @return
     */
    public MazeState resume() {
        //initial mode
        this.mode = this.copy(this.init_mode);
        //add background
        for (int i = 0; i < this.maze_label.length; i++) {
            for (int j = 0; j < this.maze_label[0].length; j++) {
                //icon
                if (this.mode[i][j] < 0) {
                    this.maze_label[i][j].setIcon(this.devil_icon);
                } else if (this.mode[i][j] > 0) {
                    this.maze_label[i][j].setIcon(this.award_icon);
                }
            }
        }
        //add adv
        this.moveTo(this.init_x, this.init_y, true);
        //initial state
        this.current_award = 0;
        //broadcast event
        MazeState state = new MazeState(MazeState.STATE_START, this.init_x, this.init_y, 0, this.current_award, this.total_reward);
        //broadcast
        this.broadcastState(state);
        //System.out.println(state);
        //return state
        return state;
    }

    /**
     *
     * @return 
     */
    public MazeState moveNorth() {
        //move to next location
        this.moveTo(this.current_x - 1, this.current_y, false);
        return this.judgeState(this.current_x, this.current_y);
    }

    /**
     *
     * @return 
     */
    public MazeState moveSouth() {
        //move to next location
        this.moveTo(this.current_x + 1, this.current_y, false);
        return this.judgeState(this.current_x, this.current_y);
    }

    /**
     *
     * @return 
     */
    public MazeState moveWest() {
        //move to next location
        this.moveTo(this.current_x, this.current_y - 1, false);
        return this.judgeState(this.current_x, this.current_y);
    }

    /**
     *
     * @return 
     */
    public MazeState moveEast() {
        //move to next location
        this.moveTo(this.current_x, this.current_y + 1, false);
        return this.judgeState(this.current_x, this.current_y);
    }

    /**
     * judge if win or not
     */
    private MazeState judgeState(int x, int y) {
        if (this.mode[x][y] < 0) {
            //if walk in devil, failed
            MazeState state = new MazeState(MazeState.STATE_LOSE, x, y, 0, this.current_award, this.total_reward);
            //broadcast event
            this.broadcastState(state);
            //print
            //System.out.println(state);
            return state;
        } else if (this.mode[x][y] > 0) {
            //if walk in devil, get reward
            this.current_award = this.current_award + this.mode[x][y];
            //System.out.println("get reward :" + this.mode[x][y]+", current reward :"+this.current_award+", total rawart:"+this.total_reward);  
            //each reward can get once
            this.mode[x][y] = 0;
            //if get all the award, win
            if (this.current_award == this.total_reward) {
                MazeState state = new MazeState(MazeState.STATE_WIN, x, y, this.init_mode[x][y], this.current_award, this.total_reward);
                //System.out.println(state);
                //broadcast event
                this.broadcastState(state);
                return state;
                //wait for a minute
                //this.resume();
            }
        }
        //if not win yet, continue
        MazeState state = new MazeState(MazeState.STATE_CONTINUE, x, y, this.init_mode[x][y], this.current_award, this.total_reward);
        //System.out.println(state);
        //broadcast event
        this.broadcastState(state);
        return state;
    }

    /**
     *
     * @param x
     * @param y
     */
    private void moveTo(int x, int y, boolean init) {
        //do nothing if illegal
        if (x < 0 || y < 0 || x >= this.maze_label.length || y >= this.maze_label[0].length) {
            return;
        }
        if (init == false) {
            //clear
            this.maze_label[this.current_x][this.current_y].setIcon(null);
            //move to next location
            this.maze_label[x][y].setIcon(this.adv_icon);
            //if devil
            if (this.mode[this.current_x][this.current_y] < 0) {
                this.maze_label[this.current_x][this.current_y].setIcon(this.devil_icon);
            }
        } else {
            this.maze_label[x][y].setIcon(this.adv_icon);
        }
        //update current location
        this.current_x = x;
        this.current_y = y;
    }

    /**
     *
     * @param state
     * @param current_x
     * @param current_y
     * @param current_reward
     */
    private void broadcastState(MazeState state) {
        if (this.maze_listener != null) {
            this.maze_listener.actionChanged(state);
        }
    }

    /**
     *
     * @param lis
     */
    public void setListener(MazeListener lis) {
        this.maze_listener = lis;
    }

    /**
     *
     * @param path
     * @param width
     * @param height
     * @return
     * @throws Exception
     */
    final public Image loadImage(String path, int width, int height) throws Exception {
        BufferedImage image = ImageIO.read(new File(path));
        return image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    /**
     *
     * @param orig
     * @return
     */
    final public int[][] copy(int[][] orig) {
        int[][] dist = new int[orig.length][orig[0].length];
        for (int i = 0; i < dist.length; i++) {
            for (int j = 0; j < dist[0].length; j++) {
                dist[i][j] = orig[i][j];
            }
        }
        return dist;
    }

}
