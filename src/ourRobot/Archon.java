package ourRobot;

import battlecode.common.*;
import com.sun.glass.ui.Robot;

import java.util.Arrays;

public class Archon {
    /**
     * Run a single turn for an Archon.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    static void runArchon(RobotController rc) throws GameActionException {
        // TODO: Review this strategy
        if(RobotPlayer.miners < 5){
            buildTowardsLowRubble(rc, RobotType.MINER);
        } else if (RobotPlayer.soldiers < 10){
            buildTowardsLowRubble(rc, RobotType.SOLDIER);
        } else if (RobotPlayer.builders < 1){
            buildTowardsLowRubble(rc, RobotType.BUILDER);
        } else if (RobotPlayer.miners < RobotPlayer.soldiers / 2 && rc.getTeamLeadAmount(rc.getTeam()) < 5000){
            buildTowardsLowRubble(rc, RobotType.MINER);
        } else if (RobotPlayer.builders < RobotPlayer.soldiers / 10){
            buildTowardsLowRubble(rc, RobotType.BUILDER);
        } else {
            buildTowardsLowRubble(rc, RobotType.SOLDIER);
        }
    }

    static void buildTowardsLowRubble(RobotController rc, RobotType type) throws GameActionException {
        Direction[] dirs = Arrays.copyOf(RobotPlayer.directions, RobotPlayer.directions.length);
        Arrays.sort(dirs, (a, b) -> getRubble(rc, a) - getRubble(rc, b));
        for (Direction d : dirs){
            if(rc.canBuildRobot(type, d)){
                rc.buildRobot(type, d);
                switch(type){
                    case MINER: RobotPlayer.miners++; break;
                    case SOLDIER: RobotPlayer.soldiers++; break;
                    case BUILDER: RobotPlayer.builders++; break;
                    case SAGE: RobotPlayer.sages++; break;
                    default: break;
                }
            }
        }
    }

    static int getRubble(RobotController rc, Direction d){
        try {
            MapLocation loc = rc.getLocation().add(d);
            return rc.senseRubble(loc);
        } catch (GameActionException e){
            e.printStackTrace();
            return 0;
        }
    }
}
