import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;



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

public class SixTextures extends Window implements ApplicationLogic {

	private final int BYTES_PER_FLOAT = 4;
	private ShaderLoader shaderLoader;
	private final float vertices[] = {
		//   x      y      z      		nx     ny     nz     r      g      b      a			u		v
		    // back quad
		         1.0f,  1.0f,  1.0f,  0.0f,  0.0f,  1.0f,  1.0f,  0.0f,  0.0f,  1.0f,
		        -1.0f,  1.0f,  1.0f,  0.0f,  0.0f,  1.0f,  1.0f,  0.0f,  0.0f,  1.0f,
		        -1.0f, -1.0f,  1.0f,  0.0f,  0.0f,  1.0f,  1.0f,  0.0f,  0.0f,  1.0f,
		         1.0f, -1.0f,  1.0f,  0.0f,  0.0f,  1.0f,  1.0f,  0.0f,  0.0f,  1.0f,

		    // front quad
		         1.0f,  1.0f, -1.0f,  0.0f,  0.0f, -1.0f,  0.0f,  1.0f,  0.0f,  1.0f,
		        -1.0f,  1.0f, -1.0f,  0.0f,  0.0f, -1.0f,  0.0f,  1.0f,  0.0f,  1.0f,
		        -1.0f, -1.0f, -1.0f,  0.0f,  0.0f, -1.0f,  0.0f,  1.0f,  0.0f,  1.0f,
		         1.0f, -1.0f, -1.0f,  0.0f,  0.0f, -1.0f,  0.0f,  1.0f,  0.0f,  1.0f,

		    // left quad
		        -1.0f,  1.0f, -1.0f, -1.0f,  0.0f,  0.0f,  0.0f,  0.0f,  1.0f,  1.0f,
		        -1.0f,  1.0f,  1.0f, -1.0f,  0.0f,  0.0f,  0.0f,  0.0f,  1.0f,  1.0f,
		        -1.0f, -1.0f,  1.0f, -1.0f,  0.0f,  0.0f,  0.0f,  0.0f,  1.0f,  1.0f,
		        -1.0f, -1.0f, -1.0f, -1.0f,  0.0f,  0.0f,  0.0f,  0.0f,  1.0f,  1.0f,
		        
		    // right quad
		         1.0f,  1.0f, -1.0f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,  1.0f,  1.0f,
		         1.0f,  1.0f,  1.0f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,  1.0f,  1.0f,
		         1.0f, -1.0f,  1.0f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,  1.0f,  1.0f,
		         1.0f, -1.0f, -1.0f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,  1.0f,  1.0f,

		    // top quad
		        -1.0f,  1.0f, -1.0f,  0.0f,  1.0f,  0.0f,  1.0f,  1.0f,  0.0f,  1.0f,
		        -1.0f,  1.0f,  1.0f,  0.0f,  1.0f,  0.0f,  1.0f,  1.0f,  0.0f,  1.0f,
		         1.0f,  1.0f,  1.0f,  0.0f,  1.0f,  0.0f,  1.0f,  1.0f,  0.0f,  1.0f,
		         1.0f,  1.0f, -1.0f,  0.0f,  1.0f,  0.0f,  1.0f,  1.0f,  0.0f,  1.0f,

		    // bottom quad
		        -1.0f, -1.0f, -1.0f,  0.0f, -1.0f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,
		        -1.0f, -1.0f,  1.0f,  0.0f, -1.0f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,
		         1.0f, -1.0f,  1.0f,  0.0f, -1.0f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,
		         1.0f, -1.0f, -1.0f,  0.0f, -1.0f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,
	};



	private int programID;

	private int positionHandle;
	private int colorHandle;

	private int transformationHandle;
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

	private int textureCoordsBufferObject;
	private int texCoordsHandle;
	private PNGLoader azulejo;
	private int normalHandle;

	public SixTextures() {
		super("Triangle", 500, 400);

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
		azulejo = new PNGLoader("res/azulejo.png");

		textureID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, azulejo.getWidth(),azulejo.getHeight(), 0,GL_RGBA, GL_UNSIGNED_BYTE, azulejo.getDataBuffer());

		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		glEnable(GL_TEXTURE_2D);
	}

	private void initBuffers() {
		FloatBuffer verticesFloatBuffer = BufferUtils
				.createFloatBuffer(vertices.length);
		verticesFloatBuffer.put(vertices);
		verticesFloatBuffer.flip();
		

		verticesBufferObject = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, verticesBufferObject);
		glBufferData(GL_ARRAY_BUFFER, verticesFloatBuffer, GL_STREAM_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	

		modelMatrix = new Matrix4f();
		viewMatrix = new Matrix4f();
		projectionMatrix = new Matrix4f();

		modelPos = new Vector3f(0.0f, 2.0f, -5.0f);

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
		modelMatrix.rotate(rotation, new Vector3f(0, 1, 0));
		modelMatrix.rotate(rotation, new Vector3f(0, 0, 1));// rotate model by
															// two axis

		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, textureID);
		glUniform1i(textureHandle, 0);
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

		glEnableVertexAttribArray(positionHandle);
		glEnableVertexAttribArray(colorHandle);
		glEnableVertexAttribArray(texCoordsHandle);
		
		glBindBuffer(GL_ARRAY_BUFFER, verticesBufferObject);
		glVertexAttribPointer(positionHandle, 3, GL_FLOAT, false, 10*BYTES_PER_FLOAT, 0);// new
		glVertexAttribPointer(normalHandle, 3, GL_FLOAT, false, 10*BYTES_PER_FLOAT, 3*BYTES_PER_FLOAT);// new
		glVertexAttribPointer(colorHandle, 4, GL_FLOAT, false, 10*BYTES_PER_FLOAT, 6*BYTES_PER_FLOAT);// new
	
		//glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementsBufferObject);
		glDrawArrays(GL_TRIANGLES, 0, vertices.length/10);

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
		glEnableVertexAttribArray(colorHandle);
		glEnableVertexAttribArray(texCoordsHandle);

		modelMHandle = glGetUniformLocation(programID, "model");
		viewMHandle = glGetUniformLocation(programID, "view");
		projectionMHandle = glGetUniformLocation(programID, "projection");

		textureHandle = glGetUniformLocation(programID, "texture");

	}

}
