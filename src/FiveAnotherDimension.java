import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import glutils.MathUtils;
import glutils.ShaderInterface;
import glutils.ShaderLoader;
import windowtemplate.ApplicationLogic;
import windowtemplate.Window;

public class FiveAnotherDimension extends Window implements ApplicationLogic {

	private final int BYTES_PER_FLOAT = 4;
	private ShaderLoader shaderLoader;
	private final float vertices[]={  // front
		    -1.0f, -1.0f,  1.0f,
		     1.0f, -1.0f,  1.0f,
		     1.0f,  1.0f,  1.0f,
		    -1.0f,  1.0f,  1.0f,
		    // back
		    -1.0f, -1.0f, -1.0f,
		     1.0f, -1.0f, -1.0f,
		     1.0f,  1.0f, -1.0f,
		    -1.0f,  1.0f, -1.0f};
	
	private final float colors[] = {
		    // front colors
		    1.0f, 0.0f, 0.0f,
		    0.0f, 1.0f, 0.0f,
		    0.0f, 0.0f, 1.0f,
		    1.0f, 1.0f, 1.0f,
		    // back colors
		    1.0f, 0.0f, 0.0f,
		    0.0f, 1.0f, 0.0f,
		    0.0f, 0.0f, 1.0f,
		    1.0f, 1.0f, 1.0f
		  };
	private final short elements[] = {
		    // front
		    0, 1, 2,
		    2, 3, 0,
		    // top
		    1, 5, 6,
		    6, 2, 1,
		    // back
		    7, 6, 5,
		    5, 4, 7,
		    // bottom
		    4, 0, 3,
		    3, 7, 4,
		    // left
		    4, 5, 1,
		    1, 0, 4,
		    // right
		    3, 2, 6,
		    6, 7, 3,
		  };
	private int programID;

	private int positionHandle;
	private int colorHandle;

	private int transformationHandle;
	private FloatBuffer matrixFloatBuffer;
	private int verticesBufferObject;
	private int colorsBufferObject;
	private int elementsBufferObject;
	private Matrix4f modelMatrix;
	private Matrix4f viewMatrix;
	private Matrix4f projectionMatrix;
	private Vector3f modelPos;
	private Vector3f cameraPos;
	
	private int modelMHandle;
	private int projectionMHandle;
	private int viewMHandle;
	private float rotation;
	private FloatBuffer secondmatrixFloatBuffer;
	private Vector3f modelPos2;

	public FiveAnotherDimension() {
		super("Triangle", 500, 400);

		setApplicationLogic(this);
		super.startWindow();
	}

	@Override
	public void init() {
		initShaders();
		initBuffers();

		glEnable(GL_BLEND);
		glEnable(GL_DEPTH_TEST);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

	}

	private void initBuffers() {
		FloatBuffer verticesFloatBuffer = BufferUtils
				.createFloatBuffer(vertices.length);
		verticesFloatBuffer.put(vertices);
		verticesFloatBuffer.flip();
		
		FloatBuffer colorsFloatBuffer = BufferUtils
				.createFloatBuffer(colors.length);
		colorsFloatBuffer.put(colors);
		colorsFloatBuffer.flip();
		
		ShortBuffer elementsShortBuffer = BufferUtils
				.createShortBuffer(elements.length);
		 elementsShortBuffer.put(elements);
		 elementsShortBuffer.flip();

		 
		verticesBufferObject=glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER,verticesBufferObject);
		glBufferData(GL_ARRAY_BUFFER, verticesFloatBuffer, GL_STREAM_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		colorsBufferObject=glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER,colorsBufferObject);
		glBufferData(GL_ARRAY_BUFFER, colorsFloatBuffer, GL_STREAM_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		elementsBufferObject=glGenBuffers();//index buffer object
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,elementsBufferObject);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementsShortBuffer, GL_STREAM_DRAW);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);


		modelMatrix = new Matrix4f();
		viewMatrix=new Matrix4f();
		projectionMatrix=new Matrix4f();
		
		modelPos=new Vector3f(0.0f,2.0f,-5.0f);

		cameraPos=new Vector3f(0,0,0);
	
		modelMatrix.translate(modelPos);
		viewMatrix.translate(cameraPos);//set camera position
		projectionMatrix=MathUtils.setupPerspective(500, 400, 60.0f, 1.0f, 10.0f);
		
		
		matrixFloatBuffer = BufferUtils.createFloatBuffer(4 * 4);
		secondmatrixFloatBuffer = BufferUtils.createFloatBuffer(4 * 4);
		
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
		rotation+=0.01f;
		modelMatrix.rotate(rotation, new Vector3f(0,1,0));
		modelMatrix.rotate(rotation, new Vector3f(0,0,1));//rotate model by two axis
		
		drawCube();

		modelPos2=new Vector3f(-0.1f,-0.2f,-2.0f);	
		modelMatrix.setIdentity();
		modelMatrix.translate(modelPos2);
		modelMatrix.rotate(rotation, new Vector3f(0,1,0));
		modelMatrix.scale(new Vector3f(0.5f,0.5f,0.5f));
		drawCube();

		
		
		glUseProgram(0);
	}
	private void drawCube(){
	
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

	glBindBuffer(GL_ARRAY_BUFFER, verticesBufferObject);
	glVertexAttribPointer(positionHandle, 3, GL_FLOAT, false,
			0, 0);
	
	glBindBuffer(GL_ARRAY_BUFFER, colorsBufferObject);
	glVertexAttribPointer(colorHandle, 3, GL_FLOAT, false,
			0, 0);
	
	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementsBufferObject);
	glDrawElements(GL_TRIANGLES, elements.length,GL_UNSIGNED_SHORT,0);
	
	
	glDrawElements(GL_TRIANGLES, elements.length,GL_UNSIGNED_SHORT,0);
	
	
	
	// Reset
	glDisableVertexAttribArray(positionHandle);
	glDisableVertexAttribArray(colorHandle);
	}
	@Override
	public void shutdown() {

	}

	private void initShaders() {
		shaderLoader = new ShaderLoader("res/shaders/five/vertex.glsl",
				"res/shaders/five/fragment.glsl");
		this.programID = shaderLoader.getProgramID();

		positionHandle = glGetAttribLocation(programID, "in_Position");
		colorHandle = glGetAttribLocation(programID, "in_Color");
		glEnableVertexAttribArray(positionHandle);
		glEnableVertexAttribArray(colorHandle);

		modelMHandle = glGetUniformLocation(programID, "model");
		viewMHandle = glGetUniformLocation(programID, "view");
		projectionMHandle = glGetUniformLocation(programID, "projection");
		

	}

}
