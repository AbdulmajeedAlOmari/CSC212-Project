public class TimeInterval implements Comparable<TimeInterval> {
	private TimeImp startTime, endTime;
	
	public TimeInterval() {
		startTime = null;
		endTime = null;
	}
	
	public TimeInterval(TimeImp startTime, TimeImp endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	@Override
	public int compareTo(TimeInterval that) {
		if (startTime.compareTo(that.endTime) > 0) {
			return 1;
		}
		if (endTime.compareTo(that.startTime) < 0) {
			return -1;
		}
		return 0;
	}

	public TimeImp getStartTime() {
		return startTime;
	}

	public void setStartTime(TimeImp startTime) {
		this.startTime = startTime;
	}

	public TimeImp getEndTime() {
		return endTime;
	}

	public void setEndTime(TimeImp endTime) {
		this.endTime = endTime;
	}
	
	
}
