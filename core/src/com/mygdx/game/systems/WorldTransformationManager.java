package com.mygdx.game.systems;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.math.Matrix3;
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

    public int getParent(int entityId) {
        Parented parented = mParented.get(entityId);
        return parented != null
                ? parented.Parent
                : -1;
    }

    //region Local Properties
    public Vector2 getLocalPosition(int entityId) throws IllegalArgumentException {
        Transform transform = mTransform.get(entityId);

        if (transform == null)
            throw new IllegalArgumentException("Entity has no Transform component");

        return transform.Position;
    }

    public void setLocalPosition(int entityId, float x, float y) throws IllegalArgumentException {
        Transform transform = mTransform.get(entityId);

        if (transform == null)
            throw new IllegalArgumentException("Entity has no Transform component");

        transform.Position.set(x, y);
        markDirty(entityId);
    }

    public float getLocalRotation(int entityId) throws IllegalArgumentException {
        Transform transform = mTransform.get(entityId);

        if (transform == null)
            throw new IllegalArgumentException("Entity has no Transform component");

        return transform.Rotation;
    }

    public void setLocalRotation(int entityId, float degrees) throws IllegalArgumentException {
        Transform transform = mTransform.get(entityId);

        if (transform == null)
            throw new IllegalArgumentException("Entity has no Transform component");

        transform.Rotation = degrees;
        markDirty(entityId);
    }

    public Vector2 getLocalScale(int entityId) throws IllegalArgumentException {
        Transform transform = mTransform.get(entityId);

        if (transform == null)
            throw new IllegalArgumentException("Entity has no Transform component");

        return transform.Scale;
    }

    public void setLocalScale(int entityId, float x, float y) {
        Transform transform = mTransform.get(entityId);

        if (transform == null)
            throw new IllegalArgumentException("Entity has no Transform component");

        transform.Scale.set(x, y);
        markDirty(entityId);
    }
    //endregion

    //region World Properties
    public Vector2 getWorldPosition(int entityId) {
        Vector2 localPos = getLocalPosition(entityId);

        int parent = getParent(entityId);
        if (parent != -1)
            return transformPoint(parent, localPos);

        return localPos;
    }

    public void setWorldPosition(int entityId, float x, float y) {
        int parent = getParent(entityId);
        if (parent != -1)
        {
            Vector2 finalPos = inverseTransformPoint(parent, x, y);
            setLocalPosition(entityId, finalPos.x, finalPos.y);
        }
        else
            setLocalPosition(entityId, x, y);
    }

    public float getWorldRotation(int entityId) {
        float localRot = getLocalRotation(entityId);

        int parent = getParent(entityId);
        if (parent != -1)
            return getLocalRotation(parent) + localRot;

        return localRot;
    }

    public void setWorldRotation(int entityId, float degrees) {
        int parent = getParent(entityId);
        if (parent != -1)
            setLocalRotation(entityId, degrees - getLocalRotation(parent));
        else
            setLocalRotation(entityId, degrees);
    }

    public Vector2 getWorldScale(int entityId) {
        Vector2 localScale = getLocalScale(entityId);

        int parent = getParent(entityId);
        if (parent != -1)
            return transformVector(parent, localScale);

        return localScale;
    }

    public void setWorldScale(int entityId, float x, float y) {
        int parent = getParent(entityId);
        if (parent != -1)
        {
            Vector2 finalScale = inverseTransformVector(parent, x, y);
            setLocalScale(entityId, finalScale.x, finalScale.y);
        }
        else
            setLocalScale(entityId, x, y);
    }
    //endregion

    //region Matrices
    public Matrix3 getLocalToParentMatrix(int entityId) {
        // todo: cache result and make sure to create a copy from cache so cache isn't modified.

        Transform transform = mTransform.get(entityId);

        if (transform == null)
            throw new IllegalArgumentException("Entity has no Transform component");

        Matrix3 matrix = new Matrix3();
        matrix.translate(transform.Position.x, transform.Position.y);
        matrix.scale(transform.Scale.x, transform.Scale.y);
        matrix.rotate(transform.Rotation);
        return matrix;
    }

    public Matrix3 getLocalToWorldMatrix(int entityId) {
        // todo: cache result

        Matrix3 localToParentMatrix = getLocalToParentMatrix(entityId);
        int parent = getParent(entityId);

        if (parent != -1)
            return getLocalToWorldMatrix(parent).mul(localToParentMatrix);

        return localToParentMatrix;
    }

    public Matrix3 getWorldToLocalMatrix(int entityId) {
        // todo: cache result
        return getLocalToWorldMatrix(entityId).inv();
    }
    //endregion

    //region Matrix transformation helpers
    public Vector2 transformPoint(int entityId, float x, float y) {
        return new Vector2(x, y).mul(getLocalToWorldMatrix(entityId));
    }

    public Vector2 transformPoint(int entityId, Vector2 localPoint) {
        return transformPoint(entityId, localPoint.x, localPoint.y);
    }

    public Vector2 inverseTransformPoint(int entityId, float x, float y) {
        return new Vector2(x, y).mul(getWorldToLocalMatrix(entityId));
    }

    public Vector2 inverseTransformPoint(int entityId, Vector2 worldPoint) {
        return inverseTransformPoint(entityId, worldPoint.x, worldPoint.y);
    }

    public Vector2 transformDirection(int entityId, Vector2 localDirection) {
        Matrix3 matrix = getLocalToWorldMatrix(entityId);
        return mulNormal(localDirection.cpy(), matrix);
    }

    public Vector2 inverseTransformDirection(int entityId, Vector2 worldDirection) {
        Matrix3 matrix = getWorldToLocalMatrix(entityId);
        return mulNormal(worldDirection.cpy(), matrix);
    }

    private Vector2 mulNormal(Vector2 vec, Matrix3 matrix) {
        float tmpX = vec.x;
        float tmpY = vec.y;
        vec.x = (tmpX * matrix.val[Matrix3.M11]) + (tmpY * matrix.val[Matrix3.M21]);
        vec.y = (tmpX * matrix.val[Matrix3.M12]) + (tmpY * matrix.val[Matrix3.M22]);
        return vec;
    }

    public Vector2 transformVector(int entityId, float x, float y) {
        Matrix3 matrix = getLocalToWorldMatrix(entityId);

        Vector2 result = new Vector2();
        matrix.getScale(result);
        result.scl(x, y);
        return result;
    }

    public Vector2 transformVector(int entityId, Vector2 localVector) {
        return transformVector(entityId, localVector.x, localVector.y);
    }

    public Vector2 inverseTransformVector(int entityId, float x, float y) {
        Matrix3 matrix = getWorldToLocalMatrix(entityId);

        Vector2 result = new Vector2();
        matrix.getScale(result);
        result.scl(x, y);
        return result;
    }

    public Vector2 inverseTransformVector(int entityId, Vector2 worldVector) {
        return inverseTransformVector(entityId, worldVector.x, worldVector.y);
    }
    //endregionsss

    private void markDirty(int entityId) {
        // just remove cached value
    }
}
