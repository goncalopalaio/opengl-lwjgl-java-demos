import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import glutils.ShaderInterface;
import glutils.ShaderLoader;
import windowtemplate.ApplicationLogic;
import windowtemplate.Window;

public class ThreeInterweavedCoordsAndColors extends Window implements
		ApplicationLogic {

	private final int BYTES_PER_FLOAT=4;
	private ShaderLoader shaderLoader;
	private final float data[] = { 
			  0.0f,  0.8f,   	1.0f, 1.0f, 0.0f,
			 -0.8f, -0.8f,   	0.0f, 0.0f, 1.0f,
			  0.8f, -0.8f,   	1.0f, 0.0f, 0.0f,
	};

	private int programID;

	private int positionHandle;
	private int colorHandle;
	private int interweavedBufferObject;

	public ThreeInterweavedCoordsAndColors() {
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
		FloatBuffer interweavedFloatBuffer = BufferUtils
				.createFloatBuffer(data.length);
		interweavedFloatBuffer.put(data);
		interweavedFloatBuffer.flip();

		interweavedBufferObject = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, interweavedBufferObject);
		glBufferData(GL_ARRAY_BUFFER, interweavedFloatBuffer, GL_STREAM_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

	}

	@Override
	public void display() {
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT);

		glUseProgram(programID);

		glBindBuffer(GL_ARRAY_BUFFER, interweavedBufferObject);
		glVertexAttribPointer(positionHandle, 2, GL_FLOAT, false, 5*BYTES_PER_FLOAT, 0);// new
																		// position
																		// shows
																		// up 5
																		// positions
																		// later, 0 is the offset of first element

		glVertexAttribPointer(colorHandle, 3, GL_FLOAT, false, 5*BYTES_PER_FLOAT, 2*BYTES_PER_FLOAT);

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

		positionHandle = glGetAttribLocation(programID, "in_Position");
		colorHandle = glGetAttribLocation(programID, "in_Color");
		glEnableVertexAttribArray(positionHandle);
		glEnableVertexAttribArray(colorHandle);

	}

}
