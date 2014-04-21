package game.essentials;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import game.essentials.Controller.PressedButtons;

/**
 * A {@code HighScore} instance represent a high score from a single person.
 * @author Pojahn Moradi
 *
 */
public class HighScore implements java.io.Serializable, Comparable<HighScore>
{
	private static final long serialVersionUID = -8294988208058754641L;

	public static final Comparator<HighScore> DATE_SORT = new Comparator<HighScore>()
	{
		@Override
		public int compare(HighScore hs1, HighScore hs2) 
		{
			int value = hs1.date.compareTo(hs2.date);
			if(value > 0)
				value = -1;
			else if(value < 0)
				value = 1;
			
			return value;
		}
	};
	
	public static final Comparator<HighScore> NAME_SORT = new Comparator<HighScore>()
	{
		@Override
		public int compare(HighScore hs1, HighScore hs2) 
		{
			int value = hs1.name.compareTo(hs2.name);
			if(value > 0)
				value = -1;
			else if(value < 0)
				value = 1;
			
			if (value == 0) 
			{
				if (hs1.time > hs2.time)
					return -1;
				
				if (hs1.time < hs2.time)
					return 1;
				
				return 0;
			} 
			else 
				return value;
		}
	};
	
	public static final Comparator<HighScore> STAGE_SORT = new Comparator<HighScore>()
	{
		@Override
		public int compare(HighScore hs1, HighScore hs2) 
		{
			int value =  hs1.stageName.compareTo(hs2.stageName);
			if(value > 0)
				value = -1;
			else if(value < 0)
				value = 1;
			
			if (value == 0) 
			{
				if (hs1.time > hs2.time)
					return -1;
				
				if (hs1.time < hs2.time)
					return 1;
				
				return 0;
			} 
			else 
				return value;
		}
	};
	
	public static final Comparator<HighScore> TIME_SORT = new Comparator<HighScore>()
	{
		@Override
		public int compare(HighScore hs1, HighScore hs2) 
		{
			Double d1 = hs1.time;
			Double d2 = hs2.time;
			
			int value = d1.compareTo(d2);
			if(value > 0)
				value = -1;
			else if(value < 0)
				value = 1;
			
			return value;
		}
	};

	public double time;
	public String name, stageName, date, result;
	public Serializable meta;
	public Class<?> className;
	public List<List<PressedButtons>> replays;
	
	public HighScore()
	{}
	
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