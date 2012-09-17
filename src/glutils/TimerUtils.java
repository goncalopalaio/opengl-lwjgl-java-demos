package glutils;

import org.lwjgl.Sys;

public class TimerUtils {
	private long lastFrame;

	
	public TimerUtils() {

	}

	/**
	 * Calculate how many milliseconds have passed since last frame.
	 * 
	 * @return milliseconds passed since last frame
	 */
	public int getDelta() {
		long time = getTimeLWJGL();
		int delta = (int) (time - lastFrame);
		lastFrame = time;

		return delta;
	}
	/**
	 * Get the time in milliseconds
	 * 
	 * @return The system time in milliseconds
	 */
	public long getTimeLWJGL() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	/**
	 * Get the time in milliseconds (using java libs)
	 * 
	 * @return the system time in milliseconds
	 */
	 public long getTimeJAVA() {
	 return System.nanoTime() / 1000000; }
	 
}
