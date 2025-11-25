package com.pushknight.utils;

/**
 * Utility class for 2D vector operations.
 * Used for position calculations, movement, and distance measurements.
 */
public class Vector2D {
    private double x;
    private double y;
    
    /**
     * Creates a new vector with the specified coordinates.
     * 
     * @param x The x component
     * @param y The y component
     */
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Creates a zero vector (0, 0).
     */
    public Vector2D() {
        this(0, 0);
    }
    
    /**
     * Creates a copy of another vector.
     * 
     * @param other The vector to copy
     */
    public Vector2D(Vector2D other) {
        this(other.x, other.y);
    }
    
    /**
     * Gets the x component.
     * 
     * @return The x component
     */
    public double getX() {
        return x;
    }
    
    /**
     * Gets the y component.
     * 
     * @return The y component
     */
    public double getY() {
        return y;
    }
    
    /**
     * Sets the x component.
     * 
     * @param x The new x component
     */
    public void setX(double x) {
        this.x = x;
    }
    
    /**
     * Sets the y component.
     * 
     * @param y The new y component
     */
    public void setY(double y) {
        this.y = y;
    }
    
    /**
     * Sets both components.
     * 
     * @param x The new x component
     * @param y The new y component
     */
    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Adds another vector to this vector.
     * 
     * @param other The vector to add
     * @return A new vector representing the sum
     */
    public Vector2D add(Vector2D other) {
        return new Vector2D(this.x + other.x, this.y + other.y);
    }
    
    /**
     * Adds another vector to this vector in place.
     * 
     * @param other The vector to add
     * @return This vector for method chaining
     */
    public Vector2D addInPlace(Vector2D other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }
    
    /**
     * Subtracts another vector from this vector.
     * 
     * @param other The vector to subtract
     * @return A new vector representing the difference
     */
    public Vector2D subtract(Vector2D other) {
        return new Vector2D(this.x - other.x, this.y - other.y);
    }
    
    /**
     * Subtracts another vector from this vector in place.
     * 
     * @param other The vector to subtract
     * @return This vector for method chaining
     */
    public Vector2D subtractInPlace(Vector2D other) {
        this.x -= other.x;
        this.y -= other.y;
        return this;
    }
    
    /**
     * Multiplies this vector by a scalar.
     * 
     * @param scalar The scalar to multiply by
     * @return A new vector representing the product
     */
    public Vector2D multiply(double scalar) {
        return new Vector2D(this.x * scalar, this.y * scalar);
    }
    
    /**
     * Multiplies this vector by a scalar in place.
     * 
     * @param scalar The scalar to multiply by
     * @return This vector for method chaining
     */
    public Vector2D multiplyInPlace(double scalar) {
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }
    
    /**
     * Calculates the magnitude (length) of this vector.
     * 
     * @return The magnitude
     */
    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }
    
    /**
     * Calculates the squared magnitude (length squared) of this vector.
     * Faster than magnitude() as it avoids the square root.
     * 
     * @return The squared magnitude
     */
    public double magnitudeSquared() {
        return x * x + y * y;
    }
    
    /**
     * Normalizes this vector (makes it unit length).
     * 
     * @return A new normalized vector
     */
    public Vector2D normalize() {
        double mag = magnitude();
        if (mag == 0) {
            return new Vector2D(0, 0);
        }
        return new Vector2D(x / mag, y / mag);
    }
    
    /**
     * Normalizes this vector in place.
     * 
     * @return This vector for method chaining
     */
    public Vector2D normalizeInPlace() {
        double mag = magnitude();
        if (mag != 0) {
            this.x /= mag;
            this.y /= mag;
        }
        return this;
    }
    
    /**
     * Calculates the distance to another vector.
     * 
     * @param other The other vector
     * @return The distance between the two vectors
     */
    public double distance(Vector2D other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * Calculates the squared distance to another vector.
     * Faster than distance() as it avoids the square root.
     * 
     * @param other The other vector
     * @return The squared distance between the two vectors
     */
    public double distanceSquared(Vector2D other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return dx * dx + dy * dy;
    }
    
    /**
     * Calculates the dot product with another vector.
     * 
     * @param other The other vector
     * @return The dot product
     */
    public double dot(Vector2D other) {
        return this.x * other.x + this.y * other.y;
    }
    
    @Override
    public String toString() {
        return String.format("Vector2D(%.2f, %.2f)", x, y);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vector2D vector2D = (Vector2D) obj;
        return Double.compare(vector2D.x, x) == 0 && Double.compare(vector2D.y, y) == 0;
    }
    
    @Override
    public int hashCode() {
        return Double.hashCode(x) * 31 + Double.hashCode(y);
    }
}

