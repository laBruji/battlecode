package sandboxplayer3;

import battlecode.common.*;

strictfp class Pathing {

    /**
     * Any location with less than or equal to this amount of rubble is not an obstacle.
     * All other squares are obstacles.
     */
    private static final int ACCEPTABLE_RUBBLE = 25;

    /**
     * The direction that we are trying to use to go around the obstacle.
     * It is null if we are not trying to go around an obstacle.
     */
    private static Direction bugDirection = null;
    private static boolean firstTimeAround = false;
    private static boolean secondTimeAround = false;
    private static MapLocation initLocation = null;
    private static Direction initDirection = null;
    private static int minDistance = 0;
    private static MapLocation idealLocation = null;

    static void walkTowards(RobotController rc, MapLocation target) throws GameActionException {
        if (!rc.isMovementReady()) {
            return;
        }

        MapLocation currentLocation = rc.getLocation();
        if (currentLocation.equals(target)) {
            // We're already at our goal
            // reset variables in case target was reached in the firstTimeAround
            firstTimeAround = false;
            initLocation = null;
            initDirection = null;
            minDistance = 0;
            return;
        }

        // start second round
        if(firstTimeAround && currentLocation.equals(initLocation)){
            firstTimeAround = false;
            secondTimeAround = true;
        }

        // arrived at ideal location after scouting the entire obstacle
        if (secondTimeAround && currentLocation.equals(idealLocation)){
            secondTimeAround = false;
            bugDirection = null;
            initLocation = null;
            initDirection = null;
            minDistance = 0;
            idealLocation = null;
        }

        Direction currentDirection = currentLocation.directionTo(target);

        if (rc.canMove(currentDirection) && !isObstacle(rc, currentDirection) && !(firstTimeAround || secondTimeAround)) {
            // No obstacle in the way
            rc.move(currentDirection);
            bugDirection = null;
        } else {
            int currentDistance = currentLocation.distanceSquaredTo(target);
            if (!secondTimeAround){
                if (currentDistance < minDistance && initDirection.equals(currentDirection) && rc.canMove(currentDirection) && !isObstacle(rc, currentDirection)) {
                    minDistance = currentDistance;
                    idealLocation = currentLocation;
                }
            }
            // There is an obstacle in the way, so we're gonna have to go around it
            if (bugDirection == null) {
                // start by the initial Direction (signal there's an obstacle)
                bugDirection = currentDirection;
                initLocation = currentLocation;
                initDirection = currentDirection;
                minDistance = currentDistance;
                firstTimeAround = true;
            }

            // go around the obstacle using bugDirection
            // Repeat 8 times to try all 8 possible directions.
            for (int i = 0; i < 8; i++) {
                if (rc.canMove(bugDirection) && !isObstacle(rc, bugDirection)) {
                    rc.move(bugDirection);
                    bugDirection = bugDirection.rotateLeft();
                    break;
                } else {
                    bugDirection = bugDirection.rotateRight();
                }
            }

        }


    }

    /**
     * Checks if the square we reach by moving in direction d is an obstacle.
     */
    private static boolean isObstacle(RobotController rc, Direction d) throws GameActionException {
        MapLocation adjacentLocation = rc.getLocation().add(d);
        int rubbleOnLocation = rc.senseRubble(adjacentLocation);
        return rubbleOnLocation > ACCEPTABLE_RUBBLE;
    }
}
