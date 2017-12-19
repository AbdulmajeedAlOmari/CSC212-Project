import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SubtitleSeqFactory {

	// Return an empty subtitles sequence 
	public static SubtitleSeq getSubtitleSeq() {
		return new SubtitleSeqOld();
	}
	
	/***
	 * Load a subtitle sequence from an SRT file. If the file does not exist or is corrupted (incorrect format), null is returned.
	 * @param fileName
	 * @return SubtitleSeq
	 */
	public static SubtitleSeq loadSubtitleSeq(String fileName) {
		SubtitleSeq subSeq = getSubtitleSeq();
		
		BufferedReader br = null; //The reader that is used to read the file
		String line = null; //Contains the current line information
		int sequence = 1; //A counter for the sequences to check the format of the file
		
		try {
			br = new BufferedReader(new FileReader(new File(fileName)));
			line = br.readLine(); //Saves the first line of the file in the variable
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		if(br == null)
			return null;
		
		while(line != null) {
			int tempSequence = 0; //Contains the current subtitle sequence
			
			try {
				tempSequence = Integer.parseInt(line); //Saves and checks if the current sequence is correct or not
			} catch(NumberFormatException e) {
				e.printStackTrace();
				return null;
			}
			
			if(tempSequence != sequence)
				return null;
			
			try {
				line = br.readLine(); //Jumping to time interval line (eg. 01:04:54,315 --> 01:05:02,023)
			}
			catch(IOException e) { 
				e.printStackTrace();
				return null;
			}
			
			//Checking the format of the file:
			if(!line.contains(" --> "))
				return null;
			
			String[] timeInterval = line.split(" --> "); //Splits the interval into two Strings
			
			String startTime = timeInterval[0]; //Contains the whole start time (eg. 01:04:54,315)
			String endTime = timeInterval[1]; //Contains the whole end time (eg. 01:05:02,023)
			
			if(!startTime.contains(":") || !startTime.contains(",") || !endTime.contains(":") || !endTime.contains(","))
				return null;
			//End checking
			
			
			String[] startTimeFormat = startTime.split(":"); //Contains splited start time (eg. {[01], [04], [54,315]})
			String[] endTimeFormat = endTime.split(":"); //Contains splited end time (eg. {[01], [05], [02,023]})
			
			String[] startTimeSSMS = startTimeFormat[2].split(","); //Contains starting Seconds and Milliseconds (eg. {[54], [315]})
			String[] endTimeSSMS = endTimeFormat[2].split(","); //Contains ending Seconds and Milliseconds (eg. {[02], [023]})
			
			if(startTimeFormat[0].length() != 2 || startTimeFormat[1].length() != 2 || startTimeFormat[2].length() != 6
					|| endTimeFormat[0].length() != 2 || endTimeFormat[1].length() != 2 || endTimeFormat[2].length() != 6)
				return null;
			
			int sTimeHH = 0, sTimeMM = 0, sTimeSS = 0, sTimeMS = 0
					, eTimeHH = 0, eTimeMM = 0, eTimeSS = 0, eTimeMS = 0;
			
			try {
				//Parsing Strings into Integers and saving them in the variables
				sTimeHH = Integer.parseInt(startTimeFormat[0]);
				sTimeMM = Integer.parseInt(startTimeFormat[1]);
				sTimeSS = Integer.parseInt(startTimeSSMS[0]);
				sTimeMS = Integer.parseInt(startTimeSSMS[1]);
				
				eTimeHH = Integer.parseInt(endTimeFormat[0]);
				eTimeMM = Integer.parseInt(endTimeFormat[1]);
				eTimeSS = Integer.parseInt(endTimeSSMS[0]);
				eTimeMS = Integer.parseInt(endTimeSSMS[1]);
			} catch(NumberFormatException e) {
				e.printStackTrace();
				return null;
			}
			
			//Creating startTime and endTime objects
			Time sTime = new TimeImp(sTimeHH, sTimeMM, sTimeSS, sTimeMS);
			Time eTime = new TimeImp(eTimeHH, eTimeMM, eTimeSS, eTimeMS);
			
			//Checking wether the time interval is valid or not
			if(toMilliSec(sTime) >= toMilliSec(eTime))
				return null;
			
			String subText = ""; //Contains the text of the subtitle
			
			try {
				line = br.readLine(); //Jumping to subtitle text line (eg. "That's the last thing I would want.")
			} catch(IOException e) {
				e.printStackTrace();
				return null;
			}
			
			while(line != null && !line.equals("")) {
				//Check if it is the first text, if true print it in the first line, otherwise print it in the last line
				if(subText.equals(""))
					subText += line; //adding on the first line
				else
					subText += "\n" + line; //adding on the last line
				
				
				try {
					line = br.readLine(); //Jumping to the next subtitle text if any
				} catch(IOException e) {
					e.printStackTrace();
					return null;
				}
			}
			
			//Create the subtitle object using the given info above
			Subtitle subtitle = new SubtitleImp(sTime, eTime, subText);
			
			subSeq.addSubtitle(subtitle); //Insert the subtitle in the SubtitleSeq object
			
			if(line!=null) {
				try {
					line = br.readLine();
				}
				catch(IOException e) { 
					e.printStackTrace();
				}
			}
			sequence++;
		}
		return subSeq;
	}
	
	private static int toMilliSec(Time time) {
		return time.getHH()*3600*1000 + time.getMM()*60*1000 + time.getSS()*1000 + time.getMS();
	}
}
