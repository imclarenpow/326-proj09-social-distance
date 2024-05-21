import java.util.*;
import java.awt.Point;

public class Distancing {
    private static ArrayList<Point> people = new ArrayList<>(); // arraylist of the points that people are at
    private static int[] gridSize = new int[2]; // size of the grid (also the end point)

    public static void main(String[] args) {
        ArrayList<String> rawIn = new ArrayList<>();
        Scanner sc = new Scanner(System.in);
        ArrayList<Point> points = new ArrayList<>();
        int totalDistance = 0;
        int smallestDistance = Integer.MAX_VALUE;
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.isEmpty() && rawIn.size() > 0) {
                inputHandler(rawIn);

                // Prints out the grid and the people but not the path,
                // for testing that the grid has been created correctly
                // testVisualisation();
                
                points = aStarter();
                totalDistance = 0;
                smallestDistance = Integer.MAX_VALUE;
                for (Point p : people) {
                    int temp = closestPointDistance(p, points);
                    totalDistance += temp;
                    if (temp < smallestDistance) {
                        smallestDistance = temp;
                    }
                }
                System.out.println("min " + smallestDistance + ", total " + totalDistance + "\n");
                visualisation(points);
                rawIn.clear();
                people.clear();
            } else {
                rawIn.add(line);
            }
        }

        sc.close();

        if (rawIn.size() > 0) {
            inputHandler(rawIn);
            points = aStarter();
            totalDistance = 0;
            smallestDistance = Integer.MAX_VALUE;
            for (Point p : people) {
                int temp = closestPointDistance(p, points);
                totalDistance += temp;
                if (temp < smallestDistance) {
                    smallestDistance = temp;
                }
            }
            System.out.println("min " + smallestDistance + ", total " + totalDistance + "\n");
            visualisation(points);
        }
    }

    // handler for the recursive function
    public static ArrayList<Point> aStarter() {
        ArrayList<Point> path = new ArrayList<>();
        Point goal = new Point(gridSize[1] - 1, gridSize[0] - 1);
        Point current = new Point(0, 0);
        aStar(current, goal, path);
        return path;
    }

    // the actual search algorithm
    public static void aStar(Point current, Point goal, ArrayList<Point> path) {
        PriorityQueue<State> costs = new PriorityQueue<>(Comparator.comparingInt(a -> a.cost));
        Map<Point, Integer> costAtPt = new HashMap<>();
        Map<Point, Point> cameFrom = new HashMap<>();
        costs.add(new State(current, sumOfDistance(current), closestPointDistance(current, people)));
        costAtPt.put(current, 0);

        while (!costs.isEmpty()) {
            State curr = costs.poll(); // state with the lowest cost (best path)
            if (curr.position.equals(goal)) {
                Point temp = curr.position;
                while (temp != null) {
                    path.add(temp);
                    temp = cameFrom.get(temp);
                }
                Collections.reverse(path);
                return;
            }
            // neighbours will be the points to the right and below the current point
            
            Point neighbourY = new Point(curr.position.x, curr.position.y + 1);
            Point neighbourX = new Point(curr.position.x + 1, curr.position.y);
            // i think this is right? it uses manhattan distance and then incentivises
            // staying as far away as possible from the closest point
            // however it may have some issues if the closest point is further away than the
            // hueristic

            int costY = curr.cost + heuristic(neighbourY, goal, curr.closestEver);
            int costX = curr.cost + heuristic(neighbourX, goal, curr.closestEver);
            if (!costAtPt.containsKey(neighbourY) || costY < costAtPt.get(neighbourY)) {
                costAtPt.put(neighbourY, costY);
                int closest = curr.closestEver;
                if(curr.closestEver > closestPointDistance(neighbourY, people)){
                    closest += curr.closestEver;
                    costY += 99;
                } else {
                    closest += closestPointDistance(neighbourY, people);
                }
                costs.add(new State(neighbourY, costY, closest));
                cameFrom.put(neighbourY, curr.position);
            }
            if (!costAtPt.containsKey(neighbourX) || costX < costAtPt.get(neighbourX)) {
                costAtPt.put(neighbourX, costX);
                int closest;
                if(curr.closestEver > closestPointDistance(neighbourY, people)){
                    closest = curr.closestEver;
                    costX += 99;
                } else {
                    closest = closestPointDistance(neighbourY, people);
                }
                costs.add(new State(neighbourX, costX, closest));
                cameFrom.put(neighbourX, curr.position);
            }
        }
    }
    // TODO: Figure out what a sensible heuristic function would be for this problem
    // something something if the minimum distance is the same as the previous minimum distance its not as bad
    public static int heuristic(Point current, Point goal, int closestEver) {
        // Calculate the distance to the goal
        int goalDistance = Math.abs(goal.x - current.x) + Math.abs(goal.y - current.y);
        
        // Combine the two components with some weight (you can adjust this weight)
        int weightedDistanceToGoal = 5 * goalDistance; // Weight for goal-directed movement
        double weightedDistanceToClosest = (2 * 1/(closestPointDistance(current, people)+2)); // Weight for avoiding people
        // don't go through people
        for(Point p : people){
            if(p.y == current.y && p.x == current.x){
                weightedDistanceToClosest += 999;
                break;
            }
        }
        // Return the combined heuristic value
        return weightedDistanceToGoal + (int)weightedDistanceToClosest;
    }
    
    

    public static int sumOfDistance(Point current) {
        int output = 0;
        for (Point p : people) {
            output += Math.abs(p.getX() - current.getX());
            output += Math.abs(p.getY() - current.getY());
        }
        return output;
    }

    // handle input - doesn't do error handling, just adds input to the correct
    // variables
    public static void inputHandler(ArrayList<String> input) {
        gridSize = new int[] { Integer.parseInt(input.get(0).split(" ")[0]),
                Integer.parseInt(input.get(0).split(" ")[1]) };
        for (int i = 1; i < input.size(); i++) {
            String line = input.get(i);
            String[] split = line.split(" ");
            if (Character.isDigit(split[0].trim().charAt(0)) && Character.isDigit(split[1].trim().charAt(0))) {
                people.add(new Point(Integer.parseInt(split[1]), Integer.parseInt(split[0])));
            }
        }
    }

    public static int closestPointDistance(Point current, ArrayList<Point> points) {
        int min = Integer.MAX_VALUE;
        for (Point p : points) {
            int distance = Math.abs(current.x - p.x) + Math.abs(current.y - p.y);
            if (distance < min) {
                min = distance;
            }
        }
        return min;
    }

    public static void visualisation(ArrayList<Point> points) {
        char[][] grid = new char[gridSize[0]][gridSize[1]];
        for (char[] row : grid) {
            Arrays.fill(row, '.');
        }
        for (Point p : points) {
            grid[p.y][p.x] = 'x';
        }
        for (Point p : people) {
            grid[p.y][p.x] = 'P';
        }

        // Print the grid with x and y axis
        System.out.print("   ");
        for (int i = 0; i < gridSize[1]; i++) {
            System.out.print(" " + i);
        }
        System.out.println();
        System.out.print("   ");
        for (int i = 0; i < gridSize[1]; i++) {
            System.out.print("--");
        }
        System.out.println();
        for (int j = 0; j < gridSize[0]; j++) {
            System.out.print(j + " | ");
            for (int i = 0; i < gridSize[1]; i++) {
                System.out.print(grid[j][i] + " ");
            }
            System.out.println();
        }
    }

    public static void testVisualisation() {
        char[][] grid = new char[gridSize[0]][gridSize[1]];
        for (char[] row : grid) {
            Arrays.fill(row, '.');
        }
        for (Point p : people) {
            grid[p.y][p.x] = 'P';
        }

        // Print the grid with x and y axis
        System.out.print("   ");
        for (int i = 0; i < gridSize[1]; i++) {
            System.out.print(" " + i);
        }
        System.out.println();
        System.out.print("   ");
        for (int i = 0; i < gridSize[1]; i++) {
            System.out.print("--");
        }
        System.out.println();
        for (int j = 0; j < gridSize[0]; j++) {
            System.out.print(j + " | ");
            for (int i = 0; i < gridSize[1]; i++) {
                System.out.print(grid[j][i] + " ");
            }
            System.out.println();
        }
    }

    static class State {

        int closestEver;
        Point position;
        int cost; // adds sum of previous distance also

        public State(Point position, int cost, int closestEver) {
            this.position = position;
            this.cost = cost;
            this.closestEver = closestEver;
        }
    }
}