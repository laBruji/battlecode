package ourRobot;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.GameActionException;

import java.util.Map;

public class Walk {
    static void walk(RobotController rc, MapLocation target) throws GameActionException {
        MapLocation initLocation = rc.getLocation();
        MapLocation currentLocation;
        MapLocation idealLocation = initLocation;
        Direction initDir = initLocation.directionTo(target);
        Direction currentDir = initDir;
        Direction dirToTarget;
        int minDistance = initLocation.distanceSquaredTo(target);
        int currentDistance;


        // if there are no obstacles, move towards target
        if (rc.canMove(initDir)) {
            rc.move(initDir);
            return;
        }

        // move along obstacle
        while(!rc.canMove(currentDir)){
            currentDir = currentDir.rotateRight();
        }
        rc.move(currentDir);
        currentLocation = rc.getLocation();

        // next step
        while(!currentLocation.equals(initLocation)) {
            if (rc.canMove(currentDir.rotateLeft())) {
                currentDir = currentDir.rotateLeft();

            } else {
                currentDir = currentDir.rotateRight();
                while(!rc.canMove(currentDir)){
                    currentDir = currentDir.rotateRight();
                }
            }
            rc.move(currentDir);
            currentLocation = rc.getLocation();
            currentDistance = currentLocation.distanceSquaredTo(target);
            dirToTarget = currentLocation.directionTo(target);
            if(currentDistance < minDistance && dirToTarget.equals(initDir)) {
                minDistance = currentDistance;
                idealLocation = currentLocation;
            }
        }

        currentDir = initDir;
        while(!rc.canMove(currentDir)){
            currentDir = currentDir.rotateRight();
        }
        rc.move(currentDir);
        currentLocation = rc.getLocation();


        while(!currentLocation.equals(idealLocation)){
            if (rc.canMove(currentDir.rotateLeft())) {
                currentDir = currentDir.rotateLeft();

            } else {
                currentDir = currentDir.rotateRight();
                while(!rc.canMove(currentDir)){
                    currentDir = currentDir.rotateRight();
                }
            }
            rc.move(currentDir);
            currentLocation = rc.getLocation();
        }

        if(rc.canMove(initDir)){
            rc.move(initDir);
        }

    }
}
