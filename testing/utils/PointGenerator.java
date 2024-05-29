import java.util.ArrayList;
import java.awt.Point;

public class PointGenerator {
    public static void main(String[] args){
        // input the amount of points you want
        int amtOfPts = 20; // change to amount of people you want
        int xMax = 30; // change to xMax of the grid
        int yMax = 30; // change to yMax of the grid
        System.out.println(xMax + " " + yMax);
        ArrayList<Point> points = new ArrayList<Point>();
        for(int i = 0; i < amtOfPts; i++){
            int x = (int)(Math.random() * xMax);
            int y = (int)(Math.random() * yMax);
            if(!points.contains(new Point(x, y)) || (x == 0 && y == 0) || (x == xMax && y == yMax)){
                points.add(new Point(x, y));
                System.out.println(x + " " + y);
            } else {
                i--;
            }
        }
    }
}
