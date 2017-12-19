
public class SubtitleSeqOld implements SubtitleSeq
{
	private List<Subtitle> subtitleList;
	
	public SubtitleSeqOld() {
		subtitleList = new LinkedList<>();
	}
	
	/***
	 * This method inserts the Subtitle in its chronological place
	 */
	public void addSubtitle(Subtitle st) {
		int stStartTime = toMilliSec(st.getStartTime());
		int stEndTime = toMilliSec(st.getEndTime());
		
		if(subtitleList.full() || stStartTime < 0 || stEndTime <= stStartTime) //Checking for invalid input or full List
			return;
		
		if(subtitleList.empty()) {
			subtitleList.insert(st);
		} else {
			subtitleList.findFirst();
			
			Subtitle tempSub = null;
			int tempSubStart = 0;
			int tempSubEnd = 0;
			
			while(stEndTime > toMilliSec(subtitleList.retrieve().getStartTime()) && !subtitleList.last()) {
				tempSub = subtitleList.retrieve();
				tempSubStart = toMilliSec(tempSub.getStartTime());
				tempSubEnd = toMilliSec(tempSub.getEndTime());
				
				if(isWithinRange(stStartTime, stEndTime, tempSubStart, tempSubEnd)) //if it interrupts any subtitle, it returns.
					return;
				
				subtitleList.findNext();
			}
			
			tempSub = subtitleList.retrieve(); //This is after the (st)
			tempSubStart = toMilliSec(tempSub.getStartTime());
			tempSubEnd = toMilliSec(tempSub.getEndTime());
			
			if(!isWithinRange(stStartTime, stEndTime, tempSubStart, tempSubEnd)) {
				if(stEndTime < tempSubStart) { //Check if the (st) is less than the current or not
					subtitleList.update(st); //update the current
					subtitleList.insert(tempSub); //insert the previous current
				} else {
					subtitleList.insert(st); //Normal insertion if it is the last subtitle
				}
			}
		}
	}
	
	/***
	 * This method returns all subtitles in their chronological order.
	 */
	public List<Subtitle> getSubtitles() {
		return subtitleList;
	}
	
	/***
	 * This method returns the subtitle displayed at the specified time, null if no subtitle is displayed.
	 */
	public Subtitle getSubtitle(Time time) {
		if(subtitleList.empty())
			return null;
		
		subtitleList.findFirst();
		
		int specified = toMilliSec(time);
		Subtitle temp = null;
		int sTime = 0;
		int eTime = 0;
		
		while(!subtitleList.last()) {
			temp = subtitleList.retrieve();
			sTime = toMilliSec(temp.getStartTime());
			eTime = toMilliSec(temp.getEndTime());
			if(specified >= sTime && specified <= eTime)
				return temp;
			
			subtitleList.findNext();
		}
		
		temp = subtitleList.retrieve();
		sTime = toMilliSec(temp.getStartTime());
		eTime = toMilliSec(temp.getEndTime());
		if(specified >= sTime && specified <= eTime)
			return temp;
		
		return null;
	}

	@Override
	public int nbNodesInSearchPath(Time time) {
		return 0;
	}

	/***
	 * Return, in chronological order, all subtitles displayed between the
	 * specified start and end times. The first element of this list is the
	 * subtitle of which the display interval contains or otherwise comes
	 * Immediately after startTime. The last element of this list is the
	 * subtitle of which the display interval contains or otherwise comes
	 * immediately before endTime.
	 */
	public List<Subtitle> getSubtitles(Time startTime, Time endTime) {
		List<Subtitle> subtitles = new LinkedList<>();
		
		if (subtitleList.empty()) {
			return subtitles;
		} else {
			int specifiedStart = toMilliSec(startTime);
			int specifiedEnd = toMilliSec(endTime);
			subtitleList.findFirst();
			while (!subtitleList.last()) {
				int sTime = toMilliSec(subtitleList.retrieve().getStartTime());
				int eTime = toMilliSec(subtitleList.retrieve().getEndTime());
				
				if(isWithinRange(specifiedStart, specifiedEnd, sTime, eTime)){
					subtitles.insert(subtitleList.retrieve());
				}
				subtitleList.findNext();
			}
			int sTime = toMilliSec(subtitleList.retrieve().getStartTime());
			int eTime = toMilliSec(subtitleList.retrieve().getEndTime());
			// (sTime >= specifiedStart && eTime <= specifiedEnd) || (sTime<specifiedStart && eTime>specifiedEnd) || (sTime<specifiedStart && eTime<specifiedEnd)
			if(isWithinRange(specifiedStart, specifiedEnd, sTime, eTime)){
				subtitles.insert(subtitleList.retrieve());
			}
		}
		
		return subtitles;
	}
	
