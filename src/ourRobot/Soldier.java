package ourRobot;

import battlecode.common.*;

// TODO: change the move function to avoid obstacles and rubble

public class Soldier {
    /**
     * Run a single turn for a Soldier.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    static void runSoldier(RobotController rc) throws GameActionException {
        int radius = rc.getType().actionRadiusSquared;
        Team opponent = rc.getTeam().opponent();
        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);

        // try to attack
        boolean attacking = attack(rc, enemies);

        // Check if there are any enemy archons to find
        int archonsFound = rc.readSharedArray(0);
        if (archonsFound < RobotPlayer.initialArchons) {
            archonsFound = findArchons(rc, enemies, archonsFound);
        }

        if (!attacking) {
            // If not attacking, try to move close to an archon
            Direction toMove = getDirection(rc, archonsFound);
            if (rc.canMove(toMove)) {
                rc.move(toMove);
            }
        }
    }

    /*
    Choose the direction where the soldier is moving next:
        1. toward the closest archon
        2. toward an enemy robot
        3. randomly
     */
    static Direction getDirection(RobotController rc, int archonsFound){
        Direction dir = null;
        MapLocation me = rc.getLocation();
        try {
            for (int i = 0; i < archonsFound; i++) {
                MapLocation tryArchonLoc = new MapLocation(rc.readSharedArray(1 + 2 * i), rc.readSharedArray(1 + 2 * i + 1));

                // TODO: review logic here. I eliminated rewriting the array if the archon isn't valid.
                // TODO: if it isn't then just don't move towards it
                if (rc.canSenseLocation(tryArchonLoc)) {
                    if (rc.canSenseRobotAtLocation(tryArchonLoc)) {
                        RobotInfo tryArchon = rc.senseRobotAtLocation(tryArchonLoc);
                        if (tryArchon.getType().equals(RobotType.ARCHON)) {
                            dir = me.directionTo(tryArchonLoc);
                            System.out.println("I moved towards archon!");
                            // TODO: it's not moving towards closest archon, just toward an archon
                            break;
                        }
                    }
                }
            }

            // attack heuristic
            if (dir == null) {
                MapLocation h = new MapLocation(rc.readSharedArray(9), rc.readSharedArray(10));

                if (rc.canSenseLocation(h) && rc.canSenseRobotAtLocation(h)) {
                    RobotInfo robotInfo = rc.senseRobotAtLocation(h);
                    if (robotInfo.getTeam().equals(rc.getTeam().opponent())) {
                        // TODO: changed the logic a bit to avoid rewriting
                        dir = me.directionTo(h);
                        System.out.println("I moved towards heuristic!");
                    } else {
                        dir = RobotPlayer.directions[RobotPlayer.rng.nextInt(RobotPlayer.directions.length)];
                        System.out.println("I moved randomly " + archonsFound + ":" + RobotPlayer.initialArchons);
                    }
                }
            }
        } catch (GameActionException e){
            System.out.println(rc.getType() + " Exception");
            e.printStackTrace();
        }
        return dir;
    }

    /*
    Find archons among the "sensed" enemies
     */
    static int findArchons(RobotController rc, RobotInfo[] enemies, int archonsFound){
        // check if there are any enemy archons around
        try {
            for (RobotInfo robotInfo : enemies) {
                // write location of archon found, if new, to shared array
                if (robotInfo.getType().equals(RobotType.ARCHON)) {
                    boolean newArchon = true;
                    for (int i = 0; i < archonsFound; i++) {
                        if (rc.readSharedArray(1 + 2 * i) == robotInfo.location.x &&
                                rc.readSharedArray(1 + 2 * i + 1) == robotInfo.location.y)
                            newArchon = false;
                    }

                    if (newArchon) {
                        rc.writeSharedArray(0, archonsFound + 1);
                        rc.writeSharedArray(1 + 2 * archonsFound, robotInfo.location.x);
                        rc.writeSharedArray(1 + 2 * archonsFound + 1, robotInfo.location.y);
                        archonsFound++;
                        System.out.println("New Archon, yayyyyyyyy!!!!");
                    }
                } else {
                    // first 9 positions correspond to the archon locations
                    // [4, x1, y1, x2, y2, x3, y3, x4, y4, Hx, Hy]
                    //                                     ^ index 9
                    // index 9 and 10 are for the attack heuristic
                    // TODO: this is not efficient, choose which one should be written in the heuristic
                    // TODO: which type of robot should go here?
                    rc.writeSharedArray(9, robotInfo.location.x);
                    rc.writeSharedArray(10, robotInfo.location.y);
                }
            }
        } catch (GameActionException e){
            System.out.println(rc.getType() + " Exception");
            e.printStackTrace();
        }
        return archonsFound;
    }

    /*
        Attack archons if within range of attack, otherwise, attack enemies in the range.
        TODO: they attack everything if there are no archons, should we prioritize based on the type of enemy?
     */
    static boolean attack(RobotController rc, RobotInfo[] enemies){
        if (enemies.length > 0) {
            MapLocation toAttack = null;

            for (int i = 0; i < enemies.length; i++)
                if (enemies[i].getType().equals(RobotType.ARCHON)) {
                    toAttack = enemies[i].location;
                    break;
                }

            if (toAttack == null)
                toAttack = enemies[0].location;

            if (rc.canAttack(toAttack)) {
                try {
                    rc.attack(toAttack);
                    return true;
                } catch (GameActionException e){
                    System.out.println(rc.getType() + " Exception");
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
