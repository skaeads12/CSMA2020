import java.io.BufferedOutputStream;
import java.io.IOException;

public class Link implements Runnable
{
	private int cycle;
	private int useCount;
	private int maxCycle;
	Node node[];
	private int sourceIndex;
	private int targetIndex;
	private BufferedOutputStream bos;
	
	public Link(BufferedOutputStream bos, int maxCycle, int nodeSize)
	{
		this.cycle = 0;
		this.node = new Node[nodeSize];
		this.maxCycle = maxCycle;
		this.bos = bos;
	}
	
	public void linkNode(int index, Node node)
	{
		this.node[index] = node;
	}
	
	public boolean isUsing()
	{
		return this.useCount > 0;
	}
	
	public int getCycle()
	{
		return this.cycle;
	}
	
	public void loggingText(String text)
	{
		int cycle = this.getCycle();
		int sec = cycle / 1000;
		int msec = cycle % 1000;
		
		text = "00:" + sec + ":" + msec + " " + text + "\n\n";
		
		try
		{
			this.bos.write(text.getBytes());
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public boolean sendRequest(int sourceIndex, int targetIndex, int sendCount)
	{
		String text = "Node" + sourceIndex + " Send Request To Node" + targetIndex;
		this.loggingText(text);
		
		if(this.isUsing())
		{
			text = "Reject: " + text;
			this.loggingText(text);

			return false;
		}
		else
		{
			boolean success = this.node[targetIndex].receiveRequest(sourceIndex, sendCount);
			
			if(success)
			{
				text = "Accept: " + text;
				this.loggingText(text);
				
				this.useCount = sendCount;
				this.sourceIndex = sourceIndex;
				this.targetIndex = targetIndex;
				
				return true;
			}
			else
			{
				text = "Reject: " + text;
				
				this.loggingText(text);
				
				return false;
			}
		}
	}
	
	public void upCycle()
	{
		this.cycle++;
		
		if(this.isUsing())
		{
			this.useCount--;
			
			if(this.useCount == 0)
			{
				String text = "Node" + this.sourceIndex + " Data Send Finished To Node" + this.targetIndex;
				this.loggingText(text);
			}
		}
		
		for(int i = 0; i < this.node.length; i++)
		{
			this.node[i].upCycle();
		}
	}
	
	public void run()
	{
		while(this.cycle < this.maxCycle)
		{
			for(int i = 0; i < this.node.length; i++)
			{
				this.node[i].run();
			}
			
			this.upCycle();
		}
	}
}
