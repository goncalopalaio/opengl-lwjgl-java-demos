package OBJLoader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Pattern;

/**
 * This is an imcomplete .obj loader, only works with triangulated faces (use
 * GL_TRIANGLES) and you have to provide normals and texture mapping on the obj
 * file
 * Only works well for small obj files, without groups.
 * */
public class OBJParser {

	private Pattern slashes;
	private Pattern spaces;
	private final String fileName;
	private LinkedList<Vec3> positions;
	private LinkedList<Vec2> texCoords;
	private LinkedList<Vec3> normals;
	private LinkedList<MeshFaceIndex> faces;
	private Mesh mesh;
	private LinkedList<OBJData> data;

	public OBJParser(String fileName) {
		this.fileName = fileName;
		positions = new LinkedList<Vec3>();
		texCoords = new LinkedList<Vec2>();
		normals = new LinkedList<Vec3>();
		faces = new LinkedList<MeshFaceIndex>();
		mesh = new Mesh();
		data = new LinkedList<OBJData>();

		String splitPattern = "[\\s]+";// stringtokenizer is faster but
										// deprecated
		spaces = Pattern.compile(splitPattern);
		splitPattern = "[/ ]+";
		slashes = Pattern.compile(splitPattern);
		System.out.println("Beginning parsing");
		parse();
		System.out.println("Parsing ended,processing data..");
		process();
		System.out.println(fileName + " END");
	}

	public void parse() {

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e1) {

			e1.printStackTrace();
		}
		String line;

		try {
			while ((line = reader.readLine()) != null) {
				System.out.println(".");
				if (line.startsWith("v ")) {
					parseVertex(line);
				} else if (line.startsWith("vt")) {
					parseTexCoordinates(line);
				} else if (line.startsWith("vn")) {
					parseVertexNormals(line);
				} else if (line.startsWith("f ")) {
					parseFaces(line);
				}
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < faces.size(); i++) {

			MeshFace face = new MeshFace();
			for (int j = 0; j < 3; ++j) {

				//int pos = faces.get(i).posIndex[j] - 1;
				// System.out.println(positions.size() + " pos" +pos);
				// System.out.println(face.vertices[0] + " ");

				face.vertices[j].pos = positions
						.get(faces.get(i).posIndex[j] - 1);

				if (faces.get(i).textured == true) {
					face.vertices[j].texCoord = texCoords
							.get(faces.get(i).texIndex[j] - 1);
				} else {
					face.vertices[j].texCoord = new Vec2();// no texturecoords
				}
				face.vertices[j].normal = normals
						.get(faces.get(i).norIndex[j] - 1);
			}
			mesh.faces.add(face);

		}

	}

	private void parseFaces(String line) {
		MeshFaceIndex faceIndex = new MeshFaceIndex();

		String[] el = slashes.split(line);

		/*
		 * There is really no need to save all the face indexes, but it
		 * simplifies reading
		 */
		// System.out.println("faces: " + line);
		// System.out.println("N: " + el.length);
		if (el.length == 10) {
			int pos = 1;
			for (int i = 0; i < 3; ++i) {
				faceIndex.posIndex[i] = pI(el[pos]);
				faceIndex.texIndex[i] = pI(el[pos + 1]);
				faceIndex.norIndex[i] = pI(el[pos + 2]);
				pos += 3;

			}
		} else if (el.length == 7) {
			int pos = 1;
			for (int i = 0; i < 3; ++i) {
				// System.out.println(el[pos]);
				// System.out.println(el[pos + 1]);

				faceIndex.posIndex[i] = pI(el[pos]);
				faceIndex.norIndex[i] = pI(el[pos + 1]);
				faceIndex.textured = false;
				faceIndex.texIndex[i] = 0;// HACK no texture index, redirect to
											// empty tex coordinates
				pos += 2;

			}
		}

		faces.add(faceIndex);

	}

