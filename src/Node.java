import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Node implements Runnable
{
	private int cycle;
	private int sendCount;
	private int receiveCount;
	private int waitCount;
	private int transNum;
	private int maxTransNum;
	private int nodeIndex;
	private Link link;
	
	private double prob;
	private int targetIndex; // index to send from node
	private int sourceIndex; // index to receive from node
	private BufferedOutputStream bos;
	
	public Node(BufferedOutputStream bos, Link link, int nodeSize, int nodeIndex)
	{
		this.link = link;
		this.cycle = 0;
		this.nodeIndex = nodeIndex;
		this.bos = bos;
		this.transNum = 0;
		this.targetIndex = -1;
	}
	
	public int getCycle()
	{
		return this.cycle;
	}
	
	public void setProb(double prob)
	{
		this.prob = prob;
	}
	
	public double getProb()
	{
		return this.prob;
	}
	
	public void setWait(int waitCount)
	{
		this.waitCount = waitCount;
	}
	
	public boolean isWaiting()
	{
		return this.waitCount > 0;
	}
	
	public boolean isSending()
	{
		return this.sendCount > 0;
	}
	
	public boolean isReceiving()
	{
		return this.receiveCount > 0;
	}
	
	public boolean receiveRequest(int sourceIndex, int receiveCount)
	{
		if(this.isSending() || this.isReceiving())
		{
			return false;
		}
		else
		{
			this.sourceIndex = sourceIndex;
			this.receiveCount = receiveCount;
			String text = "Data Receive Start from Node" + sourceIndex;
			this.loggingText(text);
			
			return true;
		}
	}
	
	public boolean sendRequest()
	{
		int targetIndex;
		
		if(this.targetIndex == -1)
			targetIndex = this.nodeIndex;
		else
			targetIndex = this.targetIndex;
		
		Random random = new Random();
		
		while(targetIndex == this.nodeIndex)
		{
			targetIndex = (int) (random.nextDouble() * 4);
		}
		
		this.targetIndex = targetIndex;
		
		return this.sendRequest(targetIndex);
	}
	
	public boolean sendRequest(int targetIndex)
	{
		String text = "Data Send Request To Node" + targetIndex;
		this.loggingText(text);
		
		boolean success = link.sendRequest(this.nodeIndex, targetIndex, 5);
		
		if(!success)
		{
			this.transNum++;
			
			if(this.transNum == this.maxTransNum)
			{
				this.transNum = 0;
			}
			
			int waitingTime = this.BackoffTimer(this.transNum);
			this.setWait(waitingTime);
			
			text = "Data Send Request Reject from Link";
			this.loggingText(text);
			
			text = "Exponential Back-off Time: " + waitingTime + " msec";
			this.loggingText(text);
			
			return false;
		}
		else
		{
			text = "Data Send Request Accept from Link";
			this.loggingText(text);
			
			this.sendCount = 5;
			this.transNum = 0;
			
			return true;
		}
	}
	
	public int BackoffTimer(int transNum)
	{ 
		int random;
		int temp;
		temp = Math.min(transNum, 10);
		random = (int) (Math.random() * (Math.pow(2, temp) - 1));
		
		if(random < 1)
			return 1;
		else
			return random;
	}
	
	public void loggingText(String text)
	{
		int cycle = this.getCycle();
		int sec = cycle / 1000;
		int msec = cycle % 1000;
		
		text = "00:" + sec + ":" + msec + " " + text + "\n\n";
		
		try
		{
			System.out.println("[" + this.nodeIndex + "] " + text);
			this.bos.write(text.getBytes());
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void upCycle()
	{
		this.cycle++;
		
		if(this.isWaiting())
		{
			this.waitCount--;
			
			if(this.waitCount == 0)
			{
				if(!this.isReceiving())
					this.sendRequest();
			}
		}
		else if(this.isSending())
		{
			this.sendCount--;
			
			if(this.sendCount == 0)
			{
				String text = "Data Send Finished To Node" + this.targetIndex;
				this.loggingText(text);
				this.targetIndex = -1;
			}
		}
		else if(this.isReceiving())
		{
			this.receiveCount--;
			
			if(this.receiveCount == 0)
			{
				String text = "Data Receive Finished To Node" + this.sourceIndex;
				this.loggingText(text);
				this.sourceIndex = -1;
			}
		}
	}
	
	public void run()
	{
		Random random = new Random();
		double randomSeed = random.nextDouble();
		
		if(randomSeed < this.getProb()) // 10%
		{
			if(this.isSending() || this.isReceiving() || this.isWaiting())
			{
				return;
			}
			else
			{
				this.sendRequest();
			}
		}
	}
}
