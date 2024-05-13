import java.util.ArrayList;
import java.awt.Point;
import java.util.Scanner;

public class Distancing{
    private static ArrayList<Point> people = new ArrayList<>(); // arraylist of the points that people are at
    private static int[] gridSize = new int[2]; // size of the grid (also the end point)

    public static void main(String[] args){
        ArrayList<String> rawIn = new ArrayList<>();
        Scanner sc = new Scanner(System.in);
        while(sc.hasNextLine()){
            rawIn.add(sc.nextLine());
        }
        sc.close();
        inputHandler(rawIn);
        //debugInputHandling(); method in debug methods text file
    }
    // handler for the recursive function
    public static ArrayList<Point> aStarter(){
        ArrayList<Point> path = new ArrayList<>();
        Point goal = new Point(gridSize[0]-1, gridSize[1]-1);
        Point current = new Point(0,0);
        aStar(current, goal, path);
        return path;
    }
    public static void aStar(Point current, Point goal, ArrayList<Point> path){

    }

    public static int heuristic(Point current, Point goal){
        // pythagoras theorem for euclidean distance c = sqrt(a^2 + b^2)
        double distance = Math.sqrt(Math.pow(Math.abs(current.getX() - goal.getX()), 2) + Math.pow(Math.abs(current.getY() - goal.getY()), 2));
        return (int) distance;
    }

    public static int sumOfDistance(Point current){
        int output = 0;
        for(Point p : people){
            output += Math.abs(current.getX() - p.getX());
            output += Math.abs(current.getY() - p.getY());
        }
        return output;
    }

    // handle input - doesn't do error handling, just adds input to the correct variables
    public static void inputHandler(ArrayList<String> input){
        gridSize = new int[]{Integer.parseInt(input.get(0).split(" ")[0]), Integer.parseInt(input.get(0).split(" ")[1])};
        for(int i = 1; i < input.size(); i++){
            String line = input.get(i);
            String[] split = line.split(" ");
            if(Character.isDigit(split[0].trim().charAt(0)) && Character.isDigit(split[1].trim().charAt(0))){
                people.add(new Point(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
            }
        }
    }
    static class State{
        Point position;
        int cost;
        int sumOfDistance;
        public State(Point position, int cost, int sumOfDistance){
            this.position = position;
            this.cost = cost;
            this.sumOfDistance = sumOfDistance;
        }
    }
}