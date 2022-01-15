package ourRobot;

import battlecode.common.*;

public class Builder {

    static void nearbyRepair(RobotController rc){
        MapLocation me = rc.getLocation();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                MapLocation repLocation = new MapLocation(me.x + dx, me.y + dy);
                try {
                    while (rc.canRepair(repLocation)) {
                        rc.repair(repLocation);
                    }
                } catch (GameActionException e){
                    System.out.println(rc.getType() + " Exception");
                    e.printStackTrace();
                }
            }
        }
    }

    static void runBuilder(RobotController rc) throws GameActionException{
        // repair neighboring building
        nearbyRepair(rc);

        RobotInfo[] robots = rc.senseNearbyRobots();
        int distance = Integer.MAX_VALUE;
        Direction dir = null;

        // find direction of closest robot of the team
        for(RobotInfo robot : robots){
            if(robot.getTeam().equals(rc.getTeam()) && robot.type.isBuilding() && robot.health < robot.type.getMaxHealth(robot.level)){
                if(rc.getLocation().distanceSquaredTo(robot.location) < distance){
                    dir = rc.getLocation().directionTo(robot.getLocation());
                    distance = rc.getLocation().distanceSquaredTo(robot.location);
                }
            }
        }
        if (dir == null){
            int directionIndex = RobotPlayer.rng.nextInt(RobotPlayer.directions.length);
            dir = RobotPlayer.directions[directionIndex];
        }

        if (rc.canMove(dir)) {
            rc.move(dir);
        }

        // TODO: review strategy to build watchtowers & laboratories
        if(rc.getTeamLeadAmount(rc.getTeam()) > 7000 && RobotPlayer.turnCount % 100 == 0 && rc.canBuildRobot(RobotType.WATCHTOWER, dir)){
            rc.buildRobot(RobotType.WATCHTOWER, dir);
        }
    }
}
