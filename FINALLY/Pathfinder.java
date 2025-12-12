package application;

import java.util.*;

public class Pathfinder {
    private static class Node implements Comparable<Node> {
        int x, y;
        Node parent;
        double gCost;
        double hCost;
        double fCost;
        
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
    
    public int[] getNextMove(int startX, int startY, int targetX, int targetY, int[][] grid) {
        List<Node> path = findPath(startX, startY, targetX, targetY, grid);
        
        if (path == null || path.size() < 2) {
            return null;
        }
        
        Node nextStep = path.get(1);
        
        int dirX = Integer.compare(nextStep.x, startX);
        int dirY = Integer.compare(nextStep.y, startY);
        
        return new int[]{dirX, dirY};
    }
    
    private List<Node> findPath(int startX, int startY, int targetX, int targetY, int[][] grid) {
        int width = grid.length;
        int height = grid[0].length;
        
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<Node> closedSet = new HashSet<>();
        Map<String, Node> allNodes = new HashMap<>();
        
        Node startNode = new Node(startX, startY);
        startNode.gCost = 0;
        startNode.hCost = heuristic(startX, startY, targetX, targetY);
        startNode.calculateFCost();
        
        openSet.add(startNode);
        allNodes.put(startX + "," + startY, startNode);
        
        int maxIterations = 500;
        int iterations = 0;
        
        while (!openSet.isEmpty() && iterations < maxIterations) {
            iterations++;
            
            Node current = openSet.poll();
            closedSet.add(current);
            
            if (current.x == targetX && current.y == targetY) {
                return reconstructPath(current);
            }
            
            int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
            
            for (int[] dir : directions) {
                int neighborX = current.x + dir[0];
                int neighborY = current.y + dir[1];
                
                if (neighborX < 0 || neighborX >= width || neighborY < 0 || neighborY >= height) {
                    continue;
                }
                
                int tileType = grid[neighborX][neighborY];
                if (tileType == 1 || tileType == 3) {
                    continue;
                }
                
                String key = neighborX + "," + neighborY;
                Node neighbor = allNodes.get(key);
                
                if (neighbor == null) {
                    neighbor = new Node(neighborX, neighborY);
                    allNodes.put(key, neighbor);
                }
                
                if (closedSet.contains(neighbor)) {
                    continue;
                }
                
                double tentativeGCost = current.gCost + 1;
                
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
        
        return null;
    }
    
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
    
    private double heuristic(int x1, int y1, int x2, int y2) {
        return Math.abs(x2 - x1) + Math.abs(y2 - y1);
    }
}