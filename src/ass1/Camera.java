package ass1;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

/**
 * The camera is a GameObject that can be moved, rotated and scaled like any other.
 * 
 * TODO: You need to implment the setView() and reshape() methods.
 *       The methods you need to complete are at the bottom of the class
 *
 * @author malcolmr
 */
public class Camera extends GameObject {

    private float[] myBackground;

    public Camera(GameObject parent) {
        super(parent);

        myBackground = new float[4];
    }

    public Camera() {
        this(GameObject.ROOT);
    }
    
    public float[] getBackground() {
        return myBackground;
    }

    public void setBackground(float[] background) {
        myBackground = background;
    }

    // ===========================================
    // COMPLETE THE METHODS BELOW
    // ===========================================
   
    
    public void setView(GL2 gl) {
        
        // TODO 1. clear the view to the background colour
    	//gl.glClearColor(1.0f,1.0f,1.0f,0.0f);
    	gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
    	gl.glClearColor(getBackground()[0],getBackground()[1],getBackground()[2],getBackground()[3]);
        //gl.glClearColor(myBackground[0], myBackground[1], myBackground[2], myBackground[3]);
        // TODO 2. set the view matrix to account for the camera's position  
        gl.glScaled(1 , 1 , 1);
        gl.glRotated(0, 0, 0, 1);
        gl.glTranslated(0, 0, 0);
    }

    public void reshape(GL2 gl, int x, int y, int width, int height) {
        // TODO  1. match the projection aspect ratio to the viewport
        // to avoid stretching
    	
    	//myCamera.setAspect(1.0 * width / height);
    	Double aspect = 1.0 * width / height;
    	
    	
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        
        // coordinate system (left, right, bottom, top)
        GLU glu = new GLU();
        if(width>height){
        	glu.gluOrtho2D(-20*aspect, 20*aspect, -20, 20);
        } else {
        	glu.gluOrtho2D(-20, 20, -20/aspect, 20/aspect);
        }
    }
}
