package game.core;

import game.core.Engine.Direction;
import game.essentials.Image2D;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * This class holds a number of static methods for essential calculations such as collision detection etc.
 * @author Pojahn Moradi
 */
public class Fundementals 
{
	/**
	 * Checks if the two rectangles are colliding.<br>
	 * Do not support rotation(i e the angle variable must be 0).
	 * @param rec1 The first rectangle.
	 * @param rec2 The second rectangle.
	 * @return True if the two rectangles are colliding.
	 */
	public static final boolean rectangleVsRecganle(GameObject rec1, GameObject rec2)
	{
		if ((rec1.loc.y + rec1.height() < rec2.loc.y) ||
	        (rec1.loc.y > rec2.loc.y + rec2.height()) ||
	        (rec1.loc.x + rec1.width() < rec2.loc.x)  ||
	        (rec1.loc.x > rec2.loc.x + rec2.width()))
	        return false;
		
		return true;
	}
	
	/**
	 * Checks if the two given rectangle collides.
	 * @return True if the two rectangle collides.
	 */
	public static boolean rectangleVsRectangle(float x1, float y1, float width1, float height1, float x2, float y2, float width2, float height2)
	{
		if(	y1 + height1 < y2 ||
	        y1 > y2 + height2 ||
	        x1 + width1 < x2  ||
	        x1 > x2 + width2)
				return false;
			
		return true;
	}
	
	/**
	 * Checks if the two rectangles are colliding.<br>
	 * Note: Since this function support rotated rectangles, it should be used with care due to expensive calculations.
	 * @param rec1 The first rectangle.
	 * @param rec2 The second rectangle.
	 * @return True if the two rectangles are colliding.
	 */
	public static boolean rotatedRectanglesCollision(GameObject rec1, GameObject rec2)
	{
		RotRect rr1 = new RotRect();
		rr1.C = new Vector2(rec1.loc.x + (rec1.width * rec1.scale) / 2, rec1.loc.y + (rec1.height * rec1.scale) / 2);
		rr1.S = new Vector2((rec1.width * rec1.scale) / 2, (rec1.height * rec1.scale) / 2);
		rr1.ang = (float) Math.toRadians(rec1.rotation);
		
		RotRect rr2 = new RotRect();
		rr2.C = new Vector2(rec2.loc.x + (rec2.width * rec2.scale) / 2, rec2.loc.y + (rec2.height * rec2.scale) / 2);
		rr2.S = new Vector2((rec2.width * rec2.scale) / 2, (rec2.height * rec2.scale) / 2);
		rr2.ang = (float) Math.toRadians(rec2.rotation);
		
		Vector2 A,B,C,BL,TR;
		
		float ang = rr1.ang - rr2.ang,
			  cosa = (float) Math.cos(ang),
			  sina = (float) Math.sin(ang),
			  t,x,a,dx,ext1,ext2;
		
		C = new Vector2(rr2.C);
		subVectors2D(C, rr1.C);
		
		rotateVector2DClockwise(C, rr2.ang);
		
		BL = new Vector2(C);
		TR = new Vector2(C);
		subVectors2D(BL, rr2.S);
		addVectors2D(TR, rr2.S);

		A = new Vector2();
		B = new Vector2();
		A.x = -rr1.S.y * sina;
		B.x = A.x;
		t = rr1.S.x * cosa;
		A.x += t;
		B.x -= t;
		A.y =  rr1.S.y * cosa;
		B.y = A.y;
		t = rr1.S.x * sina;
		A.y += t; 
		B.y -= t;
		
		t = sina*cosa;
		
		if (t < 0)
		{
			t = A.x; A.x = B.x; B.x = t;
			t = A.y; A.y = B.y; B.y = t;
		}
		if (sina < 0)
		{
			B.x = -B.x;
			B.y = -B.y;
		}
		if (B.x > TR.x || B.x > -BL.x) 
			return false;
		
		if (t == 0)
		{
			ext1 = A.y;
			ext2 = -ext1;
		}
		else
		{
			x = BL.x-A.x;
			a = TR.x-A.x;
			ext1 = A.y;
			  
			if (a*x > 0)
			{
				dx = A.x;
				if (x < 0)
				{
					dx -= B.x;
					ext1 -= B.y;
					x = a;
				}
				else
				{
					dx += B.x;
					ext1 += B.y;
				}
				ext1 *= x;
				ext1 /= dx;
				ext1 += A.y;
			}
		
			x = BL.x+A.x;
			a = TR.x+A.x;
			ext2 = -A.y;
		
			if (a*x > 0)
			{
				dx = -A.x;
		
				if (x < 0)
				{
					dx -= B.x;
					ext2 -= B.y;
					x = a;
				}
				else
				{
					dx += B.x;
					ext2 += B.y;
				}
		
				ext2 *= x;
				ext2 /= dx;
				ext2 -= A.y;
			}
		}
		return !((ext1 < BL.y && ext2 < BL.y) || (ext1 > TR.y && ext2 > TR.y));
	}
	
