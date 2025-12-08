package application;

import java.util.*;

/**
 * A* pathfinding algorithm for grid-based enemy AI.
 * Finds optimal paths around obstacles to reach the player.
 */
public class Pathfinder {
    
    /**
     * Node class for A* pathfinding.
     */
    private static class Node implements Comparable<Node> {
        int x, y;                   // Grid position
        Node parent;                // Parent node in path
        double gCost;               // Cost from start
        double hCost;               // Heuristic cost to goal
        double fCost;               // Total cost (g + h)
        
        Node(int x, int y) {
            this.x = x;
            this.y = y;
            this.parent = null;
            this.gCost = 0;
            this.hCost = 0;
            this.fCost = 0;
        }
        
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
            return this.x == other.x && this.y == other.y;
        }
        
        @Override
        public int hashCode() {
            return x * 1000 + y;
        }
    }
    
    /**
     * Gets the next move direction for an enemy using A* pathfinding.
     * @param startX Enemy's current X position
     * @param startY Enemy's current Y position
     * @param targetX Player's X position
     * @param targetY Player's Y position
     * @param grid Game grid (0=empty, 1=wall, 2=enemy, 3=spikes, 4=campfire)
     * @return Array [dirX, dirY] where each is -1, 0, or 1, or null if no path
     */
    public int[] getNextMove(int startX, int startY, int targetX, int targetY, int[][] grid) {
        List<Node> path = findPath(startX, startY, targetX, targetY, grid);
        
        if (path == null || path.size() < 2) {
            return null; // No path found or already at target
        }
        
        // Get the next step in the path (index 1, since index 0 is current position)
        Node nextStep = path.get(1);
        
        // Calculate direction
        int dirX = Integer.compare(nextStep.x, startX);
        int dirY = Integer.compare(nextStep.y, startY);
        
        return new int[]{dirX, dirY};
    }
    
    /**
     * Finds a path using A* algorithm.
     * @param startX Start X position
     * @param startY Start Y position
     * @param targetX Target X position
     * @param targetY Target Y position
     * @param grid Game grid
     * @return List of nodes forming the path, or null if no path exists
     */
    private List<Node> findPath(int startX, int startY, int targetX, int targetY, int[][] grid) {
        int width = grid.length;
        int height = grid[0].length;
        
        // Initialize open and closed sets
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<Node> closedSet = new HashSet<>();
        Map<String, Node> allNodes = new HashMap<>();
        
        // Create start node
        Node startNode = new Node(startX, startY);
        startNode.gCost = 0;
        startNode.hCost = heuristic(startX, startY, targetX, targetY);
        startNode.calculateFCost();
        
        openSet.add(startNode);
        allNodes.put(startX + "," + startY, startNode);
        
        // A* main loop
        int maxIterations = 500; // Prevent infinite loops
        int iterations = 0;
        
        while (!openSet.isEmpty() && iterations < maxIterations) {
            iterations++;
            
            // Get node with lowest f cost
            Node current = openSet.poll();
            closedSet.add(current);
            
            // Check if reached goal
            if (current.x == targetX && current.y == targetY) {
                return reconstructPath(current);
            }
            
            // Check all neighbors (4-directional movement)
            int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}}; // Up, Down, Left, Right
            
            for (int[] dir : directions) {
                int neighborX = current.x + dir[0];
                int neighborY = current.y + dir[1];
                
                // Check bounds
                if (neighborX < 0 || neighborX >= width || neighborY < 0 || neighborY >= height) {
                    continue;
                }
                
                // Check if walkable (enemies can walk through campfires but not walls/spikes)
                int tileType = grid[neighborX][neighborY];
                if (tileType == 1 || tileType == 3) { // Wall or Spikes
                    continue;
                }
                
                // Create neighbor node
                String key = neighborX + "," + neighborY;
                Node neighbor = allNodes.get(key);
                
                if (neighbor == null) {
                    neighbor = new Node(neighborX, neighborY);
                    allNodes.put(key, neighbor);
                }
                
                // Skip if in closed set
                if (closedSet.contains(neighbor)) {
                    continue;
                }
                
                // Calculate costs
                double tentativeGCost = current.gCost + 1; // Each step costs 1
                
                // If this path is better or node not in open set
                if (tentativeGCost < neighbor.gCost || !openSet.contains(neighbor)) {
                    neighbor.parent = current;
                    neighbor.gCost = tentativeGCost;
                    neighbor.hCost = heuristic(neighborX, neighborY, targetX, targetY);
                    neighbor.calculateFCost();
                    
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }
        
        // No path found
        return null;
    }
    
    /**
     * Reconstructs the path from end node to start.
     * @param endNode Final node in path
     * @return List of nodes from start to end
     */
    private List<Node> reconstructPath(Node endNode) {
        List<Node> path = new ArrayList<>();
        Node current = endNode;
        
        while (current != null) {
            path.add(current);
            current = current.parent;
        }
        
        Collections.reverse(path);
        return path;
    }
    
    /**
     * Heuristic function (Manhattan distance for grid-based movement).
     * @param x1 Start X
     * @param y1 Start Y
     * @param x2 End X
     * @param y2 End Y
     * @return Estimated distance
     */
    private double heuristic(int x1, int y1, int x2, int y2) {
        return Math.abs(x2 - x1) + Math.abs(y2 - y1);
    }
}