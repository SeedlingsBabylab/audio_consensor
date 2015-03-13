import java.io.*;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/*
This code is adapted from Munna's FileOrganizer project
 */
public class BrowsePanel extends JPanel implements ActionListener {
	static private final String newline = "\n";
	JButton openButton1, openButton2, openButton3, openButton4,openButton5,openButton6,saveButton;
	JButton openButton7, openButton8, openButton9; 
	JTextArea log;
	JFileChooser fc;
	private String fileName;
	ArrayList<File> upFiles = new ArrayList<File>();
	ArrayList<File> downFiles = new ArrayList<File>();
	ArrayList<File> mainFiles = new ArrayList<File>();

	private File file1, file2, file3, file4, file5, file6, file7, file8, file9;
	private int countUp, countDown, countMain;

	public BrowsePanel() {
		super(new BorderLayout());

		//Create the log first, because the action listeners
		//need to refer to it.
		log = new JTextArea(2,30);
		log.setMargin(new Insets(5,5,5,5));
		log.setEditable(false);
		JScrollPane logScrollPane = new JScrollPane(log);

		//Create a file chooser
		fc = new JFileChooser();

		//Uncomment one of the following lines to try a different
		//file selection mode.  The first allows just directories
		//to be selected (and, at least in the Java look and feel,
		//shown).  The second allows both files and directories
		//to be selected.  If you leave these lines commented out,
		//then the default mode (FILES_ONLY) will be used.
		//
		//fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		//fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		//Create the open button.  We use the image from the JLF
		//Graphics Repository (but we extracted it from the jar).



		openButton1 = new JButton("Coder 1");
		openButton2 = new JButton("Coder 2");
		
		openButton3 = new JButton("Completed Consensus");

		openButton1.addActionListener(this);
		openButton2.addActionListener(this);
		openButton3.addActionListener(this);


		//Create the save button.  We use the image from the JLF
		//Graphics Repository (but we extracted it from the jar).
		saveButton = new JButton("Save a File...");
		saveButton.addActionListener(this);

		//For layout purposes, put the buttons in a separate panel
		setLayout(new GridLayout(4,4));
		JPanel buttonPanel1 = new JPanel(); //use FlowLayout
		buttonPanel1.add(openButton1);
		buttonPanel1.add(openButton2);
		JPanel buttonPanel2 = new JPanel(); //use FlowLayout
buttonPanel2.add(openButton3);
		//Add the buttons and the log to this panel.
		add(buttonPanel1, BorderLayout.PAGE_START);
		add(buttonPanel2, BorderLayout.PAGE_START);

		add(logScrollPane, BorderLayout.PAGE_END);
	}

	public void actionPerformed(ActionEvent e) {
		//Handle open button action for up arrows
		if (e.getSource() == openButton1 | e.getSource() == openButton2) {

			int returnVal = fc.showOpenDialog(BrowsePanel.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				upFiles.add(fc.getSelectedFile());
				log.append("Opening: " + upFiles.get(countUp).getName() + "." + newline);
				//fileName = upFiles[i].getName();
				countUp++;
			} else {
				log.append("Open command cancelled by user." + newline);
			}
			log.setCaretPosition(log.getDocument().getLength());
			//Handle save button action.
		}
		
		if (e.getSource() == openButton3) {

			int returnVal2 = fc.showOpenDialog(BrowsePanel.this);
			if (returnVal2 == JFileChooser.APPROVE_OPTION) {
				downFiles.add(fc.getSelectedFile());
				//This is where a real application would open the file.
				log.append("Opening: " + downFiles.get(countDown).getName() + "." + newline);
				//fileName = upFiles[i].getName();
				countDown++;
			} else {
				log.append("Open command cancelled by user." + newline);
			}
			log.setCaretPosition(log.getDocument().getLength());
			//Handle save button action.
		}
		
		if (e.getSource() == openButton7 | e.getSource() == openButton8 | e.getSource() == openButton9) {

			int returnVal3 = fc.showOpenDialog(BrowsePanel.this);
			if (returnVal3 == JFileChooser.APPROVE_OPTION) {
				mainFiles.add(fc.getSelectedFile());
				//This is where a real application would open the file.
				log.append("Opening: " + mainFiles.get(countMain).getName() + "." + newline);
				//fileName = upFiles[i].getName();
				countMain++;
			} else {
				log.append("Open command cancelled by user." + newline);
			}
			log.setCaretPosition(log.getDocument().getLength());
			//Handle save button action.
		}
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = BrowsePanel.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event dispatch thread.
	 */
	static void createAndShowGUI() {
		//Create and set up the window.
		JFrame frame = new JFrame("FileChooserDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Add content to the window.
		frame.add(new BrowsePanel());

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}
	public ArrayList<File> getUpFiles(){
		return upFiles;
	}
	public ArrayList<File> getDownFiles(){
		return downFiles;
	}
	public ArrayList<File> getMainFiles(){
		return mainFiles;
	}

	public int getCountUp(){
		return countUp;
	}
	public int getCountDown(){
		return countDown;
	}
	public int getCountMain(){
		return countMain;
	}
	}
