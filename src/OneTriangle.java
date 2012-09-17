import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;


import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;


import glutils.ShaderInterface;
import glutils.ShaderLoader;
import windowtemplate.ApplicationLogic;
import windowtemplate.Window;

public class OneTriangle extends Window implements ApplicationLogic,
		ShaderInterface {

	private int projectionMatrixLocation;
	private int viewMatrixLocation;
	private int modelMatrixLocation;
	private ShaderLoader shaderLoader;
	private final float vertexPositions[] = {
			0.75f,  0.75f, 0.0f, 1.0f,
			0.75f, -0.75f, 0.0f, 1.0f,
		   -0.75f, -0.75f, 0.0f, 1.0f};
	private int positionBufferObject;
	private int  vao;
	private int programID;

		
	public OneTriangle() {
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
		FloatBuffer vertexPositionsBuffer = BufferUtils.createFloatBuffer(vertexPositions.length);
		vertexPositionsBuffer.put(vertexPositions);
		vertexPositionsBuffer.flip();
		
        
		positionBufferObject = glGenBuffers();	       
		glBindBuffer(GL_ARRAY_BUFFER, positionBufferObject);
	    glBufferData(GL_ARRAY_BUFFER, vertexPositionsBuffer, GL_STREAM_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	@Override
	public void display() {
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT);

		glUseProgram(programID);

		glBindBuffer(GL_ARRAY_BUFFER, positionBufferObject);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, 4, GL_FLOAT, false, 0, 0);

		glDrawArrays(GL_TRIANGLES, 0, 3);

		glDisableVertexAttribArray(0);
		glUseProgram(0);
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

	private void initShaders() {
		shaderLoader = new ShaderLoader("res/shaders/one/vertex.glsl",
				"res/shaders/one/fragment.glsl", this);
		this.programID=shaderLoader.getProgramID();
	}

	@Override
	public void setShaderVarsLocation(int pId) {

		// Position information will be attribute 0
		glBindAttribLocation(pId, 0, "in_Position");
		// Color information will be attribute 1
		glBindAttribLocation(pId, 1, "in_Color");
		// Textute information will be attribute 2
		glBindAttribLocation(pId, 2, "in_TextureCoord");
		// Get matrices uniform locations
		projectionMatrixLocation = glGetUniformLocation(pId,
				"projectionMatrix");
		viewMatrixLocation = glGetUniformLocation(pId, "viewMatrix");
		modelMatrixLocation =glGetUniformLocation(pId, "modelMatrix");

	}

}
