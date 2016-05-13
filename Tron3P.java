import uk.ac.warwick.dcs.maze.logic.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import static java.awt.GraphicsDevice.WindowTranslucency.*;
import java.io.File;

public class Tron{

	private MazeEvent event;
	private Overlay overlay;
	private int pollRun = 0;
	private Player player1;
	private Player player2;
	private IRobot robot;
	private int play; // 0-play, 1-not play
	private Maze maze;
	private int heading;

	public Tron(){
		event = new MazeEvent();
		player1 = new Player(Color.RED,10,10,1,0,"Player 1");
		player2 = new Player(Color.ORANGE,40,40,-1,0,"Player 2");
		overlay = new Overlay(); //Creates the initial blank overlay to resize screen
		JOptionPane.showMessageDialog(new JFrame(),"Welcome to Tron.\nPlayer 1 is red and is controlled by WASD. \nPlayer 2 is orange and is controlled by the arrow keys.\nPress Begin to start!","Welcome",JOptionPane.PLAIN_MESSAGE);
	}
	
	public void controlRobot(IRobot robot) {
		
		if((robot.getRuns() == 0) && (pollRun ==0)){
			this.robot = robot;
			event.setDelay();
			overlay.close();
			overlay = new Overlay(player1, player2);
			try{
				maze = new Maze(new File("./TronMaze.warwickmaze"), robot);
				event.loadMaze(maze);
				//loads TronMaze
			}catch(Exception e){
				System.err.println("ERROR: Cannot find maze. Place TronMaze in same directory as controller.");
			}
			try{
				//sleeps for 1sec
				Thread.sleep(1000);
			}catch(InterruptedException e){}
			
		}
	
		if(robot.getMaze().getCellType(player1.getX(), player1.getY()) == 2){
			//Player2 wins
			player2.incrementScore();
			overlay.notOnTop();
			//Shows winning screen on top of overlay
			play = JOptionPane.showConfirmDialog(new JFrame(),"Player 2 wins! \nCurrent Score: \nPlayer 1: " + player1.getScore() + "\t Player 2: " + player2.getScore () + "\nWould you like to play again?","",JOptionPane.YES_NO_OPTION);
			overlay.onTop();
			resetMaze();
			if(play == 0){
				try{
					//.5 sec pause
					Thread.sleep(500);
				}catch(InterruptedException e){}
			}
		}
		if(robot.getMaze().getCellType(player2.getX(), player2.getY()) == 2){
			//Player1 wins
			player1.incrementScore();
			overlay.notOnTop();
			//Shows winning screen on top of overlay
			play = JOptionPane.showConfirmDialog(new JFrame(),"Player 1 wins! \nCurrent Score: \nPlayer 1: " + player1.getScore() + "\t Player 2: " + player2.getScore () + "\nWould you like to play again?","",JOptionPane.YES_NO_OPTION);
			overlay.onTop();
			resetMaze();
			if(play == 0){
				try{
					//.5 sec pause
					Thread.sleep(500);
				}catch(InterruptedException e){}
			}
		}
		
		if(play == 0){
			
			// 1:North, 2:East, 3:South, 4:West
			
			switch(overlay.getP1()){ //Controls Player1
				case 1: 
					if(player1.getPlayerHeading().getY() != 1)
						player1.setPlayerHeading(0,-1);
					break;
				case 2: 
					if(player1.getPlayerHeading().getX() != -1)
						player1.setPlayerHeading(1,0);
					break;
				case 3: 
					if(player1.getPlayerHeading().getY() != -1)
						player1.setPlayerHeading(0,1);
					break;
				case 4: 
					if(player1.getPlayerHeading().getX() != 1)
						player1.setPlayerHeading(-1,0);
			}

			switch(overlay.getP2()){ //Controls Player2
				case 1: 
					if(player2.getPlayerHeading().getY() != 1)
						player2.setPlayerHeading(0,-1);
					break;
				case 2: 
					if(player2.getPlayerHeading().getX() != -1)
						player2.setPlayerHeading(1,0);
					break;
				case 3: 
					if(player2.getPlayerHeading().getY() != -1)
						player2.setPlayerHeading(0,1);
					break;
				case 4: 
					if(player2.getPlayerHeading().getX() != 1)
						player2.setPlayerHeading(-1,0);
			}
			
			//Advances players and sets previous square to a wall
			robot.getMaze().setCellType((int) player1.getPlayerLocation().getX(), (int) player1.getPlayerLocation().getY(), 2);
			robot.getMaze().setCellType((int) player2.getPlayerLocation().getX(), (int) player2.getPlayerLocation().getY(), 2);
			player1.advancePlayer();
			player2.advancePlayer();
			
			overlay.movePlayerIcons(); //Updates overlay
		
			event.regenMaze(); //Makes maze load the walls (not really needed any more)
		
		}
		if(play == 1){
			player1.resetScore();
			player2.resetScore();
		}
		pollRun++;
	}
	
