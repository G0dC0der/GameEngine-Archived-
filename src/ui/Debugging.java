package ui;

import java.io.IOException;
import java.lang.management.ManagementFactory;

import javax.swing.JOptionPane;

public class Debugging
{
	public static void startDumpThread()
	{
		Thread t = new Thread()
		{
			@Override
			public void run() 
			{
				JOptionPane.showMessageDialog(null, "Click to dump the heap.", "Debugging", JOptionPane.INFORMATION_MESSAGE);
				
				while(true)
				{
					String name = ManagementFactory.getRuntimeMXBean().getName();
					String pid = name.substring(0, name.indexOf("@"));
					String[] cmd = { "C:/Program Files/Java/jdk1.7.0_10/bin/jmap.exe", "-dump:file=C:/Users/Pojahns Dator/Desktop/dump" + System.currentTimeMillis() + ".txt", pid };
					try 
					{
						Runtime.getRuntime().exec(cmd);
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}
					JOptionPane.showMessageDialog(null, "Heap generated. Click to dump again.", "Debugging", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		};
		t.setDaemon(true);
		t.start();
	}
}
