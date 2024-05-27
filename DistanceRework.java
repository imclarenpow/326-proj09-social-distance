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
            HashMap<Point, State> allStates = allStates();
            for(int i = 0; i < gridSize[0]; i++){
                for(int j = 0; j < gridSize[1]; j++){
                    Point currentPoint = new Point(i, j);
                    State state = allStates.get(currentPoint);
                    if (state != null) {
                        System.out.println(state.position.x + " " + state.position.y + "  total: " + state.total + " min: " + state.min);
                    }
                }
            }
        }
    }

    public static HashMap<Point, State> allStates(){
        HashMap<Point, State> output = new HashMap<>();
        for(int i = 0; i < gridSize[0]; i++){
            for(int j = 0; j < gridSize[1]; j++){
                // can't walk on people
                if(people.contains(new Point(i, j))){ continue; }
                // else add
                Point p = new Point(i, j);
                output.put(p, new State(p, 
                    totalDistance(p),
                    0, closestPtDist(p)));
            }
        }
        return output;
    }

    public static int totalDistance(Point current){
        int output = 0;
        for(Point p : people){
            output += Math.abs(p.x - current.x) + Math.abs(p.y - current.y);
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
        int min;
        Point position;
        int cost;
        HashMap<Point, Integer> closestEver = new HashMap<>();
        int total;

        public State(Point position, int total, int cost, int closestPt) {
            this.position = position;
            this.cost = cost;
            min = closestPt;
            this.total = total;
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
