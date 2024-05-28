import java.util.*;
import java.awt.Point;

public class DistanceRework {
    private static HashSet<Point> people = new HashSet<>();
    private static int[] gridSize = new int[2];

    private static List<HashMap<Point, Integer>> allMinDistances = new ArrayList<>();
    private static List<List<Point>> allPaths = new ArrayList<>();
    /*
     * Notes on storage: HashMap has: point on grid then key[0] = total & key[1] =
     * min
     */
    public static void main(String[] args) {
        // get input
        ArrayList<ArrayList<int[]>> scenarios = stdIn();
        // loop through each scenario
        for (ArrayList<int[]> scenario : scenarios) {
            System.out.println();
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
            // create hashmap of all points stats (min & total)
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
            // stored as (current point, (person, distance from person))
            HashMap<Point, HashMap<Point, Integer>> allDistances = allDistancesAtPts(workingMap);
            List<HashMap<Point, Integer>> allMins = findAllPaths(allDistances, new Point(0, 0), new Point(gridSize[0] - 1, gridSize[1] - 1));
            int total = 0;
            int pathNum = -1;
            for(int i = 0; i < allMins.size(); i++){
                HashMap<Point, Integer> path = allMins.get(i);
                int temp = path.values().stream().mapToInt(Integer::intValue).sum();
                if(temp > total){
                    total = temp;
                    pathNum = i;
                }
            }
            HashSet<Point> path = new HashSet<>(allPaths.get(pathNum));
            visualisation(path);
            System.out.println("min: " + minimumValue + " total: " + total);
            System.out.println(allMins.get(pathNum).toString());
            people = new HashSet<>();
            allMinDistances = new ArrayList<>();
            allPaths = new ArrayList<>();
        }
    }
    // set variables for the path and distance
    public static void setVariables(List<HashMap<Point, Integer>> allMin, List<List<Point>> allPath){
        allMinDistances = allMin;
        allPaths = allPath;
    }

    public static List<HashMap<Point, Integer>> findAllPaths(HashMap<Point, HashMap<Point, Integer>> allDistances, Point start, Point end) {
        List<List<Point>> allPths = new ArrayList<>();
        List<Point> currentPath = new ArrayList<>();
        Set<Point> visited = new HashSet<>();
        HashMap<Point, Integer> totalMin = startingMinToEach(start, end);
        List<HashMap<Point, Integer>> minDists = new ArrayList<>();
        dfs(allDistances, start, end, visited, currentPath, allPths, totalMin, minDists);
        setVariables(minDists, allPths);
        return allMinDistances;
    }

    private static void dfs(HashMap<Point, HashMap<Point, Integer>> allDistances, Point current, Point end,
                            Set<Point> visited, List<Point> currentPath, List<List<Point>> allPaths, HashMap<Point, Integer> totalMin, List<HashMap<Point, Integer>> allMinDistances) {
        // Add current point to the path and mark as visited
        currentPath.add(current);
        visited.add(current);

        // If we've reached the end point, add the current path to the list of all paths
        if (current.equals(end)) {
            allMinDistances.add(totalMin);
            allPaths.add(new ArrayList<>(currentPath));
        } else {
            // Explore neighbors (right and down)
            Point[] neighbors = {
                new Point(current.x + 1, current.y),
                new Point(current.x, current.y + 1),
            };

            for (Point neighbor : neighbors) {
                if (allDistances.containsKey(neighbor) && !visited.contains(neighbor)){
                    totalMin = returnMinTotal(totalMin, allDistances.get(neighbor));
                    dfs(allDistances, neighbor, end, visited, currentPath, allPaths, totalMin, allMinDistances);
                }
            }
        }

        // Backtrack: remove the current point from the path and mark it as unvisited
        currentPath.remove(currentPath.size() - 1);
        visited.remove(current);
    }

    /* below this, code is fine */
    // checks if path is possible
    public static Boolean canFormPath(HashSet<Point> points) {
        if (points.isEmpty()) {
            return false;
        }
        if(!points.contains(new Point(0,0))){
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

            // If the current point is the end point, return true
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

        // If the queue is empty and we haven't returned true, there is no path to the end point
        return false;
    }
    
    public static int getTotalDistance(HashMap<Point, HashMap<Point, Integer>> allDistances){
        int output = 0;
        HashSet<HashMap<Point, Integer>> work = new HashSet<>(allDistances.values());
        HashMap<Point, Integer> temp = new HashMap<>();
        for(HashMap<Point, Integer> h : work){
            for(Point p : h.keySet()){
                if(!temp.containsKey(p) || temp.get(p) > h.get(p)){
                    //System.out.println("adding: " + p.x + " " + p.y + " value: " + h.get(p));
                    temp.put(p, h.get(p));
                }
            }
        }
        for (int value : temp.values()) {
            output += value;
        }
        return output;
    }
    public static HashMap<Point, Integer> returnMinTotal(HashMap<Point, Integer> current, HashMap<Point, Integer> next){
        HashMap<Point, Integer> output = new HashMap<>();
        for(Point p : current.keySet()){
            if(next.containsKey(p)){
                int temp = next.get(p);
                if(temp < current.get(p)){
                    output.put(p, temp);
                } else {
                    output.put(p, current.get(p));
                }
            }
        }
        return output;
    }
    /**
     * iterates through all possible points
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

    public static HashMap<Point, HashMap<Point, Integer>> allDistancesAtPts(HashMap<Point, Integer> workingMap){
        HashMap<Point, HashMap<Point, Integer>> output = new HashMap<>();
        for(Point w : workingMap.keySet()){
            for(Point p : people){
                int dist = Math.abs(p.x - w.x) + Math.abs(p.y - w.y);
                if(output.containsKey(w)){
                    output.get(w).put(p, dist);
                } else { 
                    HashMap<Point, Integer> temp = new HashMap<>();
                    temp.put(p, dist);
                    output.put(w, temp);
                }
            }
        }
        return output;
    }

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

    // minimum values to start with, this dictates where we start in the pathfinding
    // loops
    public static int startingMin(int start, int end) {
        int output;
        if (start < end) {
            output = start;
        } else {
            output = end;
        }
        return output;
    }

    public static HashMap<Point, Integer> startingMinToEach(Point start, Point end) {
        HashMap<Point, Integer> output = new HashMap<>();
        for (Point p : people) {
            int distStart = Math.abs(p.x - start.x) + Math.abs(p.y - start.y);
            int distEnd = Math.abs(p.x - end.x) + Math.abs(p.y - end.y);
            if (distStart < distEnd) {
                output.put(p, distStart);
            } else {
                output.put(p, distEnd);
            }
        }
        return output;
    }

    /** this method returns the closest distance to point ever */
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
    /* this visualises the graph */
    public static void visualisation(HashSet<Point> path) {
        char[][] grid = new char[gridSize[0]][gridSize[1]];
        for (int i = 0; i < gridSize[0]; i++) {
            for (int j = 0; j < gridSize[1]; j++) {
                grid[i][j] = '.';
            }
        }
        for (Point s : path) {
            grid[s.x][s.y] = 'X';
        }
        for (Point p : people) {
            grid[p.x][p.y] = 'P';
        }

        for (char[] row : grid) {
            for (char c : row) {
                System.out.print(c + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

}
