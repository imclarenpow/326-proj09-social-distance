import java.util.*;
import java.awt.Point;

public class DistanceTristan {
    private static HashSet<Point> people = new HashSet<>();
    private static int[] gridSize = new int[2];

    public static void main(String[] args) {
        // get input
        ArrayList<ArrayList<int[]>> scenarios = stdIn();
        // loop through each scenario
        for (ArrayList<int[]> scenario : scenarios) {
            // this adds the needed elements to the variables
            for (int i = 0; i < scenario.size(); i++) {
                // add first element to gridSize
                if (i == 0) {
                    gridSize = scenario.get(i);
                }
                // add all other elements to people
                else {
                    people.add(new Point(scenario.get(i)[0], scenario.get(i)[1]));
                }
            }
            // create hashmap of all points (that aren't people) and their closest distance
            // to a person
            HashMap<Point, Integer> allStates = allStates();

            int startingMin = startingMin(allStates.get(new Point(0, 0)),
                    allStates.get(new Point(gridSize[0] - 1, gridSize[1] - 1)));

            HashMap<Point, Integer> workingMap = new HashMap<>();
            int minimumValue = 0;
            for (int min = startingMin; min > 0; min--) {
                HashMap<Point, Integer> temp = workingMap(workingMap, allStates, min);
                if (canFormPath(new HashSet<>(temp.keySet()))) {
                    minimumValue = min;
                    break;
                }
                workingMap = temp;
            }

            List<Point> useablePoints = filterPointsByDistance(minimumValue);
            int total = findBestPath(useablePoints);
            System.out.println("min " + minimumValue + ", total " + total);
            //if (args.length!=0 && args[0].equals("-v")) {
            //    visualisation(bestPath);
            //}
            people.clear();
        }
    }

    /**
     * Calculates the Manhattan distance between two points
     * 
     * @param p1 The first point
     * @param p2 The second point
     * @return The Manhattan distance between the two points
     */
    public static int ManhattanDistance(Point p1, Point p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }

    /**
     * Finds the best path from the start point to the end point using only the
     * available points
     * 
     * @param availablePoints The points that can be visited
     * @return The best path from the start point to the end point
     */
    public static int findBestPath(List<Point> availablePoints) {
        Point start = new Point(0, 0);
        Point end = new Point(gridSize[0] - 1, gridSize[1] - 1);
        Set<Integer> pathTotals = new HashSet<>();
        // TODO: this is the line that uses a star, if you want to remove the a star code this is the only line you need to remove
        pathTotals.add(getTotal(aStarter()));

        // perform a depth-first search to find all possible paths and their totals
        dfs(start, end, availablePoints, new ArrayList<>(), pathTotals);

        // find the path with the largest "total" and return it
        /*Set<Point> bestBranch = null;
        int maxTotal = Integer.MIN_VALUE;
        for (Map.Entry<Set<Point>, Integer> entry : pathTotals.entrySet()) {
            if (entry.getValue() > maxTotal) {
                maxTotal = entry.getValue();
                bestBranch = entry.getKey();
            }
        }*/
        int bestBranch = 0;
        for(int i : pathTotals){
            if(i>bestBranch){
                bestBranch = i;
            }
        }
        return bestBranch;
    }

    /**
     * Performs a depth-first search to find all possible paths from the current
     * point to the end point
     * 
     * @param current         The current point
     * @param end             The end point
     * @param availablePoints The points that can be visited
     * @param path            The current path
     * @param pathTotals      A map to store the total distance of each path
     */
    private static void dfs(Point current, Point end, List<Point> availablePoints, List<Point> path,
            Set<Integer> pathTotals) {
        path.add(current);
        if(getTotal(path)<=bestTotal(pathTotals)){
            path.remove(current);
            return;
        }
        if (current.equals(end)) {
            int total = getTotal(path);
            pathTotals.add(total);
        } else {
            Point[] neighbours = {
                    new Point(current.x, current.y + 1),
                    new Point(current.x + 1, current.y),
                    new Point(current.x, current.y - 1),
                    new Point(current.x - 1, current.y)
            };

            for (Point neighbour : neighbours) {
                if (availablePoints.contains(neighbour) && !path.contains(neighbour)) {
                    dfs(neighbour, end, availablePoints, path, pathTotals);
                }
            }
        }

        path.remove(current);
    }
    public static int bestTotal(Set<Integer> pathTotals){
        int best = 0;
        for(int i : pathTotals){
            if(i>best){
                best = i;
            }
        }
        return best;
    }
    /**
     * Calculates the total distance of a path
     * 
     * @param path The path to calculate the total distance of
     * @return The total distance of the path
     */
    public static int getTotal(List<Point> path) {
        int total = 0;
        for (Point person : people) {
            int min = Integer.MAX_VALUE;
            for (Point point : path) {
                int temp = ManhattanDistance(person, point);
                if (temp < min) {
                    min = temp;
                }
            }
            total += min;
        }
        //System.out.println("got total: " + total);
        return total;
    }

    /**
     * Filters the points by distance from the people. If a point on the grid is
     * closer than the best "min" to any person, it is removed from the list of
     * available points.
     * 
     * @param minValue The minimum distance a point must be from all people
     * @return The list of points that are far enough away from all people
     */
    public static List<Point> filterPointsByDistance(int minValue) {
        int maxX = gridSize[0] - 1;
        int maxY = gridSize[1] - 1;

        List<Point> result = new ArrayList<>();

        for (int x = 0; x <= maxX; x++) {
            for (int y = 0; y <= maxY; y++) {
                Point currentPoint = new Point(x, y);
                boolean isFarEnough = true;
                for (Point person : people) {
                    if (ManhattanDistance(currentPoint, person) < minValue) {
                        isFarEnough = false;
                        break;
                    }
                }
                if (isFarEnough) {
                    result.add(currentPoint);
                }
            }
        }

        return result;
    }

