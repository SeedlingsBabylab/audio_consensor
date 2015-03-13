import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import difflib.DeleteDelta;


public class L2B{

	private StringBuilder sb = new StringBuilder();
	StringBuilder l2b(List<?> l){ //this method creates strings to be written into .txt
		sb.delete(0, sb.length());
		for (Object object : l) {
			sb.append(object+"\n");
		}
		if(sb.length()>=1)sb.deleteCharAt(sb.length()-1); // last "\n"
		return sb;
	}
	static String directory;
//	static String OSV;//osversion
	private static void comparableTest() throws IOException{    
		BrowsePanel browsePanel = new BrowsePanel();
		File file1, file2, file3;
		int result = JOptionPane.showConfirmDialog(null, browsePanel,
				"Upload Files", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);//launches browsePanel
		if (result == JOptionPane.OK_OPTION){
			if (browsePanel.getUpFiles().size()==2){//checks if we are creating a check me file
				file1 = browsePanel.getUpFiles().get(0);
				file2 = browsePanel.getUpFiles().get(1);
				checkMe(file1,file2);
			}
			else if(browsePanel.getDownFiles().size()==1){//checks whether the consensus version is completed
				file3 = browsePanel.getDownFiles().get(0);
				decider(file3);
			}
		}
	}
	private static void checkMe(File file1,File file2) throws IOException{//takes in two files and spits out a version outlining differences
		List<String> original = new LinkedList<String>();
		List<String> revised = new LinkedList<String>();
		BufferedReader br1 = new BufferedReader(new FileReader(file1.getAbsolutePath()));
		BufferedReader br2 = new BufferedReader(new FileReader(file2.getAbsolutePath()));
		directory = file1.getParent();
		directory = directory.replace("\\","/");
		directory=directory+"/";
		try {
			String lineaud = br1.readLine();
			while (lineaud != null) {//this line excludes lines where there is no tab space (if we want comments not outlined as differences, we use space after %com:) 
				if (lineaud.contains("	")){
					original.add(lineaud);
				}
				lineaud = br1.readLine();
			}
		}finally {
			br1.close();
		}

		try {//read coder 2's file
			String lineaud = br2.readLine();
			while (lineaud != null) {
				if (lineaud.contains("	")){
					revised.add(lineaud);
				}
				lineaud = br2.readLine();
			}
		}finally {
			br2.close();
		}
		//these colors are for the html file outlining differences
		String[] cors = new String[]{"palegreen","khaki","pink","moccasin","lightskyblue","lightyellow","coral","aliceblue","yellowgreen","beige","lightpink"};
		StringBuilder tl = new StringBuilder();
		StringBuilder tr = new StringBuilder();
		StringBuilder tf = new StringBuilder(); //original template (without html included in tl and tr)
		Patch patch = DiffUtils.diff(original, revised); //this creates all the differences
		List<Delta> deltas = patch.getDeltas();
		L2B l2B = new L2B();
		int cori = 0;
		int last = 0;
		int differenceCounter=1;
		for (Delta delta : deltas) {//for each difference
			if(last + 1 < delta.getOriginal().getPosition()){ // not continuous, if the line isn't different
				tl.append("<pre style='font-size:smaller;'>\n");//left side of html
				tr.append("<pre style='font-size:smaller;'>\n");//right side of html
				for(int i = last + 1; i < delta.getOriginal().getPosition(); i++){//print all lines that are not different
					tl.append(original.get(i)+"\n");//adds the original text before differences
					tr.append(original.get(i)+"\n");
					tf.append(original.get(i)+"\n");


				}
				tl.append("</pre>\n");
				tr.append("</pre>\n");
			}
			List<?> or = delta.getOriginal().getLines();
			//now we print differences, color changes each difference
			tl.append("%com: Decision: "+differenceCounter+"<pre style='background-color:"+cors[cori]+";'>\n"+l2B.l2b(or)+"\n</pre>");
			List<?> re = delta.getRevised().getLines();
			//outline differences on the final consensus_me.cex file
			tf.append("---\n");
			if (l2B.l2b(or).length()>0 & l2B.l2b(re).length()==0){
				tf.append("%com: Decision: [" + l2B.l2b(or)+ "]\n");
			}else if(l2B.l2b(re).length()>0 & l2B.l2b(or).length()==0){
				tf.append("%com: Decision: [" + l2B.l2b(or)+ "]\n");
			}else{
				tf.append("%com: Decision: []\n");
			}
			tf.append("%com: Choice:" + 1+ " \n");
			tf.append(l2B.l2b(or)+"\n");
			tf.append("%com: Choice:" + 2+ " \n");
			tf.append(l2B.l2b(re)+"\n");
			tf.append("%com: Difference " + differenceCounter + " end \n");
			tf.append("---\n");
			tr.append("Difference: "+differenceCounter+"<pre style='background-color:"+cors[cori]+";'>\n"+l2B.l2b(re)+"\n</pre>");
			if (cori<cors.length-1){       
				cori = cori+1;
			}else{
				cori=0;
			}
			last = delta.getOriginal().last();
			differenceCounter++;
		}
		if(last + 1 < original.size()){ //last is not delta
			tl.append("<pre style='font-size:smaller;'>\n");
			tr.append("<pre style='font-size:smaller;'>\n");
			for(int i = last + 1; i < original.size(); i++){
				tl.append(original.get(i)+"\n");
				tr.append(original.get(i)+"\n");
				tf.append(original.get(i)+"\n");
			}
			tl.append("</pre>\n");
			tr.append("</pre>\n");
		}
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(directory +"consensus_me.cex", true)))) {
			out.println(tf.toString());
			out.println("\n");
		}catch (IOException e) {
			//exception handling left as an exercise for the reader
		}
		FileWriter fWriter = null;
		BufferedWriter writer = null;
		try {
			fWriter = new FileWriter(directory + "differences.html");
			writer = new BufferedWriter(fWriter);
			writer.write("<html><table><tr><td style='vertical-align:top;'>"+tl.toString()+"</td><td style='vertical-align:top;'>"+tr.toString()+"</td></tr></table></html>");
			writer.close(); //make sure you close the writer object 
		} catch (Exception e) {
			//catch any exceptions here
		}
	}
	private static void decider(File inputFile) throws IOException{  //this method finalizes the decisions
		StringBuilder fConsensus = new StringBuilder(); 
		directory = inputFile.getParent();
		directory = directory.replace("\\","/");
		directory=directory+"/";			String[] ss, ss2;
		String[] choice1 = null,choice2 = null;
		int diffCount=1;
		int count;
		List<Integer> choices=new ArrayList<Integer>();
		//	List<Integer> choices=new ArrayList<Integer>();
		String choicetext = "test";
		BufferedReader br = new BufferedReader(new FileReader(inputFile.getAbsolutePath()));
		try{
			String line = br.readLine();
			while (line != null) {
				if (line.contains("---")){
					choicetext = "";
					fConsensus.append("\n");
					line = br.readLine();
					while(!line.contains("---")){//detect difference
						if (line.contains("[")){ //start
							ss= line.split("\\[");
							ss2= ss[1].split("\\]");
							if(ss2[0].matches("1")|ss2[0].matches("2")){//decision
								choices.add(Integer.parseInt(ss2[0])); //choices 1 or 2
							}else{ //if new text is written
								choices.add(3);//manual entry
								count=1;
								if (line.contains("]")){
									choicetext=ss2[0];
								}else{
									while(!line.contains("]")){//in case the manually entry carries over to a new line
										if(count==1){
											choicetext=ss2[0];
										}else{
											choicetext=choicetext+"\n"+line;
										}
										count++;
										line=br.readLine();
									}
									if(line.contains("]")){//finally the end of a manual entry
										choicetext=choicetext+"\n"+line;
										choicetext = choicetext.replace("]", "");
										line=br.readLine();
									}
								}
								while(!line.contains("%com: Difference")){//read until end of the difference
									line=br.readLine();
								}
								fConsensus.append(choicetext);
								fConsensus.append("\n");
							}
							//								line=br.readLine();
						}

						if (choices.get(choices.size()-1)==1 & line.contains("[1]")){//choice 1
							count=1;
							while(!line.contains("%com: Choice:1")){
								line=br.readLine();
							}
								line = br.readLine();
								while(!line.contains("%com: Choice:2")){//read until this line
									if(count!=1){//if the difference is not one line
										choicetext=choicetext+"\n"+line;//print each line on a separate line in the output
									}else{
										choicetext=line;//a one line difference
									}
									line = br.readLine();
									count++;
								}
								fConsensus.append(choicetext);
								fConsensus.append("\n");
						}
						if (choices.get(choices.size()-1)==2 & line.contains("[2]")){
							while(!line.contains("%com: Choice:2")){
								line=br.readLine();
							}
							count=1;
							line = br.readLine();
							while(!line.contains("%com: Difference")){
								if(count!=1){
									choicetext=choicetext+"\n"+line;
								}else{
									choicetext=line;
								}
								line = br.readLine();
								count++;
							}
							fConsensus.append(choicetext);
							fConsensus.append("\n");
						}
						line = br.readLine();
					}
					diffCount++;
				} else{
					fConsensus.append(line);
					fConsensus.append("\n");
				}
				line = br.readLine();
			}
			try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(directory +"final_consensus.cex", true)))) {
				out.println(fConsensus.toString());
				out.println("\n");
			}catch (IOException e) {
				//exception handling left as an exercise for the reader
			}
		}finally{
			br.close();
		}
	}
	public static void main(String[] args) throws IOException{
//		String OS = System.getProperty("os.name").toLowerCase();
//		if (OS.indexOf("win") >= 0){
//			OSV="win";
//		}else if(OS.indexOf("mac") >= 0){
//			OSV="mac";
//		}
		comparableTest();
	}
}
