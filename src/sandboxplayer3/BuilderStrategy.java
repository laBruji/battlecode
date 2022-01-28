package sandboxplayer3;

import battlecode.common.*;

import java.util.Random;

strictfp class BuilderStrategy {

    static int turn = 0;

    static void runBuilder(RobotController rc) throws GameActionException{
        
        turn ++;
        //repair neighboring buildings
        MapLocation me = rc.getLocation();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                MapLocation repLocation = new MapLocation(me.x + dx, me.y + dy);
                while (rc.canRepair(repLocation)) {
                    rc.repair(repLocation);
                }
            }
        }

        RobotInfo[] robots = rc.senseNearbyRobots();
        int distance = Integer.MAX_VALUE;
        MapLocation targetLocation = null;
        for(RobotInfo robot : robots){
            if(robot.getTeam().equals(rc.getTeam()) && robot.type.isBuilding()  &&robot.health < robot.type.getMaxHealth(robot.level)){
                if(rc.getLocation().distanceSquaredTo(robot.location) < distance){
                    targetLocation = robot.getLocation();
                    distance = rc.getLocation().distanceSquaredTo(robot.location);
                }
            }
        }

        if (targetLocation != null) {
            Pathing.walkTowards(rc, targetLocation);
        }

        int directionIndex = RobotPlayer.rng.nextInt(RobotPlayer.directions.length);
        Direction dir = RobotPlayer.directions[directionIndex];
        if (rc.canMove(dir)) {
            rc.move(dir);
            System.out.println("I moved!");
        }

        if(rc.getTeamLeadAmount(rc.getTeam()) > 10000 && turn % 150 == 0 && rc.canBuildRobot(RobotType.LABORATORY, dir)){
            rc.buildRobot(RobotType.LABORATORY, dir);
        } else if(rc.getTeamLeadAmount(rc.getTeam()) > 7000 && turn % 100 == 0 && rc.canBuildRobot(RobotType.WATCHTOWER, dir)){
            rc.buildRobot(RobotType.WATCHTOWER, dir);
        }
    }
}
