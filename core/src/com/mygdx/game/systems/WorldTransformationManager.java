package com.mygdx.game.systems;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.components.Parented;
import com.mygdx.game.components.Transform;

/**
 * Created by Casper on 19-07-2016.
 */
public class WorldTransformationManager extends BaseEntitySystem {

    private static final Vector3 forwardAxis = new Vector3(0, 0, 1);

    private ComponentMapper<Parented> mParented;
    private ComponentMapper<Transform> mTransform;

//    private Map<Transform, Matrix4> worldMatrixMap = new IdentityHashMap<Transform, Matrix4>();

    public WorldTransformationManager() {
        super(Aspect.all(Parented.class, Transform.class));
    }

    @Override
    protected void processSystem() {

    }

    public Vector2 getPosition(int entityId)
    {
        final Vector3 position = new Vector3();
        Matrix4 matrix = getMatrix(entityId);
        matrix.getTranslation(position);
        return new Vector2(position.x, position.y);
    }

    public Vector2 getScale(int entityId)
    {
        Matrix4 matrix = getMatrix(entityId);
        return new Vector2(matrix.getScaleX(), matrix.getScaleY());
    }

    public float getRotation(int entityId)
    {
        final Quaternion quaternion = new Quaternion();
        Matrix4 matrix = getMatrix(entityId);
        matrix.getRotation(quaternion, true);
        return quaternion.getAxisAngle(forwardAxis);
    }

    public Matrix4 getMatrix(int entityId)
    {
        Matrix4 localMatrix = getLocalMatrix(mTransform.get(entityId));
        Parented parented = mParented.get(entityId);

        if (parented != null)
            return localMatrix.mulLeft(getMatrix(parented.Parent));

        return localMatrix;
    }

    private Matrix4 getLocalMatrix(Transform transform)
    {
        Matrix4 matrix = new Matrix4();
        matrix.translate(transform.Position.x, transform.Position.y, 0);
        matrix.scale(transform.Scale.x, transform.Scale.y, 1);
        matrix.rotate(forwardAxis, transform.Rotation);
        return matrix;
    }
}