    /**
     * Checks if a path can be formed from the given points
     * 
     * @param points The points that can be visited
     * @return Whether a path can be formed
     */
    public static Boolean canFormPath(HashSet<Point> points) {
        if (points.isEmpty()) {
            return false;
        }
        if (!points.contains(new Point(0, 0))) {
            return false;
        }
        Point start = new Point(0, 0);
        Point end = new Point(gridSize[0] - 1, gridSize[1] - 1);
        Set<Point> visited = new HashSet<>();
        Queue<Point> queue = new LinkedList<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            if (current.equals(end)) {
                return true;
            }

            Point[] neighbours = {
                    new Point(current.x, current.y + 1),
                    new Point(current.x + 1, current.y),
                    new Point(current.x, current.y - 1),
                    new Point(current.x - 1, current.y)
            };

            for (Point neighbour : neighbours) {
                if (points.contains(neighbour) && !visited.contains(neighbour)) {
                    visited.add(neighbour);
                    queue.add(neighbour);
                }
            }
        }

        return false;
    }

    /**
     * Iterates through all possible points
     * 
     * @returns hashmap of points and totalDistance and closestPtDist
     */
    public static HashMap<Point, Integer> allStates() {
        HashMap<Point, Integer> output = new HashMap<>();
        for (int i = 0; i < gridSize[0]; i++) {
            for (int j = 0; j < gridSize[1]; j++) {
                // can't walk on people
                if (people.contains(new Point(i, j))) {
                    continue;
                }
                // else add
                Point p = new Point(i, j);
                output.put(p, closestPtDist(p));
            }
        }
        return output;
    }

    /**
     * Returns a hashmap of all points that are within the minimum
     * 
     * @param current the current hashmap of points
     * @param all     the hashmap of all points
     * @param min     the minimum distance
     * @return hashmap of points and totalDistance and closestPtDist
     */
    public static HashMap<Point, Integer> workingMap(HashMap<Point, Integer> current, HashMap<Point, Integer> all,
            int min) {
        HashMap<Point, Integer> output = current;
        for (Point p : all.keySet()) {
            if (current.containsKey(p)) {
                continue;
            } // skip if already in current
            int tempMin = all.get(p);
            if (min <= tempMin) {
                output.put(p, tempMin);
            }
        }
        return output;
    }

    /**
     * Returns the starting minimum of the two points
     * 
     * @param start the starting point
     * @param end   the ending point
     * @return the minimum of the two points
     */
    public static int startingMin(int start, int end) {
        return Math.min(start, end);
    }

    /**
     * Returns the closest distance to a person
     * 
     * @param current the current point
     * @return the closest distance to a person
     */
    public static int closestPtDist(Point current) {
        int min = Integer.MAX_VALUE;
        for (Point p : people) {
            int dist = Math.abs(p.x - current.x) + Math.abs(p.y - current.y);
            if (dist < min) {
                min = dist;
            }
        }
        return min;
    }

    /**
     * this method handles standard in, this is to declutter the main method
     * 
     * @return the input in a 2D arraylist of all scenarios and their points
     */
    public static ArrayList<ArrayList<int[]>> stdIn() {

        ArrayList<String> rawIn = new ArrayList<>();
        ArrayList<ArrayList<int[]>> output = new ArrayList<>();
        output.add(new ArrayList<int[]>());
        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            rawIn.add(sc.nextLine());
        }
        for (String s : rawIn) {
            if (s.trim().isEmpty()) {
                output.add(new ArrayList<int[]>());
            } else {
                output.get(output.size() - 1).add(Arrays.stream(s.trim().split("\\s+"))
                        .mapToInt(Integer::parseInt).toArray());
            }
        }
        sc.close();
        return output;
    }

    /**
     * Visualises the path
     * 
     * @param path the path to visualise
     */
    public static void visualisation(Set<Point> path) {
        char[][] grid = new char[gridSize[0]][gridSize[1]];
        for (int i = 0; i < gridSize[0]; i++) {
            for (int j = 0; j < gridSize[1]; j++) {
                grid[i][j] = '.';
            }
        }

        for (Point p : people) {
            grid[p.x][p.y] = 'P';
        }
        for (Point s : path) {
            grid[s.x][s.y] = 'X';
        }

        for (char[] row : grid) {
            for (char c : row) {
                System.out.print(c + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    /* the following code has been taken from the a star implementation previously and aims to prune out some of the unnecessary paths when dfs'ing */
    // handler for the recursive function
    public static List<Point> aStarter() {
        List<Point> path = new ArrayList<>();
        Point goal = new Point(gridSize[1] - 1, gridSize[0] - 1);
        Point current = new Point(0, 0);
        aStar(current, goal, path);
        return path;
    }

    // the actual search algorithm
    public static void aStar(Point current, Point goal, List<Point> path) {
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

    public static int closestPointDistance(Point current, HashSet<Point> points) {
        int min = Integer.MAX_VALUE;
        for (Point p : points) {
            int distance = Math.abs(current.x - p.x) + Math.abs(current.y - p.y);
            if (distance < min) {
                min = distance;
            }
        }
        return min;
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
