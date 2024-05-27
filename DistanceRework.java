import java.util.*;
import java.awt.Point;

public class DistanceRework {
    private static ArrayList<Point> people = new ArrayList<>();
    private static int[] gridSize = new int[2];
    /* Notes on storage: HashMap has: point on grid then key[0] = total & key[1] = min */
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
            // create hashmap of all points stats (min & total)
            HashMap<Point, int[]> allStates = allStates();
            // this just iterates and returns every point
            for(int i = 0; i < gridSize[0]; i++){
                for(int j = 0; j < gridSize[1]; j++){
                    Point currentPoint = new Point(i, j);
                    int[] state = allStates.get(currentPoint);
                    if (state != null) {
                        System.out.println(i + " " + j + "  total: " + state[0] + " min: " + state[1]);
                    }
                }
            }
            int[] startingMins = startingMins(allStates.get(new Point(0,0)), allStates.get(new Point(gridSize[0]-1, gridSize[1]-1)));
            for(int i = startingMins[0]; i >= 0; i--){
                for(int j = startingMins[1]; j >= 0; j--){
                    HashMap<Point, int[]> workingMap = workingMap(new HashMap<Point, int[]>(), allStates, i, j);
                    // insert if(isPossiblePath) here then print:
                    // System.out.println("min: " + i + " total: " + j);
                }
            
            }
        }
    }

    /**
     * iterates through all possible points
     * @returns hashmap of points and totalDistance and closestPtDist
     */
    public static HashMap<Point, int[]> allStates(){
        HashMap<Point, int[]> output = new HashMap<>();
        for(int i = 0; i < gridSize[0]; i++){
            for(int j = 0; j < gridSize[1]; j++){
                // can't walk on people
                if(people.contains(new Point(i, j))){ continue; }
                // else add
                Point p = new Point(i, j);
                output.put(p, new int[]{totalDistance(p), closestPtDist(p)}); 
            }
        }
        return output;
    }
    public static HashMap<Point, int[]> workingMap(HashMap<Point, int[]> current, HashMap<Point, int[]> all, int total, int min){
        HashMap<Point, int[]> output = current;
        for(Point p : all.keySet()){
            if(current.containsKey(p)){ continue; } // skip if already in current
            int[] temp = all.get(p);
            if(total <= temp[0] && min <= temp[1]){
                output.put(p, temp);
            }
        }
        return output;
    }
    // minimum values to start with, this dictates where we start in the pathfinding loops
    public static int[] startingMins(int[] start, int[] end){
        int[] output = new int[2];
        if(start[0] < end[0]){ output[0] = start[0]; } else { output[0] = end[0]; }
        if(start[1] < end[1]){ output[1] = start[1]; } else { output[1] = end[1]; }
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
}