	public void removeWalls(){
		//Loops through all squares and sets them to path
		for(int i=1; i<= robot.getMaze().getWidth()-2; i++){
			for(int j=1; j<=robot.getMaze().getHeight()-2; j++){
				robot.getMaze().setCellType(i,j,1);
			}
		}
		event.regenMaze();
	}
	
	public void reset(){
		player1.resetScore();
		player2.resetScore();
		play = 0; //Makes game ready to play
		resetMaze(); 
	}
	
	public void resetMaze(){
		overlay.close(); 
		player1.resetPlayer();
		player2.resetPlayer();
		player1.setPlayerHeading(1,0);
		player2.setPlayerHeading(-1,0);
		overlay = new Overlay(player1, player2);
		removeWalls();
	}
	
	//PLAYERS
	public class Player{
		private Color colour;
		private Point start;
		private Point location;
		private Point heading;
		private String name;
		private int score;
		
		public Player(Color colour, int x, int y, int dx, int dy, String name){
			this.colour = colour;
			start = new Point(x,y);
			location = new Point(x,y);
			heading = new Point(dx, dy);
			this.name = name;
			score = 0;
		}
		//Sets heading
		public void setPlayerHeading(int dx, int dy){
			heading.setLocation(dx,dy);
		}
		//Returns heading 
		public Point getPlayerHeading(){
			return heading;
		}
		//Moves player forward one
		public void advancePlayer(){
			location.translate((int) heading.getX(), (int) heading.getY());
		}
		//Returns current location
		public Point getPlayerLocation(){
			return location;
		}
		//Returns current X coord
		public int getX(){
			return (int) location.getX();
		}//Returns current Y coord
		public int getY(){
			return (int) location.getY();
		}
		//Gets colour
		public Color getColour(){
			return colour;
		}
		//Sets current location to start location
		public void resetPlayer(){
			location.setLocation(start);
		}
		//Increases score by 1
		public void incrementScore(){
			score++;
		}
		//Sets score to 0
		public void resetScore(){
			score = 0;
		}
		//Returns current score
		public int getScore(){
			return score;
		}
		
	}
	
	
	//EVENT STUFF
	public class MazeEvent implements IEventClient{
	
		public MazeEvent(){
			EventBus.addClient(this);
		}
		//Cause maze elements to update
		public void regenMaze(){
			EventBus.broadcast(new uk.ac.warwick.dcs.maze.logic.Event(102, new Point(1, 1)));
		}
		//Meant to call reset, does work correctly
		public void resetMaze(){
			EventBus.broadcast(new uk.ac.warwick.dcs.maze.logic.Event(103, 0.25));
		}
		//Sets the delay between moves
		public void setDelay(){
			EventBus.broadcast(new uk.ac.warwick.dcs.maze.logic.Event(113, 50));
		}
		//Loads a maze
		public void loadMaze(Maze maze){
			EventBus.broadcast(new uk.ac.warwick.dcs.maze.logic.Event(107, maze));
		}
		
		public void notify(IEvent event){}
		
	}
	
