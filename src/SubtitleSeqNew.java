
public class SubtitleSeqNew implements SubtitleSeq {
	private SortedBST<TimeInterval, Subtitle> tree;
	
	public SubtitleSeqNew() {
		tree = new SortedBST<>();
	}
	
	@Override
	public void addSubtitle(Subtitle st) {
		TimeInterval t = new TimeInterval((TimeImp) st.getStartTime(), (TimeImp) st.getEndTime());
		tree.insert(t, st);
	}

	@Override
	public List<Subtitle> getSubtitles() {
		List<Subtitle> list = new LinkedList<>();
		
		if(tree.empty())
			return list;
		
		tree.findFirst();
		
		while(!tree.last()) {
			list.insert(tree.retrieve().second);
			tree.findNext();
		}
		list.insert(tree.retrieve().second);
		
		return list;
	}

	@Override
	public Subtitle getSubtitle(Time time) {
//		if(tree.empty())
//			return null;
//		
//		tree.findLast();
//		if(tree.retrieve().first.getEndTime().compareTo(time) < 0)
//			return null;
//		
//		tree.findFirst();
//		if(tree.retrieve().first.getStartTime().compareTo(time) > 0)
//			return null;
//		
//		TimeImp sTime = null;
//		TimeImp eTime = null;
//		while(!tree.last() && tree.retrieve().first.getStartTime().compareTo(time) > 0) {
//			sTime = tree.retrieve().first.getStartTime();
//			eTime = tree.retrieve().first.getEndTime();
//			
//			if(sTime.compareTo(time) >= 0 && eTime.compareTo(time) <= 0) {
//				return tree.retrieve().second;
//			}
//			
//			tree.findNext();
//		}
//		
//		sTime = tree.retrieve().first.getStartTime();
//		eTime = tree.retrieve().first.getEndTime();
//		
//		if(sTime.compareTo(time) >= 0 && eTime.compareTo(time) <= 0)
//			return tree.retrieve().second;
//		
//		return null;
		if(tree.empty())
			return null;
		
		TimeInterval t = new TimeInterval((TimeImp) time, (TimeImp) time);
		
		if(tree.find(t))
			return tree.retrieve().second;
		
		
		return null;
	}

	@Override
	public int nbNodesInSearchPath(Time time) {
		TimeInterval t = new TimeInterval((TimeImp) time, (TimeImp) time);
		return tree.nbNodesInSearchPath(t);
	}

	// Return, in chronological order, all subtitles displayed between the
		// specified start and end times. The first element of this list is the
		// subtitle of which the display interval contains or otherwise comes
		// Immediately after startTime. The last element of this list is the
		// subtitle of which the display interval contains or otherwise comes
		// immediately before endTime.
	@Override
	public List<Subtitle> getSubtitles(Time startTime, Time endTime) {
		List<Subtitle> list = new LinkedList<>();
		
		if(tree.empty())
			return list;
		
		TimeInterval k1 = new TimeInterval((TimeImp) startTime, (TimeImp) startTime);
		TimeInterval k2 = new TimeInterval((TimeImp) endTime, (TimeImp) endTime);
		
		List<Pair<TimeInterval, Subtitle>> pairList = tree.inRange(k1, k2);
		
		if(pairList.empty())
			return list;
		
		pairList.findFirst();
		
		while(!pairList.last()) {
			list.insert(pairList.retrieve().second);
			pairList.findNext();
		}
		list.insert(pairList.retrieve().second);
		
		return list;
	}

	// Shift the subtitles by offseting their start/end times with the specified
	// offset (in milliseconds). The value offset can be positive or negative.
	// Negative time is not allowed and must be replaced with 0. If the end time
	// becomes 0, the subtitle must be removed.
	@Override
	public void shift(int offset) {
		if(tree.empty())
			return;

		Pair<TimeInterval, Subtitle> pair;
		TimeImp newSTime;
		TimeImp newETime;

		if(offset > 0) {
			tree.findLast();

			while(!tree.first()) {
				pair = tree.retrieve();
				newSTime = pair.first.getStartTime();
				newETime = pair.first.getEndTime();
				newSTime = (TimeImp) toTime(toMilliSec(newSTime) + offset);
				newETime = (TimeImp) toTime(toMilliSec(newETime) + offset);

				updateInterval(pair, newSTime, newETime);

				tree.findPrevious();
			}
			pair = tree.retrieve();
			newSTime = pair.first.getStartTime();
			newETime = pair.first.getEndTime();
			newSTime = (TimeImp) toTime(toMilliSec(newSTime) + offset);
			newETime = (TimeImp) toTime(toMilliSec(newETime) + offset);

			updateInterval(pair, newSTime, newETime);

		} else if(offset < 0) {
			tree.findFirst();

			while(!tree.last()) {
				pair = tree.retrieve();
				newSTime = pair.first.getStartTime();
				newETime = pair.first.getEndTime();
				int sTimeMilli = toMilliSec(newSTime) + offset;
				int eTimeMilli = toMilliSec(newETime) + offset;

				boolean isRemoved = false;

				if(eTimeMilli < 0) {
					tree.remove();
					isRemoved = true;
				} else if (sTimeMilli < 0) {
					sTimeMilli = 0;
				}

				if(!isRemoved) {
					newSTime = (TimeImp) toTime(sTimeMilli);
					newETime = (TimeImp) toTime(eTimeMilli);
					updateInterval(pair,newSTime,newETime);

					tree.findNext();
				}
			}

			pair = tree.retrieve();
			newSTime = pair.first.getStartTime();
			newETime = pair.first.getEndTime();
			int sTimeMilli = toMilliSec(newSTime) + offset;
			int eTimeMilli = toMilliSec(newETime) + offset;

			boolean isRemoved = false;

			if(eTimeMilli < 0) {
				tree.remove();
				isRemoved = true;
			} else if (sTimeMilli < 0) {
				sTimeMilli = 0;
			}

			if(!isRemoved) {
				newSTime = (TimeImp) toTime(sTimeMilli);
				newETime = (TimeImp) toTime(eTimeMilli);
				updateInterval(pair,newSTime,newETime);
			}
		}
	}

	private void updateInterval(Pair<TimeInterval, Subtitle> pair, TimeImp newSTime, TimeImp newETime) {
		pair.first.setStartTime(newSTime);
		pair.first.setEndTime(newETime);

		pair.second.setStartTime(newSTime);
		pair.second.setEndTime(newETime);

		tree.update(pair.second);
		tree.updateKey(pair.first);
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

	private int toMilliSec(Time time) {
		return time.getHH()*3600*1000 + time.getMM()*60*1000 + time.getSS()*1000 + time.getMS();
	}
}
