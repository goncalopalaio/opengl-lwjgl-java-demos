import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import glutils.ShaderInterface;
import glutils.ShaderLoader;
import windowtemplate.ApplicationLogic;
import windowtemplate.Window;

public class FourMatricesAsUniforms extends Window implements ApplicationLogic {

	private final int BYTES_PER_FLOAT = 4;
	private ShaderLoader shaderLoader;
	private final float data[] = { 0.0f, 0.8f, 1.0f, 1.0f, 0.0f, -0.8f, -0.8f,
			0.0f, 0.0f, 1.0f, 0.8f, -0.8f, 1.0f, 0.0f, 0.0f, };

	private int programID;

	private int positionHandle;
	private int colorHandle;
	private int interweavedBufferObject;
	private int transformationHandle;
	private Matrix4f transformationMatrix;
	private FloatBuffer matrixFloatBuffer;

	public FourMatricesAsUniforms() {
		super("Triangle", 500, 400);

		setApplicationLogic(this);
		super.startWindow();
	}

	@Override
	public void init() {
		initShaders();
		initBuffers();

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

	}

	private void initBuffers() {
		FloatBuffer interweavedFloatBuffer = BufferUtils
				.createFloatBuffer(data.length);
		interweavedFloatBuffer.put(data);
		interweavedFloatBuffer.flip();

		interweavedBufferObject = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, interweavedBufferObject);
		glBufferData(GL_ARRAY_BUFFER, interweavedFloatBuffer, GL_STREAM_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		transformationMatrix = new Matrix4f();
		transformationMatrix.setIdentity();
		matrixFloatBuffer = BufferUtils.createFloatBuffer(4 * 4);
		transformationMatrix.store(matrixFloatBuffer);
		matrixFloatBuffer.flip();

	}
	float rotation=0;
	@Override
	public void display() {
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT);

		glUseProgram(programID);

		glEnableVertexAttribArray(positionHandle);
		glEnableVertexAttribArray(colorHandle);

		glBindBuffer(GL_ARRAY_BUFFER, interweavedBufferObject);
		// new position shows up 5 floats later, 0 is the offset of the first
		// element
		glVertexAttribPointer(positionHandle, 2, GL_FLOAT, false,
				5 * BYTES_PER_FLOAT, 0);
		glVertexAttribPointer(colorHandle, 3, GL_FLOAT, false,
				5 * BYTES_PER_FLOAT, 2 * BYTES_PER_FLOAT);
		
		rotation+=0.0001f % 2;
		transformationMatrix.rotate(rotation, new Vector3f(0, 0, 1));
		transformationMatrix.store(matrixFloatBuffer);
		matrixFloatBuffer.flip();
		glUniformMatrix4(transformationHandle, false, matrixFloatBuffer);

		glDrawArrays(GL_TRIANGLES, 0, 3);

		// Reset
		glDisableVertexAttribArray(positionHandle);
		glDisableVertexAttribArray(colorHandle);
		glUseProgram(0);
	}

	@Override
	public void shutdown() {

	}

	private void initShaders() {
		shaderLoader = new ShaderLoader("res/shaders/four/vertex.glsl",
				"res/shaders/four/fragment.glsl");
		this.programID = shaderLoader.getProgramID();

		positionHandle = glGetAttribLocation(programID, "in_Position");
		colorHandle = glGetAttribLocation(programID, "in_Color");
		glEnableVertexAttribArray(positionHandle);
		glEnableVertexAttribArray(colorHandle);

		transformationHandle = glGetUniformLocation(programID, "transformation");

	}

}