	/***
	 *  Return, in chronological order, all subtitles containing str as a sub-string in their text.
	 */
	public List<Subtitle> getSubtitles(String str) {
		List<Subtitle> list = new LinkedList<>();
		
		if(subtitleList.empty())
			return list;
		
		subtitleList.findFirst();
		
		while(!subtitleList.last()) {
			if(subtitleList.retrieve().getText().contains(str))
				list.insert(subtitleList.retrieve());
			subtitleList.findNext();
		}
		
		if(subtitleList.retrieve().getText().contains(str))
			list.insert(subtitleList.retrieve());
		
		return list;
	}

	public void remove(String str) {
		if(subtitleList.empty())
			return;
		
		subtitleList.findFirst();
		
		while(!subtitleList.last()) {
			if(subtitleList.retrieve().getText().contains(str))
				subtitleList.remove();
			else
				subtitleList.findNext();
		}
		
		if(subtitleList.retrieve().getText().contains(str))
			subtitleList.remove();
	}

	// Replace str1 with str2 in all subtitles.
	public void replace(String str1, String str2) {
		if(subtitleList.empty())
			return;
		
		subtitleList.findFirst();
		
		Subtitle sub = null;
		
		while(!subtitleList.last()) {
			sub = subtitleList.retrieve();
			if(sub.getText().contains(str1))
				sub.setText(sub.getText().replaceAll(str1, str2));
			subtitleList.findNext();
		}
		
		sub = subtitleList.retrieve();
		if(sub.getText().contains(str1))
			sub.setText(sub.getText().replaceAll(str1, str2));
	}

	/***
	 * Shift the subtitles by offseting their start/end times with the specified
	 * offset (in milliseconds). The value offset can be positive or negative.
	 * Negative time is not allowed and must be replaced with 0. If the end time
	 * becomes 0, the subtitle must be removed.
	 */
	public void shift(int offset) {
		if(subtitleList.empty())
			return;
		
		subtitleList.findFirst();
		
		Subtitle sub = null;
		int subStartTime = 0;
		int subEndTime = 0;
		
		while(!subtitleList.last()) {
			sub = subtitleList.retrieve();
			
			subStartTime = toMilliSec(sub.getStartTime());
			subEndTime = toMilliSec(sub.getEndTime());
			
			
			
			if(subEndTime+offset < 0) {
				subtitleList.remove();
			} else {
				if(subStartTime+offset < 0)
					subStartTime = 0;
				else
					subStartTime += offset;
				
				subEndTime += offset;

				Time newStartTime = toTime(subStartTime);
				sub.setStartTime(newStartTime);

				Time newEndTime = toTime(subEndTime);
				sub.setEndTime(newEndTime);
				
				subtitleList.findNext();
			}
		}
		
		sub = subtitleList.retrieve();
		
		subStartTime = toMilliSec(sub.getStartTime());
		subEndTime = toMilliSec(sub.getEndTime());
		
		if(subEndTime+offset < 0) {
			subtitleList.remove();
		} else {
			if(subStartTime+offset < 0)
				subStartTime = 0;
			else
				subStartTime += offset;
			
			subEndTime += offset;

			Time newStartTime = toTime(subStartTime);
			sub.setStartTime(newStartTime);

			Time newEndTime = toTime(subEndTime);
			sub.setEndTime(newEndTime);
			
			subtitleList.findNext();
		}
	}

