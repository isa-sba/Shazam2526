package org.firstinspires.ftc.teamcode.pedroPathing; // make sure this aligns with class location
import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import  com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Configurable
@Autonomous(name = "RedGoalAuto", group = "Examples")
public class RedGoalAuto extends OpMode {
    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;
    private int pathState;
    public PathChain Path1;
    public PathChain Path2;

    DcMotor leftOuttake, rightOuttake;
    CRServo loading;
    DcMotor intake;

    private final Pose startPose = new Pose(115.343, 133.119, Math.toRadians(215));
    public boolean launchingBalls(Timer timer,double shooterSpinUpTime){
        if(timer.getElapsedTimeSeconds()>10){
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
        if(timer.getElapsedTimeSeconds()>shooterSpinUpTime) {
            intake.setPower(1);
            loading.setPower(1);
            System.out.println("intake and loading");
        }
        return false;

    }
    public void buildPaths() {
        Path1 = follower
                .pathBuilder()
                .addPath(
                        new BezierLine(new Pose(115.343, 133.119), new Pose(98.936, 98.526))
                )
                .setLinearHeadingInterpolation(Math.toRadians(215), Math.toRadians(225))
                .setBrakingStrength(0.5)
                .build();

        Path2 = follower
                .pathBuilder()
                .addPath(
                        new BezierLine(new Pose(98.936, 98.526), new Pose(131.70981507823612, 95.24893314366999))
                )
                .setLinearHeadingInterpolation(Math.toRadians(225), Math.toRadians(225))
                .setBrakingStrength(0.5)
                .build();
    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(Path1);
                setPathState(1);
                break;

            case 1:

            /* You could check for
            - Follower State: "if(!follower.isBusy()) {}"
            - Time: "if(pathTimer.getElapsedTimeSeconds() > 1) {}"
            - Robot Position: "if(follower.getPose().getX() > 36) {}"
            */

                System.out.println("case 2");
                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the scorePose's position */
                if (!follower.isBusy()) {
                    //System.out.println("in");
                    if (launchingBalls(pathTimer,5)) {
                        /* Score Preload */
                        System.out.println("yippee");

                        /* Since this is a pathChain, we can have Pedro hold the end point while we are grabbing the sample */
                        follower.followPath(Path2, true);
                        setPathState(-1);
                    }
                }
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
        leftOuttake = hardwareMap.get(DcMotor.class,"lo");
        rightOuttake = hardwareMap.get(DcMotor.class,"ro");
        loading = hardwareMap.get(CRServo.class,"l");
        intake = hardwareMap.get(DcMotor.class,"i");

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