	/**
	 * Check if the given circle and rectangle are colliding.
	 * @param circle The GameObject with circular hitbox.
	 * @param rect The GameObject with rectangular hitbox.
	 * @return True if the two objects are colliding.
	 */
	public static boolean circleVsRectangle(GameObject circle, GameObject rect)
	{
	    float circleDistanceX = Math.abs((circle.loc.x + circle.width()  / 2) - (rect.loc.x + rect.width()  / 2));
	    float circleDistanceY = Math.abs((circle.loc.y + circle.height() / 2) - (rect.loc.y + rect.height() / 2));
	    float radius = circle.width / 2;

	    if (circleDistanceX > (rect.width() / 2 + radius) || (circleDistanceY > (rect.height() / 2 + radius)))
	    	return false;
	    
	    if ((circleDistanceX <= (rect.width() / 2)) || (circleDistanceY <= (rect.height() / 2)))
	    	return true;

	    double cornerDistance_sq = Math.pow(circleDistanceX - rect.width() /2, 2) +
	                               Math.pow(circleDistanceY - rect.height()/2, 2);

	    return (cornerDistance_sq <= (radius * radius));
	}

	public static boolean rotatedRectangleVsCircle(GameObject rect, GameObject circle)
	{	
		float oldRot = circle.rotation;
		circle.rotation = 0;
		boolean results = rotatedRectanglesCollision(rect, circle);
		circle.rotation = oldRot;
		
		return results;
		/*
		double rectCx = rect.loc.x + rect.width / 2,	//Rectangle center x
			   rectCy = rect.loc.y + rect.height / 2,	//Rectangle center y
			   ccX = circle.loc.x + circle.width / 2,	//Circle center x
			   ccY = circle.loc.y + circle.height/ 2,	//Circle center y
			   rot = Math.toRadians(rect.rotation),
			   rotSin = Math.sin(rot),
			   rotCos = Math.cos(rot),
			   cx = rotCos * (ccX - rectCx) - rotSin * (ccY - rectCy) + rectCx,
			   cy = rotSin * (ccX - rectCx) + rotCos * (ccY - rectCy) + rectCy,
			   x,y;
		 
		if (cx < rect.loc.x)
		    x = rect.loc.x;
		else if (cx > rect.loc.x + rect.width)
		    x = rect.loc.x + rect.width;
		else
		    x = cx;
		 
		if (cy < rect.loc.y)
		    y = rect.loc.y;
		else if (cy > rect.loc.y + rect.height)
		    y = rect.loc.y + rect.height;
		else
		    y = cy;
		 
		boolean result;
		if (findDistance(cx, cy, x, y) < circle.width / 2)
			result = true;
		else
			result = false;
		
		return result;
		*/
	}
	
	/**
	 * Check if the two circles are colliding.
	 * @param c1 The first circle.
	 * @param c2 The second circle.
	 * @return True if they are colliding.
	 */
	public static boolean circleVsCircle(GameObject c1, GameObject c2)
	{
		float x1 = c1.loc.x + c1.width()  / 2,
			  y1 = c1.loc.y + c1.height() / 2,
			  x2 = c2.loc.x + c2.width()  / 2,
			  y2 = c2.loc.y + c2.height() / 2,
			  r1 = c1.width() / 2,
			  r2 = c2.height() / 2;

	    float dx = x2 - x1;
	    float dy = y2 - y1;
	    float d = r1 + r2;
	    return (dx * dx + dy * dy) < (d * d);
	}
	
