import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
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
import windowtemplate.ApplicationLogic;
import windowtemplate.Window;

public class OBJTest extends Window implements ApplicationLogic {

	private final int BYTES_PER_FLOAT = 4;
	private ShaderLoader shaderLoader;

	private int programID;

	private int positionHandle;
	private int colorHandle;
	private FloatBuffer matrixFloatBuffer;
	private int verticesBufferObject;
	private Matrix4f modelMatrix;
	private Matrix4f viewMatrix;
	private Matrix4f projectionMatrix;
	private Vector3f modelPos;
	private Vector3f cameraPos;

	private int modelMHandle;
	private int projectionMHandle;
	private int viewMHandle;
	private float rotation;

	private int textureID;
	private int textureHandle;

	private PNGLoader azulejo;
	private int normalHandle;
	private ByteBuffer verticesByteBuffer;
	private int vboId;
	private int texCoordsHandle;
	private OBJParser obj;
	String objFileName = "res/models/cube.obj";
	String texFileName = "res/wood.png";

	public OBJTest() {
		super("Triangle", 500, 400);
		obj = new OBJParser(objFileName);

		setApplicationLogic(this);
		super.startWindow();
	}

	@Override
	public void init() {
		initShaders();
		initBuffers();
		initTextures();

		glEnable(GL_BLEND);
		glEnable(GL_DEPTH_TEST);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

	}

	private void initTextures() {
		azulejo = new PNGLoader(texFileName);

		textureID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, azulejo.getWidth(),
				azulejo.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE,
				azulejo.getDataBuffer());

		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		glEnable(GL_TEXTURE_2D);
	}

	private void initBuffers() {

		verticesByteBuffer = BufferUtils.createByteBuffer(obj.getData().size()
				* OBJData.stride);
		FloatBuffer verticesFloatBuffer = verticesByteBuffer.asFloatBuffer();
		for (int i = 0; i < obj.getData().size(); i++) {
			// Add position, color and texture floats to the buffer
			verticesFloatBuffer.put(obj.getData().get(i).getElements());
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

		modelPos = new Vector3f(0.0f, 1.0f, -2.0f);

		cameraPos = new Vector3f(0, 0, 0);

		modelMatrix.translate(modelPos);
		viewMatrix.translate(cameraPos);// set camera position
		projectionMatrix = MathUtils.setupPerspective(500, 400, 60.0f, 1.0f,
				10.0f);

		matrixFloatBuffer = BufferUtils.createFloatBuffer(4 * 4);

		projectionMatrix.store(matrixFloatBuffer);
		matrixFloatBuffer.flip();

		rotation = 0.0f;

	}

	@Override
	public void display() {
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glUseProgram(programID);

		modelMatrix.setIdentity();
		modelMatrix.translate(modelPos);
		rotation += 0.01f;

		modelMatrix.rotate(rotation, new Vector3f(0, 1, 0));// rotate model by
															// two axis

		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, textureID);
		glUniform1i(textureHandle, 0);

		modelPos = new Vector3f(0.5f, -1.0f, -1.0f);
		drawCube();

		glUseProgram(0);
	}

	private void drawCube() {

		modelMatrix.store(matrixFloatBuffer);
		matrixFloatBuffer.flip();
		glUniformMatrix4(modelMHandle, false, matrixFloatBuffer);

		viewMatrix.store(matrixFloatBuffer);
		matrixFloatBuffer.flip();
		glUniformMatrix4(viewMHandle, false, matrixFloatBuffer);

		projectionMatrix.store(matrixFloatBuffer);
		matrixFloatBuffer.flip();
		glUniformMatrix4(projectionMHandle, false, matrixFloatBuffer);

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
		glDrawArrays(GL_TRIANGLES, 0, obj.getData().size());

		// Reset

		glDisableVertexAttribArray(colorHandle);
	}

	@Override
	public void shutdown() {
		glDeleteTextures(textureID);
	}

	private void initShaders() {
		
		 shaderLoader = new ShaderLoader("res/shaders/six/vertex.glsl",
		  "res/shaders/six/fragment.glsl");
		 


		this.programID = shaderLoader.getProgramID();

		positionHandle = glGetAttribLocation(programID, "in_Position");
		colorHandle = glGetAttribLocation(programID, "in_Color");
		texCoordsHandle = glGetAttribLocation(programID, "in_TexCoord");
		normalHandle = glGetAttribLocation(programID, "in_Normal");
		glEnableVertexAttribArray(positionHandle);
		glEnableVertexAttribArray(normalHandle);
		glEnableVertexAttribArray(colorHandle);
		glEnableVertexAttribArray(texCoordsHandle);

		modelMHandle = glGetUniformLocation(programID, "model");
		viewMHandle = glGetUniformLocation(programID, "view");
		projectionMHandle = glGetUniformLocation(programID, "projection");

		textureHandle = glGetUniformLocation(programID, "texture");

	}

}
