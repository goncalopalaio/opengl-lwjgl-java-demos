import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import OBJLoader.OBJData;
import OBJLoader.OBJParser;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import glutils.MathUtils;
import glutils.PNGLoader;
import glutils.ShaderInterface;
import glutils.ShaderLoader;
import glutils.TimerUtils;
import windowtemplate.ApplicationLogic;
import windowtemplate.Window;

public class MultipleObjects extends Window implements ApplicationLogic {

	/* Shading */
	private ShaderLoader shaderLoader;
	private int programID;

	/* Handles */
	private int modelMHandle;
	private int positionHandle;
	private int colorHandle;
	private int projectionMHandle;
	private int viewMHandle;
	private int normalHandle;
	private int texCoordsHandle;
	/* Textures */
	private int textureID;
	private int textureHandle;
	PNGLoader textureData;

	/* Matrices */
	private Matrix4f modelMatrix;
	private Matrix4f viewMatrix;
	private Matrix4f projectionMatrix;
	/* Positions */
	private Vector3f modelPos;
	private Vector3f cameraPos;
	private float rotation;
	private Vector3f lightDir;

	/* Buffers */
	private ByteBuffer verticesByteBuffer;
	private FloatBuffer matrixFloatBuffer;
	private FloatBuffer matrix3FloatBuffer;
	private int verticesBufferObject;

	/* Models */
	private OBJParser object;

	/* Strings */
	String objFileName = "res/models/cube.obj";

	String texFileName = "res/azulejo.png";
	String vertexShaderFileLoc = "res/shaders/diffuse1.vert";
	String fragmentShaderFileLoc = "res/shaders/diffuse1.frag";
	private Vector3f modelAngle;
	private TimerUtils timer;
	private Matrix4f modelView;
	private int modelViewMHandle;
	private int normalMHandle;


	public MultipleObjects() {
		super("Triangle", 640, 480);
		object = new OBJParser(objFileName);
		setApplicationLogic(this);

		super.startWindow();
	}

	public void delete() {
		glDeleteTextures(textureID);
	}

	@Override
	public void display() {
		logic();
		drawing();
	}

	private void drawCube() {

		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, textureID);
		glUniform1i(textureHandle, 0);

		float time = (float) (timer.getTimeJAVA());

		glUniform1f(glGetUniformLocation(programID, "time"), time);
		glUniform3f(glGetUniformLocation(programID, "lightDir"), lightDir.x,
				lightDir.y, lightDir.z);

		
		glBindBuffer(GL_ARRAY_BUFFER, verticesBufferObject);
		glVertexAttribPointer(positionHandle, OBJData.positionElementCount,
				GL_FLOAT, false, OBJData.stride, OBJData.positionByteOffset);// new
		glVertexAttribPointer(normalHandle, OBJData.normalElementCount,
				GL_FLOAT, false, OBJData.stride, OBJData.normalByteOffset);// new
		glVertexAttribPointer(colorHandle, OBJData.colorElementCount, GL_FLOAT,
				false, OBJData.stride, OBJData.colorByteOffset);// new
		glVertexAttribPointer(texCoordsHandle, OBJData.textureElementCount,
				 GL_FLOAT, false, OBJData.stride, OBJData.textureByteOffset);// new
		// glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementsBufferObject);
		glDrawArrays(GL_TRIANGLES, 0, object.getData().size());

		// Reset

