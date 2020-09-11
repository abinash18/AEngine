package net.abi.abisEngine.math;

public class Matrix4f implements Matrix {
	private float[][] m;

	public Matrix4f() {
		m = new float[4][4];
	}

	public Matrix4f(float[][] _m) {
		m = _m;
	}

	public Matrix4f initIdentity() {
		m[0][0] = 1;
		m[0][1] = 0;
		m[0][2] = 0;
		m[0][3] = 0;
		m[1][0] = 0;
		m[1][1] = 1;
		m[1][2] = 0;
		m[1][3] = 0;
		m[2][0] = 0;
		m[2][1] = 0;
		m[2][2] = 1;
		m[2][3] = 0;
		m[3][0] = 0;
		m[3][1] = 0;
		m[3][2] = 0;
		m[3][3] = 1;

		return this;
	}

	/**
	 * Initializes The Perspective.
	 * 
	 * @param fov
	 * @param width
	 * @param height
	 * @param zNear
	 * @param zFar
	 * @return
	 */
	public Matrix4f initPerspective(float fov, float width, float height, float zNear, float zFar) {

		float aspectRatio = width / height;
		float tanHalfFOV = (float) Math.tan(Math.toRadians(fov / 2));
		float zRange = zNear - zFar;

		m[0][0] = 1.0f / (tanHalfFOV * aspectRatio);
		m[0][1] = 0;
		m[0][2] = 0;
		m[0][3] = 0;
		m[1][0] = 0;
		m[1][1] = 1.0f / tanHalfFOV;
		m[1][2] = 0;
		m[1][3] = 0;
		m[2][0] = 0;
		m[2][1] = 0;
		m[2][2] = (-zNear - zFar) / zRange;
		m[2][3] = 2 * zFar * zNear / zRange;
		m[3][0] = 0;
		m[3][1] = 0;
		m[3][2] = 1;
		m[3][3] = 0;

		return this;
	}

	public Matrix4f initProjection(float fov, float aspectRatio, float zNear, float zFar) {

		// float aspectRatio = width / height;
		// float tanHalfFOV = (float) Math.tan(Math.toRadians(fov / 2));
		float tanHalfFOV = (float) Math.tan(fov / 2);
		float zRange = zNear - zFar;

		m[0][0] = 1.0f / (tanHalfFOV * aspectRatio);
		m[0][1] = 0;
		m[0][2] = 0;
		m[0][3] = 0;
		m[1][0] = 0;
		m[1][1] = 1.0f / tanHalfFOV;
		m[1][2] = 0;
		m[1][3] = 0;
		m[2][0] = 0;
		m[2][1] = 0;
		m[2][2] = (-zNear - zFar) / zRange;
		m[2][3] = 2 * zFar * zNear / zRange;
		m[3][0] = 0;
		m[3][1] = 0;
		m[3][2] = 1;
		m[3][3] = 0;

		return this;
	}

	/**
	 *
	 * @return
	 */
	public Matrix4f initOrthographic(float left, float right, float bottom, float top, float near, float far) {

		float width = right - left, height = top - bottom, depth = far - near;
		m[0][0] = 2 / width;
		m[0][1] = 0;
		m[0][2] = 0;
		m[0][3] = -(right + left) / width;
		m[1][0] = 0;
		m[1][1] = 2 / height;
		m[1][2] = 0;
		m[1][3] = -(top + bottom) / height;
		m[2][0] = 0;
		m[2][1] = 0;
		m[2][2] = -2 / depth;
		m[2][3] = -(far + near) / depth;
		m[3][0] = 0;
		m[3][1] = 0;
		m[3][2] = 0;
		m[3][3] = 1;

		return this;
	}

	public Matrix4f initTranslation(float x, float y, float z) {
		m[0][0] = 1;
		m[0][1] = 0;
		m[0][2] = 0;
		m[0][3] = x;
		m[1][0] = 0;
		m[1][1] = 1;
		m[1][2] = 0;
		m[1][3] = y;
		m[2][0] = 0;
		m[2][1] = 0;
		m[2][2] = 1;
		m[2][3] = z;
		m[3][0] = 0;
		m[3][1] = 0;
		m[3][2] = 0;
		m[3][3] = 1;

		return this;
	}

	public Matrix4f initScale(float x, float y, float z) {
		m[0][0] = x;
		m[0][1] = 0;
		m[0][2] = 0;
		m[0][3] = 0;
		m[1][0] = 0;
		m[1][1] = y;
		m[1][2] = 0;
		m[1][3] = 0;
		m[2][0] = 0;
		m[2][1] = 0;
		m[2][2] = z;
		m[2][3] = 0;
		m[3][0] = 0;
		m[3][1] = 0;
		m[3][2] = 0;
		m[3][3] = 1;

		return this;
	}

