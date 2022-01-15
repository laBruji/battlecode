package v1;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class ArchonStrategy {
    /**
     * Run a single turn for an Archon.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    static void runArchon(RobotController rc) throws GameActionException {
        // Pick a direction to build in.
        int directionIndex = RobotPlayer.rng.nextInt(RobotPlayer.directions.length);
        Direction dir = RobotPlayer.directions[directionIndex];

        if (RobotPlayer.rng.nextBoolean()) {
            // Let's try to build a miner.
            rc.setIndicatorString("Trying to build a miner");
            if (rc.canBuildRobot(RobotType.MINER, dir)) {
                rc.buildRobot(RobotType.MINER, dir);
            }
        } else {
            // Let's try to build a soldier.
            rc.setIndicatorString("Trying to build a soldier");
            if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
                rc.buildRobot(RobotType.SOLDIER, dir);
            }
        }
    }
}
