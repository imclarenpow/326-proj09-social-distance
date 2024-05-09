import java.util.ArrayList;
import java.awt.Point;
import java.util.Scanner;

public class Distancing{
    private static ArrayList<Point> people = new ArrayList<>(); // arraylist of the points that people are at
    private static Point player = new Point(0,0); // the point that moving person / player is at
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
    
}