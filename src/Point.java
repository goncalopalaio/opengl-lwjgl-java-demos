import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import glutils.MathUtils;
import glutils.ShaderLoader;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import windowtemplate.ApplicationLogic;
import windowtemplate.Window;

public class Point extends Window implements ApplicationLogic {
	public Point(){
		super("Triangle", 640, 480);
		setApplicationLogic(this);
		super.startWindow();
		// TODO Auto-generated constructor stub
	}

	private final String vertexShaderLoc = "res/shaders/light.vert";
	private final String fragmentShaderLoc = "res/shaders/light.frag";
	private int vbo;
	private Matrix4f modelMatrix;
	private int programID;
	private int positionHandle;
	private int mvpHandle;
	private Vector3f lightPosition;
	private Matrix4f mvpMatrix;
	private FloatBuffer matrixFloatBuffer;
	private FloatBuffer vertexFloatBuffer;
	private float positions[];
	private Matrix4f view;
	private Matrix4f projection;
	@Override
	public void init() {
	
		System.out.println("Creating shader program");
		ShaderLoader shader = new ShaderLoader(vertexShaderLoc,
				fragmentShaderLoc);
		System.out.println("...done");

		programID = shader.getProgramID();
		
		positionHandle = glGetAttribLocation(programID, "in_Position");
		mvpHandle = glGetUniformLocation(programID, "mvp");

		mvpMatrix = new Matrix4f();
		modelMatrix = new Matrix4f();
		view=new Matrix4f();
		projection=new Matrix4f();
		view = MathUtils.setupView(new Vector3f(0,0,0), new Vector3f(0.0f, 0.0f,
				-1.0f), new Vector3f(0.0f, 1.0f, 0.0f));

		projection= MathUtils.setupPerspective(500, 400, 60.0f, 1.0f,
				10.0f);

		
		matrixFloatBuffer = BufferUtils.createFloatBuffer(4 * 4);
		positions = new float[] { 0, 0, 0 };

		lightPosition=new Vector3f(0,0,0.2f);
		
		
		vertexFloatBuffer = BufferUtils.createFloatBuffer(positions.length);
		vertexFloatBuffer.put(positions);
		vertexFloatBuffer.flip();
		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, vbo, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		glEnableVertexAttribArray(positionHandle);
		
	}

	@Override
	public void display() {
		keyboardEvents();
		glUseProgram(programID);

		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glEnableVertexAttribArray(positionHandle);
		glVertexAttribPointer(positionHandle, 3, GL_FLOAT, false, 0, 0);
		

		modelMatrix.translate(lightPosition);
		
		Matrix4f.mul(view, modelMatrix, mvpMatrix);
		Matrix4f.mul(projection, mvpMatrix, mvpMatrix);
		mvpMatrix.store(matrixFloatBuffer);
		matrixFloatBuffer.flip();
		glUniformMatrix4(mvpHandle, false, matrixFloatBuffer);

		glDrawArrays(GL_POINTS, 0, 1);
		glUseProgram(0);		
	}
	private void keyboardEvents() {
		float rotationDelta = 15f;
		float lightDelta = 0.5f;
		float posDelta = 1.2f;
		while (Keyboard.next()) {
			// Only listen to events where the key was pressed (down event)
			if (!Keyboard.getEventKeyState())
				continue;

			// Change model scale, rotation and translation values
			switch (Keyboard.getEventKey()) {
			// Move
			case Keyboard.KEY_W:
				lightPosition.z += posDelta;
				break;
			case Keyboard.KEY_S:
				lightPosition.z -= posDelta;
				break;
			// Scale
			// Rotation
			case Keyboard.KEY_A:
				lightPosition.x += posDelta;
				break;
			case Keyboard.KEY_D:
				lightPosition.x -= posDelta;;
				break;
			case Keyboard.KEY_Q:
				lightPosition.y += posDelta;
				break;
			case Keyboard.KEY_E:
				lightPosition.y -= posDelta;;
				break;
			
			}
			System.out.println("LightPosition "+lightPosition);
		}

	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

}
