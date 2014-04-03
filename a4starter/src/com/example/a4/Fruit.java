/**
 * CS349 Winter 2014
 * Assignment 4 Demo Code
 * Jeff Avery
 */
package com.example.a4;
import java.util.Random;

import android.R;
import android.graphics.*;
import android.graphics.Region.Op;
import android.util.Log;
import android.view.Display;

/**
 * Class that represents a Fruit. Can be split into two separate fruits.
 */
public class Fruit {
    private Path path = new Path();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Matrix transform = new Matrix();
    Random r = new Random();
    public int gravity = r.nextInt(35 - 20) + MainActivity.displaySize.y / 25;
    public int velocity = r.nextInt(10 - 0);
    public int posx = r.nextInt(MainActivity.displaySize.x);
    public int posy = 0;
    public boolean sliced = false;

    /**
     * A fruit is represented as Path, typically populated 
     * by a series of points 
     */
    Fruit(float[] points) {
        init();
        this.path.reset();
        this.path.moveTo(points[0], points[1]);
        for (int i = 2; i < points.length; i += 2) {
            this.path.lineTo(points[i], points[i + 1]);
        }
        this.path.moveTo(points[0], points[1]);
    }

    Fruit(Region region) {
        init();
        this.path = region.getBoundaryPath();
    }

    Fruit(Path path) {
        init();
        this.path = path;
    }

    private void init() {
        this.paint.setColor(Color.BLUE);
        this.paint.setStrokeWidth(5);
    }

    /**
     * The color used to paint the interior of the Fruit.
     */
    public int getFillColor() { return paint.getColor(); }
    public void setFillColor(int color) { paint.setColor(color); }

    /**
     * The width of the outline stroke used when painting.
     */
    public double getOutlineWidth() { return paint.getStrokeWidth(); }
    public void setOutlineWidth(float newWidth) { paint.setStrokeWidth(newWidth); }

    /**
     * Concatenates transforms to the Fruit's affine transform
     */
    public void rotate(float theta) { transform.postRotate(theta); }
    public void scale(float x, float y) { transform.postScale(x, y); }
    public void translate(float tx, float ty) { transform.postTranslate(tx, ty); }

    /**
     * Returns the Fruit's affine transform that is used when painting
     */
    public Matrix getTransform() { return transform; }

    /**
     * The path used to describe the fruit shape.
     */
    public Path getTransformedPath() {
        Path originalPath = new Path(path);
        Path transformedPath = new Path();
        originalPath.transform(transform, transformedPath);
        return transformedPath;
    }

    /**
     * Paints the Fruit to the screen using its current affine
     * transform and paint settings (fill, outline)
     */
    public void draw(Canvas canvas) {
    	canvas.drawPath(getTransformedPath(), this.paint);
    }

    /**
     * Tests whether the line represented by the two points intersects
     * this Fruit.
     */
    public boolean intersects(PointF p1, PointF p2) {
    	Region region1 = new Region();
    	Region region2 = new Region();
    	Region clip = new Region(0,0,MainActivity.displaySize.x,MainActivity.displaySize.y);
    	Path line = new Path();
    	line.moveTo(p1.x, p1.y - 1);
    	line.lineTo(p2.x,  p2.y - 1);
    	line.lineTo(p2.x, p2.y + 1);
    	line.moveTo(p1.x, p1.y - 1);
    	region1.setPath(getTransformedPath(), clip);
    	region2.setPath(line, clip);
    	return region1.op(region2,Region.Op.INTERSECT);
    }

    /**
     * Returns whether the given point is within the Fruit's shape.
     */
    public boolean contains(PointF p1) {
        Region region = new Region();
        boolean valid = region.setPath(getTransformedPath(), new Region());
        return valid && region.contains((int) p1.x, (int) p1.y);
    }

    /**
     * This method assumes that the line represented by the two points
     * intersects the fruit. If not, unpredictable results will occur.
     * Returns two new Fruits, split by the line represented by the
     * two points given.
     */
    public Fruit[] split(PointF p1, PointF p2) {
    	Region clip = new Region(0,0,10000,10000);
    	Path topPath = null;
    	Path bottomPath = null;
    	
    	double dx = p2.x - p1.x;
    	double dy = p2.y - p1.y;
    	float angle = (float) Math.atan2(dy, dx) * 180 / (float)Math.PI;
    	
    	Matrix t = new Matrix();
    	Matrix inverse = new Matrix();
    	t.setRotate(-1 * angle, p1.x, p1.y);
    	inverse.setRotate(angle, p1.x, p1.y);
    	
    	Path temp = getTransformedPath();
    	temp.transform(t);
    	Region tempRegion = new Region();
    	tempRegion.setPath(temp, clip);
    	
    	topPath = new Path(); 
        Region topRegion = new Region();
        topPath.addRect(-10000, (int)p1.y - 10000, 10000, (int)p1.y, Path.Direction.CW);
        topRegion.setPath(topPath, clip);
        
        bottomPath = new Path();
        Region bottomRegion = new Region();
        bottomPath.addRect(-10000, (int)p1.y, 10000, (int)p1.y + 10000, Path.Direction.CW);
        bottomRegion.setPath(bottomPath, clip);
        
        topRegion.op(tempRegion, Region.Op.INTERSECT);
        bottomRegion.op(tempRegion, Region.Op.INTERSECT);
        
        
        if (topPath != null && bottomPath != null) {
        	Fruit topFruit = new Fruit(topRegion);
        	Fruit botFruit = new Fruit(bottomRegion);
        	topFruit.gravity = 0;
        	botFruit.gravity = 0;
        	topFruit.sliced = true;
        	botFruit.sliced = true;
        	topFruit.posy = Integer.MAX_VALUE;
        	botFruit.posy = Integer.MAX_VALUE;
        	topFruit.path.transform(inverse);
        	botFruit.path.transform(inverse);
            return new Fruit[] { topFruit, botFruit };
        }
        return new Fruit[0];
    }    
}