	/**
	 * Checks for collision with the polygons stored by each object. The polygons are updated if their data don't match with their holder.
	 * @param obj1 The first object.
	 * @param obj2 The second object.
	 * @return True if the polygons are colliding.
	 */
	public static boolean polygonCollision(GameObject obj1, GameObject obj2)
	{
		if(obj1.poly == null || obj2.poly == null)
			throw new RuntimeException("Can not peform a polygon collision if no polygon is set.");
		
		for(int i = 0; i < 2; i++)
		{
			GameObject obj = i == 0 ? obj1 : obj2;
			
			if(	obj.loc.x 				!= obj.poly.getX() 	||
				obj.loc.y 				!= obj.poly.getY() 	|| 
				obj.poly.getRotation() 	!= obj.rotation 	|| 
				obj.poly.getScaleX() 	!= obj.scale		|| 
				obj.poly.getScaleY() 	!= obj.scale)
			{
				obj.poly.setRotation(obj.rotation);
				obj.poly.setScale(obj.scale, obj.scale);
				obj.poly.setPosition(obj.loc.x, obj.loc.y);
			}
		}
		
		return Intersector.overlapConvexPolygons(obj1.poly, obj2.poly);
	}
	
	/**
	 * Check if the two objects image(the current one) are colliding using pixel perfect detection.<br>
	 * In other words, the precision if this function is exact.<br>
	 * If one of the {@code GameObjects} do not have any image, the function will return false.<br> This function also require that the image have color data(which they have by default).
	 * Rotation, scale customization and width/height manipulation is not supported.
	 * @param obj1 The first GameObject.
	 * @param obj2 The second GameObject.
	 * @return True if the two GameObjects are colliding.
	 */
	public static boolean pixelPerfect(GameObject obj1, GameObject obj2)
	{	
		if(obj1.rotation != 0 || obj2.rotation != 0)
			throw new IllegalStateException("Pixel pefect do not work on rotated entities.");
		
		Image2D image1 = getEntityImage(obj1);
		Image2D image2 = getEntityImage(obj2);
		
		if(image1 == null ||image2 == null)
			return false;
		
		final float width1  = image1.getWidth();
		final float width2  = image2.getWidth();
		final float height1 = image1.getHeight();
		final float height2 = image2.getHeight();
		final int top    = (int) Math.max(obj1.loc.y, obj2.loc.y);
		final int bottom = (int) Math.min(obj1.loc.y + height1, obj2.loc.y + height2);
		final int left   = (int) Math.max(obj1.loc.x, obj2.loc.x);
		final int right  = (int) Math.min(obj1.loc.x + width1, obj2.loc.x + width2);
		
		for (int y = top; y < bottom; y++)
		{
			for (int x = left; x < right; x++)
			{
				int x1 = (obj1.flipX) ? (int)(width1  - (x - obj1.loc.x) - 1) : (int) (x - obj1.loc.x);
				int y1 = (obj1.flipY) ? (int)(height1 - (y - obj1.loc.y) - 1) : (int) (y - obj1.loc.y);
				
				int x2 = (obj2.flipX) ? (int)(width2  - (x - obj2.loc.x) - 1) : (int) (x - obj2.loc.x);
				int y2 = (obj2.flipY) ? (int)(height2 - (y - obj2.loc.y) - 1) : (int) (y - obj2.loc.y);
				
				if (image1.getColor(x1, y1) != 0 && image2.getColor(x2, y2) != 0)
					return true;
			}
		}
		return false;
	}
	
	public static boolean pixelPerfectRotation(GameObject obj1, GameObject obj2)	//TODO:
	{
		throw new RuntimeException("Method not implemented yet.");
		/*
		Image2D image1 = getEntityImage(obj1);
		Image2D image2 = getEntityImage(obj2);
		
		if(image1 == null ||image2 == null)
			return false;
		
		Matrix4 m1 = new Matrix4().setToRotation(obj1.centerX(), obj2.centerY(), 0, obj1.rotation);
		Matrix4 m2 = new Matrix4().setToRotation(obj2.centerX(), obj2.centerY(), 0, obj2.rotation);
		
		Matrix4 transformAToB = new Matrix4(m1).mul(new Matrix4(m2).inv());
		
		Vector3 stepX = new Vector3(Vector3.X).mul(transformAToB).nor();
		Vector3 stepY = new Vector3(Vector3.Y).mul(transformAToB).nor();

		Vector3 yPosInB = new Vector3(Vector3.Zero).rot(transformAToB);
		
		for (int yA = 0; yA < obj1.height; yA++)
		{
			Vector3 posInB = new Vector3(yPosInB);
			
			for (int xA = 0; xA < obj1.width; xA++)
			{
				int xB = Math.round(posInB.x);
				int yB = Math.round(posInB.y);
              
				if(	0 <= xB && xB < obj2.width  && 0 <= yB && yB < obj2.height && image1.getColor(xA,yA) != 0 && image2.getColor(xB,yB) != 0 )
					return true;
              
              	posInB.x += stepX.x;
              	posInB.y += stepX.y;
			}
			
			yPosInB.x += stepY.x;
			yPosInB.y += stepY.y;
		}
		
		return false;*/
	}
	
