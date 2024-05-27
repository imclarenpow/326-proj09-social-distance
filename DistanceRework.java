import java.util.*;
import java.awt.Point;

public class DistanceRework {
    private static ArrayList<Point> people = new ArrayList<>();
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
            HashSet<State> allStates = allStates();
            for(int i = 0; i < gridSize[0]; i++){
                for(int j = 0; j < gridSize[1]; j++){
                    Point currentPoint = new Point(i, j);
                    for (State state : allStates) {
                        if (state.position.equals(currentPoint)) {
                            System.out.println(state.position.x + " " + state.position.y + "  total: " + state.total + " min: " + state.closestPt);
                            break;
                        }
                    }
                }
            }
        }
    }

    public static HashSet<State> allStates(){
        HashSet<State> output = new HashSet<>();
        for(int i = 0; i < gridSize[0]; i++){
            for(int j = 0; j < gridSize[1]; j++){
                output.add(new State(new Point(i, j), 
                    returnDistances(new Point(i, j)),
                    0, closestPtDist(new Point(i, j))));
            }
        }
        return output;
    }

    /**
     * this method returns the minimum distance ever
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
     * this method returns a hashmap of all distances from the point to all the
     * people.
     */
    public static HashMap<Point, Integer> returnDistances(Point current) {
        HashMap<Point, Integer> distances = new HashMap<>();
        for (Point p : people) {
            distances.put(p, Math.abs(p.x - current.x) + Math.abs(p.y - current.y));
        }
        return distances;
    }

    /**
     * this method looks through the current point and previous hashMap and checks
     * if the minimum distance is breached. this is to add cost if it has.
     * if its equal to the minimum distance it doesn't care
     * 
     * @returns how many minimum distances have been changed
     */
    public static int minChanges(Point current, HashMap<Point, Integer> prevDist) {
        int output = 0;
        for (Point p : prevDist.keySet()) {
            int distance = Math.abs(current.x - p.x) + Math.abs(current.y - p.y);
            if (distance < prevDist.get(p)) {
                output++;
            }
        }
        return output;
    }

    public static int sumOfChanges(Point current, HashMap<Point, Integer> prevDist) {
        int output = 0;
        for (Point p : prevDist.keySet()) {
            int distance = Math.abs(current.x - p.x) + Math.abs(current.y - p.y);
            if (distance < prevDist.get(p)) {
                output += distance;
            }
        }
        return output;
    }

    /**
     * this method is used to update the closest ever map so that we have a record
     * of the last closest ever
     * this is so that we can check if any minimum distances have changed and add
     * cost accordingly
     * used in the aStar method to find any changes in the minimum distances
     */
    public static HashMap<Point, Integer> closestEverMap(Point current, HashMap<Point, Integer> prevDist) {
        HashMap<Point, Integer> output = new HashMap<>();
        for (Point p : prevDist.keySet()) {
            int distance = Math.abs(current.x - p.x) + Math.abs(current.y - p.y);
            if (distance < prevDist.get(p)) {
                output.put(p, distance);
            } else {
                output.put(p, prevDist.get(p));
            }
        }
        return output;
    }

    /**
     * only used in main method to get the total minimum distances, this just looks
     * at the last state
     * and adds all the hash values together
     */
    public static int calcTotal(ArrayList<State> path, Point person) {
        int output = Integer.MAX_VALUE;
        for (State s : path) {
            int distance = Math.abs(s.position.x - person.x) + Math.abs(s.position.y - person.y);
            if (distance < output) {
                output = distance;
            }
        }
        return output;
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

    public static void visualisation(ArrayList<State> points) {
        char[][] grid = new char[gridSize[0]][gridSize[1]];
        for (int i = 0; i < gridSize[0]; i++) {
            for (int j = 0; j < gridSize[1]; j++) {
                grid[i][j] = '.';
            }
        }
        for (State s : points) {
            grid[s.position.x][s.position.y] = 'X';
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

    static class State {
        int closestPt;
        Point position;
        int cost;
        HashMap<Point, Integer> closestEver = new HashMap<>();
        int total;

        public State(Point position, HashMap<Point, Integer> closestEver, int cost, int closestPt) {
            this.position = position;
            this.cost = cost;
            this.closestPt = closestPt;
            this.closestEver = closestEver;
            this.total = closestEver.values().stream().mapToInt(Integer::intValue).sum();
        }
        @Override
        public boolean equals(Object o){
            if(this == o ) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return Objects.equals(position, state.position);
        }
        @Override
        public int hashCode(){
            return Objects.hash(position);
        }
    }
}
