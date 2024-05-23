import java.util.*;
import java.awt.Point;
public class DistanceRefactor {
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
            closestToPerson = new HashMap<>();
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
        costs.add(new State(current, returnDistances(current), 0));
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
            int closenessComparatorY = SumOfMap(returnDistances(neighbourY)) - SumOfMap(curr.closestEver);
            int closenessComparatorX = SumOfMap(returnDistances(neighbourX)) - SumOfMap(curr.closestEver);
            int costY = curr.cost + heuristic(neighbourY, goal, closenessComparatorY) + (minChanges(neighbourY, curr.closestEver)*9);
            int costX = curr.cost + heuristic(neighbourX, goal, closenessComparatorX) + (minChanges(neighbourX, curr.closestEver)*9);

            if(!costAtPt.containsKey(neighbourY) || costY < costAtPt.get(neighbourY)){
                costAtPt.put(neighbourY, costY);
                HashMap<Point, Integer> yClosest = closestEver(neighbourY, curr.closestEver);
                State temp = new State(neighbourY, yClosest, costY);
                costs.add(temp);
                cameFrom.put(temp, curr);
            }
            if(!costAtPt.containsKey(neighbourX) || costX < costAtPt.get(neighbourX)){
                costAtPt.put(neighbourX, costX);
                HashMap<Point, Integer> xClosest = closestEver(neighbourX, curr.closestEver);
                State temp = new State(neighbourX, xClosest, costX);
                costs.add(temp);
                cameFrom.put(temp, curr);
            }
        }
    }

    public static int heuristic(Point current, Point goal, int closenessComparator){
        int output = 0;
        int manhattan = Math.abs(current.x - goal.x) + Math.abs(current.y - goal.y);
        
        
        // adjust for wanted weight
        output = (5 * manhattan) + (1 * closenessComparator);
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
            if(!closestToPerson.containsKey(current)){
                closestToPerson.put(p, distance);
            }else if(distance < closestToPerson.get(current)){
                closestToPerson.put(p, distance);
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
    public static int SumOfMap(HashMap<Point, Integer> map){
        int output = 0;
        for(int i : map.values()){
            output += i;
        }
        return output;
    }
    public static HashMap<Point, Integer> returnDistances(Point current){
        HashMap<Point, Integer> output = new HashMap<>();
        for(Point p : people){
            int distance = Math.abs(current.x - p.x) + Math.abs(current.y - p.y);
            output.put(p, distance);
        }
        return output;
    }
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
    public static HashMap<Point, Integer> closestEver(Point current, HashMap<Point, Integer> prevDist){
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
        int closestPt;
        Point position;
        int cost;
        HashMap<Point, Integer> closestEver = new HashMap<>();
        public State(Point position, HashMap<Point, Integer> closestEver, int cost){
            this.position = position;
            this.cost = cost;
            this.closestEver = closestEver;
            this.closestPt = closestPointDistance(position);
            
        }
    }
}
