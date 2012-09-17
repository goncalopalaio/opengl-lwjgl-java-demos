import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import glutils.ShaderLoader;

public class LightPoint {
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

	public LightPoint() {

		System.out.println("Creating shader program");
		ShaderLoader shader = new ShaderLoader(vertexShaderLoc,
				fragmentShaderLoc);
		System.out.println("...done");

		programID = shader.getProgramID();
		init();
	}

	private void init() {
		positionHandle = glGetAttribLocation(programID, "in_Position");
		mvpHandle = glGetUniformLocation(programID, "mvp");

		mvpMatrix = new Matrix4f();
		modelMatrix = new Matrix4f();
		matrixFloatBuffer = BufferUtils.createFloatBuffer(4 * 4);
		positions = new float[] { 0, 0, 0 };

		vertexFloatBuffer = BufferUtils.createFloatBuffer(positions.length);
		vertexFloatBuffer.put(positions);
		vertexFloatBuffer.flip();
		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, vbo, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		glEnableVertexAttribArray(positionHandle);

	}

	public void render(Matrix4f view, Matrix4f projection) {

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

}
