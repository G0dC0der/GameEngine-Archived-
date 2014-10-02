package stages.forbiddencastle;

import kuusisto.tinysound.Sound;

import com.badlogic.gdx.graphics.Color;

import game.core.GameObject;
import game.core.MainCharacter.CharacterState;
import game.development.AutoDispose;
import game.development.AutoInstall;
import game.development.AutoLoad;
import game.development.StageBuilder;
import game.essentials.Factory;
import game.essentials.Frequency;
import game.essentials.Image2D;
import ui.accessories.Playable;

@AutoDispose
@AutoInstall(mainPath="res/general", path=ForbiddenCastle.PATH)
@Playable(name="Forbidden Castle", description="Stage: Forbidden Castle\nAuthor: Pojahn Moradi\nDifficulty: 7\nAverage time: 100 sec\nProfessional time: 60 sec\nObjective: Enter goal.")
public class ForbiddenCastle extends StageBuilder
{
	static final String PATH = "res/forbiddencastle";
	
	@AutoLoad(path=PATH, type=VisualType.IMAGE)
	private Image2D weak[], coin[];
	@AutoLoad(path=PATH, type=VisualType.SOUND)
	private Sound weakDieSound;
	
	@Override
	public void init() 
	{
		super.init();
		setStageMusic(PATH + "/song.ogg", 0.60f, 1.0f);
	}
	
	@Override
	public void build() 
	{
		super.build();
		game.timeColor = Color.WHITE;
		
		addWeak(18,28);
		
		for(int i = 24; i <= 32; i++)
			addWeak(i, 26);
		
		for(int i = 24; i <= 31; i++)
			addWeak(i, 23);
		
		for(int i = 25; i <= 32; i++)
			addWeak(i, 20);
		
		addWeak(37, 21);
		addWeak(42, 21);
		addWeak(44, 18);
		addWeak(44, 17);
		addWeak(41, 14);
		addWeak(41, 13);
		addWeak(41, 12);
		addWeak(46, 13);
		addWeak(46, 12);
		addWeak(46, 11);
		addWeak(53, 14);
		addWeak(53, 13);
		addWeak(53, 12);
		addWeak(53, 11);
		addWeak(53, 10);
		addWeak(53, 9);
		addWeak(53, 8);
		addWeak(49, 8);
		addWeak(49, 7);
		addWeak(49, 6);
		addWeak(49, 5);
		addWeak(49, 4);
		addWeak(53, 4);
		addWeak(54, 4);
		addWeak(55, 4);
		addWeak(56, 4);
		addWeak(57, 4);
		
		for(int i = 75; i <= 83; i++)
			addWeak(i, 17);

		for(int i = 76; i <= 82; i++)
			addWeak(i, 14);

		for(int i = 77; i <= 81; i++)
			addWeak(i, 11);
		
		for(int i = 78; i <= 80; i++)
			addWeak(i, 8);

		addWeak(79, 5);
		addWeak(44, 16);
		
		/*
		 * Goal
		 */
		GameObject goal = new GameObject();
		goal.setImage(5, coin);
		goal.moveTo(79 * 30, 3 * 30);
		goal.currX += 10;
		goal.currY += 15;
		goal.addEvent(()->{
			if(goal.collidesWith(gm))
			{
				discard(goal);
				gm.setState(CharacterState.FINISH);
			}
		});
		add(goal);
	}
	
	void addWeak(int x, int y)
	{
		GameObject w = new GameObject();
		w.moveTo(x * 30, y * 30);
		w.setImage(weak[0]);
		
		Frequency<Image2D> weakDie = new Frequency<>(5, weak);
		weakDie.setLoop(false);
		
		w.addEvent(Factory.weakPlatform(w, weakDie, 60, weakDieSound, gm));
		
		add(w);
	}
}
