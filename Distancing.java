import java.util.*;
import java.awt.Point;

public class Distancing {
    private static Set<Point> people = new HashSet<>();
    private static int[] gridSize = new int[2];

    public static void main(String[] args) {
        // Get input
        List<List<int[]>> scenarios = stdIn();

        // Process each scenario
        for (List<int[]> scenario : scenarios) {
            processScenario(scenario);
        }
    }
    /**
     * Processes a scenario by initializing the variables, 
     * creating a map of all points and their closest distances to a person, 
     * finding the minimum value for which a path can be formed, and calculating the total using the best path.
     * @param scenario
     */
    private static void processScenario(List<int[]> scenario) {
        // Initialize variables for the scenario
        initializeScenario(scenario); 
        // Create a map of all points and their closest distances to a person
        Map<Point, Integer> allStates = allStates();
        int startingMin = getStartingMin(allStates.get(new Point(0, 0)), allStates.get(new Point(gridSize[0] - 1, gridSize[1] - 1))); 
        // Find the minimum value for which a path can be formed
        int minimumValue = findMinimumValue(startingMin, allStates); 
        // Calculate the total using the best path
        int total = calculateTotal(minimumValue); 
        // Output the results
        System.out.println("min " + minimumValue + ", total " + total);
        people.clear();
    }
    /** 
     * Initializes the scenario by setting the grid size and adding people to the set.
     */
    private static void initializeScenario(List<int[]> scenario) {
        for (int i = 0; i < scenario.size(); i++) {
            if (i == 0) {
                gridSize = scenario.get(i);
            } else {
                people.add(new Point(scenario.get(i)[0], scenario.get(i)[1]));
            }
        }
    }
    /**
     * Method that finds the minimum value for which a path can be formed.
     */
    private static int findMinimumValue(int startingMin, Map<Point, Integer> allStates) {
        Map<Point, Integer> workingMap = new HashMap<>();
        for (int min = startingMin; min > 0; min--) {
            Map<Point, Integer> temp = updateWorkingMap(workingMap, allStates, min);
            if (canFormPath(new HashSet<>(temp.keySet()))) {
                return min;
            }
            workingMap = temp;
        }
        return 0;
    }
    /**
     * Method that calculates the total using the best path.
     * @param minimumValue
     * @returns the total
     */
    private static int calculateTotal(int minimumValue) {
        List<Point> usablePoints = filterPointsByDistance(minimumValue);
        if (gridSize[0] * gridSize[1] < 100 && people.size() < 10) {
            return findBestPath(usablePoints);
        } else {
            return getTotal(aStarter());
        }
    }
    /** returns manhattan distance between the two @param Point */
    private static int ManhattanDistance(Point p1, Point p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p2.y - p1.y);
    }
    /** finds the best path out of the points available using dfs (this is if the grid size is smaller than 100) */
    private static int findBestPath(List<Point> availablePoints) {
        Point start = new Point(0, 0);
        Point end = new Point(gridSize[0] - 1, gridSize[1] - 1);
        Set<Integer> pathTotals = new HashSet<>();
        pathTotals.add(getTotal(aStarter()));

        dfs(start, end, availablePoints, new ArrayList<>(), pathTotals);

        return pathTotals.stream().max(Integer::compareTo).orElse(0);
    }
    /** dfs implementation */
    private static void dfs(Point current, Point end, List<Point> availablePoints, List<Point> path, Set<Integer> pathTotals) {
        path.add(current);
        // skips over paths that aren't as good as the best we've found so far
        if (getTotal(path) <= bestTotal(pathTotals)) {
            path.remove(current);
            return;
        }
        if (current.equals(end)) {
            pathTotals.add(getTotal(path));
        } else {
            for (Point neighbor : getNeighbors(current)) {
                if (availablePoints.contains(neighbor) && !path.contains(neighbor)) {
                    dfs(neighbor, end, availablePoints, path, pathTotals);
                }
            }
        }
        path.remove(current);
    }
    /** gets neighbours of a specified point */
    private static Point[] getNeighbors(Point current) {
        return new Point[]{
                new Point(current.x, current.y + 1),
                new Point(current.x + 1, current.y),
                new Point(current.x, current.y - 1),
                new Point(current.x - 1, current.y)
        };
    }
    /** simple streaming sum method, here to make the code look less messy */
    private static int bestTotal(Set<Integer> pathTotals) {
        return pathTotals.stream().max(Integer::compareTo).orElse(0);
    }
    /** @returns the total min sum from the given @param path */
    private static int getTotal(List<Point> path) {
        int total = 0;
        for (Point person : people) {
            int min = path.stream()
                    .mapToInt(point -> ManhattanDistance(person, point))
                    .min()
                    .orElse(Integer.MAX_VALUE);
            total += min;
        }
        return total;
    }
    /** filters all points by distance */
    private static List<Point> filterPointsByDistance(int minValue) {
        List<Point> result = new ArrayList<>();
        for (int x = 0; x < gridSize[0]; x++) {
            for (int y = 0; y < gridSize[1]; y++) {
                Point currentPoint = new Point(x, y);
                if (people.stream().allMatch(person -> ManhattanDistance(currentPoint, person) >= minValue)) {
                    result.add(currentPoint);
                }
            }
        }
        return result;
    }
    /** this checks if a path can be formed when searching for largest possible minimum value */
    private static boolean canFormPath(Set<Point> points) {
        if (points.isEmpty() || !points.contains(new Point(0, 0))) {
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
            for (Point neighbor : getNeighbors(current)) {
                if (points.contains(neighbor) && visited.add(neighbor)) {
                    queue.add(neighbor);
                }
            }
        }
        return false;
    }
    /** returns all distances */
    private static Map<Point, Integer> allStates() {
        Map<Point, Integer> output = new HashMap<>();
        for (int i = 0; i < gridSize[0]; i++) {
            for (int j = 0; j < gridSize[1]; j++) {
                Point p = new Point(i, j);
                if (!people.contains(p)) {
                    output.put(p, closestPtDist(p));
                }
            }
        }
        return output;
    }
    /** method that updates the working map */
    private static Map<Point, Integer> updateWorkingMap(Map<Point, Integer> current, Map<Point, Integer> all, int min) {
        Map<Point, Integer> output = new HashMap<>(current);
        for (Map.Entry<Point, Integer> entry : all.entrySet()) {
            Point p = entry.getKey();
            int tempMin = entry.getValue();
            if (!current.containsKey(p) && min <= tempMin) {
                output.put(p, tempMin);
            }
        }
        return output;
    }
    /** simple method that returns the smallest number out of two */
    private static int getStartingMin(int start, int end) {
        return Math.min(start, end);
    }
    /** returns the closest minimum distance at that point */
    private static int closestPtDist(Point current) {
        return people.stream()
                .mapToInt(p -> ManhattanDistance(p, current))
                .min()
                .orElse(Integer.MAX_VALUE);
    }
    /** this is the stdIn() which processes the input */
    private static List<List<int[]>> stdIn() {
        List<String> rawIn = new ArrayList<>();
        List<List<int[]>> output = new ArrayList<>();
        output.add(new ArrayList<>());

        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            rawIn.add(sc.nextLine());
        }

        for (String s : rawIn) {
            if (s.trim().isEmpty()) {
                output.add(new ArrayList<>());
            } else {
                output.get(output.size() - 1).add(Arrays.stream(s.trim().split("\\s+"))
                        .mapToInt(Integer::parseInt).toArray());
            }
        }
        sc.close();
        return output;
    }
    /** this is the aStar algorithm starter, it handles setting it up. */
    private static List<Point> aStarter() {
        List<State> path = new ArrayList<>();
        Point goal = new Point(gridSize[0] - 1, gridSize[1] - 1);
        Point current = new Point(0, 0);
        aStar(current, goal, path);

        List<Point> output = new ArrayList<>();
        for (State s : path) {
            output.add(s.position);
        }
        return output;
    }
    /** a star search algorithm, used for larger gridSizes to save on time, less accurate but far faster than dfs for bigger problems. */
    private static void aStar(Point current, Point goal, List<State> path) {
        PriorityQueue<State> costs = new PriorityQueue<>(Comparator.comparingInt(a -> a.cost + a.distance));
        Map<Point, Integer> mapCosts = new HashMap<>();

        costs.add(new State(current, 0, ManhattanDistance(current, goal)));
        mapCosts.put(current, 0);

        while (!costs.isEmpty()) {
            State s = costs.poll();
            if (s.position.equals(goal)) {
                path.add(s);
                while (s.previous != null) {
                    path.add(0, s.previous);
                    s = s.previous;
                }
                return;
            }

            for (Point neighbor : getNeighbors(s.position)) {
                int tempCost = s.cost + 1;
                if (!mapCosts.containsKey(neighbor) || tempCost < mapCosts.get(neighbor)) {
                    mapCosts.put(neighbor, tempCost);
                    costs.add(new State(neighbor, tempCost, ManhattanDistance(neighbor, goal), s));
                }
            }
        }
    }
    /** state object used by the a star search algorithm */
    private static class State {
        private final Point position;
        private final int cost;
        private final int distance;
        private final State previous;

        public State(Point position, int cost, int distance, State previous) {
            this.position = position;
            this.cost = cost;
            this.distance = distance;
            this.previous = previous;
        }

        public State(Point position, int cost, int distance) {
            this(position, cost, distance, null);
        }
    }
}
