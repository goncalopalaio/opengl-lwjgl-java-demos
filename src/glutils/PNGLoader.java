package glutils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class PNGLoader {
	private int width;
	private int height;
	private String fileName;
	private ByteBuffer dataBuffer;

	public PNGLoader(String fileName) {
		this.fileName = fileName;

		int error = loadPNGTexture(fileName);
		if (error == 1) {
			System.out.println("error loading texture: " + fileName);
		}

	}

	private int loadPNGTexture(String fileName) {
		dataBuffer = null;

		try {
			// Open the PNG file as an InputStream
			InputStream in = new FileInputStream(fileName);
			// Link the PNG decoder to this stream
			PNGDecoder decoder = new PNGDecoder(in);

			// Get the width and height of the texture
			width = decoder.getWidth();
			height = decoder.getHeight();

			// Decode the PNG file in a BytedataBufferfer
			dataBuffer = ByteBuffer.allocateDirect(4 * decoder.getWidth()
					* decoder.getHeight());
			decoder.decode(getDataBuffer(), decoder.getWidth() * 4, Format.RGBA);
			getDataBuffer().flip();

			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			return 1;
		}

		return 0;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String getFileName() {
		return fileName;
	}

	public ByteBuffer getDataBuffer() {
		if (dataBuffer == null) {
			System.out
					.println("Warning: Texture data buffer empty:" + fileName);
		}
		return dataBuffer;
	}

}