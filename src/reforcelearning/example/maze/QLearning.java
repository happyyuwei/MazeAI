/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reforcelearning.example.maze;

import java.util.*;
import java.io.*;

/**
 *
 * @author happy
 */
public class QLearning {

    //reward
    final private double lose_reward = -1000;
    final private double win_reward = 2000;
    final private double continue_reward = -1;
    final private double achieve_reward = 500;

    //q table
    final private Map<Integer, double[]> q_table;
    //learning rate
    final private double learning_rate = 0.8;
    //gama
    final private double gama = 0.5;

    //state num and action num
    final private int state_num;
    final private int action_num;
    //row num
    final private int row_num;
    //col num
    final private int col_num;
    //maze game
    final private Maze maze;
    //monitor
    private Monitor monitor;

    //arg for discover rate change
    final private double pow_arg_rate = 1.0 / 8;

    //e- greedy, if e<discover_rate then discover, else use current
    private double discover_rate;

    //current total round
    private int current_round;
    //win round
    private int win_round;

    /**
     *
     * @param row_num
     * @param col_num
     * @param action_num
     * @param maze
     */
    public QLearning(int row_num, int col_num, int action_num, Maze maze) {
        //create table
        this.q_table = new HashMap<>();
        //row and col num
        this.row_num = row_num;
        this.col_num = col_num;
        //init state num and action num
        this.state_num = row_num * col_num;
        this.action_num = action_num;
        //init rate, the initial is 1, means discover anyway, the rate will decrease by 1/sqrt(round)
        this.discover_rate = 1;
        //current round
        this.current_round = 0;
        //win round
        this.win_round=0;
        //save maze game instance
        this.maze = maze;
    }

    /**
     *
     * @param round
     * @param init_row
     * @param init_col
     * @throws java.lang.Exception
     */
    public void train(int round, int init_row, int init_col) throws Exception {
        int cumulate_total_round = round + this.getCurrent_round();
        //loop
        for (int i = 1; i <= round; i++) {
            //update round
            this.current_round = this.getCurrent_round() + 1;
            //according cumulate round to calculate discover rate
            this.updateDiscoverRate();
            //report round
            System.out.println(new java.util.Date() + ", start " + this.getCurrent_round() + "/" + cumulate_total_round + " round, discover rate=" + this.getDiscover_rate());

            //start the game
            this.getMaze().start(init_row, init_col);
            int state_row = init_row;
            int state_col = init_col;
            //maze state
            MazeState feedback;
            Thread.sleep(1000);
            //start training
            do {
                //decide action
                int act = this.action(state_row, state_col);
                //System.out.println(act);
                //get feedback
                feedback = this.doAction(act);
                //get reward
                double reward = this.reward(feedback);
                //System.out.println("reward="+reward);
                //update q table
                double[] action_list = this.getActionList(QLearning.encodeState(state_row, state_col));
                double[] next_action_list = this.getActionList(QLearning.encodeState(feedback.getCurrent_x(), feedback.getCurrent_y()));
                action_list[act] = (1 - this.getLearning_rate()) * action_list[act] + this.getLearning_rate() * (reward + this.getGama() * (this.max(next_action_list)));
                //update state
                state_row = feedback.getCurrent_x();
                state_col = feedback.getCurrent_y();
                //win or lose to show
                FinishUI ui = null;
                if (feedback.getState().equals(MazeState.STATE_WIN)) {
                    ui = new FinishUI(true, 0);
                    this.win_round=this.getWin_round()+1;
                } else if (feedback.getState().equals(MazeState.STATE_LOSE)) {
                    ui = new FinishUI(false, 0);
                }
                Thread.sleep(100);
                if (ui != null) {
                    Thread.sleep(500);
                    ui.dispose();
                }
                //until lose or win
            } while (feedback.getState().equals(MazeState.STATE_CONTINUE) == true);
            //monitor
            if (this.getMonitor() != null) {
                this.getMonitor().epochTrained(this,feedback);
            }
        }
    }

