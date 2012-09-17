package OBJLoader;

public class OBJData {
	private float[] xyz = new float[] { 0f, 0f, 0f };
	private float[] rgba = new float[] { 1f, 1f, 1f, 1f };
	private float[] nxnynz = new float[] { 0f, 0f, 0f };
	private float[] st = new float[] { 0f, 0f };

	// number of byte per element is 4bytes
	public static final int elementBytes = 4;
	// elements per parameter
	public static final int positionElementCount = 3;
	public static final int colorElementCount = 4;
	public static final int textureElementCount = 2;
	public static final int normalElementCount = 3;
	// number of bytes per parameter
	public static final int positionBytesCount = positionElementCount
			* elementBytes;
	public static final int colorByteCount = colorElementCount * elementBytes;
	public static final int textureByteCount = textureElementCount
			* elementBytes;
	public static final int normalByteCount = normalElementCount * elementBytes;
	// byte offset per parameter
	public static final int positionByteOffset = 0;
	public static final int colorByteOffset = positionByteOffset
			+ positionBytesCount;
	public static final int textureByteOffset = colorByteOffset
			+ colorByteCount;
	public static final int normalByteOffset = textureByteOffset
			+ textureByteCount;

	// number of elements per vertex
	public static final int elementCount = positionElementCount
			+ colorElementCount + textureElementCount + normalElementCount;
	// size of each group elements per vertex in bytes
	public static final int stride = positionBytesCount + colorByteCount
			+ textureByteCount + normalByteCount;

	public void setST(float s, float t) {
		this.st = new float[] { s, t };
	}

	public void setXYZW(float x, float y, float z) {
		this.xyz = new float[] { x, y, z };
	}

	public void setNXNYNZ(float x, float y, float z) {
		this.nxnynz = new float[] { x, y, z };
	}

	public void setRGBA(float r, float g, float b, float a) {
		this.rgba = new float[] { r, g, b, a };
	}

	// Getters
	public float[] getElements() {
		float[] out = new float[OBJData.elementCount];
		int i = 0;

		// Insert XYZW elements
		out[i++] = this.xyz[0];
		out[i++] = this.xyz[1];
		out[i++] = this.xyz[2];
		// Insert RGBA elements
		out[i++] = this.rgba[0];
		out[i++] = this.rgba[1];
		out[i++] = this.rgba[2];
		out[i++] = this.rgba[3];
		// Insert ST elements
		out[i++] = this.st[0];
		out[i++] = this.st[1];
		// Insert Normal elements
		out[i++] = this.nxnynz[0];
		out[i++] = this.nxnynz[1];
		out[i++] = this.nxnynz[2];

		return out;
	}

	public String toString() {
		return (this.xyz[0] + " " + this.xyz[1] + " " + this.xyz[2]);
	}
}
