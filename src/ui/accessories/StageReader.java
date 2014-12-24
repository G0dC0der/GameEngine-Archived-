package ui.accessories;

import game.core.Stage;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;

public class StageReader 
{
	public static List<Class<? extends Stage>> loadAll()
	{
		List<Class<? extends Stage>> stages = new LinkedList<>(); 
		
		stages.add(stages.ataristyle.AtariStyle.class);
		stages.add(stages.blocks.SquareTown.class);
		stages.add(stages.bridge.GhostBridge.class);
		stages.add(stages.climb.ClimbMe.class);
		stages.add(stages.clubber.DontClubberMe.class);
		stages.add(stages.deathegg.DeathEgg.class);
		stages.add(stages.diamondcave.DiamondCave.class);
		stages.add(stages.factory.SteelFactory.class);
		stages.add(stages.flyingbat.FlyingBattery.class);
		stages.add(stages.forbiddencastle.ForbiddenCastle.class);
		stages.add(stages.hill.GreenHill.class);
		stages.add(stages.lasers.LaserEverywhere.class);
		stages.add(stages.lightsout.LightsOut.class);
		stages.add(stages.mtrace.MountainRace.class);
		stages.add(stages.mutantlabb.MutantLabb.class);
		stages.add(stages.orbitalstation.SecretLaboratory.class);
		stages.add(stages.pihell.PiranhaHell.class);
		stages.add(stages.race.Race.class);
		stages.add(stages.sand.Sandopolis.class);
		stages.add(stages.shroom.DontEatShroom.class);
		stages.add(stages.spirit.SpiritTemple.class);
		stages.add(stages.stress.StressMap.class);
		stages.add(stages.tr1.TraningStage1.class);
		stages.add(stages.tr2.TraningStage2.class);
		stages.add(stages.tr3.TraningStage3.class);
		stages.add(stages.tr4.TraningStage4.class);
		stages.sort((clazz1, clazz2)-> clazz1.getSimpleName().compareTo(clazz2.getSimpleName()));
		
		return stages;
	}

	public static Playable getPlayable(Class<?> clazz)
	{
		Annotation[] ans = clazz.getAnnotations();
		for(Annotation an : ans)
			if(an instanceof Playable)
				return (Playable)an;
		
		return null;
	}
}