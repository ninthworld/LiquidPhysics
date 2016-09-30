package org.ninthworld.liquidphysics.helper;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.liquidphysics.entities.CameraEntity;

/**
 * Created by NinthWorld on 9/29/2016.
 */
public class MatrixHelper {
    public static Matrix4f createTransformationMatrix(Vector2f translation, float rotation, Vector2f scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.m00 = 1f;
        matrix.m11 = 1f;
        matrix.m22 = 1f;
        matrix.m33 = 1f;
        matrix.m03 = 2f*translation.getX()/((float) Display.getWidth());
        matrix.m13 = -2f*translation.getY()/((float) Display.getHeight());
        matrix.m33 = 1f;

        return matrix;
    }

    public static Matrix4f createViewMatrix(CameraEntity camera) {
        Matrix4f matrix = new Matrix4f();
        matrix.m00 = 1f;
        matrix.m11 = 1f;
        matrix.m22 = 1f;
        matrix.m33 = 1f;
        matrix.m03 = -2f*camera.getPosition().getX()/((float) Display.getWidth());
        matrix.m13 = 2f*camera.getPosition().getY()/((float) Display.getHeight());
        matrix.m33 = 1f;

        return matrix;
    }

    public static Matrix4f createProjectionMatrix() {
        Matrix4f matrix = new Matrix4f();
        matrix.m00 = 2f / Display.getWidth();
        matrix.m11 = 2f / Display.getHeight();
        matrix.m22 = 1f;
        matrix.m33 = 1f;
        matrix.m03 = -1f;
        matrix.m13 = 1f;

        return matrix;
    }
}