	/*** 
	 * Cut all subtitles between the specified start and end times. The first
	 * subtitle to be removed is the one for which the display interval contains
	 * or otherwise comes immediately after startTime. The last subtitle to be
	 * removed is the one for which the display interval contains or otherwise
	 * comes immediately before endTime. The start and end times of all
	 * subtitles must be adjusted to reflect the new time.
	 */
	public void cut(Time startTime, Time endTime) {
		int specifiedStart = toMilliSec(startTime);
		int specifiedEnd = toMilliSec(endTime);
		
		if(subtitleList.empty() || specifiedEnd < specifiedStart)
			return;
		
		subtitleList.findFirst();
		
		Subtitle tempSub = null;
		int sTime = 0;
		int eTime = 0;
		
		while(!subtitleList.last()) {
			tempSub = subtitleList.retrieve();
			sTime = toMilliSec(tempSub.getStartTime());
			eTime = toMilliSec(tempSub.getEndTime());
			
			if(isWithinRange(specifiedStart, specifiedEnd, sTime, eTime))
				subtitleList.remove();
			else {
				if(sTime > specifiedEnd) {
					shiftFromCurrent(specifiedStart-(specifiedEnd+1));
					return;
				}
				subtitleList.findNext();
			}
		}
		
		tempSub = subtitleList.retrieve();
		sTime = toMilliSec(tempSub.getStartTime());
		eTime = toMilliSec(tempSub.getEndTime());
		
		if(isWithinRange(specifiedStart, specifiedEnd, sTime, eTime))
			subtitleList.remove();
		else if(sTime > specifiedEnd)
			shiftFromCurrent(specifiedStart-(specifiedEnd+1));
	}
	
	private int toMilliSec(Time time) {
		return time.getHH()*3600*1000 + time.getMM()*60*1000 + time.getSS()*1000 + time.getMS();
	}
	
	private boolean isWithinRange(int specifiedStart, int specifiedEnd, int sTime, int eTime) {
		return (sTime>=specifiedStart&&sTime<=specifiedEnd) || (eTime>=specifiedStart&&eTime<=specifiedEnd)
				|| (sTime<specifiedStart && eTime>specifiedEnd);
	}
	
	private Time toTime(int milliSec) {
		Time time = new TimeImp();
		int ms = milliSec%1000;
		int totalSec = ((milliSec-ms)/1000);
		int ss = totalSec%60;
		int mm = ((totalSec-ss)/60)%60;
		int hh = ((totalSec-ss)/3600)%24;
		
		time.setMS(ms);
		time.setSS(ss);
		time.setMM(mm);
		time.setHH(hh);
		
		return time;
	}
	
	private void shiftFromCurrent(int offset) {
		Subtitle sub = null;
		int subStartTime = 0;
		int subEndTime = 0;
		
		while(!subtitleList.last()) {
			sub = subtitleList.retrieve();
			
			subStartTime = toMilliSec(sub.getStartTime());
			subEndTime = toMilliSec(sub.getEndTime());
			
			if(subEndTime+offset < 0) {
				subtitleList.remove();
			} else {
				if(subStartTime+offset < 0)
					subStartTime = 0;
				else
					subStartTime += offset;
				
				subEndTime += offset;
				
				Time newStartTime = toTime(subStartTime);
				sub.setStartTime(newStartTime);

				Time newEndTime = toTime(subEndTime);
				sub.setEndTime(newEndTime);
				
				subtitleList.findNext();
			}
		}
		
		sub = subtitleList.retrieve();
		
		subStartTime = toMilliSec(sub.getStartTime());
		subEndTime = toMilliSec(sub.getEndTime());
		
		if(subEndTime+offset < 0) {
			subtitleList.remove();
		} else {
			if(subStartTime+offset < 0)
				subStartTime = 0;
			else
				subStartTime += offset;
			
			subEndTime += offset;

			Time newStartTime = toTime(subStartTime);
			sub.setStartTime(newStartTime);

			Time newEndTime = toTime(subEndTime);
			sub.setEndTime(newEndTime);
		}
	}
}
