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

            ArrayList<State> path = aStarter();
            int total = 0;
            int smallestDistance = Integer.MAX_VALUE;
            for(Point p : people){
                total += calcTotal(path, p);
            }
            System.out.println("min " + path.get(path.size()-1).closestEver + ", total " + total);
            visualisation(path);
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
        PriorityQueue<State> costs = new PriorityQueue<>(Comparator.comparingInt(a -> a.cost));
        Map<Point, Integer> costAtPt = new HashMap<>();
        Map<State, State> cameFrom = new HashMap<>();
        int firstClosePt = closestPointDistance(current);
        costs.add(new State(current, firstClosePt, 0));
        costAtPt.put(current, 0);
        while(!costs.isEmpty()){
            State curr = costs.poll();
            if(curr.position.equals(goal)){
                State temp = curr;
                while (temp != null){
                    path.add(temp);
                    temp = cameFrom.get(temp);
                }
                Collections.reverse(path);
                return;
            }

            Point neighbourY = new Point(curr.position.x, curr.position.y+1);
            Point neighbourX = new Point(curr.position.x+1, curr.position.y);

            int costY = curr.cost + heuristic(neighbourY, goal, curr.closestEver);
            int costX = curr.cost + heuristic(neighbourX, goal, curr.closestEver);

            if(!costAtPt.containsKey(neighbourY) || costY < costAtPt.get(neighbourY)){
                costAtPt.put(neighbourY, costY);
                int cd = closestPointDistance(neighbourY);
                if(cd > curr.closestEver){ cd = curr.closestEver; }
                State temp = new State(neighbourY, cd, costY);
                costs.add(temp);
                cameFrom.put(temp, curr);
            }
            if(!costAtPt.containsKey(neighbourX) || costX < costAtPt.get(neighbourX)){
                costAtPt.put(neighbourX, costX);
                int cd = closestPointDistance(neighbourX);
                if(cd > curr.closestEver){ cd = curr.closestEver; }
                State temp = new State(neighbourX, cd, costX);
                costs.add(temp);
                cameFrom.put(temp, curr);
            }
        }
    }

    public static int heuristic(Point current, Point goal, int closestEver){
        int output = 0;
        int manhattan = Math.abs(current.x - goal.x) + Math.abs(current.y - goal.y);
        int closeWeight = 0;
        if(closestEver > closestPointDistance(current)){
            closeWeight = 1/(closestEver - closestPointDistance(current)) * 100;
        }
        // adjust for wanted weight
        output = (5 * manhattan) + (1 * closeWeight);
        return output;
    }

    /** this method finds the closestPoint
     * @returns the manhattan distance between the handed through point and the closest person
     */
    public static int closestPointDistance(Point current){
        int closest = Integer.MAX_VALUE;
        for(Point p : people){
            int distance = Math.abs(current.x - p.x) + Math.abs(current.y - p.y);
            if(distance < closest){
                closest = distance;
            }
        }
        return closest;
    }

    public static int calcTotal(ArrayList<State> path, Point person){
        int output = Integer.MAX_VALUE;
        for(State s : path){
            int distance = Math.abs(s.position.x - person.x) + Math.abs(s.position.y - person.y);
            if(distance < output){
                output = distance;
            }
        }
        return output;
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

    public static void visualisation(ArrayList<State> points) {
        char[][] grid = new char[gridSize[0]][gridSize[1]];
        for(int i = 0; i < gridSize[0]; i++){
            for(int j = 0; j < gridSize[1]; j++){
                grid[i][j] = '.';
            }
        }
        for(State s : points){
            grid[s.position.x][s.position.y] = 'X';
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

    static class State {
        int closestEver;
        int closestPt;
        Point position;
        int cost;
        public State(Point position, int closestEver, int cost){
            this.closestEver = closestEver;
            this.position = position;
            this.cost = cost;
            this.closestPt = closestPointDistance(position);
        }
    }
}
