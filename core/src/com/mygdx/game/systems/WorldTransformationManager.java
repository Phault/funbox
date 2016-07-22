package com.mygdx.game.systems;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.link.LinkListener;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntMap;
import com.mygdx.game.components.Children;
import com.mygdx.game.components.Parented;
import com.mygdx.game.components.Transform;

import java.util.IdentityHashMap;

/**
 * Created by Casper on 19-07-2016.
 */
public class WorldTransformationManager extends BaseSystem implements LinkListener {

    private ComponentMapper<Parented> mParented;
    private ComponentMapper<Children> mChildren;
    private ComponentMapper<Transform> mTransform;

    private final IntMap<Matrix3> localToParentCache = new IntMap<Matrix3>();
    private final IntMap<Matrix3> localToWorldCache = new IntMap<Matrix3>();
    private final IntMap<Matrix3> worldToLocalCache = new IntMap<Matrix3>();

    public WorldTransformationManager() {

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

    public IntBag getChildren(int entityId) {
        Children children = mChildren.get(entityId);
        return children != null
                ? children.Targets
                : null;
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

        if (transform.Position.epsilonEquals(x, y, MathUtils.FLOAT_ROUNDING_ERROR))
            return;

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

        if (MathUtils.isEqual(transform.Rotation, degrees))
            return;

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

        if (transform.Scale.epsilonEquals(x, y, MathUtils.FLOAT_ROUNDING_ERROR))
            return;

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

    // todo: might not be needed anymore due to LinkListener
//    public void setParent(int entityId, int parent)
//    {
//        Parented parented = mParented.create(entityId);
//        parented.Parent = parent;
//        markDirty(entityId);
//    }
//
//    public void removeParent(int entityId)
//    {
//        mParented.remove(entityId);
//        markDirty(entityId);
//    }

    //region Matrices
    public Matrix3 getLocalToParentMatrix(int entityId) {
        if (localToParentCache.containsKey(entityId))
            return localToParentCache.get(entityId);

        Transform transform = mTransform.get(entityId);

        if (transform == null)
            throw new IllegalArgumentException("Entity has no Transform component");

        Matrix3 matrix = new Matrix3();
        matrix.translate(transform.Position.x, transform.Position.y);
        matrix.scale(transform.Scale.x, transform.Scale.y);
        matrix.rotate(transform.Rotation);

        localToParentCache.put(entityId, matrix);

        return matrix;
    }

    public Matrix3 getLocalToWorldMatrix(int entityId) {
        if (localToWorldCache.containsKey(entityId))
            return localToWorldCache.get(entityId);

        Matrix3 localToParentMatrix = getLocalToParentMatrix(entityId);
        int parent = getParent(entityId);

        Matrix3 result = localToParentMatrix;

        if (parent != -1)
            result = new Matrix3(getLocalToWorldMatrix(parent)).mul(localToParentMatrix);

        localToWorldCache.put(entityId, result);

        return result;
    }

    public Matrix3 getWorldToLocalMatrix(int entityId) {
        if (worldToLocalCache.containsKey(entityId))
            return worldToLocalCache.get(entityId);

        Matrix3 result = new Matrix3(getLocalToWorldMatrix(entityId)).inv();
        worldToLocalCache.put(entityId, result);
        return result;
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
    //endregion

    private void markDirty(int entityId) {
        IntBag children = getChildren(entityId);
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                markDirty(children.get(i));
            }
        }

        localToParentCache.remove(entityId);
        localToWorldCache.remove(entityId);
        worldToLocalCache.remove(entityId);
    }

    @Override
    public void onLinkEstablished(int sourceId, int targetId) {
        Gdx.app.log("WTM", "link established");

        Children children = mChildren.create(targetId);
        children.Targets.add(sourceId);

        markDirty(sourceId);
    }

    @Override
    public void onLinkKilled(int sourceId, int targetId) {
        Gdx.app.log("WTM", "link killed");

        removeFromChildren(sourceId, targetId);
    }

    @Override
    public void onTargetDead(int sourceId, int deadTargetId) {
        Gdx.app.log("WTM", "link target dead");

        mParented.remove(sourceId);
        markDirty(sourceId);
    }

    @Override
    public void onTargetChanged(int sourceId, int targetId, int oldTargetId) {
        Gdx.app.log("WTM", "link target changed");

        Children children = mChildren.create(targetId);
        children.Targets.add(sourceId);
        removeFromChildren(sourceId, oldTargetId);
    }

    private void removeFromChildren(int childId, int parentId) {
        Children children = mChildren.get(parentId);
        if (children != null)
        {
            children.Targets.removeValue(childId);
            if (children.Targets.size() == 0)
                mChildren.remove(parentId);
        }

        markDirty(childId);
    }
}
