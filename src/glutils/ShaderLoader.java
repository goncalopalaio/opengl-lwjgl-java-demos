package glutils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.glu.GLU;

public class ShaderLoader {
	private ShaderInterface variablesSetup;
	private int programID;

	public ShaderLoader(String vertexShaderFileLoc,
			String fragmentShaderFileLoc, ShaderInterface variablesSetup) {
		setVariablesSetup(variablesSetup);
		createProgram(vertexShaderFileLoc, fragmentShaderFileLoc);
	}

	/**
	 * Generates shader program from two files
	 * */
	public ShaderLoader(String vertexShaderFileLoc, String fragmentShaderFileLoc) {
		setVariablesSetup(null);
		createProgramFromFile(vertexShaderFileLoc, fragmentShaderFileLoc);
	}

	/**
	 * Generates shader program from two strings
	 * */
	public ShaderLoader(String vertexAndFragmentStrings[]) {
		setVariablesSetup(null);
		createProgramFromString(vertexAndFragmentStrings[0],
				vertexAndFragmentStrings[1]);
	}

	private int createProgramFromFile(String vertexShaderFileLoc,
			String fragmentShaderFileLoc) {

		String vertexShaderString = getStringFromFile(vertexShaderFileLoc);
		String fragmentShaderString = getStringFromFile(fragmentShaderFileLoc);
		System.out.println(vertexShaderString + "\n"+fragmentShaderString);
		return createProgram(vertexShaderString, fragmentShaderString);

	}

	private int createProgramFromString(String vertexShaderString,
			String fragmentShaderString) {

		return createProgram(vertexShaderString, fragmentShaderString);

	}

	private int createProgram(String vertexShaderString,
			String fragmentShaderString) {
		int pId = 0; // shader program id
		// Load the vertex shader
		System.out.println("Loading vertex Shader..");
		int vsId = createShaderFromString(vertexShaderString,
				GL20.GL_VERTEX_SHADER);
		System.out.println("...done");
		System.out.println("Loading fragment Shader..");
		// Load the fragment shader
		int fsId = createShaderFromString(fragmentShaderString,
				GL20.GL_FRAGMENT_SHADER);
		System.out.println("...done");
		

		// Create a new shader program that links both shaders
		pId = GL20.glCreateProgram();
		GL20.glAttachShader(pId, vsId);
		GL20.glAttachShader(pId, fsId);
		GL20.glLinkProgram(pId);

		if (GL20.glGetProgram(pId, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
			System.out.println("Could not link shader: "
					+ GL20.glGetProgramInfoLog(pId, 1000));
		}

		if (variablesSetup != null) {
			variablesSetup.setShaderVarsLocation(pId);
		}

		/*
		 * // Position information will be attribute 0
		 * GL20.glBindAttribLocation(pId, 0, "in_Position"); // Color
		 * information will be attribute 1 GL20.glBindAttribLocation(pId, 1,
		 * "in_Color"); // Textute information will be attribute 2
		 * GL20.glBindAttribLocation(pId, 2, "in_TextureCoord");
		 * 
		 * // Get matrices uniform locations projectionMatrixLocation =
		 * GL20.glGetUniformLocation(pId, "projectionMatrix");
		 * viewMatrixLocation = GL20.glGetUniformLocation(pId, "viewMatrix");
		 * modelMatrixLocation = GL20.glGetUniformLocation(pId, "modelMatrix");
		 */

		GL20.glValidateProgram(pId);
		if (GL20.glGetProgram(pId, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE) {
			System.out.println("Could not link shader: "
					+ GL20.glGetProgramInfoLog(pId, 1000));
		}

		this.exitOnGLError("setupShaders");

		GL20.glDetachShader(pId, vsId);
		GL20.glDetachShader(pId, fsId);

		this.programID = pId;
		return pId;

	}

	private int createShaderFromString(String shaderSource, int type) {
		int shaderID = 0;
		
		String shaderTypeName = "";
		shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);

		if (GL20.glGetShader(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {

			switch (type) {
			case GL20.GL_VERTEX_SHADER:
				shaderTypeName = "Vertex Shader";
				break;
			case GL20.GL_FRAGMENT_SHADER:
				shaderTypeName = "Vertex Shader";
				break;
			/*
			 * case (?) GL_GEOMETRY_SHADER : shaderTypeName="Geometry Shader";
			 * break;
			 */
			default:
				shaderTypeName = "Unknown shader type!";
				break;
			}

			System.err.println("Could not compile [" + shaderTypeName + "] "
					+ shaderSource + " \n"
					+ GL20.glGetShaderInfoLog(shaderID, 1000));
			System.exit(-1);
		}

		
		this.exitOnGLError("Create shader from string:[type:"+shaderTypeName+"]");

		return shaderID;
	}

	private String getStringFromFile(String fileName) {

		StringBuilder shaderSource = new StringBuilder();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line;
			// #TODO Prepend precision ifdefs to be opengl es2.0 compatible
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Could not read file.");
			e.printStackTrace();
			System.exit(-1);
		}
		return shaderSource.toString();
	}

	private void exitOnGLError(String errorMessage) {
		int errorValue = GL11.glGetError();

		if (errorValue != GL11.GL_NO_ERROR) {
			String errorString = GLU.gluErrorString(errorValue);
			System.err.println("Error value:"+errorValue);
			System.err.println("ERROR - " + errorMessage + ": " + errorString);
			
			if (Display.isCreated())
				Display.destroy();
			System.exit(-1);
		}
	}

	public int getProgramID() {
		return programID;
	}

	public void setVariablesSetup(ShaderInterface variablesSetup) {
		this.variablesSetup = variablesSetup;
	}
}
