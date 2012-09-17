import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import glutils.ShaderInterface;
import glutils.ShaderLoader;
import windowtemplate.ApplicationLogic;
import windowtemplate.Window;

public class TwoPositionsPlusColor extends Window implements ApplicationLogic {

	private ShaderLoader shaderLoader;
	private final float vertexPositions[] = { 0.75f, 0.75f, 0.0f, 1.0f, 0.75f,
			-0.75f, 0.0f, 1.0f, -0.75f, -0.75f, 0.0f, 1.0f };
	private final float color[] = { 
			1.0f, 1.0f, 0.0f, 
			1.0f, 0.0f, 1.0f, 
			1.0f, 0.0f, 0.0f };
	private int positionBufferObject;
	private int programID;
	private int colorBufferObject;
	private int positionHandle;
	private int colorHandle;

	public TwoPositionsPlusColor() {
		super("Triangle", 500, 400);

		setApplicationLogic(this);
		super.startWindow();
	}

	@Override
	public void init() {
		initShaders();
		initVertexBuffer();

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

	}

	private void initVertexBuffer() {
		FloatBuffer vertexPositionsBuffer = BufferUtils
				.createFloatBuffer(vertexPositions.length);
		vertexPositionsBuffer.put(vertexPositions);
		vertexPositionsBuffer.flip();

		positionBufferObject = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, positionBufferObject);
		glBufferData(GL_ARRAY_BUFFER, vertexPositionsBuffer, GL_STREAM_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(color.length);
		colorBuffer.put(color);
		colorBuffer.flip();

		colorBufferObject = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, colorBufferObject);
		glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

	}

	@Override
	public void display() {
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT);

		glUseProgram(programID);

		

		glBindBuffer(GL_ARRAY_BUFFER, colorBufferObject);
		glVertexAttribPointer(colorHandle, 3, GL_FLOAT, false, 0, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER, positionBufferObject);
		glVertexAttribPointer(positionHandle, 4, GL_FLOAT, false, 0, 0);

		glDrawArrays(GL_TRIANGLES, 0, 3);
		
		
		glUseProgram(0);
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

	private void initShaders() {
		shaderLoader = new ShaderLoader("res/shaders/two/vertex.glsl",
				"res/shaders/two/fragment.glsl");
		this.programID = shaderLoader.getProgramID();
		
		positionHandle=glGetAttribLocation(programID, "in_Position");
		colorHandle=glGetAttribLocation(programID, "in_Color");
		glEnableVertexAttribArray(positionHandle);
		glEnableVertexAttribArray(colorHandle);
		
	}


}
