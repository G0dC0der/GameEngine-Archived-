package game.essentials;

import java.util.Comparator;
import game.essentials.Controller.PressedButtons;

/**
 * A {@code HighScore} instance represent a high score from a single person.
 * @author Pojahn Moradi
 *
 */
public class HighScore implements java.io.Serializable, Comparable<HighScore>
{
	private static final long serialVersionUID = -8294988208058754641L;
	
	public static final Comparator<HighScore> RESULT_SORT = new Comparator<HighScore>()
	{
		@Override
		public int compare(HighScore hs1, HighScore hs2) 
		{
			return hs1.result.compareTo(hs2.result);
		}
	};

	public static final Comparator<HighScore> DATE_SORT = new Comparator<HighScore>()
	{
		@Override
		public int compare(HighScore hs1, HighScore hs2) 
		{
			return hs1.date.compareTo(hs2.date);
		}
	};
	
	public static final Comparator<HighScore> NAME_SORT = new Comparator<HighScore>()
	{
		@Override
		public int compare(HighScore hs1, HighScore hs2) 
		{
			return hs1.name.compareTo(hs2.name);
		}
	};
	
	public static final Comparator<HighScore> STAGE_SORT = new Comparator<HighScore>()
	{
		@Override
		public int compare(HighScore hs1, HighScore hs2) 
		{
			return hs1.stageName.compareTo(hs2.stageName);
		}
	};
	
	public static final Comparator<HighScore> TIME_SORT = new Comparator<HighScore>()
	{
		@Override
		public int compare(HighScore hs1, HighScore hs2) 
		{
			Double d1 = hs1.time;
			Double d2 = hs2.time;
			
			return d1.compareTo(d2);
		}
	};
	
	public static final Comparator<HighScore> INPUT_SORT = new Comparator<HighScore>()
	{
		@Override
		public int compare(HighScore hs1, HighScore hs2) 
		{
			return hs1.replay.length - hs2.replay.length;
		}
	};

	public String name, stageName, meta, date, result;
	public double time;
	public Class<?> className;
	public PressedButtons[] replay;
	
	public HighScore()
	{}
	
	public HighScore(String name, String stageName, String meta, double time)
	{
		this.name = name;
		this.stageName = stageName;
		this.time = time;
		this.meta = meta;
	}
	
	@Override
	public int compareTo(HighScore hs) 
	{
		int stringOrder = this.stageName.compareTo(hs.stageName);
		
		if (stringOrder == 0) 
		{
			if (this.time > hs.time)
				return 1;
			
			if (this.time < hs.time) 
				return -1;
			
			return 0;
		} 
		else
			return stringOrder;
	}
	
	@Override
	public String toString()
	{
		return name + "\t" + stageName + "\t" + time;
	}
}