		// glDisableVertexAttribArray(colorHandle);
	}

	private void drawing() {

		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glUseProgram(programID);

		updateMatrices(modelPos);
		drawCube();
		updateMatrices(new Vector3f(0.5f, -1.0f, -3.0f));
		drawCube();
		glUseProgram(0);
		


	}

	public Matrix4f getModelMatrix() {
		return modelMatrix;
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public Matrix4f getViewMatrix() {
		return viewMatrix;
	}

	@Override
	public void init() {
		initShaders();
		initBuffers();
		initTextures();

		timer = new TimerUtils();
		lightDir = new Vector3f(1.0f, -5.0f, -5.0f);
		


		//glEnable(GL_BLEND);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		// glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );//reset with GL_FILL
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}

	private void initBuffers() {

		verticesByteBuffer = BufferUtils.createByteBuffer(object.getData()
				.size() * OBJData.stride);
		FloatBuffer verticesFloatBuffer = verticesByteBuffer.asFloatBuffer();
		for (int i = 0; i < object.getData().size(); i++) {
			// Add position, color and texture floats to the buffer
			verticesFloatBuffer.put(object.getData().get(i).getElements());
		}
		verticesFloatBuffer.flip();

		verticesBufferObject = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, verticesBufferObject);
		glBufferData(GL_ARRAY_BUFFER, verticesFloatBuffer, GL_STREAM_DRAW);

		glEnableVertexAttribArray(positionHandle);
		glEnableVertexAttribArray(colorHandle);
		glEnableVertexAttribArray(texCoordsHandle);
		glEnableVertexAttribArray(normalHandle);
		// Put the position coordinates in attribute list 0
		glVertexAttribPointer(positionHandle, OBJData.positionElementCount,
				GL11.GL_FLOAT, false, OBJData.stride,
				OBJData.positionByteOffset);
		// Put the color components in attribute list 1
		glVertexAttribPointer(colorHandle, OBJData.colorElementCount,
				GL11.GL_FLOAT, false, OBJData.stride, OBJData.colorByteOffset);
		// Put the texture coordinates in attribute list 2
		glVertexAttribPointer(texCoordsHandle, OBJData.textureElementCount,
				GL11.GL_FLOAT, false, OBJData.stride, OBJData.textureByteOffset);
		glVertexAttribPointer(normalHandle, OBJData.normalElementCount,
				GL11.GL_FLOAT, false, OBJData.stride, OBJData.normalByteOffset);

		modelMatrix = new Matrix4f();
		viewMatrix = new Matrix4f();
		projectionMatrix = new Matrix4f();
		modelAngle = new Vector3f(0, 0, 0);

		modelPos = new Vector3f(1.4f, -2.0f, -3.2f);

		cameraPos = new Vector3f(1.0f, 2.0f, 1.0f);

		modelMatrix.translate(modelPos);
		viewMatrix = MathUtils.setupView(cameraPos, new Vector3f(0.0f, 0.0f,
				1.0f), new Vector3f(0.0f, 1.0f, 0.0f));

		projectionMatrix = MathUtils.setupPerspective(500, 400, 60.0f, 1.0f,
				10.0f);

		matrixFloatBuffer = BufferUtils.createFloatBuffer(4 * 4);
		matrix3FloatBuffer = BufferUtils.createFloatBuffer(3 * 3);

		projectionMatrix.store(matrixFloatBuffer);
		matrixFloatBuffer.flip();

		rotation = 0.0f;

	}

	private void initShaders() {

		shaderLoader = new ShaderLoader(vertexShaderFileLoc,
				fragmentShaderFileLoc);
		this.programID = shaderLoader.getProgramID();

		positionHandle = glGetAttribLocation(programID, "in_Position");
		colorHandle = glGetAttribLocation(programID, "in_Color");
		texCoordsHandle = glGetAttribLocation(programID, "in_TexCoord");
		normalHandle = glGetAttribLocation(programID, "in_Normal");
		glEnableVertexAttribArray(positionHandle);
		glEnableVertexAttribArray(colorHandle);
		glEnableVertexAttribArray(texCoordsHandle);
		glEnableVertexAttribArray(normalHandle);

		modelMHandle = glGetUniformLocation(programID, "model");
		viewMHandle = glGetUniformLocation(programID, "view");
		projectionMHandle = glGetUniformLocation(programID, "projection");
		modelViewMHandle = glGetUniformLocation(programID, "modelview");
		normalMHandle = glGetUniformLocation(programID, "normalmatrix");

		textureHandle = glGetUniformLocation(programID, "texture");

	}

	private void initTextures() {
		textureData = new PNGLoader(texFileName);

		textureID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, textureData.getWidth(),
				textureData.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE,
				textureData.getDataBuffer());

		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		glEnable(GL_TEXTURE_2D);
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
			case Keyboard.KEY_UP:
				modelPos.z += posDelta;
				break;
			case Keyboard.KEY_DOWN:
				modelPos.z -= posDelta;
				break;
			// Scale
			// Rotation
			case Keyboard.KEY_LEFT:
				modelAngle.z += rotationDelta;
				break;
			case Keyboard.KEY_RIGHT:
				modelAngle.z -= rotationDelta;;
				break;
			case Keyboard.KEY_Q:
				modelAngle.y += rotationDelta;
				break;
			case Keyboard.KEY_W:
				modelAngle.y -= rotationDelta;
				break;
			case Keyboard.KEY_J:
				lightDir.y += lightDelta;
				break;
			case Keyboard.KEY_L:
				lightDir.y -= lightDelta;
				break;

			case Keyboard.KEY_K:
				lightDir.z += lightDelta;
				break;

			case Keyboard.KEY_I:
				lightDir.z -= lightDelta;
				break;
			}
			System.out.println(lightDir + " " + modelPos);
		}

	}

	private void logic() {
		keyboardEvents();

	}

	public void setModelMatrix(Matrix4f modelMatrix) {
		this.modelMatrix = modelMatrix;
	}

	public void setProjectionMatrix(Matrix4f projectionMatrix) {
		this.projectionMatrix = projectionMatrix;
	}

	public void setViewMatrix(Matrix4f viewMatrix) {
		this.viewMatrix = viewMatrix;
	}

	@Override
	public void shutdown() {

	}

	private void updateMatrices(Vector3f otherPosition) {
		viewMatrix = new Matrix4f();
		modelMatrix = new Matrix4f();
		modelView = new Matrix4f();

		// Translate camera
		Matrix4f.translate(cameraPos, viewMatrix, viewMatrix);

		// Scale, translate and rotate model

		Matrix4f.translate(otherPosition, modelMatrix, modelMatrix);
		Matrix4f.rotate(MathUtils.degreesToRadians(modelAngle.z), new Vector3f(
				0, 0, 1), modelMatrix, modelMatrix);
		Matrix4f.rotate(MathUtils.degreesToRadians(modelAngle.y), new Vector3f(
				0, 1, 0), modelMatrix, modelMatrix);
		Matrix4f.rotate(MathUtils.degreesToRadians(modelAngle.x), new Vector3f(
				1, 0, 0), modelMatrix, modelMatrix);

		// Matrix4f.scale(new Vector3f(0.1f,0.5f,0.5f), modelMatrix,
		// modelMatrix);
		// send projection matrix as uniform
		projectionMatrix.store(matrixFloatBuffer);
		matrixFloatBuffer.flip();
		glUniformMatrix4(projectionMHandle, false, matrixFloatBuffer);

		// send view matrix as uniform
		viewMatrix.store(matrixFloatBuffer);
		matrixFloatBuffer.flip();
		glUniformMatrix4(viewMHandle, false, matrixFloatBuffer);

		// send model matrix as uniform
		modelMatrix.store(matrixFloatBuffer);
		matrixFloatBuffer.flip();
		glUniformMatrix4(modelMHandle, false, matrixFloatBuffer);

		// System.out.println("ViewMatrix:\n"+viewMatrix+"\nModel:\n"+modelMatrix);
		// Calculate modelview and NormalMatrix
		Matrix4f.mul(viewMatrix, modelMatrix, modelView);
		Matrix3f normalMatrix = MathUtils.getNormalMatrix(modelView);

		// send modelview matrix as uniform
		modelView.store(matrixFloatBuffer);
		matrixFloatBuffer.flip();
		glUniformMatrix4(modelViewMHandle, false, matrixFloatBuffer);

		// send normal matrix as uniform
		normalMatrix.store(matrix3FloatBuffer);
		matrix3FloatBuffer.flip();
		glUniformMatrix4(normalMHandle, false, matrix3FloatBuffer);

	}

}
