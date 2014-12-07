package stages.tr3;

import game.core.Engine.Direction;
import game.core.GameObject;
import game.core.MainCharacter.CharacterState;
import game.development.AutoInstall;
import game.development.AutoLoad;
import game.essentials.Animation;
import game.essentials.BigImage.RenderOption;
import game.essentials.Factory;
import game.essentials.Image2D;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;
import stages.traning.AbstractTraningStage;
import ui.accessories.Playable;
import com.badlogic.gdx.graphics.Color;

@AutoInstall(mainPath="res/general", path=TraningStage3.PATH)
@Playable(name="Traning Stage 3", description="A traning stage where the difficulty is slightly increasing.")
public class TraningStage3 extends AbstractTraningStage
{
	static final String PATH = "res/traning3";
	
	@AutoLoad(path=PATH, type=VisualType.IMAGE)
	private Image2D cloud[], weak, weakdie, melon;

	@AutoLoad(path=PATH, type=VisualType.SOUND)
	private Sound talking;
	
	private Sound collect, collapsing;
	
	private static final Color DARK_BLUE = Color.valueOf("0000AAFF");
	
	@Override
	public void init() 
	{
		super.init();
		
		foregroundImg.setRenderOption(RenderOption.PORTION);
		backgroundImg.setRenderOption(RenderOption.PARALLAX);
		backgroundImg.setScrollRatio(.4f);
		
		collect = TinySound.loadSound(new java.io.File("res/general/collect1.wav"));
		collapsing = TinySound.loadSound(new java.io.File("res/climb/collapsing.wav"));
		
		setFriendFont(game.fpsFont);
		setFriendTextColor(DARK_BLUE);
		setFriendImage(cloud);
		setTalking(talking);
		friendImageSpeed(5);
		
		setStageMusic(PATH + "/song.ogg", 0, 1);
	}
	
	@Override
	public void build() 
	{
		super.build();
		game.timeColor = DARK_BLUE;
		setPeople(gm);
		
		/*
		 * Main Character
		 */
		gm.addTileEvent(Factory.slipperWalls(gm));
		gm.facing = Direction.W;
		
		/*
		 * Weak Platforms
		 */
		GameObject w = new GameObject();
		w.setImage(weak);
		w.setCloneEvent((clone)-> clone.addEvent(Factory.weakPlatform(clone, new Animation<>(1, weakdie), 90, collapsing, gm)));
		
		add(w.getClone(2630, 3893),
			w.getClone(2490, 3893),
			w.getClone(2340, 3853),
			w.getClone(2390, 3753));
		
		/*
		 * Goal
		 */
		GameObject g = new GameObject();
		g.setImage(melon);
		g.moveTo(300, 262);
		g.addEvent(()->{
			if(g.collidesWith(gm))
			{
				discard(g);
				collect.play();
				gm.setState(CharacterState.FINISH);
			}
		});
		add(g);
		
		
		/*
		 * Friends
		 */
		add(getFriend(2900, 3801, -120, -40, "Welcome to Traning Paradise!\nJump on the weak platforms and continue upwards."),
			getFriend(1391, 3367, -20, -55, "Perform a wall jump\nto get up there."),
			getFriend(868, 2703, -100, -20, "Continue left my friend."),
			getFriend(1651, 1985, -100, -20, "Jump at the very end of this platform."),
			getFriend(3033, 2093, -180, -20, "North-east is where you want to go :=)"),
			getFriend(2891, 1022, -100, -20, "Jump as high and far as possible"),
			getFriend(1138, 241, -60, -20, "Almost done ^_^"));
	}
}