	private void parseVertexNormals(String line) {
		Vec3 normal = new Vec3();
		String[] el = spaces.split(line);
		normal.x = pF(el[1]);
		normal.y = pF(el[2]);
		normal.z = pF(el[3]);

		normals.add(normal);

	}

	private void parseTexCoordinates(String line) {
		Vec2 tex = new Vec2();
		String[] el = spaces.split(line);
		tex.x = pF(el[1]);
		tex.y = pF(el[2]);
		texCoords.add(tex);
	}

	private void parseVertex(String line) {
		Vec3 pos = new Vec3();
		String[] el = spaces.split(line);
		pos.x = pF(el[1]);
		pos.y = pF(el[2]);
		pos.z = pF(el[3]);

		positions.add(pos);

	}

	private void process() {

		for (int faceIndex = 0; faceIndex < mesh.faces.size(); ++faceIndex) {
			MeshFace currentFace = mesh.faces.get(faceIndex);
			System.out.println(".");
			for (int vertexIndex = 0; vertexIndex < 3; ++vertexIndex) {
				OBJData v = new OBJData();
				MeshVertex vertex = currentFace.vertices[vertexIndex];

				v.setNXNYNZ(vertex.normal.x, vertex.normal.y, vertex.normal.z);
				v.setST(vertex.texCoord.x, vertex.texCoord.y);
				v.setXYZW(vertex.pos.x, vertex.pos.y, vertex.pos.z);

				getData().push(v);

			}
		}

		positions = null;
		texCoords = null;
		normals = null;
		faces = null;
		mesh = null;

		System.out.println("Finished packing data");

	}

	/*
	 * public void immediateRender(){ // Begin drawing of triangles.
	 * glBegin(GL_TRIANGLES);
	 * 
	 * // Iterate over each face. for(int faceIndex = 0; faceIndex <
	 * mesh.faces.size(); ++faceIndex) { MeshFace current_face
	 * =mesh.faces.get(faceIndex);
	 * 
	 * for(int vertexIndex = 0; vertexIndex < 3; ++vertexIndex) { MeshVertex
	 * vertex = current_face.vertices[vertexIndex]; glNormal3f(vertex.normal.x,
	 * vertex.normal.y, vertex.normal.z); glTexCoord2f(vertex.texCoord.x,
	 * vertex.texCoord.y); glVertex3f(vertex.pos.x, vertex.pos.y, vertex.pos.z);
	 * } } // End drawing of triangles. glEnd(); }
	 */

	private float pF(String toParse) {
		return Float.parseFloat(toParse);
	}

	private int pI(String toParse) {
		return Integer.parseInt(toParse);
	}

	public LinkedList<OBJData> getData() {
		return data;
	}

	/**********************
	 * Small classes below
	 **********************/
	private class Vec2 {
		float x, y;

		public Vec2() {
			x = 0;
			y = 0;
		}
	}

	private class Vec3 {
		float x, y, z;

		public Vec3() {
			x = 0;
			y = 0;
			z = 0;
		}
	}

	private class MeshVertex {
		Vec3 pos;
		Vec2 texCoord;
		Vec3 normal;

		public MeshVertex() {
			pos = new Vec3();
			texCoord = new Vec2();
			normal = new Vec3();
		}
	}

	private class MeshFace {
		MeshVertex vertices[];

		public MeshFace() {
			vertices = new MeshVertex[3];
			vertices[0] = new MeshVertex();
			vertices[1] = new MeshVertex();
			vertices[2] = new MeshVertex();

		}
	}

	private class Mesh {
		LinkedList<MeshFace> faces;

		public Mesh() {
			faces = new LinkedList<MeshFace>();
		}
	}

	private class MeshFaceIndex {
		int posIndex[];
		int texIndex[];
		int norIndex[];
		boolean textured;

		public MeshFaceIndex() {
			posIndex = new int[3];
			texIndex = new int[3];
			norIndex = new int[3];
			textured = true;

		}
	}
}