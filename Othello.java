/* Auva Amirmokri and Arden Matikyan
 * Othello
 * This program is an othello game between two players
 * Use matrix of picpanels 
 */
import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;

import java.util.*;

public class Othello extends JFrame implements ActionListener {

	private PicPanel[][] allPanels;

	private JButton skipButton;

	public final int[] HORZDISP = { 1, 1, 1, 0, -1, -1, -1, 0 };
	public final int[] VERTDISP = { -1, 0, 1, 1, 1, 0, -1, -1 };
	private BufferedImage whitePiece;
	private BufferedImage blackPiece;


	private JLabel blackCountLabel;
	private int blackCount = 2;

	private JLabel whiteCountLabel;
	private int whiteCount = 2;

	private JLabel turnLabel;
	private boolean blackTurn = true;

	private Color curColor;

	private ArrayList<PicPanel>toFlip; 

	public Othello() {

		setSize(1200, 950);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setTitle("Othello");
		getContentPane().setBackground(Color.white);

		curColor = Color.black;
		allPanels = new PicPanel[8][8];

		JPanel gridPanel = new JPanel();
		gridPanel.setLayout(new GridLayout(8, 8, 2, 2));
		gridPanel.setBackground(Color.black);
		gridPanel.setBounds(95, 50, 800, 814);
		gridPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				allPanels[row][col] = new PicPanel(row, col);
				gridPanel.add(allPanels[row][col]);
			}
		}

		try {
			whitePiece = ImageIO.read(new File("white.jpg"));
			blackPiece = ImageIO.read(new File("black.jpg"));

		} catch (IOException ioe) {
			JOptionPane.showMessageDialog(null, "Could not read in the pic");
			System.exit(0);
		}

		ArrayList<PicPanel>toFlip = new ArrayList<PicPanel>(); 

		//creating buttons/labels
		skipButton = new JButton("Skip Turn");
		skipButton.addActionListener(this);
		skipButton.setBounds(925, 475, 150, 50);

		blackCountLabel = new JLabel("Black: 2 ");
		blackCountLabel.setFont(new Font("Calibri", Font.PLAIN, 35));
		blackCountLabel.setBounds(925, 150, 275, 50);

		whiteCountLabel = new JLabel("White: 2 ");
		whiteCountLabel.setFont(new Font("Calibri", Font.PLAIN, 35));
		whiteCountLabel.setBounds(925, 225, 275, 50);

		turnLabel = new JLabel("Turn: Black ");
		turnLabel.setFont(new Font("Calibri", Font.PLAIN, 35));
		turnLabel.setBounds(925, 375, 275, 75);

		add(gridPanel);
		add(skipButton);
		add(blackCountLabel);
		add(whiteCountLabel);
		add(turnLabel);

		// add 4 starting pieces 
		allPanels[3][3].myColor = Color.white;
		allPanels[4][3].myColor = Color.black;
		allPanels[4][4].myColor = Color.white;
		allPanels[3][4].myColor = Color.black;

		setVisible(true);
	}

	// update labels for player turn and number of pieces
	private void updateLabels() {
		whiteCountLabel.setText("White: " + whiteCount);
		blackCountLabel.setText("Black: " + blackCount);

		String turn = "Black";

		//change turns
		if (!blackTurn) {
			turn = "White";
			curColor = Color.white;
		}
		else {
			curColor = Color.black;
		}

		turnLabel.setText("Turn: " + turn);

	}


	public void actionPerformed(ActionEvent ae) {
		String turn = "Black";
		if (blackTurn) {
			turn = "White";
			blackTurn = false;
		} else {
			blackTurn = true;
		}

		turnLabel.setText("Turn: " + turn);

	}

	class PicPanel extends JPanel implements MouseListener {

		private int row;
		private int col;

		private Color myColor;

		public PicPanel(int r, int c) {
			row = r;
			col = c;

			this.addMouseListener(this);

		}

		// flips the color of the piece (if found)
		public void flip() {
			if (myColor == null)
				return;
			if (myColor == Color.black)
				myColor = Color.white;
			else
				myColor = Color.black;

			repaint();
		}

		// this will draw the image (piece or green background). You will never call
		// this method
		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			if (myColor == null) {
				setBackground(new Color(0, 108, 89));
			} else if (myColor == Color.white) {
				g.drawImage(whitePiece, 0, 0, this);
			} else {
				g.drawImage(blackPiece, 0, 0, this);
			}

		}

		// add code here to react to the user clicking on the panel
		public void mouseClicked(MouseEvent me) {

			// can't pick pre-existing sopt
			if (allPanels[row][col].myColor != null) {
				return;
			}


			if(validMove()) {

				//sets piece color to corresponding player's color
				if(blackTurn) {
					allPanels[row][col].myColor = Color.black;
					blackCount++; 
				}
				else {
					allPanels[row][col].myColor = Color.white;
					whiteCount++; 
				}
				allPanels[row][col].repaint();	

				blackTurn = !blackTurn;
				updateLabels();

			}

		}

		// method to check if the spot selected has an adjacent piece with the opposite color
		public boolean validMove() {

			boolean pathGood;  
			int neighbors = 0;
			ArrayList<PicPanel> toFlip = new ArrayList<PicPanel>();


			Color colorToFind = Color.black;
			if (blackTurn)
				colorToFind = Color.white;

			//loops through all 8 directions 
			for (int offset = 0; offset < VERTDISP.length; offset++) {

				int rowCheck = row + VERTDISP[offset];
				int colCheck = col + HORZDISP[offset];


				// if neighboring location is inbounds
				if (rowCheck < allPanels.length && rowCheck >= 0) {
					if (colCheck < allPanels[0].length && colCheck >= 0) {

						//if the space is not null and contains the color to find
						if (!(allPanels[rowCheck][colCheck].myColor == null)) {
							if (allPanels[rowCheck][colCheck].myColor.equals(colorToFind)) {

								neighbors++; 

								ArrayList<PicPanel>lineToAdd = new ArrayList<PicPanel>();
								lineToAdd = scanLine(VERTDISP[offset], HORZDISP[offset]); 

								//adds pieces of opposite color to flip
								for(int i = 0; i < lineToAdd.size(); i ++) {
									toFlip.add(lineToAdd.get(i));
								}	
							}

						}

					}
				}
			}

			//prints corresponding error message
			if(neighbors == 0) {
				JOptionPane.showMessageDialog(null, "Must have opposite color piece adjacent");
				return false; 
			}else if(toFlip.isEmpty()) {
				JOptionPane.showMessageDialog(null, "No available pathways for selected spot");
				return false; 
			}

			//flips pieces
			for(int i = 0 ; i < toFlip.size(); i++) {
				toFlip.get(i).flip();
			}

			int size = toFlip.size();

			//accounts for number of each piece color
			if(blackTurn) {
				blackCount+=size;
				whiteCount-=size;
			}

			else { 
				whiteCount+=size;
				blackCount-=size;
			}

			return true; 
		}


		//checks potential pathways
		private ArrayList<PicPanel> scanLine (int rc, int cc){  

			int mult = 1; 
			ArrayList<PicPanel>line = new ArrayList<PicPanel>(); 

			int nextR; 
			int nextC; 

			//loops to check each path
			do {

				//gets next values to check 
				nextR = rc*mult + row;
				nextC = cc*mult + col;

				//if there is no piece at the spot, return empty arraylist
				if(allPanels[nextR][nextC].myColor == null) {
					return new ArrayList<>();
				} 

				//end of line 
				else if(allPanels[nextR][nextC].myColor == curColor) {
					return line;
				}

				line.add(allPanels[nextR][nextC]); 

				mult++; 
			}while(isInBounds(nextR, nextC)); 

			return new ArrayList<>(); 

		}

		// helper to check if the row/col values are in bounds
		private boolean isInBounds(int r, int c) {
			if(r < allPanels.length-1 && c < allPanels[0].length-1) {
				if( r > 0 && c > 0)
					return true;
			}

			return false; 


		}
		public void mouseEntered(MouseEvent arg0) {
			// DO NOT IMPLEMENT

		}

		public void mouseExited(MouseEvent arg0) {
			// DO NOT IMPLEMENT

		}

		public void mousePressed(MouseEvent arg0) {
			// DO NOT IMPLEMENT

		}

		public void mouseReleased(MouseEvent arg0) {
			// DO NOT IMPLEMENT

		}
	}

	public static void main(String[] args) {
		new Othello();
	}
}