	/**
	 * Check if the two lines are colliding.
	 * @param x1 Line 1
	 * @param y1 Line 1
	 * @param x2 Line 1
	 * @param y2 Line 1
	 * @param x3 Line 2
	 * @param y3 Line 2
	 * @param x4 Line 2
	 * @param y4 Line 2
	 * @return True if the two lines are colliding.
	 */
	public static boolean lineIntersect(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) 
	{
		float bx = x2 - x1; 
		float by = y2 - y1; 
		float dx = x4 - x3; 
		float dy = y4 - y3;
		float b_dot_d_perp = bx * dy - by * dx;
		if(b_dot_d_perp == 0) 
			return false;
		  
		float cx = x3 - x1;
		float cy = y3 - y1;
		float t = (cx * dy - cy * dx) / b_dot_d_perp;
		if(t < 0 || t > 1) 
			return false;
		  
		float u = (cx * by - cy * bx) / b_dot_d_perp;
		if(u < 0 || u > 1)
			return false;
		  
		return true;
	}
	
	/**
	 * Checks if the specified line collides with the specified circle({@code GameObject}).
	 * @param Ax
	 * @param Ay
	 * @param Bx
	 * @param By
	 * @param circle
	 * @return True if there is a collision.
	 */
	public static boolean circleVsLine(float Ax, float Ay, float Bx, float By, GameObject circle)
	{
		float r = circle.width() / 2;
		float Cx = circle.loc.x + r;
		float Cy = circle.loc.y + circle.height() / 2;
		
		double LAB = Math.sqrt((Bx-Ax)*(Bx-Ax) + (By-Ay)*(By-Ay));
		double Dx = (Bx-Ax)/LAB;
		double Dy = (By-Ay)/LAB;
		double t = Dx*(Cx-Ax) + Dy*(Cy-Ay);
		double Ex = t*Dx+Ax;
		double Ey = t*Dy+Ay;
		double LEC = Math.sqrt((Ex-Cx)*(Ex-Cx) + (Ey-Cy)*(Ey-Cy));
		if(LEC <= r)
			return true;
		
		return false;
	}
	
	/**
	 * Searches for solid space between the two points. Out of bounds will also count as solid space.
	 * @param x0 The x position of the first point.
	 * @param y0 The y position of the first point.
	 * @param x1 The x position of the second point.
	 * @param y1 The y position of the second point.
	 * @return The point where solid space was found.
	 */
	public static Vector2 findWallPoint(int x0, int y0, final int x1, final int y1)	//TODO: Remove this function, use tileSearch instead.
	{
		final int dx = Math.abs(x1-x0);
		final int dy = Math.abs(y1-y0); 
		final int sx = (x0 < x1) ? 1 : -1;
		final int sy = (y0 < y1) ? 1 : -1;
		int err = dx-dy;
		final byte[][] b = Stage.STAGE.stageData;
		
		while (true)
		{
			if (MovableObject.outOfBounds(x0, y0) || b[y0][x0] == Engine.SOLID)
				return new Vector2(x0, y0);
			
			final int e2 = 2 * err;
			if (e2 > -dy)
			{
				err -= dy;
				x0 += sx;
			}
			if (e2 < dx)
			{
				err += dx;
				y0 += sy;
			}
		}
	}
	
	/**
	 * Reefer to findWallPoint(int, int, int, int)
	 */
	public static Vector2 findWallPoint(float x0, float y0, float x1, float y1)
	{
		return Fundementals.findWallPoint((int)x0,(int)y0,(int)x1,(int)y1);
	}
	
