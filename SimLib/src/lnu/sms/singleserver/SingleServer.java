package lnu.sms.singleserver;

import java.io.*;

import ited.simlib.ContinStat;
import ited.simlib.DiscreteStat;
import ited.simlib.EmptyListException;
import ited.simlib.List;
import ited.simlib.Random;
import ited.simlib.Timer;
import lnu.sms.SimObject;

public class SingleServer 
{
  private DataOutputStream outStream;
  
  public SingleServer(String s)
  {
	  try
	  {
		  outStream = new DataOutputStream(new FileOutputStream(s));
	  }
	  catch (IOException e)
	  {
	  }
  }
  
  public void addRecord(String s)
  {
	  try
	  {
		  outStream.writeBytes(s);
		  outStream.writeBytes("\n\r");
	  }
	  catch (IOException e)
	  {
	  }
	  
  }
  
  public void closeFile()
  {
	  try
	  {
		  outStream.close();
	  }
	  catch (IOException e)
	  {
	  }
  }
						  
  public static void main(String args[])
  {	
    // The output file prepared
	SingleServer ss = new SingleServer("out/output.txt");
	
		 
	// The next 2 lines create the simulation clock
	Timer clock = new Timer();
	clock.setTime(0);  // Timer initialized to zero

	List eventList = new List();  // A list to hold events
	List queue = new List();  // A list to represent a queue
	
	// Discrete statistic
	DiscreteStat waitTime = new DiscreteStat();
	
	// Continuous statistics
	int queueSize = 0; // Customer queue is empty
	int busy = 0; // Server not busy
	
	// The next 2 lines must follow the initialization of the clock
	// and the initialization of the continuous statistics we want to track
	ContinStat aveQueueSize = new ContinStat((float)queueSize, clock.getTime());
	ContinStat utilization = new ContinStat((float)busy, clock.getTime());
	
	final float MEANARR = (float)1.000;
	final float MEANPRO = (float)0.500;
	final int ARRSTR = 1;
	final int PROSTR = 2;

	int numServed = 0;
	
	// The next 4 lines create the first arrival
	SimObject event = new SimObject();
	event.setName("arrive"); 
	event.setTime(clock.getTime() + Random.expon(MEANARR, ARRSTR));
	eventList.insertInOrder(event, event.getTime());

	try
	{
	  while(numServed < 1000)
      {
		// The next 2 lines do what timing did
		SimObject removed = eventList.removeFromFront();
		clock.setTime(removed.getTime());
		// ss.addRecord(removed.getName().toString());
		// ss.addRecord("Time = " + String.valueOf(clock.getTime()));
	
		// Now the event is processed
		if (removed.getName() == "arrive")  // if the event is an arrival
		{
		  // First the next customer arrival is scheduled
	      event = new SimObject();
	      event.setName("arrive"); 
		  event.setTime(clock.getTime() + Random.expon(MEANARR,ARRSTR));
		  eventList.insertInOrder(event, event.getTime());

		  if (busy == 1)  // If the server is busy the customer waits
		  {
			// Record customer arrival time and put customer in queue
			SimObject waitingCustomer = new SimObject();
			waitingCustomer.setTime(clock.getTime());
			queue.insertAtBack(waitingCustomer);
			// Increment queue size and collect continuous statistic 
			queueSize++;
			aveQueueSize.recordContin((float)queueSize, clock.getTime());
		  }
		  else // If the server is not busy the customer is helped immediately
		  {
			waitTime.recordDiscrete((float) 0);  // Zero wait recorded
			busy = 1;
			utilization.recordContin((float)busy, clock.getTime());
			event = new SimObject();
			event.setName("depart"); 
			event.setTime(clock.getTime() + Random.expon(MEANPRO, PROSTR));
			eventList.insertInOrder(event, event.getTime());
		  }
		}
		else // A departure event
		{	
		  numServed++;
		  if (queue.isEmpty())
		  {
		    busy = 0;
			utilization.recordContin((float)busy, clock.getTime());
		  }
		  else
		  {
		    // Remove next customer from queue and record statistics
			SimObject dequeued = queue.removeFromFront();
			queueSize--;
            aveQueueSize.recordContin((float)queueSize, clock.getTime());
			waitTime.recordDiscrete(clock.getTime() - dequeued.getTime());							 
						  
			// Schedule the departure of the customer who has begun to be helped
			event = new SimObject();
			event.setName("depart");
			event.setTime(clock.getTime() + Random.expon(MEANPRO, PROSTR));
			eventList.insertInOrder(event, event.getTime());
		  }
		}
	  }
	}
	catch (EmptyListException e)
	{
	  System.err.println("\n" + e.toString());
	}
	
	ss.addRecord("The time average size of the queue was: " + 
				 String.valueOf(aveQueueSize.getContinAve(clock.getTime())));
	ss.addRecord("The time average utilization of the server was: " + 
				 String.valueOf(utilization.getContinAve(clock.getTime())));	  
	ss.addRecord("The aveage wait in queue was: " + 
				 String.valueOf(waitTime.getDiscreteAverage()));
	ss.addRecord("Wait times were divided by: " + 
				 String.valueOf(waitTime.getDiscreteObs()));
	ss.closeFile();
  }
}
