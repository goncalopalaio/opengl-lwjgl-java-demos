package windowtemplate;

import java.nio.channels.ShutdownChannelGroupException;

import javax.naming.InitialContext;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.*;

public class Window {

	private final String windowTitle;
	private int height;
	private int width;
	private int fps;
	private ApplicationLogic app;
	private long lastFPS;
	/** time at last frame */
	long lastFrame;
	
	public Window(String windowTitle, int width, int height) {
		this.windowTitle = windowTitle;
		this.width = width;
		this.height = height;
		fps = 60;
		lastFPS = getTime();
		// System.out.println(windowTitle);

	}

	private void updateFPS() {
		if (getTime() - lastFPS > 1000) {
			Display.setTitle("FPS: " + fps);
			fps = 0; // reset the FPS counter
			lastFPS += 1000; // add one second
		}
		fps++;
	}

	protected void setApplicationLogic(ApplicationLogic app) {
		this.app = app;
	}

	public void startWindow() {
		try {
			Display.setTitle(windowTitle);
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.setResizable(true);
			Display.create();
			// printGLVersion();

		} catch (LWJGLException e) {
			System.out.println("Could not create window");
			e.printStackTrace();
			System.exit(-1);
		}

		app.init();
		startRendering();
		app.shutdown();

	}

	public final void startRendering() {
		getDelta(); // call once before loop to initialise lastFrame
		lastFPS = getTime(); // call before loop to initialise fps timer
		while (!Display.isCloseRequested()) {
			int delta = getDelta();/*millseconds since last frmae*/

			app.display();
			Display.sync(fps);
			Display.update();
		}
	}

	private void printGLVersion() {
		System.out.println("GL42:" + GLContext.getCapabilities().OpenGL42);
		System.out.println("GL41:" + GLContext.getCapabilities().OpenGL41);
		System.out.println("GL40:" + GLContext.getCapabilities().OpenGL40);
		System.out.println("GL33:" + GLContext.getCapabilities().OpenGL33);
		System.out.println("GL32:" + GLContext.getCapabilities().OpenGL32);
		System.out.println("GL31:" + GLContext.getCapabilities().OpenGL31);
		System.out.println("GL30:" + GLContext.getCapabilities().OpenGL30);
		System.out.println("GL21:" + GLContext.getCapabilities().OpenGL21);
		System.out.println("GL20:" + GLContext.getCapabilities().OpenGL20);
		System.out.println("GL15:" + GLContext.getCapabilities().OpenGL15);
		System.out.println("GL14:" + GLContext.getCapabilities().OpenGL14);
		System.out.println("GL13:" + GLContext.getCapabilities().OpenGL13);
		System.out.println("GL12:" + GLContext.getCapabilities().OpenGL12);
		System.out.println("GL11:" + GLContext.getCapabilities().OpenGL11);
		System.out.println(GL11.glGetString(GL11.GL_VERSION));
	}

	/**
	 * Get the time in milliseconds
	 * 
	 * @return The system time in milliseconds
	 */
	public long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	/** 
	 * Calculate how many milliseconds have passed 
	 * since last frame.
	 * 
	 * @return milliseconds passed since last frame 
	 */
	public int getDelta() {
	    long time = getTime();
	    int delta = (int) (time - lastFrame);
	    lastFrame = time;
	 
	    return delta;
	}
	
}