	/**
	 * Returns the bounding box of the (rotated) rectangle.
	 * @param go The {@code GameObject} to calculate the bounding box on.
	 * @return The bounding box.
	 */
	public static Rectangle getBoundingBox(GameObject go)
	{
		if(go.rotation == 0)
			return new Rectangle(go.loc.x, go.loc.y, go.width(), go.height());
		
		ArrayList<Vector2> points = new ArrayList<>(4);
		float[] arr = new float[8];
		
		for(int i = 0; i < 4; i++)
		{
			float 	x = 0,
					y = 0;
			
			switch(i)
			{
				case 0:
					x = go.loc.x;
					y = go.loc.y;
					break;
				case 1:
					x = go.loc.x + go.width();
					y = go.loc.y;
					break;
				case 2:
					x = go.loc.x;
					y = go.loc.y + go.height();
					break;
				case 3:
					x = go.loc.x + go.width();
					y = go.loc.y + go.height();
					break;
			}

			arr[0] = x;
			arr[1] = y;
			arr[2] = arr[3] = arr[4] = arr[5] = arr[6] = arr[7] = 0;
			
			AffineTransform at = new AffineTransform();
			at.rotate(Math.toRadians(go.rotation), go.centerX(), go.centerY());
			at.transform(arr, 0, arr, 0, 4);
			
			points.add(new Vector2(arr[0], arr[1]));
		}
		
		float minX, maxX, minY, maxY, value1, value2;
		
		value1 = Math.min(points.get(0).x, points.get(1).x);
		value2 = Math.min(points.get(2).x, points.get(3).x);
		minX = Math.min(value1, value2);
		
		value1 = Math.max(points.get(0).x, points.get(1).x);
		value2 = Math.max(points.get(2).x, points.get(3).x);
		maxX = Math.max(value1, value2);
		
		value1 = Math.min(points.get(0).y, points.get(1).y);
		value2 = Math.min(points.get(2).y, points.get(3).y);
		minY = Math.min(value1, value2);
		
		value1 = Math.max(points.get(0).y, points.get(1).y);
		value2 = Math.max(points.get(2).y, points.get(3).y);
		maxY = Math.max(value1, value2);
		
		return new Rectangle(minX, minY, maxX - minX, maxY - minY);
	}
	
	/**
	 * Rotates {@code x} and {@code y} and return the new coordinate.
	 * @param x The x coordinate we want after a rotation(absolute).
	 * @param y The y coordinate we want after a rotation(absolute).
	 * @param cx Center x coordinate of the rectangle(absolute).
	 * @param cy Center y coordinate of the rectangle(absolute).
	 * @param rotation The angle in degrees.
	 * @return The point that contains the rotated coordinates.
	 */
	public static Vector2 getRotatedPoint(float x, float y, float cx, float cy, float rotation)
	{
		if(rotation == 0)
			return new Vector2(x,y);
		
		final float[] arr = {x, y, 0, 0, 0, 0, 0, 0};
		
		AffineTransform at = new AffineTransform();
		at.rotate(Math.toRadians(rotation), cx, cy);
		at.transform(arr, 0, arr, 0, 4);
		
		return new Vector2(arr[0], arr[1]);
	}
	
	/**
	 * Iterates through the specified line and continues in the specified path, searching for the given tile type.<br>
	 * Return a non-null value if the given tile was found between the two points.
	 * @param x0 The x position of the first point.
	 * @param y0 The y position of the first point.
	 * @param x1 The x position of the second point.
	 * @param y1 The y position of the second point.
	 * @param tile The tile to scan for.
	 * @return The point where the tile was found, or null if the tile was not found.
	 */
	public static Vector2 searchTile(int x0, int y0, final int x1, final int y1, byte tile)
	{
		final int dx = Math.abs(x1-x0);
		final int dy = Math.abs(y1-y0); 
		final int sx = (x0 < x1) ? 1 : -1;
		final int sy = (y0 < y1) ? 1 : -1;
		int err = dx-dy;
		final byte[][] b = Stage.STAGE.stageData;
		
		while (true)
		{
			if(MovableObject.outOfBounds(x0, y0))
				return null;
			else if(b[y0][x0] == tile)
				return new Vector2(x0, y0);
			
			final int e2 = 2 * err;
			if (e2 > -dy)
			{
				err -= dy;
				x0 += sx;
			}
			if (e2 < dx)
			{
				err += dx;
				y0 += sy;
			}
		}
	}
	
