
public class SubtitleImp implements Subtitle
{
	private Time startTime, endTime;
	private String text;
	
	public SubtitleImp()
	{
		startTime = null;
		endTime = null;
		text = "";
	}
	
	public SubtitleImp(Time startTime, Time endTime, String text)
	{
		this.startTime = startTime;
		this.endTime = endTime;
		this.text = text;
	}
	
	public Time getStartTime()
	{
		return startTime;
	}

	public Time getEndTime()
	{
		return endTime;
	}

	public String getText()
	{
		return text;
	}

	public void setStartTime(Time startTime)
	{
		this.startTime = startTime;
	}

	public void setEndTime(Time endTime)
	{
		this.endTime = endTime;
	}

	public void setText(String text)
	{
		this.text = text;
	}
	
}
