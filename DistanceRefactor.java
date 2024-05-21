import java.util.*;
import java.awt.Point;
public class DistanceRefactor {
    private static ArrayList<Point> people = new ArrayList<>();
    private static int[] gridSize = new int[2];

    public static void main(String[] args){
        // get input
        ArrayList<ArrayList<int[]>> scenarios = stdIn();
        // loop through each scenario
        for(ArrayList<int[]> scenario : scenarios){
            // this adds the needed elements to the variables
            for(int i = 0; i < scenario.size(); i++){
                // add first element to gridSize
                if(i == 0){ gridSize = scenario.get(i); }
                // add all other elements to people
                else{ people.add(new Point(scenario.get(i)[0], scenario.get(i)[1])); }
            }

            // in here run aStar
            // then print results
            // resetting variables for next scenario
            people = new ArrayList<>();
            gridSize = new int[2];
        }
    }
    /** handler for the aStar algorithm (keeps main cleaner) */
    public static ArrayList<State> aStarter() {
        ArrayList<State> path = new ArrayList<>();
        Point goal = new Point(gridSize[0]-1, gridSize[1]-1);
        Point current = new Point(0,0);
        aStar(current, goal, path);
        return path;
    }
    /** aStar search algorithm implementation */
    public static void aStar(Point current, Point goal, ArrayList<State> path){
        PriorityQueue<State> cost = new PriorityQueue<>(Comparator.comparingInt(a -> a.cost));
        Map<Point, Integer> costAtPt = new HashMap<>();
        Map<Point, Point> cameFrom = new HashMap<>();
    }

    /** this method handles standard in, this is to declutter the main method
     * @return the input in a 2D arraylist of all scenarios and their points
     */
    public static ArrayList<ArrayList<int[]>> stdIn(){

        ArrayList<String> rawIn = new ArrayList<>();
        ArrayList<ArrayList<int[]>> output = new ArrayList<>();
        output.add(new ArrayList<int[]>());
        Scanner sc = new Scanner(System.in);
        while(sc.hasNextLine()){
            rawIn.add(sc.nextLine());
        }
        for(String s : rawIn){
            if(s.trim().isEmpty()){
                output.add(new ArrayList<int[]>());
            }else{
                output.get(output.size()-1).add(Arrays.stream(s.trim().split("\\s+"))
                                            .mapToInt(Integer::parseInt).toArray());
            }
        }
        sc.close();
        return output;
    }


    static class State {
        int closestEver;
        Point position;
        int cost;
        public State(int closestEver, Point position, int cost){
            this.closestEver = closestEver;
            this.position = position;
            this.cost = cost;
        }
    }
}