	public Matrix4f initRotation(float x, float y, float z) {
		Matrix4f rx = new Matrix4f();
		Matrix4f ry = new Matrix4f();
		Matrix4f rz = new Matrix4f();

		x = (float) Math.toRadians(x);
		y = (float) Math.toRadians(y);
		z = (float) Math.toRadians(z);

		rz.m[0][0] = (float) Math.cos(z);
		rz.m[0][1] = -(float) Math.sin(z);
		rz.m[0][2] = 0;
		rz.m[0][3] = 0;
		rz.m[1][0] = (float) Math.sin(z);
		rz.m[1][1] = (float) Math.cos(z);
		rz.m[1][2] = 0;
		rz.m[1][3] = 0;
		rz.m[2][0] = 0;
		rz.m[2][1] = 0;
		rz.m[2][2] = 1;
		rz.m[2][3] = 0;
		rz.m[3][0] = 0;
		rz.m[3][1] = 0;
		rz.m[3][2] = 0;
		rz.m[3][3] = 1;

		rx.m[0][0] = 1;
		rx.m[0][1] = 0;
		rx.m[0][2] = 0;
		rx.m[0][3] = 0;
		rx.m[1][0] = 0;
		rx.m[1][1] = (float) Math.cos(x);
		rx.m[1][2] = -(float) Math.sin(x);
		rx.m[1][3] = 0;
		rx.m[2][0] = 0;
		rx.m[2][1] = (float) Math.sin(x);
		rx.m[2][2] = (float) Math.cos(x);
		rx.m[2][3] = 0;
		rx.m[3][0] = 0;
		rx.m[3][1] = 0;
		rx.m[3][2] = 0;
		rx.m[3][3] = 1;

		ry.m[0][0] = (float) Math.cos(y);
		ry.m[0][1] = 0;
		ry.m[0][2] = -(float) Math.sin(y);
		ry.m[0][3] = 0;
		ry.m[1][0] = 0;
		ry.m[1][1] = 1;
		ry.m[1][2] = 0;
		ry.m[1][3] = 0;
		ry.m[2][0] = (float) Math.sin(y);
		ry.m[2][1] = 0;
		ry.m[2][2] = (float) Math.cos(y);
		ry.m[2][3] = 0;
		ry.m[3][0] = 0;
		ry.m[3][1] = 0;
		ry.m[3][2] = 0;
		ry.m[3][3] = 1;

		m = rz.mul(ry.mul(rx)).get();

		return this;
	}

	public Matrix4f initRotation(Vector3f forward, Vector3f up) {

		Vector3f f = forward.normalize();

		Vector3f r = up.normalize();
		// This Makes r cross f so f is very angry and when he gets angry people say
		// WTF!
		r = r.cross(f);

		Vector3f u = f.cross(r);

		// m[0][0] = r.getX(); m[0][1] = r.getY(); m[0][2] = r.getZ(); m[0][3] = 0;
		// m[1][0] = u.getX(); m[1][1] = u.getY(); m[1][2] = u.getZ(); m[1][3] = 0;
		// m[2][0] = f.getX(); m[2][1] = f.getY(); m[2][2] = f.getZ(); m[2][3] = 0;
		// m[3][0] = 0; m[3][1] = 0; m[3][2] = 0; m[3][3] = 1;
		//
		return initRotation(f, u, r);
	}

	public Matrix4f initRotation(Vector3f forward, Vector3f up, Vector3f right) {

		Vector3f f = forward;
		Vector3f r = right;
		Vector3f u = up;

		m[0][0] = r.x();
		m[0][1] = r.y();
		m[0][2] = r.z();
		m[0][3] = 0;
		m[1][0] = u.x();
		m[1][1] = u.y();
		m[1][2] = u.z();
		m[1][3] = 0;
		m[2][0] = f.x();
		m[2][1] = f.y();
		m[2][2] = f.z();
		m[2][3] = 0;
		m[3][0] = 0;
		m[3][1] = 0;
		m[3][2] = 0;
		m[3][3] = 1;

		return this;
	}

