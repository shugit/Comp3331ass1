package ass1;

import java.awt.GridLayout;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JFrame;

import com.jogamp.opengl.util.FPSAnimator;

public class Runner {
    public static void main(String[] args) {
        // Initialise OpenGL
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);

        // Create a panel to draw on
        GLJPanel panel1 = new GLJPanel(caps);

        final JFrame jframe = new JFrame("Model Transform Demo");        
        jframe.setSize(400, 400);
        jframe.setLayout(new GridLayout(1,2));
        jframe.add(panel1);
        jframe.setVisible(true);

        // Catch window closing events and quit             
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // add a GL Event listener to handle rendering
        Camera c = new Camera(GameObject.ROOT);
        double[] color = {1,0,0}; //red
        double[] color2 = {0,1,0}; //green
        double[] points = {0,0,4,4,3,3,-15,10,0,-13,7,16};
        float[] colorf = {1.0f,1.0f,0.0f};
        c.setBackground(colorf);
        GameEngine ge = new GameEngine(new Camera());
        GameObject go = new GameObject(GameObject.ROOT);
        PolygonalGameObject o = new PolygonalGameObject(go, points, color, color2);
        go.rotate(30);
        go.translate(1.2, 3.4);
        go.scale(1);
        panel1.addMouseListener(Mouse.theMouse);
        panel1.addGLEventListener(ge);
        panel1.setFocusable(true);
                
        // add an Animator
        FPSAnimator animator = new FPSAnimator(60);
        animator.add(panel1);
        animator.start();
    }
}
