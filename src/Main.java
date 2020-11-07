import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		int maxCycle = 60000;
		int cycle;
		int nodeSize = 4;
		
		double prob = 0.1;
		
		BufferedOutputStream[] bufferedOutputStream = new BufferedOutputStream[nodeSize + 1];
		bufferedOutputStream[nodeSize] = new BufferedOutputStream(new FileOutputStream("Link.log"));
		String text = "00:00:000 Link Start\n\n00:00:000 System Clock Start\n\n";
		bufferedOutputStream[nodeSize].write(text.getBytes());
		
		Link link = new Link(bufferedOutputStream[nodeSize], maxCycle, nodeSize);
		Node node[] = new Node[nodeSize];
		
		for(int i = 0; i < nodeSize; i++)
		{
			bufferedOutputStream[i] = new BufferedOutputStream(new FileOutputStream("Node" + i + ".log"));
			node[i] = new Node(bufferedOutputStream[i], link, nodeSize, i);
			node[i].setProb(prob);
			text = "00:00:000 Node" + i + " Start\n\n";
			bufferedOutputStream[i].write(text.getBytes());
			
			link.linkNode(i, node[i]);
		}
		
		
		
		
		link.run();
		
	}
}