	public Matrix4f mul(Matrix4f r) {
		Matrix4f res = new Matrix4f();

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				res.set(i, j,
						m[i][0] * r.get(0, j) + m[i][1] * r.get(1, j) + m[i][2] * r.get(2, j) + m[i][3] * r.get(3, j));
			}
		}

		return res;
	}

	public float[][] get() {

		float[][] res = new float[4][4];

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				res[i][j] = m[i][j];
			}
		}

		return (res);
	}

	public float get(int x, int y) {
		return m[x][y];
	}

	public Matrix4f set(float[][] m) {
		this.m = m;
		return this;
	}

	public void set(int x, int y, float value) {
		m[x][y] = value;
	}

	public Vector3f transform(Vector3f other) {

		return new Vector3f(m[0][0] * other.x() + m[0][1] * other.y() + m[0][2] * other.z() + m[0][3],
				m[1][0] * other.x() + m[1][1] * other.y() + m[1][2] * other.z() + m[1][3],
				m[2][0] * other.x() + m[2][1] * other.y() + m[2][2] * other.z() + m[2][3]);

	}

	/**
	 * Transpose the current matrix and return it in a new one.
	 */
	public Matrix4f transpose() {
		return transpose(this);
	}

	public Matrix4f transpose(Matrix4f dest) {
		float[][] res = new float[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				res[i][j] = m[j][i];
			}
		}
		return dest.set(res);
	}

	/**
	 * 
	 */
	public Matrix4f invertGeneric() {
		return invertGeneric(this);
	}

	public Matrix4f invertGeneric(Matrix4f dest) {

		float[][] res = new float[4][4];

		float a = m[0][0] * m[1][1] - m[0][1] * m[1][0];
		float b = m[0][0] * m[1][2] - m[0][2] * m[1][0];
		float c = m[0][0] * m[1][3] - m[0][3] * m[1][0];
		float d = m[0][1] * m[1][2] - m[0][2] * m[1][1];
		float e = m[0][1] * m[1][3] - m[0][3] * m[1][1];
		float f = m[0][2] * m[1][3] - m[0][3] * m[1][2];
		float g = m[2][0] * m[3][1] - m[2][1] * m[3][0];
		float h = m[2][0] * m[3][2] - m[2][2] * m[3][0];
		float i = m[2][0] * m[3][3] - m[2][3] * m[3][0];
		float j = m[2][1] * m[3][2] - m[2][2] * m[3][1];
		float k = m[2][1] * m[3][3] - m[2][3] * m[3][1];
		float l = m[2][2] * m[3][3] - m[2][3] * m[3][2];
		float det = a * l - b * k + c * j + d * i - e * h + f * g;
		det = 1.0f / det;
		res[0][0] = (m[1][1] * l - m[1][2] * k + m[1][3] * j) * det;
		res[0][1] = (-m[0][1] * l + m[0][2] * k - m[0][3] * j) * det;
		res[0][2] = (m[3][1] * f - m[3][2] * e + m[3][3] * d) * det;
		res[0][3] = (-m[2][1] * f + m[2][2] * e - m[2][3] * d) * det;
		res[1][0] = (-m[1][0] * l + m[1][2] * i - m[1][3] * h) * det;
		res[1][1] = (m[0][0] * l - m[0][2] * i + m[0][3] * h) * det;
		res[1][2] = (-m[3][0] * f + m[3][2] * c - m[3][3] * b) * det;
		res[1][3] = (m[2][0] * f - m[2][2] * c + m[2][3] * b) * det;
		res[2][0] = (m[1][0] * k - m[1][1] * i + m[1][3] * g) * det;
		res[2][1] = (-m[0][0] * k + m[0][1] * i - m[0][3] * g) * det;
		res[2][2] = (m[3][0] * e - m[3][1] * c + m[3][3] * a) * det;
		res[2][3] = (-m[2][0] * e + m[2][1] * c - m[2][3] * a) * det;
		res[3][0] = (-m[1][0] * j + m[1][1] * h - m[1][2] * g) * det;
		res[3][1] = (m[0][0] * j - m[0][1] * h + m[0][2] * g) * det;
		res[3][2] = (-m[3][0] * d + m[3][1] * b - m[3][2] * a) * det;
		res[3][3] = (m[2][0] * d - m[2][1] * b + m[2][2] * a) * det;
		dest.set(res);
		return dest;
	}

	public Matrix4f invertPerspective(Matrix4f dest) {
		float a = 1.0f / (m[0][0] * m[0][0]);
		float l = -1.0f / (m[0][0] * m[0][0]);

		float[][] res = new float[4][4];

		res[0][0] = m[0][0] * a;
		res[0][1] = 0;
		res[0][2] = 0;
		res[0][3] = 0;
		res[1][0] = 0;
		res[1][1] = m[0][0] * a;
		res[1][2] = 0;
		res[1][3] = 0;
		res[2][0] = 0;
		res[2][1] = 0;
		res[2][2] = 0;
		res[2][3] = -m[0][0] * l;
		res[3][0] = 0;
		res[3][1] = 0;
		res[3][2] = -m[0][0] * l;
		res[3][3] = m[0][0] * l;

		dest.set(res);
		return dest;
	}

	@Override
	public int getCols() {
		return (m[0].length);
	}

	@Override
	public int getRows() {
		return (m.length);
	}

}