	public static Vector2 searchTile(float x0, float y0, final float x1, final float y1, byte tile)
	{
		return searchTile((int)x0, (int)y0, (int)x1, (int)y1, tile);
	}
	
	/**
	 * Check which of the given targets that are closest to the watcher.
	 * @param watcher The watcher.
	 * @param targets The targets to watch.
	 * @return The target that is closest to the watcher.
	 */
	public static GameObject findClosest(GameObject watcher, GameObject... targets)
	{
		if(targets.length <= 0)
			return null;
		
		if(targets.length == 1)
			return targets[0];
		
		int closestIndex  = -1; 
		double closestLength = 0;
		
		for (int i = 0; i < targets.length; i++)
		{				
			double distance = Fundementals.distance(watcher, targets[i]);
			
			if (closestLength == 0)
			{
				closestLength = distance;
				closestIndex = i;
			}
			if (distance < closestLength)
			{
				closestLength = distance;
				closestIndex = i;
			}
		}
		return targets[closestIndex];
	}

	/**
	 * Return the closest seeable target. If none of the targets are seeable, null is returned.
	 * @param watcher The unit that watches.
	 * @param targets The targets to watch.
	 * @return The object that is closest to the watcher that is also seeable.
	 */
	public static GameObject findClosestSeeable(GameObject watcher, GameObject... targets)
	{
		ArrayList<GameObject> seeable = new ArrayList<>();
		
		for (int i = 0; i < targets.length; i++)
			if(watcher.canSee(targets[i], GameObject.Accuracy.MID))
				seeable.add(targets[i]);
		
		if(seeable.size() == 0)
			return null;
		else
			return findClosest(watcher, seeable.toArray(new GameObject[seeable.size()]));
	}

	/**
	 * Check if the specified GameObject appear between the two points.
	 * @param x1 The x position of the first point.
	 * @param y1 The y position of the first point.
	 * @param x2 The x position of the second point.
	 * @param y2 The y position of the second point.
	 * @param go The GameObject that may intersect with the line.
	 * @return True if the specified GameObject is intersecting with the given line.
	 */
	public static boolean checkLine(float x1, float y1, float x2, float y2, GameObject go)
	{		
		if(go == null)
			return false;
		if(go.hitbox == GameObject.Hitbox.CIRCLE)
			return circleVsLine(x1,y1,x2,y2,go);
		else
		{
			if(lineIntersect(x1,y1,x2,y2, go.loc.x, go.loc.y, go.loc.x + go.width, go.loc.y)              			||
			   lineIntersect(x1,y1,x2,y2, go.loc.x, go.loc.y, go.loc.x, go.loc.y + go.height)             			||
			   lineIntersect(x1,y1,x2,y2, go.loc.x + go.width, go.loc.y, go.loc.x + go.width(), go.loc.y + go.height()) ||
			   lineIntersect(x1,y1,x2,y2, go.loc.x, go.loc.y + go.height, go.loc.x + go.width(), go.loc.y + go.height()))
				return true;
					
			return false;
		}
	}

	/**
	 * Checks if there is solid space between the two points.
	 * @param x0 The x coordinate of the starting point.
	 * @param y0 The y coordinate of the starting point.
	 * @param x1 The x coordinate of the end point.
	 * @param y1 The y coordinate of the end point.
	 * @return True if there is <i>no</i> solid space between the two points.
	 */
	public static boolean solidSpace (int x0, int y0, final int x1, final int y1)
	{
		final int dx = Math.abs(x1-x0);
		final int dy = Math.abs(y1-y0); 
		int sx = (x0 < x1) ? 1 : -1;
		int sy = (y0 < y1) ? 1 : -1;
		int err = dx-dy;
		final byte[][] b = Stage.STAGE.stageData;
		
		while (true)
		{
			if (b[y0][x0] == Engine.SOLID)
				return false;
	
			if (x0 == x1 && y0 == y1) 
				return true;
			
			float e2 = 2*err;
			if (e2 > -dy)
			{
				err -= dy;
				x0 += sx;
			}
			if (e2 < dx)
			{
				err += dx;
				y0 += sy;
			}
		}
	}
	
	/**
	 * Returns the angle between the two points in degrees.
	 */
	public static double getAngle(float x1, float y1, float x2, float y2)
	{
		float deltaX = x2 - x1;
		float deltaY = y2 - y1;
		
		return Math.atan2(deltaY, deltaX) * 180 / Math.PI;
	}

