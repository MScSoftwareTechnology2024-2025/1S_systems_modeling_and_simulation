package lnu.sms;
public class SimObject  
// This is an object that holds data.  SimObjects are put into lists.
						
{
	private String name;
	private float time;
	// Private data below this time are particular to this problem
	private float arriveTime;
	private int jobType;
	private int taskNumber;
	private int tellerNo;
	private float remainTime;
	
	public SimObject()
	{
		name = "";
		time = (float)0.0;
	}

	public void setName(String str)
	{
		name = str;
	}

	public String getName()
	{
		return name;
	}

	public void setTime(float fl)
	{
		time = fl;
	}

	public float getTime()
	{
		return time;
	}
	
	/* All set/gets below this line are made to support the 
	   specific simulation.  These set/gets support all the simulations
	   in Chapter 2 of Law and Kelton */
	
	public void setArriveTime(float fl)
	{
		arriveTime = fl;
	}

	public float getArriveTime()
	{
		return arriveTime;
	}
	
	public void setJobType(int job)
	{
		jobType = job;
	}

	public int getJobType()
	{
		return jobType;
	}

	public void setTaskNumber(int tn)
	{
		taskNumber = tn;
	}

	public int getTaskNumber()
	{
		return taskNumber;
	}
	
	public void setTellerNo(int t)
	{
		tellerNo = t;
	}

	public int getTellerNo()
	{
		return tellerNo;
	}
	
	public void setRemainTime(float fl)
	{
		remainTime = fl;
	}

	public float getRemainTime()
	{
		return remainTime;
	}
}
