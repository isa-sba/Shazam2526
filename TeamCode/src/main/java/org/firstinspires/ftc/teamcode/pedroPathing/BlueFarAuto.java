package org.firstinspires.ftc.teamcode.pedroPathing; // make sure this aligns with class location
import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import  com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Configurable
@Autonomous(name = "BlueFarAuto" , group = "Examples")
public class BlueFarAuto extends OpMode {
    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;
    private int pathState;
    public PathChain Path1;
    public PathChain Path2;
    public PathChain Path3;
    public PathChain Path4;
    public PathChain Path5;

    DcMotor leftOuttake, rightOuttake;
    CRServo loading;
    DcMotor intake;

    private final Pose startPose = new Pose(60, 7, Math.toRadians(0));

    public boolean launchingBalls(Timer timer, double shooterSpinUpTime) {
        if (timer.getElapsedTimeSeconds() > 6) {
            leftOuttake.setPower(0);
            rightOuttake.setPower(0);
            loading.setPower(0);
            intake.setPower(0);
            System.out.println("turned off");
            return true;
        }
        leftOuttake.setPower(1);
        rightOuttake.setPower(1);
        System.out.println("turned on shooters");
        if (timer.getElapsedTimeSeconds() > shooterSpinUpTime) {
            loading.setPower(1);
            if(timer.getElapsedTimeSeconds()>4) {
                intake.setPower(1);
            }


            System.out.println("intake and loading");
        }
        return false;

    }
    public boolean intakingBalls(){

        loading.setPower(1);
        intake.setPower(1);

        System.out.println("intake and loading");
        return true;

    }

    public void buildPaths() {
        Path1 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(60, 7),

                                new Pose(58.000, 130.000)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                .setBrakingStrength(0.2)
                .build();

        Path2 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(58.000, 130.000),

                                new Pose(47.727, 93.000)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(180))
                .setBrakingStrength(0.2)
                .build();

        Path3 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(47.727, 93.000),

                                new Pose(15, 93.000)
                        )
                ).setTangentHeadingInterpolation()
                .setBrakingStrength(0.2)
                .build();

        Path4 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(15.725, 93.000),

                                new Pose(58.000, 130.000)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(0))
                .setBrakingStrength(0.2)
                .build();

        Path5 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(58.000, 130.000),

                                new Pose(53.275, 130.317)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(-90))
                .setBrakingStrength(0.2)
                .build();
    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(Path1);
                setPathState(1);
                break;

            case 1:
                if (!follower.isBusy())
                    setPathState(6);
                break;

            case 6:

            /* You could check for
            - Follower State: "if(!follower.isBusy()) {}"
            - Time: "if(pathTimer.getElapsedTimeSeconds() > 1) {}"
            - Robot Position: "if(follower.getPose().getX() > 36) {}"
            */

                System.out.println("case 1");
                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the scorePose's position */
                    //System.out.println("in");
                if (launchingBalls(pathTimer, 2)) {
                    /* Score Preload */
                    System.out.println("yippee");

                    /* Since this is a pathChain, we can have Pedro hold the end point while we are grabbing the sample */
                    follower.followPath(Path2, true);
                    intake.setPower(1);
                    setPathState(2);
                }
                break;
            case 2:
                if (!follower.isBusy()) {
                    //System.out.println("in");
                    follower.followPath(Path3, true);
                    setPathState(3);
                }
                break;
            case 3:
                if (!follower.isBusy()) {
                    System.out.println("turned off");
                    setPathState(4);
                    follower.followPath(Path4, true);
                    intake.setPower(0);

                }
                break;
            case 4:
                if (!follower.isBusy())
                    setPathState(7);
                break;
            case 7:
                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the scorePose's position */
                if (!follower.isBusy()) {
                    //System.out.println("in");
                    if (launchingBalls(pathTimer, 3)) {
                        /* Score Preload */
                        System.out.println("yippee");

                        /* Since this is a pathChain, we can have Pedro hold the end point while we are grabbing the sample */
                        follower.followPath(Path5, true);
                        setPathState(5);
                    }
                }
                break;
            case 5:
                if (!follower.isBusy())
                    setPathState(-1);
                break;

        }
    }

    /**
     * These change the states of the paths and actions. It will also reset the timers of the individual switches
     **/
    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    /**
     * This is the main loop of the OpMode, it will run repeatedly after clicking "Play".
     **/
    @Override
    public void loop() {

        // These loop the movements of the robot, these must be called continuously in order to work
        follower.update();
        autonomousPathUpdate();

        // Feedback to Driver Hub for debugging
        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.update();
    }

    /**
     * This method is called once at the init of the OpMode.
     **/
    @Override
    public void init() {
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();
        leftOuttake = hardwareMap.get(DcMotor.class, "lo");
        rightOuttake = hardwareMap.get(DcMotor.class, "ro");
        loading = hardwareMap.get(CRServo.class, "l");
        intake = hardwareMap.get(DcMotor.class, "i");

        leftOuttake.setDirection(DcMotorSimple.Direction.REVERSE);
        loading.setDirection(DcMotorSimple.Direction.REVERSE);
        intake.setDirection(DcMotorSimple.Direction.REVERSE);

        follower = Constants.createFollower(hardwareMap);
        buildPaths();
        follower.setStartingPose(startPose);

    }

    /**
     * This method is called continuously after Init while waiting for "play".
     **/
    @Override
    public void init_loop() {
    }

    /**
     * This method is called once at the start of the OpMode.
     * It runs all the setup actions, including building paths and starting the path system
     **/
    @Override
    public void start() {
        opmodeTimer.resetTimer();
        setPathState(0);
    }

    /**
     * We do not use this because everything should automatically disable
     **/
    @Override
    public void stop() {
    }
}