	/**
	 * Return the distance between the two {@code GameObjects} middle point. The higher value returned, the further away.
	 * @return The distance between the two objects.
	 */
	public static double distance(GameObject go1, GameObject go2)
	{
		return Fundementals.distance(go1.loc.x, go1.loc.y, go2.loc.x, go2.loc.y);
	}
	
	/**
	 * Calculates the distance between the two points.
	 */
	public static double distance(float x1, float y1, float x2, float y2)
	{
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}

	/**
	 * Iterates from the observer point to the target point and continues until the edge of the stage is reached, which will be returned.
	 * @param obsX The observers X point.
	 * @param obsY The observers Y point.
	 * @param tarX The targets X point.
	 * @param tarY The targets Y point.
	 * @return The edge point.
	 */
	public static Vector2 findEdgePoint(float obsX, float obsY, float tarX, float tarY)
	{
		int width  = Stage.STAGE.size.width;
		int height = Stage.STAGE.size.height;
		
		Vector2 obs = new Vector2(obsX, obsY);
		Vector2 tar = new Vector2(tarX, tarY);
		
		float vTime = 1.0e20f;
		if 		(tar.x > obs.x) vTime = (width - obs.x) / (tar.x - obs.x);
		else if (tar.x < obs.x) vTime = (0     - obs.x) / (tar.x - obs.x);
		 
		float hTime = 1.0e20f;
		if      (tar.y > obs.y) hTime = (height - obs.y) / (tar.y - obs.y);
		else if (tar.y < obs.y) hTime = (0      - obs.y) / (tar.y - obs.y);
		 
		float time = Math.min(hTime, vTime);
		
		float newX = obs.x + time * (tar.x - obs.x);
		float newY = obs.y + time * (tar.y - obs.y);
		
		return new Vector2(newX, newY);
	}
	
	/**
	 * Uses the {@code GameObjects} middle point.<br>
	 * See findEdgePoint(float obsX, float obsY, float tarX, float tarY) for reference.
	 */
	public static Vector2 findEdgePoint(GameObject observer, GameObject target)
	{
		return findEdgePoint(observer.loc.x + observer.width / 2,
							 observer.loc.y + observer.height / 2,
							 target.loc.x + target.width / 2,
							 target.loc.y + target.height / 2);
	}
	
	public static void rotateTowardsPoint(GameObject src, GameObject target, float speed)
	{
		float rotation = rotateTowardsPoint(src.loc.x, src.loc.y, target.loc.x, target.loc.y, src.rotation, speed);
		src.rotation = rotation;
	}
	
	/**
	 * Tries to rotate the abstract source so that it faces the abstract target.<br>
	 * Like an turret in a 2D environment rotates so it faces its target.
	 * @param srcX The X position of the source.
	 * @param srcY The Y position of the source.
	 * @param targetX The X position of the target.
	 * @param targetY The Y position of the target.
	 * @param currRotation The current rotation of the source.
	 * @param speed The speed of the rotation. You may want to put a low value such as 0.01.
	 * @return The new and updated rotation value(angle) of the source.
	 */
	public static float rotateTowardsPoint(float srcX, float srcY, float targetX, float targetY, float currRotation, float speed)
	{
		float destinationRotation = (float) (Math.atan2(srcY - targetY, srcX - targetX) + Math.PI);
		currRotation = (float) Math.toRadians(currRotation);
	
		if(Math.abs((currRotation + 180 - destinationRotation) % 360 - 180) < speed)
			currRotation = destinationRotation;
		else
		{
		    if (destinationRotation > currRotation)
		    {
		        if (currRotation < destinationRotation - Math.PI)
		        	currRotation -= speed;
		        else
		        	currRotation += speed;
		    }
		    else if (destinationRotation  < currRotation)
		    {
		        if (currRotation > destinationRotation + Math.PI)
		        	currRotation += speed;
		        else
		        	currRotation -= speed;
		    } 
		    if (currRotation > Math.PI * 2.0f) currRotation = 0;
		    if (currRotation < 0) currRotation = (float) (Math.PI * 2.0f);
		}
		return (float) Math.toDegrees(currRotation);
	}
	
