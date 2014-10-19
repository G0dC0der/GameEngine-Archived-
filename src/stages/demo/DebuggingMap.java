package stages.demo;

import game.core.Fundementals;
import game.core.GameObject;
import game.core.GameObject.Hitbox;
import game.development.AutoDispose;
import game.development.AutoInstall;
import game.development.StageBuilder;
import game.essentials.Image2D;
import ui.accessories.Playable;

@AutoDispose
@AutoInstall(path=DebuggingMap.PATH, mainPath="res/general")
@Playable(name="Debugging Map", description="Used for debugging and developing purposes.")
public class DebuggingMap extends StageBuilder
{
	static final String PATH = "res/debugging";
	
	private Image2D rec = new Image2D("res/data/rec.png", true);
	
	@Override
	public void build() 
	{
		super.build();
		
		gm.flyMode(2);
		
		GameObject go = new GameObject();
		go.setImage(rec);
		go.setHitbox(Hitbox.EXACT);
		go.moveTo(150, 400);
		go.addEvent(()->{
//			go.rotation+=1;
			if(Fundementals.pixelPerfectRotation(go, gm))
				System.out.println(Math.random());
		});
		add(go);
	}

}
