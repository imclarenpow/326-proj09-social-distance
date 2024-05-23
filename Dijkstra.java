import java.util.*;
import java.awt.Point;

public class Dijkstra {
    private static ArrayList<Point> people = new ArrayList<>();
    private static int[] gridSize = new int[2];
    private static HashMap<Point, Integer> closestToPerson = new HashMap<>();
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
            ArrayList<Point> path = search();
            int total = 0;
            int smallestDistance = Integer.MAX_VALUE;
            for(Point p : people){
                //total += calcTotal(path, p);
            }
            //int minDistance = Collections.min(path.get(path.size()-1).closestEver.values());
            //System.out.println("min " + minDistance + ", total " + total );
            //System.out.println("min (" + path.get(path.size()-1).closestEver + "), total (" + total + ")");
            visualisation(path);
            people = new ArrayList<>();
            gridSize = new int[2];
            closestToPerson = new HashMap<>();
        }
    }
    // add in logic for dijstra's algorithm
    public static ArrayList<Point> search() {
        return new ArrayList<>();
    }
    public static void visualisation(ArrayList<Point> points) {
        char[][] grid = new char[gridSize[0]][gridSize[1]];
        for(int i = 0; i < gridSize[0]; i++){
            for(int j = 0; j < gridSize[1]; j++){
                grid[i][j] = '.';
            }
        }
        for(Point s : points){
            grid[s.x][s.y] = 'X';
        }
        for(Point p : people){
            grid[p.x][p.y] = 'P';
        }

        for(char[] row : grid){
            for(char c : row){
                System.out.print(c + " ");
            }
            System.out.println();
        }
        System.out.println();
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

}
