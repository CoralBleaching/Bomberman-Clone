package main;

import entity.Collider;

public class collisionHandler {

    private static final double scaler = 1;

    public static boolean rectangularCollision(Collider.CollisionBox box1, Collider.CollisionBox box2)
    {
        boolean result = false;
        // from the left
        result |= collideRectFromLeft(box1, box2);
        // from the right
        result |= collideRectFromLeft(box2, box1);
        // from the top
        result |= collideRectFromTop(box1, box2);
        // from the bottom
        result |= collideRectFromTop(box2, box1);

        return result;
    }

    public static boolean collideRectFromLeft(Collider.CollisionBox leftBox, Collider.CollisionBox rightBox) 
    {
        if (leftBox.x + leftBox.width > rightBox.x)
        {
            if (leftBox.x > rightBox.x + rightBox.width) return false;
            else if (leftBox.y > rightBox.y + rightBox.height) return false;
            else if (leftBox.y + leftBox.height < rightBox.y) return false;
            else return true;
        }
        else return false;
    }

    public static boolean collideRectFromTop(Collider.CollisionBox topBox, Collider.CollisionBox bottomBox) 
    {
        if (topBox.y + topBox.height > bottomBox.y)
        {
            if (topBox.y > bottomBox.y + bottomBox.height) return false;
            else if (topBox.x > bottomBox.x + bottomBox.width) return false;
            else if (topBox.x + topBox.width < bottomBox.x) return false;
            else return true;
        }
        else return false;
    }

    private static double euclideanDistance(int ax, int ay, int bx, int by)
    {
        return Math.sqrt(Math.pow(ax - bx,2) + Math.pow(ay - by, 2));
    }

    private static boolean collideCircleFromLeft(Collider.CollisionBox leftBox, Collider.CollisionBox rightBox)
    {
        // Operating assumption: already touching rectangular boundary
        int ax = leftBox.x + leftBox.width;
        int ay;
        int centerX = rightBox.x + rightBox.width / 2;
        int centerY = rightBox.y + rightBox.height / 2;
        double radius = rightBox.width / 2 * scaler;

        // Approaching from past the equator
        if (leftBox.y > rightBox.y + rightBox.height * (3/4)) ay = leftBox.y;
        else if (leftBox.y > rightBox.y + rightBox.height * (1/4)) ay = leftBox.y + leftBox.height;
        else return true;
        
        double distance = euclideanDistance(ax, ay, centerX, centerY);
        if (distance < radius) return true;

        return false;
    }

    private static boolean collideCircleFromRight(Collider.CollisionBox rightBox, Collider.CollisionBox leftBox)
    {
        // Operating assumption: already touching rectangular boundary
        int ax = rightBox.x;
        int ay;
        int centerX = leftBox.x + leftBox.width / 2;
        int centerY = leftBox.y + leftBox.height / 2;
        double radius = leftBox.width / 2 * scaler;

        // Approaching from past the equator
        if (rightBox.y > leftBox.y + leftBox.height * (3/4)) ay = rightBox.y;
        else if (rightBox.y > leftBox.y + leftBox.height * (1/4)) ay = rightBox.y + rightBox.height;
        else return true;
        
        double distance = euclideanDistance(ax, ay, centerX, centerY);
        if (distance < radius) return true;

        return false;
    }

    private static boolean collideCircleFromTop(Collider.CollisionBox topBox, Collider.CollisionBox bottomBox)
    {
        // Operating assumption: already touching rectangular boundary
        int ay = topBox.y + topBox.height;
        int ax;
        int centerX = bottomBox.x + bottomBox.width / 2;
        int centerY = bottomBox.y + bottomBox.height / 2;
        double radius = bottomBox.width / 2 * scaler;

        // Approaching from past the equator
        if (topBox.x > bottomBox.x + bottomBox.width * (3/4)) ax = topBox.x;
        else if (topBox.x > bottomBox.x + bottomBox.width * (1/4)) ax = topBox.x + topBox.width;
        else return true;
        
        double distance = euclideanDistance(ax, ay, centerX, centerY);
        if (distance < radius) return true;

        return false;
    }

    private static boolean collideCircleFromBottom(Collider.CollisionBox bottomBox, Collider.CollisionBox topBox)
    {
        // Operating assumption: already touching rectangular boundary
        int ay = bottomBox.y;
        int ax;
        int centerX = topBox.x + topBox.width / 2;
        int centerY = topBox.y + topBox.height / 2;
        double radius = topBox.width / 2 * scaler;

        // Approaching from past the equator
        if (bottomBox.x > topBox.x + topBox.width * (3/4)) ax = bottomBox.x;
        else if (bottomBox.x > topBox.x + topBox.width * (1/4)) ax = bottomBox.x + bottomBox.width;
        else return true;
        
        double distance = euclideanDistance(ax, ay, centerX, centerY);
        if (distance < radius) return true;

        return false;
    }

    public static boolean roundCollision(Collider.CollisionBox box1, Collider.CollisionBox box2)
    {
        boolean result = false;
        if (collideRectFromLeft(box1, box2))
        {
            result |= collideCircleFromLeft(box1, box2);
        }
        if (collideRectFromLeft(box2, box1))
        {
            result |= collideCircleFromRight(box1, box2);
        }
        if (collideRectFromTop(box1, box2))
        {
            result |= collideCircleFromTop(box1, box2);
        }
        if (collideRectFromTop(box2, box1))
        {
            result |= collideCircleFromBottom(box1, box2);
        }
        return result;
    }
}
