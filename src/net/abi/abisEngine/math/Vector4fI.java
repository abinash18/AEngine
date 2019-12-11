/**
 * 
 */
package net.abi.abisEngine.math;

/**
 * @author abinash
 *
 */
public interface Vector4fI {

	public float x();

	public void setX(float x);

	public float y();

	public void setY(float y);

	public float z();

	public void setZ(float z);

	public float w();

	public void setW(float w);

	public Vector2f xy();

	public Vector2f yz();

	public Vector2f zx();

	public Vector2f yx();

	public Vector2f zy();

	public Vector2f xz();

	public float length3f();

	public float length4f();

	public float dot(Vector4f r);

	public float dot(float x, float y, float z, float w);

	public float angleCos(Vector4f v);

	public float angle(Vector4f v);

	public Vector3f normalize3f();

	public Vector4f normalize4f();

	public Vector4f rotateAxisInternal(float angle, float aX, float aY, float aZ);

	public Vector4f rotateAxisInternal(float angle, float aX, float aY, float aZ, Vector4f dest);

	public Vector4f rotateX(float angle, Vector4f dest);

	public Vector4f rotateY(float angle);

	public Vector4f rotateY(float angle, Vector4f dest);

	public Vector4f rotateZ(float angle);

	public Vector4f rotateZ(float angle, Vector4f dest);

	public float distance(Vector4f v);

	public float distance(float x, float y, float z, float w);

	public float distanceSquared(Vector4f v);

	public float distanceSquared(float x, float y, float z, float w);

	public Vector4f zero();

	public Vector4f negate();

	public Vector4f negate(Vector4f dest);

	public Vector4f smoothStep(Vector4f v, float t);

	public Vector4f hermite(Vector4f t0, Vector4f v1, Vector4f t1, float t);

	public float max();

	public float min();

	public Vector4f set(Vector4f v);

	public Vector4f lerp(Vector4f dest, float lerpFactor);

	public boolean equals(Vector3f r);

	public Vector4f add(Vector4f r);

	public Vector4f add(float r);

	public Vector4f sub(Vector4f r);

	public Vector4f sub(float r);

	public Vector4f mul(Vector4f r);

	public Vector4f mul(float r);

	public Vector4f div(Vector4f r);

	public Vector4f div(float r);

	public Vector4f abs();

	public Vector4f set(float x, float y, float z, float w);

	public String toString();
}