	//GUI STUFF
	public class Overlay extends JFrame{
		private Frame[] frames;
		private Frame frame = new Frame();
		private int player1;
		private int player2;
		
		private JPanel panel;
		
		//Creates transparent window
		public Overlay(){
			super("Transparent Window");
			setUndecorated(true);
			frames=frame.getFrames(); //Finds the current frames, frame 0 should be maze
			setLayout(new GridBagLayout());
			frames[0].setSize(980,820); //Sets size of maze
			setSize(728,728);
			setLocationRelativeTo(frames[0]);
			setBackground(new Color(0,0,0,0));
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setVisible(true);
			setAlwaysOnTop(false);
			setFocusable(false);
		}
		
		//Creates transparent window with player icons and keylistener
		public Overlay(Player p1, Player p2){
			super("Transparent Window");
			frames=frame.getFrames(); //Finds the current frames, frame 0 should be maze
			setUndecorated(true);
			setLayout(new GridBagLayout());
			frames[0].setSize(980,820); //Sets size of maze
			setSize(728,728);
			setLocationRelativeTo(frames[0]);
			setBackground(new Color(0,0,0,0));
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			//Adding player icons
			panel = new MyPanel(p1, p2);
			setContentPane(panel);
			//Adding KeyListeners
			KeyboardFocusManager manager1 = KeyboardFocusManager.getCurrentKeyboardFocusManager();
			KeyboardFocusManager manager2 = KeyboardFocusManager.getCurrentKeyboardFocusManager();
			manager1.addKeyEventDispatcher(new Player1Controls());
			manager2.addKeyEventDispatcher(new Player2Controls());
		
			setVisible(true);
			setAlwaysOnTop(false);
			setFocusable(false);
 
		}
		//Sets AlwaysOnTop to false, use for pop-up windows
		public void notOnTop(){
			setAlwaysOnTop(false);
		}
		//Sets AlwaysOnTop to true, use after pop-up windows
		public void onTop(){
			setAlwaysOnTop(true);
		}
		//Trigs the player icons to update 
		public void movePlayerIcons(){
			panel.repaint();
		}
		
		public class MyPanel extends JPanel{
			
			private Player player1;
			private Player player2;
			
			MyPanel(Player player1, Player player2){
				super();
				this.player1 = player1;
				this.player2 = player2;
			}
		
			@Override
			public void paint(Graphics g) {
				//Paints the wall
				g.setColor(Color.CYAN);
				g.fillRect(0,0,714,14);
				g.fillRect(0,714,714,14);
				g.fillRect(0,0,14,714);
				g.fillRect(714,0,14,728);

				//Paints player1
				g.setColor(player1.getColour());
				g.fillRect(14 + (int) player1.getX()*14 , 14+ (int) player1.getY()* 14 ,14,14);
				//Paints player2
				g.setColor(player2.getColour());
				g.fillRect(14 + (int) player2.getX()*14 , 14+ (int) player2.getY()* 14 ,14,14);
			}
		}
		
		//Controls player1
		private class Player1Controls implements KeyEventDispatcher{
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_W) {
					setP1(1);
				}
				if (e.getKeyCode() == KeyEvent.VK_A) {
					setP1(4);
				}
				if (e.getKeyCode() == KeyEvent.VK_S) {
					setP1(3);
				}
				if (e.getKeyCode() == KeyEvent.VK_D) {
					setP1(2);
				}
				return false;
			}
		}
		
		//Controls player2
		private class Player2Controls implements KeyEventDispatcher{
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					setP2(1);
				}
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					setP2(4);
				}
				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					setP2(3);
				}
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					setP2(2);
				}
				return false;
			}
		}
		
		//Returns player1's input
		public int getP1(){
			return player1;
		}
		//Sets player1's input
		public void setP1(int direction){
			this.player1 = direction;
		}
		
		//Returns player2's input
		public int getP2(){
			return player2;
		}
		//Returns player2's input
		public void setP2(int direction){
			this.player2 = direction;
		}
		//Gets rid of overlay
		public void close(){
			dispose();
		}
	}
	
}
