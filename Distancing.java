import java.util.ArrayList;
import java.awt.Point;
import java.util.Scanner;

public class Distancing{
    private static ArrayList<Point> people = new ArrayList<>();
    private static Point player = new Point(0,0);
    private static int[] gridSize = new int[2];

    public static void main(String[] args){
        ArrayList<String> rawIn = new ArrayList<>();
        Scanner sc = new Scanner(System.in);
        while(sc.hasNextLine()){
            rawIn.add(sc.nextLine());
        }
        sc.close();
        inputHandler(rawIn);
        debugInputHandling();
    }
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
    public static void debugInputHandling(){
        System.out.println("Grid Size: " + gridSize[0] + " " + gridSize[1] + "\nPeople: " + people.size() + "\nPlayer: " + player.toString());
        for(Point p : people){
            System.out.println(p.toString());
        }
    }
}