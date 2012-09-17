package glutils;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class MathUtils {

	/**
	 * Setup perspective matrix
	 * */
	public static Matrix4f setupPerspective(float width, float height,
			float fov, float near, float far) {
		Matrix4f projectionMatrix = new Matrix4f();
		float fieldOfView = fov;
		float aspectRatio = width / height;
		float near_plane = near;
		float far_plane = far;

		float y_scale = coTangent(degreesToRadians(fieldOfView / 2f));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = far_plane - near_plane;

		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((far_plane + near_plane) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * near_plane * far_plane) / frustum_length);

		return projectionMatrix;
	}

	public static float coTangent(float angle) {
		return (float) (1f / Math.tan(angle));
	}

	public static float degreesToRadians(float degrees) {
		return degrees * (float) (Math.PI / 180d);
	}

	public static Matrix3f getNormalMatrix(Matrix4f modelViewMatrix) {
		Matrix3f top33 = new Matrix3f();// get top 3*3 of modelview matrix

		top33.m00 = modelViewMatrix.m00;
		top33.m01 = modelViewMatrix.m01;
		top33.m02 = modelViewMatrix.m02;
		top33.m10 = modelViewMatrix.m10;
		top33.m11 = modelViewMatrix.m11;
		top33.m12 = modelViewMatrix.m12;
		top33.m20 = modelViewMatrix.m20;
		top33.m21 = modelViewMatrix.m21;
		top33.m22 = modelViewMatrix.m22;

		// top33.invert();
		Matrix3f.invert(top33, top33);
		top33.transpose();
		Matrix3f.transpose(top33, top33);
		return top33;

	}

	public static Matrix4f setupView(Vector3f position, Vector3f lookAt,
			Vector3f up) {
		Vector3f rightVector = new Vector3f();
		Vector3f.cross(up, lookAt, rightVector);
		rightVector = (Vector3f) rightVector.normalise();
		Matrix4f result = new Matrix4f();
		result.m00 = rightVector.x;
		result.m10 = rightVector.y;
		result.m20 = rightVector.z;
		result.m30 = -(Vector3f.dot(rightVector, position));

		result.m01 = up.x;
		result.m11 = up.y;
		result.m21 = up.z;
		result.m31 = -(Vector3f.dot(up, position));

		result.m02 = lookAt.x;
		result.m12 = lookAt.y;
		result.m22 = lookAt.z;
		result.m32 = -(Vector3f.dot(lookAt, position));

		return result;
	}
}
