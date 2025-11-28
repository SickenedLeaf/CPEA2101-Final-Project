package com.pushknight.utils;

/**
 * 2D Vector utility class for position, velocity, and direction calculations.
 */
public class Vector2D {
    public double x;
    public double y;
    
    /**
     * Creates a new Vector2D with specified coordinates.
     * @param x X coordinate
     * @param y Y coordinate
     */
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Creates a new Vector2D at origin (0, 0).
     */
    public Vector2D() {
        this(0, 0);
    }
    
    /**
     * Calculates the length (magnitude) of this vector.
     * @return Length of the vector
     */
    public double length() {
        return Math.sqrt(x * x + y * y);
    }
    
    /**
     * Calculates the squared length (avoids sqrt for performance).
     * @return Squared length of the vector
     */
    public double lengthSquared() {
        return x * x + y * y;
    }
    
    /**
     * Normalizes this vector to unit length.
     */
    public void normalize() {
        double length = length();
        if (length > 0) {
            x /= length;
            y /= length;
        }
    }
    
    /**
     * Returns a normalized copy of this vector.
     * @return Normalized vector
     */
    public Vector2D normalized() {
        Vector2D result = copy();
        result.normalize();
        return result;
    }
    
    /**
     * Multiplies this vector by a scalar.
     * @param scalar Value to multiply by
     */
    public void multiply(double scalar) {
        x *= scalar;
        y *= scalar;
    }
    
    /**
     * Adds another vector to this vector.
     * @param other Vector to add
     */
    public void add(Vector2D other) {
        x += other.x;
        y += other.y;
    }
    
    /**
     * Subtracts another vector from this vector.
     * @param other Vector to subtract
     */
    public void subtract(Vector2D other) {
        x -= other.x;
        y -= other.y;
    }
    
    /**
     * Creates a copy of this vector.
     * @return Copy of this vector
     */
    public Vector2D copy() {
        return new Vector2D(x, y);
    }
    
    /**
     * Sets the coordinates of this vector.
     * @param x New X coordinate
     * @param y New Y coordinate
     */
    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Calculates the dot product with another vector.
     * @param other Vector to calculate dot product with
     * @return Dot product value
     */
    public double dot(Vector2D other) {
        return x * other.x + y * other.y;
    }
    
    /**
     * Calculates the distance between two vectors.
     * @param a First vector
     * @param b Second vector
     * @return Distance between vectors
     */
    public static double distance(Vector2D a, Vector2D b) {
        double dx = b.x - a.x;
        double dy = b.y - a.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * Calculates the squared distance between two vectors (avoids sqrt).
     * @param a First vector
     * @param b Second vector
     * @return Squared distance between vectors
     */
    public static double distanceSquared(Vector2D a, Vector2D b) {
        double dx = b.x - a.x;
        double dy = b.y - a.y;
        return dx * dx + dy * dy;
    }
    
    /**
     * Adds two vectors and returns the result.
     * @param a First vector
     * @param b Second vector
     * @return Sum of the vectors
     */
    public static Vector2D add(Vector2D a, Vector2D b) {
        return new Vector2D(a.x + b.x, a.y + b.y);
    }
    
    /**
     * Subtracts second vector from first and returns the result.
     * @param a First vector
     * @param b Second vector
     * @return Difference of the vectors
     */
    public static Vector2D subtract(Vector2D a, Vector2D b) {
        return new Vector2D(a.x - b.x, a.y - b.y);
    }
    
    /**
     * Multiplies a vector by a scalar and returns the result.
     * @param v Vector to multiply
     * @param scalar Scalar value
     * @return Scaled vector
     */
    public static Vector2D multiply(Vector2D v, double scalar) {
        return new Vector2D(v.x * scalar, v.y * scalar);
    }
    
    /**
     * Linearly interpolates between two vectors.
     * @param a Start vector
     * @param b End vector
     * @param t Interpolation factor (0 to 1)
     * @return Interpolated vector
     */
    public static Vector2D lerp(Vector2D a, Vector2D b, double t) {
        return new Vector2D(
            a.x + (b.x - a.x) * t,
            a.y + (b.y - a.y) * t
        );
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vector2D)) return false;
        Vector2D other = (Vector2D) obj;
        return Math.abs(this.x - other.x) < 0.0001 && 
               Math.abs(this.y - other.y) < 0.0001;
    }
    
    @Override
    public int hashCode() {
        long xBits = Double.doubleToLongBits(x);
        long yBits = Double.doubleToLongBits(y);
        return (int)(xBits ^ (xBits >>> 32) ^ yBits ^ (yBits >>> 32));
    }
    
    @Override
    public String toString() {
        return String.format("Vector2D(%.2f, %.2f)", x, y);
    }
}
