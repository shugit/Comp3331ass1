package ass1;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;


/**
 * A GameObject is an object that can move around in the game world.
 * 
 * GameObjects form a scene tree. The root of the tree is the special ROOT object.
 * 
 * Each GameObject is offset from its parent by a rotation, a translation and a scale factor. 
 *
 * TODO: The methods you need to complete are at the bottom of the class
 *
 * @author malcolmr
 */
public class GameObject {

    // the list of all GameObjects in the scene tree
    public final static List<GameObject> ALL_OBJECTS = new ArrayList<GameObject>();
    
    // the root of the scene tree
    public final static GameObject ROOT = new GameObject();
    
    // the links in the scene tree
    private GameObject myParent;
    private List<GameObject> myChildren;

    // the local transformation
    private double myRotation;
    private double myScale;
    private double[] myTranslation;
    
    // is this part of the tree showing?
    private boolean amShowing;

    /**
     * Special private constructor for creating the root node. Do not use otherwise.
     */
    private GameObject() {
        myParent = null;
        myChildren = new ArrayList<GameObject>();

        myRotation = 0;
        myScale = 1;
        myTranslation = new double[2];
        myTranslation[0] = 0;
        myTranslation[1] = 0;

        amShowing = true;
        
        ALL_OBJECTS.add(this);
    }

    /**
     * Public constructor for creating GameObjects, connected to a parent (possibly the ROOT).
     *  
     * New objects are created at the same location, orientation and scale as the parent.
     *
     * @param parent
     */
    public GameObject(GameObject parent) {
        myParent = parent;
        myChildren = new ArrayList<GameObject>();

        parent.myChildren.add(this);

        myRotation = 0;
        myScale = 1;
        myTranslation = new double[2];
        myTranslation[0] = 0;
        myTranslation[1] = 0;

        // initially showing
        amShowing = true;

        ALL_OBJECTS.add(this);
    }

    /**
     * Remove an object and all its children from the scene tree.
     */
    public void destroy() {
        for (GameObject child : myChildren) {
            child.destroy();
        }
        
        myParent.myChildren.remove(this);
        ALL_OBJECTS.remove(this);
    }

    /**
     * Get the parent of this game object
     * 
     * @return
     */
    public GameObject getParent() {
        return myParent;
    }

    /**
     * Get the children of this object
     * 
     * @return
     */
    public List<GameObject> getChildren() {
        return myChildren;
    }

    /**
     * Get the local rotation (in degrees)
     * 
     * @return
     */
    public double getRotation() {
        return myRotation;
    }

    /**
     * Set the local rotation (in degrees)
     * 
     * @return
     */
    public void setRotation(double rotation) {
        myRotation = rotation;
    }

    /**
     * Rotate the object by the given angle (in degrees)
     * 
     * @param angle
     */
    public void rotate(double angle) {
        myRotation += angle;
    }

    /**
     * Get the local scale
     * 
     * @return
     */
    public double getScale() {
        return myScale;
    }

    /**
     * Set the local scale
     * 
     * @param scale
     */
    public void setScale(double scale) {
        myScale = scale;
    }

    /**
     * Multiply the scale of the object by the given factor
     * 
     * @param factor
     */
    public void scale(double factor) {
        myScale *= factor;
    }

    /**
     * Get the local position of the object 
     * 
     * @return
     */
    public double[] getPosition() {
        double[] t = new double[2];
        t[0] = myTranslation[0];
        t[1] = myTranslation[1];

        return t;
    }

    /**
     * Set the local position of the object
     * 
     * @param x
     * @param y
     */
    public void setPosition(double x, double y) {
        myTranslation[0] = x;
        myTranslation[1] = y;
    }

    /**
     * Move the object by the specified offset in local coordinates
     * 
     * @param dx
     * @param dy
     */
    public void translate(double dx, double dy) {
        myTranslation[0] += dx;
        myTranslation[1] += dy;
    }

    /**
     * Test if the object is visible
     * 
     * @return
     */
    public boolean isShowing() {
        return amShowing;
    }

    /**
     * Set the showing flag to make the object visible (true) or invisible (false).
     * This flag should also apply to all descendents of this object.
     * 
     * @param showing
     */
    public void show(boolean showing) {
        amShowing = showing;
    }

    /**
     * Update the object. This method is called once per frame. 
     * 
     * This does nothing in the base GameObject class. Override this in subclasses.
     * 
     * @param dt The amount of time since the last update (in seconds)
     */
    public void update(double dt) {
        // do nothing
    }

    /**
     * Draw the object (but not any descendants)
     * 
     * This does nothing in the base GameObject class. Override this in subclasses.
     * 
     * @param gl
     */
    public void drawSelf(GL2 gl) {
        // do nothing
    }

    
    // ===========================================
    // COMPLETE THE METHODS BELOW
    // ===========================================
    
    /**
     * Draw the object and all of its descendants recursively.
     * 
     * TODO: Complete this method
     * 
     * @param gl
     */
    public void draw(GL2 gl) {
        
        // don't draw if it is not showing
        if (!amShowing) {
            return;
        }

        // TODO: draw the object and all its children recursively
        // setting the model transform appropriately 
    
        // Call drawSelf() to draw the object itself
        System.out.println("Drawing");
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        //gl.glColor3d(0.0, 1.0, 0.0);
        
        gl.glPushMatrix();
        {
            gl.glTranslated(myTranslation[0], myTranslation[1], 0);
            gl.glRotated(myRotation, 0, 0, 1.0);
            gl.glScaled(myScale, myScale, 1.0);
            //drawSelf(gl);   
            for(GameObject o : myChildren) {
            	System.out.println("GameObject is");
            	o.print();
            	o.draw(gl);
            }
            drawSelf(gl);
        }
        gl.glPopMatrix();        
    }