	/**
	 * Normalizes the two points. Returns point (NaN:NaN) if x1 == x2 && y1 == y2.
	 * @return The normalized point.
	 */
	public static Vector2 normalize(float x1, float y1, float x2, float y2)
	{
		float dx = x1 - x2;
		float dy = y1 - y2;
		double length = Math.sqrt( dx*dx + dy*dy );
		dx /= length;
		dy /= length;
		
		return new Vector2(dx,dy);
	}

	/**
	 * Normalizes the points of the two {@code GameObjects}.
	 * @param go1 The first object.
	 * @param go2 The second object.
	 * @return The normalized point.
	 */
	public static Vector2 normalize(GameObject go1, GameObject go2)
	{
		return normalize(go1.loc.x, go1.loc.y, go2.loc.x, go2.loc.y);
	}

	/**
	 * Returns an enum from {@code game.core.Engine.Direction} that represent the angle of the point.
	 * @param normalizedPoint A normalized point.
	 * @return The direction of the point.
	 */
	public static Direction getDirection(Vector2 normalizedPoint)
	{
		double x = normalizedPoint.x;
		double y = normalizedPoint.y;
		
		final double fThreshold = Math.cos(Math.PI / 8);
		 
		if (x > fThreshold)
		    return Direction.W;
		else if (x < -fThreshold)
		    return Direction.E; 
		else if (y > fThreshold)
		    return Direction.N;
		else if (y < -fThreshold)
		    return Direction.S;
		else if (x > 0 && y > 0)
		    return Direction.NW;
		else if (x > 0 && y < 0)
		    return Direction.SW;
		else if (x < 0 && y > 0)
		    return Direction.NE;
		else if (x < 0 && y < 0)
		    return Direction.SE;
		
		return null;
	}
	
	/**
	 * Continues from the start point {@code x} and {@code y} with the given direction until the edge of the stage has been reached, which is the point returned.
	 * @param x The x coordinate to start at.
	 * @param y The y coordinate to start at.
	 * @param dir The direction to move in.
	 * @return The point.
	 */
	public static Vector2 getEdgePoint(float x, float y, Direction dir)
	{
		float targetX, targetY;
		
		switch (dir)
		{
			case NW:
				targetX = x - 1;
				targetY = y - 1;
				return Fundementals.findEdgePoint(x, y, targetX, targetY);
			
			case N:
				targetX = x;
				targetY = y - 1;
				return Fundementals.findEdgePoint(x, y, targetX, targetY);
			
			case NE:
				targetX = x + 1;
				targetY = y - 1;
				return Fundementals.findEdgePoint(x, y, targetX, targetY);
			
			case E:
				targetX = x + 1;
				targetY = y;
				return Fundementals.findEdgePoint(x, y, targetX, targetY);
			
			case SE:
				targetX = x + 1;
				targetY = y + 1;
				return Fundementals.findEdgePoint(x, y, targetX, targetY);
			
			case S:
				targetX = x;
				targetY = y + 1;
				return Fundementals.findEdgePoint(x, y, targetX, targetY);
			
			case SW:
				targetX = x - 1;
				targetY = y + 1;
				return Fundementals.findEdgePoint(x, y, targetX, targetY);
			
			case W:
				targetX = x - 1;
				targetY = y;
				return Fundementals.findEdgePoint(x, y, targetX, targetY);
		
			default:
				return null;
		}
	}
	
	private static Image2D getEntityImage(GameObject go)
	{
		if(!go.isVisible())
			return null;
		
		Image2D img =  null;
		boolean stopped = go.image.isStopped();
		go.image.stop(true);
		
		if(go instanceof MainCharacter)
			img = ((MainCharacter)go).getFrameByForce();
		else
			img = go.getFrame();
		
		go.image.stop(stopped);
		return img;
	}
	
	private static void addVectors2D(Vector2 v1, Vector2 v2)
	{
		v1.x += v2.x;
		v1.y += v2.y;
	}

	private static void subVectors2D(Vector2 v1, Vector2 v2)
	{
		v1.x -= v2.x;
		v1.y -= v2.y;
	}

	private static void rotateVector2DClockwise(Vector2 v, float ang)
	{
		float cosa = (float) Math.cos(ang),
			  sina = (float) Math.sin(ang),
			  t = v.x;
		 
		v.x =  t * cosa + v.y * sina;
		v.y = -t * sina + v.y * cosa;
	}
	
	private static class RotRect
	{
		Vector2 C, S;
		float ang;
	}
}