import java.util.*;
import java.awt.Point;

public class Distancing{
    private static ArrayList<Point> people = new ArrayList<>(); // arraylist of the points that people are at
    private static int[] gridSize = new int[2]; // size of the grid (also the end point)

    public static void main(String[] args){
        ArrayList<String> rawIn = new ArrayList<>();
        Scanner sc = new Scanner(System.in);
        while(sc.hasNextLine()){
            rawIn.add(sc.nextLine());
        }
        sc.close();
        inputHandler(rawIn);
        ArrayList<Point> points = aStarter();
        int smallestDistance = Integer.MAX_VALUE;
        for(int i=0; i<points.size(); i++){
            int temp = closestPointDistance(points.get(i), people);
            if(temp < smallestDistance){
                smallestDistance = temp;
            }
        }
        System.out.println("min " + smallestDistance + ", total " + points.size());
        visualisation(points);
    }
    // handler for the recursive function
    public static ArrayList<Point> aStarter(){
        ArrayList<Point> path = new ArrayList<>();
        Point goal = new Point(gridSize[0], gridSize[1]);
        Point current = new Point(0,0);
        aStar(current, goal, path);
        return path;
    }
    // the actual search algorithm
    public static void aStar(Point current, Point goal, ArrayList<Point> path){
        PriorityQueue<State> costs = new PriorityQueue<>(Comparator.comparingInt(a -> a.sumOfDistance));
        Map<Point, Integer> costAtPt = new HashMap<>();
        Map<Point, Point> cameFrom = new HashMap<>();
        costs.add(new State(current, sumOfDistance(current)));
        costAtPt.put(current, 0);
        
        while(!costs.isEmpty()){
            State curr = costs.poll(); // state with the lowest cost (best path)
            if(curr.position.equals(goal)){
                Point temp = curr.position;
                while(temp != null){
                    path.add(temp);
                    temp = cameFrom.get(temp);
                }
                Collections.reverse(path);
                return;
            }
            // neighbours will be the points to the right and below the current point
            Point neighbourX = new Point(curr.position.x + 1, curr.position.y);
            Point neighbourY = new Point(curr.position.x, curr.position.y + 1);
            // i think this is right? it uses manhattan distance and then incentivises staying as far away as possible from the closest point
            // however it may have some issues if the closest point is further away than the hueristic
            int costX = curr.sumOfDistance + heuristic(neighbourX, goal) - closestPointDistance(neighbourX, people);
            int costY = curr.sumOfDistance + heuristic(neighbourY, goal) - closestPointDistance(neighbourY, people);
            if(!costAtPt.containsKey(neighbourY) || costY < costAtPt.get(neighbourY)){
                costAtPt.put(neighbourY, costY);
                costs.add(new State(neighbourY, costY));
                cameFrom.put(neighbourY, curr.position);
            }
            if(!costAtPt.containsKey(neighbourX) || costX < costAtPt.get(neighbourX)){
                costAtPt.put(neighbourX, costX);
                costs.add(new State(neighbourX, costX));
                cameFrom.put(neighbourX, curr.position);
            }
        }
    }

    public static int heuristic(Point current, Point goal){
        int distance = Math.abs(current.x - goal.x) + Math.abs(current.y - goal.y);
        return distance;
    }

    public static int sumOfDistance(Point current){
        int output = 0;
        for(Point p : people){
            output += Math.abs(current.getX() - p.getX());
            output += Math.abs(current.getY() - p.getY());
        }
        return output;
    }

    // handle input - doesn't do error handling, just adds input to the correct variables
    public static void inputHandler(ArrayList<String> input){
        gridSize = new int[]{Integer.parseInt(input.get(0).split(" ")[0]), Integer.parseInt(input.get(0).split(" ")[1])};
        for(int i = 1; i < input.size(); i++){
            String line = input.get(i);
            String[] split = line.split(" ");
            if(Character.isDigit(split[0].trim().charAt(0)) && Character.isDigit(split[1].trim().charAt(0))){
                people.add(new Point(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
            }
        }
    }
    public static int closestPointDistance(Point current, ArrayList<Point> points){
        int min = Integer.MAX_VALUE;
        for(Point p : points){
            int distance = Math.abs(current.x - p.x) + Math.abs(current.y - p.y);
            if(distance < min){
                min = distance;
            }
        }
        return min;
    }

    public static void visualisation(ArrayList<Point> points){
        char[][] grid = new char[gridSize[0] + 1][gridSize[1] + 1];
        for(char[] row : grid){
            Arrays.fill(row, '.');
        }
        for(Point p : points){
            grid[p.x][p.y] = 'x';
        }
        for(Point p : people){
            grid[p.x][p.y] = 'P';
        }
        
        // Print the grid with x and y axis
        System.out.print("   ");
        for (int i = 0; i <= gridSize[1]; i++) {
            System.out.print(" " + i);
        }
        System.out.println();
        System.out.print("   ");
        for(int i = 0; i <= gridSize[1]; i++){
            System.out.print("--");
        }
        System.out.println();
        for (int i = 0; i <= gridSize[0]; i++) {
            System.out.print(i + " | ");
            for (int j = 0; j <= gridSize[1]; j++) {
            System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
    }

    static class State{
        Point position;
        int sumOfDistance; // adds sum of previous distance also
        public State(Point position, int sumOfDistance){
            this.position = position;
            this.sumOfDistance = sumOfDistance;
        }
    }
}