    /**
     * Compute the object's position in world coordinates
     * 
     * TODO: Write this method
     * 
     * @return a point in world coordinats in [x,y] form
     */
    public double[] getGlobalPosition() {
    	double[] d  = new double[2];
    	//System.out.println("d is "+ d[0]+" "+d[1]);
    	GameObject p = this.getParent();
    	if(p == GameObject.ROOT){        	
        	d[0] = myTranslation[0];
        	d[1] = myTranslation[1];
        	//System.out.println("d is "+ d[0]+" "+d[1]);
        	//Equals to d = multiply(TranslationMatrix * IdentityMatrix)
    	}
    	while( p != GameObject.ROOT){
    		//System.out.println("flag9");
    		/*
    		double[] f = new double[3]; //father's position
    		f[0] = p.getPosition()[0];
    		f[1] = p.getPosition()[1];
    		f[2] = 1; 		
    		
    		double[] a = null;
    		a = MathUtil.multiply(MathUtil.scaleMatrix(this.getScale()), f);			//result * Scale
    		a = MathUtil.multiply(MathUtil.rotationMatrix(this.getRotation()), a);		//result * Rotation
    		a = MathUtil.multiply(MathUtil.translationMatrix(this.getPosition()), a);	//Origin(father's position) * Local Translation
    		//System.out.println("Number 1: a is "+ a[0]+" "+a[1]);
    		*/
    		double[] f = new double[3];
    		f[0] = getPosition()[0];
    		f[1] = getPosition()[1];
    		f[2] = 1;
    		double[] a = null;
    		a = MathUtil.multiply(MathUtil.scaleMatrix(p.getScale()),f);	
    		a = MathUtil.multiply(MathUtil.rotationMatrix(p.getRotation()),a);	
    		a = MathUtil.multiply(MathUtil.translationMatrix(p.getPosition()),a);	
    		
    		
        	d[0] = a[0];
        	d[1] = a[1];
        		
    		//System.out.println("Number 2: d is "+ d[0]+" "+d[1]);
    		p = p.getParent();
    	}
    	return d; 
    }

    /**
     * Compute the object's rotation in the global coordinate frame
     * 
     * TODO: Write this method
     * 
     * @return the global rotation of the object (in degrees) 
     */
    public double getGlobalRotation() {
    	double degree = this.myRotation;
    	GameObject p = this.getParent();
    	while(p != null){
    		degree += p.getGlobalRotation();
    		p = p.getParent();    		
    	}    	
        return degree;
    }

    /**
     * Compte the object's scale in global terms
     * 
     * TODO: Write this method
     * 
     * @return the global scale of the object 
     */
    public double getGlobalScale() {
    	
    	double scale = this.myScale;
    	GameObject p = this.getParent();
    	while(p != null){
    		scale *= p.getGlobalScale();
    		p = p.getParent();
    	}
        return scale;
    }

    /**
     * Change the parent of a game object.
     * 
     * TODO: add code so that the object does not change its global position, rotation or scale
     * when it is reparented. 
     * 
     * @param parent
     */
    public void setParent(GameObject parent) {
    	
    	double gr = this.getGlobalRotation();
    	double gs = this.getGlobalScale();
    	double[] gp = new double[2];
    	gp[0] = this.getGlobalPosition()[0];
    	gp[1] = this.getGlobalPosition()[1];
    	System.out.println("@Before All setParent: ");
        this.print();
        
        
        myParent.myChildren.remove(this);
        myParent = parent;
        myParent.myChildren.add(this);
        //System.out.println("@setParent: Global Rotation is "+this.getGlobalRotation());
       // System.out.println("@setParent: Rotation is "+this.myRotation);  
        this.setRotation(gr - myParent.getRotation());
        this.setScale(gs/myParent.getScale());
        double[] GlobalUnitDistance = new double[3]; 
        GlobalUnitDistance[0] =  myParent.getGlobalPosition()[0] - gp[0];
        GlobalUnitDistance[1] =  myParent.getGlobalPosition()[1] - gp[1];
        GlobalUnitDistance[2] = 1;
        //this.setPosition(GlobalUnitDistance[0]/myParent.getScale(), GlobalUnitDistance[1]/myParent.getScale());
        double[] result = new double [3];
        result = MathUtil.multiply(MathUtil.rotationMatrix(myParent.getGlobalRotation()), GlobalUnitDistance);
        result = MathUtil.multiply(MathUtil.scaleMatrix(1/myParent.getGlobalScale()), result);
        //result = MathUtil.multiply(MathUtil.rotationMatrix(myParent.getGlobalRotation()), result);
        
        this.setPosition(result[0], result[1]);
        
        
         
    }
    
    public void print(){
    	System.out.println("PrintObject_Global: "+this.getGlobalPosition()[0]
    			+" "+this.getGlobalPosition()[1]+" "+this.getGlobalRotation()+" "+this.getGlobalScale());
    	System.out.println("PrintObject_Local: "+ this.getPosition()[0]
    			+" "+this.getPosition()[1]+" "+this.getRotation()+" "+this.getScale());
    }
    

}
