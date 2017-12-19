
public class TimeImp implements Time, Comparable<Time>
{
	private int hh, mm, ss, ms;
	
	public TimeImp()
	{
		hh = 0;
		mm = 0;
		ss = 0;
		ms = 0;
	}
	
	public TimeImp(int hh, int mm, int ss, int ms)
	{
		this.hh = hh;
		this.mm = mm;
		this.ss = ss;
		this.ms = ms;
	}
	
	public int getHH()
	{
		return hh;
	}

	public int getMM()
	{
		return mm;
	}

	public int getSS()
	{
		return ss;
	}

	public int getMS()
	{
		return ms;
	}

	public void setHH(int hh)
	{
		this.hh = hh;
	}

	public void setMM(int mm)
	{
		this.mm = mm;
	}

	public void setSS(int ss)
	{
		this.ss = ss;
	}

	public void setMS(int ms)
	{
		this.ms = ms;
	}

	@Override
	public int compareTo(Time time) {
		int ms1 = this.toMS();
		int ms2 = ((TimeImp) time).toMS();
		
		if(ms1 > ms2)
			return 1;
		else if(ms1 == ms2)
			return 0;
		else
			return -1;
	}
	
	private int toMS() {
		return hh*3600*1000 + mm*60*1000 + ss*1000 + ms;
	}
	
}
