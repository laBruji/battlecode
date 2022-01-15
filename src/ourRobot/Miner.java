package ourRobot;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

// TODO: Review Miner; too sleepy now
public class Miner {
    /**
     * Run a single turn for a Miner.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    static void runMiner(RobotController rc) throws GameActionException {
        // Try to mine on squares around us.
        MapLocation me = rc.getLocation();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                MapLocation mineLocation = new MapLocation(me.x + dx, me.y + dy);
                while (rc.canMineGold(mineLocation)) {
                    rc.mineGold(mineLocation);
                }
                while (rc.canMineLead(mineLocation) && rc.senseLead(mineLocation) > 1) {
                    rc.mineLead(mineLocation);
                }
            }
        }

        int visionRadius = rc.getType().visionRadiusSquared;
        MapLocation[] nearbyLocations = rc.getAllLocationsWithinRadiusSquared(me, visionRadius);

        MapLocation targetLocation = null;
        int distanceToTarget = Integer.MAX_VALUE;

        for (MapLocation tryLocation : nearbyLocations) {
            if (rc.senseLead(tryLocation) > 1 || rc.senseGold(tryLocation) > 0) {
                int distanceTo = me.distanceSquaredTo(tryLocation);
                if (distanceTo < distanceToTarget) {
                    targetLocation = tryLocation;
                    distanceToTarget = distanceTo;
                }
            }
        }

        Direction dir;
        if (targetLocation != null)
            dir = me.directionTo(targetLocation);
        else
            dir = RobotPlayer.directions[RobotPlayer.rng.nextInt(RobotPlayer.directions.length)];

        if (rc.canMove(dir)) {
            rc.move(dir);
            System.out.println("I moved!");
        }
    }


}
