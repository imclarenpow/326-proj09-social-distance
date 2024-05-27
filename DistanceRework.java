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
            HashMap<Point, Integer> allStates = allStates();
            // this just iterates and returns every point
            /*for(int i = 0; i < gridSize[0]; i++){
                for(int j = 0; j < gridSize[1]; j++){
                    Point currentPoint = new Point(i, j);
                    int[] state = allStates.get(currentPoint);
                    if (state != null) {
                        System.out.println(i + " " + j + "  total: " + state[0] + " min: " + state[1]);
                    }
                }
            }*/
            
            int startingMin = startingMin(allStates.get(new Point(0,0)), allStates.get(new Point(gridSize[0]-1, gridSize[1]-1)));
            //System.out.println("Starting Iteration: " + startingMin + " First pt min: " + allStates.get(new Point(0,0)) + " Last pt min: " + allStates.get(new Point(gridSize[0]-1, gridSize[1]-1)));
            //System.out.println("People: " + people.toString());
            HashMap<Point, Integer> workingMap = new HashMap<>();
            
            for(int min = startingMin; min > 0; min--){
                HashMap<Point, Integer> temp = workingMap(workingMap, allStates, min);
                if(canFormPath(workingMap)){
                    System.out.println("min: " + min);
                    for(Point p : temp.keySet()){
                        System.out.println(p.x + " " + p.y + " min: " + temp.get(p));
                    }
                    break;
                }
                workingMap = temp;
            }
            people = new ArrayList<>();
        }
    }
    // TODO: fix it returning false positives: such as this one:
        /* testing/i1.txt
         * scenario 4:
            * returning:
            * min: 9
         * instead of min: 4
            * here are the points that are given to canFormPath to get this result:
            * 0 0, 0 1, 1 0, 10 10, 10 9, 9 10
         */
    public static Boolean canFormPath(HashMap<Point, Integer> points) {
        if (points.isEmpty()) {
            return false;
        }

        for (Point p : points.keySet()) {
            boolean hasNeighbour = false;

            Point[] neighbours = {
                    new Point(p.x, p.y + 1),
                    new Point(p.x + 1, p.y),
                    new Point(p.x, p.y - 1),
                    new Point(p.x - 1, p.y)
            };

            for (Point neighbour : neighbours) {
                if (points.containsKey(neighbour)) {
                    hasNeighbour = true;
                    break;
                }
            }

            if (!hasNeighbour) {
                return false;
            }
        }
        return true;
    }
    /**
     * iterates through all possible points
     * @returns hashmap of points and totalDistance and closestPtDist
     */
    public static HashMap<Point, Integer> allStates(){
        HashMap<Point, Integer> output = new HashMap<>();
        for(int i = 0; i < gridSize[0]; i++){
            for(int j = 0; j < gridSize[1]; j++){
                // can't walk on people
                if(people.contains(new Point(i, j))){ continue; }
                // else add
                Point p = new Point(i, j);
                output.put(p, closestPtDist(p)); 
            }
        }
        return output;
    }

    public static HashMap<Point, Integer> workingMap(HashMap<Point, Integer> current, HashMap<Point, Integer> all, int min){
        HashMap<Point, Integer> output = current;
        for(Point p : all.keySet()){
            if(current.containsKey(p)){ continue; } // skip if already in current
            int tempMin = all.get(p);
            if(min <= tempMin){
                output.put(p, tempMin);
            }
        }
        return output;
    }
    // minimum values to start with, this dictates where we start in the pathfinding loops
    public static int startingMin(int start, int end){
        int output;
        if(start < end){ output = start; } else { output = end; }
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
