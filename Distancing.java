import java.util.*;
import java.awt.Point;

public class Distancing {
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
            ArrayList<State> path = search();
            int total = 0;
            int smallestDistance = Integer.MAX_VALUE;
            for(Point p : people){
                total += calcTotal(path, p);
            }
            int minDistance = Collections.min(path.get(path.size()-1).closestEver.values());
            System.out.println("min " + minDistance + ", total " + total );
            System.out.println("min (" + path.get(path.size()-1).closestEver + "), total (" + total + ")");
            visualisation(path);
            people = new ArrayList<>();
            gridSize = new int[2];
            closestToPerson = new HashMap<>();
        }
    }
    /** 
     * this method is a handler for the a star method, 
     * it initialises it so it can run recursively
     */
    public static ArrayList<State> search() {
        ArrayList<State> path = new ArrayList<>();
        Point goal = new Point(gridSize[0]-1, gridSize[1]-1);
        Point current = new Point(0,0);
        aStar(current, goal, path);
        return path;
    }
    /** 
     * method for the a star algorithm
     */
    public static void aStar(Point current, Point goal, ArrayList<State> path){
        PriorityQueue<State> costs = new PriorityQueue<>(Comparator.comparingInt(a -> a.cost));
        Map<Point, Integer> costAtPt = new HashMap<>();
        Map<State, State> cameFrom = new HashMap<>();
        costs.add(new State(current, returnDistances(current), 0, closestPtDist(current)));
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
            // create new points for the neighbours (towards the goal position)
            Point neighbourY = new Point(curr.position.x, curr.position.y+1);
            Point neighbourX = new Point(curr.position.x+1, curr.position.y);
            HashMap<Point, Integer> yMap = returnDistances(neighbourY);
            HashMap<Point, Integer> xMap = returnDistances(neighbourX);
            // adding movement to the end goal to the cost
            int closestY = Integer.MAX_VALUE;
            int closestX = Integer.MAX_VALUE;
            int costY = curr.cost + heuristic(neighbourY, goal);
            int costX = curr.cost + heuristic(neighbourX, goal);
            // adding change in closest point distance cost
            if(curr.closestPt > closestPtDist(neighbourX)){ costX += 25; closestX = closestPtDist(neighbourX); }
                else{ closestX = curr.closestPt; }
                if(curr.closestPt > closestPtDist(neighbourY)){ costY += 25; closestY = closestPtDist(neighbourY); }
                else{ closestY = curr.closestPt; }
            // adding cost of changes to minimum distances
            costX += minChanges(neighbourX, curr.closestEver)*10;
            costY += minChanges(neighbourY, curr.closestEver)*10;
            // add all the things to state
            if(!costAtPt.containsKey(neighbourY) || costY < costAtPt.get(neighbourY)){
                costAtPt.put(neighbourY, costY);
                HashMap<Point, Integer> yClosestMap = closestEverMap(neighbourY, curr.closestEver);
                State temp = new State(neighbourY, yClosestMap, costY, closestY);
                costs.add(temp);
                cameFrom.put(temp, curr);
            }
            if(!costAtPt.containsKey(neighbourX) || costX < costAtPt.get(neighbourX)){
                costAtPt.put(neighbourX, costX);
                HashMap<Point, Integer> xClosestMap = closestEverMap(neighbourX, curr.closestEver);
                State temp = new State(neighbourX, xClosestMap, costX, closestX);
                costs.add(temp);
                cameFrom.put(temp, curr);
            }
        }
    }
    /**
     * simple method that returns the manhattan distance of the current point to the goal
     */
    public static int heuristic(Point current, Point goal){
        return Math.abs(current.x - goal.x) + Math.abs(current.y - goal.y);
    }
    /** 
     * this method returns the minimum distance ever
     */
    public static int closestPtDist(Point current){
        int min = Integer.MAX_VALUE;
        for(Point p : people){
            int dist = Math.abs(p.x - current.x) + Math.abs(p.y - current.y);
            if(dist < min){
                min = dist;
            }
        }
        return min;
    }
    /**
     * this method returns a hashmap of all distances from the point to all the people.
     */
    public static HashMap<Point, Integer> returnDistances(Point current){
        HashMap<Point, Integer> distances = new HashMap<>();
        for(Point p : people){
            distances.put(p, Math.abs(p.x - current.x) + Math.abs(p.y - current.y));
        }
        return distances;
    }
    /** 
     * this method looks through the current point and previous hashMap and checks 
     * if the minimum distance is breached. this is to add cost if it has.
     * if its equal to the minimum distance it doesn't care
     * @returns how many minimum distances have been changed
     */
    public static int minChanges (Point current, HashMap<Point, Integer> prevDist){
        int output = 0;
        for(Point p : prevDist.keySet()){
            int distance = Math.abs(current.x - p.x) + Math.abs(current.y - p.y);
            if(distance < prevDist.get(p)){
                output ++;
            }
        }
        return output;
    }
    /**
     * this method is used to update the closest ever map so that we have a record of the last closest ever
     * this is so that we can check if any minimum distances have changed and add cost accordingly
     * used in the aStar method to find any changes in the minimum distances
     */
    public static HashMap<Point, Integer> closestEverMap(Point current, HashMap<Point, Integer> prevDist){
        HashMap<Point, Integer> output = new HashMap<>();
        for(Point p : prevDist.keySet()){
            int distance = Math.abs(current.x - p.x) + Math.abs(current.y - p.y);
            if(distance < prevDist.get(p)){
                output.put(p, distance);
            }else{
                output.put(p, prevDist.get(p));
            }
        }
        return output;
    }
    /** only used in main method to get the total minimum distances, this just looks at the last state
     * and adds all the hash values together
    */
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

    static class State{
        int closestPt;
        Point position;
        int cost;
        HashMap<Point, Integer> closestEver = new HashMap<>();
        public State(Point position, HashMap<Point, Integer> closestEver, int cost, int closestPt){
            this.position = position;
            this.cost = cost;
            this.closestEver = closestEver;
        }
    }
}