    /**
     * the action list, O is north, 1 is sorth, 2 is west, 3 is east
     *
     * @param state_row
     * @param state_col
     * @return
     */
    public int action(int state_row, int state_col) {
        //if the state is the first time , create the table list
        int state_code = QLearning.encodeState(state_row, state_col);
        //get action list
        double[] action_list = this.getActionList(state_code);
        //calculate legal action
        List<Integer> legal_action = this.getNextStateScale(state_row, state_col, this.getRow_num(), this.getCol_num());
        //create a random to deceide discover or use
        double rand = Math.random();
        if (rand < this.getDiscover_rate()) {
            //discover
            int rand_action = (int) (Math.random() * this.getAction_num());
            //loop until the action is legal
            while (QLearning.contain(legal_action, rand_action) == false) {
                rand_action = (int) (Math.random() * this.getAction_num());
            }
            return rand_action;
        } else {
            //use
            return QLearning.argRandMax(action_list, legal_action);
        }
    }

    /**
     *
     * @param state_code
     * @return
     */
    public double[] getActionList(int state_code) {
        double[] action_list = this.getQ_table().get(state_code);
        //if null put
        if (action_list == null) {
            action_list = new double[this.getAction_num()];
            this.getQ_table().put(state_code, action_list);
        }
        return action_list;
    }

    /**
     *
     * @param state
     * @return
     */
    public double reward(MazeState state) {
        if (state.getState().equals(MazeState.STATE_LOSE)) {
            return this.getLose_reward();
        } else if (state.getState().equals(MazeState.STATE_WIN)) {
            return this.getWin_reward();
        } else if (state.getState().equals(MazeState.STATE_CONTINUE)) {
            if (state.getThis_action_reward() > 0) {
                return this.getAchieve_reward();
            } else {
                return this.getContinue_reward();
            }
        }
        return 0;
    }

    /**
     *
     * @param act
     * @return
     */
    public MazeState doAction(int act) {
        switch (act) {
            case 0:
                return this.getMaze().moveNorth();
            case 1:
                return this.getMaze().moveSouth();
            case 2:
                return this.getMaze().moveWest();
            case 3:
                return this.getMaze().moveEast();
            default:
                return null;
        }
    }

