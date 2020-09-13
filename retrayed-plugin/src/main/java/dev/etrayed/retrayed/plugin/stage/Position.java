package dev.etrayed.retrayed.plugin.stage;

/**
 * @author Etrayed
 */
public class Position {

    private final double x, y, z;

    private final byte yaw, pitch;

    public Position(double x, double y, double z) {
        this(x, y, z, (byte) 0, (byte) 0);
    }

    public Position(double x, double y, double z, byte yaw, byte pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        return z;
    }

    public byte yaw() {
        return yaw;
    }

    public byte pitch() {
        return pitch;
    }

    public Position setX(double x) {
        return new Position(x, y, z);
    }

    public Position setY(double y) {
        return new Position(x, y, z);
    }

    public Position setZ(double z) {
        return new Position(x, y, z);
    }

    public Position setYaw(byte yaw) {
        return new Position(x, y, z, yaw, pitch);
    }

    public Position setPitch(byte pitch) {
        return new Position(x, y, z, yaw, pitch);
    }

    public Position add(double x, double y, double z) {
        return new Position(this.x + x, this.y + y, this.z + z);
    }

    public Position subtract(double x, double y, double z) {
        return new Position(this.x - x, this.y - y, this.z - z);
    }
}
