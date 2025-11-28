package com.pushknight.utils;

import com.pushknight.entities.Obstacle;
import java.util.*;

/**
 * A* pathfinding algorithm implementation for enemy AI navigation.
 */
public class AStar {
    private static final int GRID_SIZE = 20; // Size of each grid cell in pixels
    
    /**
     * Node class for A* pathfinding.
     */
    private static class Node implements Comparable<Node> {
        Vector2D position;      // World position of this node
        Node parent;            // Parent node in path
        double gCost;           // Cost from start to this node
        double hCost;           // Heuristic cost from this node to end
        double fCost;           // Total cost (g + h)
        
        /**
         * Creates a pathfinding node.
         * @param position World position of the node
         */
        Node(Vector2D position) {
            this.position = position;
            this.parent = null;
            this.gCost = 0;
            this.hCost = 0;
            this.fCost = 0;
        }
        
        /**
         * Calculates the total cost.
         */
        void calculateFCost() {
            fCost = gCost + hCost;
        }
        
        @Override
        public int compareTo(Node other) {
            return Double.compare(this.fCost, other.fCost);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Node)) return false;
            Node other = (Node) obj;
            return this.position.equals(other.position);
        }
        
        @Override
        public int hashCode() {
            return position.hashCode();
        }
    }
    
    /**
     * Finds a path from start to end position using A* algorithm.
     * @param start Starting position
     * @param end Target position
     * @param obstacles List of obstacles to avoid
     * @return List of waypoints forming the path, or empty list if no path found
     */
    public static List<Vector2D> findPath(Vector2D start, Vector2D end, List<Obstacle> obstacles) {
        // Convert positions to grid coordinates
        Vector2D startGrid = worldToGrid(start);
        Vector2D endGrid = worldToGrid(end);
        
        // Initialize open and closed sets
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<Node> closedSet = new HashSet<>();
        Map<Vector2D, Node> allNodes = new HashMap<>();
        
        // Create start node
        Node startNode = new Node(startGrid);
        startNode.gCost = 0;
        startNode.hCost = heuristic(startGrid, endGrid);
        startNode.calculateFCost();
        
        openSet.add(startNode);
        allNodes.put(startGrid, startNode);
        
        // A* main loop
        int maxIterations = 1000; // Prevent infinite loops
        int iterations = 0;
        
        while (!openSet.isEmpty() && iterations < maxIterations) {
            iterations++;
            
            // Get node with lowest f cost
            Node current = openSet.poll();
            closedSet.add(current);
            
            // Check if reached goal
            if (Vector2D.distance(current.position, endGrid) < 1.0) {
                return reconstructPath(current);
            }
            
            // Check all neighbors
            List<Vector2D> neighbors = getNeighbors(current.position);
            
            for (Vector2D neighborPos : neighbors) {
                // Skip if in closed set
                Node neighborNode = allNodes.get(neighborPos);
                if (neighborNode != null && closedSet.contains(neighborNode)) {
                    continue;
                }
                
                // Skip if collides with obstacle
                if (isPositionBlocked(neighborPos, obstacles)) {
                    continue;
                }
                
                // Calculate g cost for this neighbor
                double tentativeGCost = current.gCost + Vector2D.distance(current.position, neighborPos);
                
                // Create neighbor node if it doesn't exist
                if (neighborNode == null) {
                    neighborNode = new Node(neighborPos);
                    allNodes.put(neighborPos, neighborNode);
                }
                
                // If this path to neighbor is better than previous
                if (tentativeGCost < neighborNode.gCost || !openSet.contains(neighborNode)) {
                    neighborNode.parent = current;
                    neighborNode.gCost = tentativeGCost;
                    neighborNode.hCost = heuristic(neighborPos, endGrid);
                    neighborNode.calculateFCost();
                    
                    if (!openSet.contains(neighborNode)) {
                        openSet.add(neighborNode);
                    }
                }
            }
        }
        
        // No path found, return direct path
        return createDirectPath(start, end);
    }
    
    /**
     * Reconstructs the path from end node to start node.
     * @param endNode The final node in the path
     * @return List of waypoints in world coordinates
     */
    private static List<Vector2D> reconstructPath(Node endNode) {
        List<Vector2D> path = new ArrayList<>();
        Node current = endNode;
        
        while (current != null) {
            path.add(gridToWorld(current.position));
            current = current.parent;
        }
        
        Collections.reverse(path);
        
        // Simplify path by removing unnecessary waypoints
        return simplifyPath(path);
    }
    
    /**
     * Simplifies path by removing waypoints that are in straight lines.
     * @param path Original path
     * @return Simplified path
     */
    private static List<Vector2D> simplifyPath(List<Vector2D> path) {
        if (path.size() <= 2) return path;
        
        List<Vector2D> simplified = new ArrayList<>();
        simplified.add(path.get(0));
        
        for (int i = 1; i < path.size() - 1; i++) {
            Vector2D prev = path.get(i - 1);
            Vector2D current = path.get(i);
            Vector2D next = path.get(i + 1);
            
            // Check if current point is necessary (not in straight line)
            Vector2D dir1 = Vector2D.subtract(current, prev);
            Vector2D dir2 = Vector2D.subtract(next, current);
            dir1.normalize();
            dir2.normalize();
            
            // If directions are significantly different, keep the point
            double dotProduct = dir1.x * dir2.x + dir1.y * dir2.y;
            if (dotProduct < 0.95) { // Allow small deviations
                simplified.add(current);
            }
        }
        
        simplified.add(path.get(path.size() - 1));
        return simplified;
    }
    
    /**
     * Creates a direct path when A* fails (fallback).
     * @param start Starting position
     * @param end Target position
     * @return Simple direct path
     */
    private static List<Vector2D> createDirectPath(Vector2D start, Vector2D end) {
        List<Vector2D> path = new ArrayList<>();
        path.add(start.copy());
        path.add(end.copy());
        return path;
    }
    
    /**
     * Gets all valid neighbor positions in grid coordinates.
     * @param gridPos Current grid position
     * @return List of neighbor positions
     */
    private static List<Vector2D> getNeighbors(Vector2D gridPos) {
        List<Vector2D> neighbors = new ArrayList<>();
        
        // 8-directional movement (including diagonals)
        int[][] directions = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},  // Cardinal directions
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}  // Diagonal directions
        };
        
        for (int[] dir : directions) {
            Vector2D neighbor = new Vector2D(
                gridPos.x + dir[0],
                gridPos.y + dir[1]
            );
            neighbors.add(neighbor);
        }
        
        return neighbors;
    }
    
    /**
     * Checks if a grid position is blocked by obstacles.
     * @param gridPos Position in grid coordinates
     * @param obstacles List of obstacles to check against
     * @return True if position is blocked
     */
    private static boolean isPositionBlocked(Vector2D gridPos, List<Obstacle> obstacles) {
        Vector2D worldPos = gridToWorld(gridPos);
        
        for (Obstacle obstacle : obstacles) {
            // Check if point is inside obstacle with small padding
            double padding = GRID_SIZE / 2.0;
            if (worldPos.x > obstacle.getX() - padding &&
                worldPos.x < obstacle.getX() + obstacle.getWidth() + padding &&
                worldPos.y > obstacle.getY() - padding &&
                worldPos.y < obstacle.getY() + obstacle.getHeight() + padding) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Heuristic function for A* (Euclidean distance).
     * @param a First position
     * @param b Second position
     * @return Estimated distance
     */
    private static double heuristic(Vector2D a, Vector2D b) {
        return Vector2D.distance(a, b);
    }
    
    /**
     * Converts world coordinates to grid coordinates.
     * @param worldPos Position in world coordinates
     * @return Position in grid coordinates
     */
    private static Vector2D worldToGrid(Vector2D worldPos) {
        return new Vector2D(
            Math.floor(worldPos.x / GRID_SIZE),
            Math.floor(worldPos.y / GRID_SIZE)
        );
    }
    
    /**
     * Converts grid coordinates to world coordinates (center of cell).
     * @param gridPos Position in grid coordinates
     * @return Position in world coordinates
     */
    private static Vector2D gridToWorld(Vector2D gridPos) {
        return new Vector2D(
            gridPos.x * GRID_SIZE + GRID_SIZE / 2.0,
            gridPos.y * GRID_SIZE + GRID_SIZE / 2.0
        );
    }
}