    /**
     *
     * @param state_row
     * @param state_col
     * @param row_num
     * @param col_num
     * @return
     */
    public List<Integer> getNextStateScale(int state_row, int state_col, int row_num, int col_num) {
        //initial action
        List<Integer> action = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            action.add(i);
        }
        //remove illegal action
        if (state_row == 0) {
            action.remove(new Integer(0));
        }
        if (state_row == (row_num - 1)) {
            action.remove(new Integer(1));
        }
        if (state_col == 0) {
            action.remove(new Integer(2));
        }
        if (state_col == (col_num - 1)) {
            action.remove(new Integer(3));
        }
        return action;
    }

    /**
     * update discover rate
     */
    public void updateDiscoverRate() {
        this.discover_rate = 1.0 / Math.pow(this.getCurrent_round(), this.getPow_arg_rate());
    }

    /**
     * find the max location from scale index, if more than two locations are
     * the same, return a random location
     *
     * @param action
     * @param scale
     * @return
     */
    public static int argRandMax(double[] action, List<Integer> scale) {
        double max = Integer.MIN_VALUE;
        List<Integer> index = new ArrayList<>();
        for (int i = 0; i < action.length; i++) {
            //if i not in the scale, do nothing
            if (QLearning.contain(scale, i) == false) {
                continue;
            }
            //judge
            if (action[i] > max) {
                index.clear();
                index.add(i);
                max = action[i];
            } else if (action[i] == max) {
                index.add(i);
            }
        }
        //return a random location
        return index.get((int) (index.size() * Math.random()));
    }

    /**
     *
     * @param action
     * @return
     */
    public double max(double[] action) {
        double max = Integer.MIN_VALUE;
        for (int i = 0; i < action.length; i++) {
            if (action[i] > max) {
                max = action[i];
            }
        }
        return max;
    }

    /**
     *
     * @param monitor
     */
    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    /**
     *
     * @param path
     */
    public void save(String path) {
        try {
            PrintStream out = new PrintStream(path);
            //save round
            out.println(this.current_round);
            //save win round
            out.println(this.getWin_round());
            //save q table
            for (Map.Entry<Integer, double[]> e : this.q_table.entrySet()) {
                out.print(e.getKey());
                double[] action = e.getValue();
                for (int i = 0; i < action.length; i++) {
                    out.print(",");
                    out.print(action[i]);
                }
                out.println();
            }
            out.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    /**
     *
     * @param path
     */
    public void load(String path) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            //clear map
            this.q_table.clear();
            String line;
            //parse round
            this.current_round = Integer.parseInt(in.readLine());
            //parse win round
            this.win_round=Integer.parseInt(in.readLine());
            //parse q table
            while ((line = in.readLine()) != null) {
                String[] value = line.split(",");
                double[] action = new double[value.length - 1];
                for (int i = 0; i < action.length; i++) {
                    action[i] = Double.parseDouble(value[i + 1]);
                }
                this.q_table.put(Integer.parseInt(value[0]), action);
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    //static method.............................................................................................................
    /**
     *
     * @param array
     * @param num
     * @return
     */
    public static boolean contain(List<Integer> array, int num) {
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i) == num) {
                return true;
            }
        }
        return false;
    }

    /**
     * encode the row column state to a int, the high 2 bytes are state row, the
     * low bytes are state col
     *
     * @param state_row
     * @param state_col
     * @return
     */
    public static int encodeState(int state_row, int state_col) {
        return (state_row << 16) + state_col;
    }

    /**
     *
     * @param state
     * @return
     */
    public static int[] decodeState(int state) {
        return new int[]{state >> 16, state & 0x0000FFFF};
    }

    //getter........................................................................................................
    /**
     * @return the lose_reward
     */
    public double getLose_reward() {
        return lose_reward;
    }

    /**
     * @return the win_reward
     */
    public double getWin_reward() {
        return win_reward;
    }

    /**
     * @return the continue_reward
     */
    public double getContinue_reward() {
        return continue_reward;
    }

    /**
     * @return the achieve_reward
     */
    public double getAchieve_reward() {
        return achieve_reward;
    }

    /**
     * @return the q_table
     */
    public Map<Integer, double[]> getQ_table() {
        return q_table;
    }

    /**
     * @return the learning_rate
     */
    public double getLearning_rate() {
        return learning_rate;
    }

    /**
     * @return the gama
     */
    public double getGama() {
        return gama;
    }

    /**
     * @return the state_num
     */
    public int getState_num() {
        return state_num;
    }

    /**
     * @return the action_num
     */
    public int getAction_num() {
        return action_num;
    }

    /**
     * @return the row_num
     */
    public int getRow_num() {
        return row_num;
    }

    /**
     * @return the col_num
     */
    public int getCol_num() {
        return col_num;
    }

    /**
     * @return the maze
     */
    public Maze getMaze() {
        return maze;
    }

    /**
     * @return the monitor
     */
    public Monitor getMonitor() {
        return monitor;
    }

    /**
     * @return the pow_arg_rate
     */
    public double getPow_arg_rate() {
        return pow_arg_rate;
    }

    /**
     * @return the discover_rate
     */
    public double getDiscover_rate() {
        return discover_rate;
    }

    /**
     * @return the current_round
     */
    public int getCurrent_round() {
        return current_round;
    }

    /**
     * @return the win_round
     */
    public int getWin_round() {
        return win_round;
    }

}
