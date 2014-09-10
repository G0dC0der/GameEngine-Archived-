package stages.tr5;

import game.core.Engine;
import game.core.GameObject;
import game.core.Engine.GameState;
import game.development.AutoDispose;
import game.development.AutoInstall;
import game.development.AutoLoad;
import game.development.StageBuilder;
import game.essentials.Factory;
import game.essentials.Image2D;
import game.movable.PathDrone;
import java.io.File;
import java.util.ArrayList;
import kuusisto.tinysound.Music;
import kuusisto.tinysound.TinySound;
import ui.accessories.Playable;
import com.badlogic.gdx.graphics.Color;

@AutoDispose
@Playable(description="Practise perfectionism.", name="Traning Stage 5")
@AutoInstall(mainPath="res/general", path="res/traning5")
public class TraningStage5 extends StageBuilder
{
	@AutoLoad(path="res/traning5",type=VisualType.IMAGE)
	private Image2D drills[];
	
	@AutoLoad(path="res/mtrace",type=VisualType.IMAGE)
	private Image2D flag[];
	
	private Music sawLoop;
	
	private ArrayList<PathDrone> allDrills = new ArrayList<>();
	
	@Override
	public void init() 
	{
		super.init();
		
		try
		{
			sawLoop = TinySound.loadMusic(new File("res/traning5/sawLoop.wav"));
			sawLoop.setVolume(0);
			sawLoop.play(true);
			
			game.timeColor = Color.WHITE;
			setStageMusic("res/traning5/song.ogg", 3.58f);
		}
		catch(Exception e)
		{
			System.err.println("IO Error!");
			e.printStackTrace();
		}
	}
	
	@Override
	public void build() 
	{
		super.build();
		
		gm.addTileEvent(Factory.slipperWalls(gm));
		gm.addTileEvent((tileType)->{
			if(tileType == Engine.AREA_TRIGGER_9)
			{
				for(PathDrone drill : allDrills)
					drill.setMoveSpeed(3);
			}
		});
		
		/*
		 * Drills
		 */
		
		allDrills.clear();
		
		for(int i = 0, y = 0; i < 34; i++, y += drills[0].getHeight())
		{
			PathDrone drill = new PathDrone(0,y);
			drill.appendPath(size.width, y);
			drill.setImage(5, drills);
			drill.addEvent(Factory.hitMain(drill, gm, -100));
			drill.setMoveSpeed(1.35f);
			drill.zIndex(200);
			
			add(drill);
			allDrills.add(drill);
		}
		
		/*
		 * Chain sound
		 */
		final GameObject drill = allDrills.get(0);
		GameObject dummy = new GameObject();
		dummy.setVisible(true);
		dummy.addEvent(()->{
			dummy.moveTo(drill.currX, gm.currY);
		});
		
		add(dummy);
		add(Factory.soundFalloff(sawLoop, dummy, gm, 550, 0, 40, 1.0f));
		
		/*
		 * Flag
		 */
		GameObject fl = new GameObject();
		fl.setImage(5, flag);
		fl.moveTo(5120, 735);
		add(fl);
	}
	
	@Override
	public void extra() 
	{
		if(game.getGlobalState() == GameState.ENDED || game.getGlobalState() == GameState.FINISH)
		{
			for(PathDrone drill : allDrills)
				drill.freeze();
		}
		
		game.tx = Math.min(size.width  - view.width,   Math.max(0, gm.currX - view.width  / 2)) + view.width  / 2; 
		
		final PathDrone aDrill = allDrills.get(0);
		
		if(aDrill.currX + view.width / 2 > gm.currX)
			game.tx = aDrill.currX + view.width / 2;
	}
}
