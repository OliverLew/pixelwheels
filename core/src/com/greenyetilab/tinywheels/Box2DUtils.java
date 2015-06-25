package com.greenyetilab.tinywheels;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * A set of utility functions for Box2D
 */
public class Box2DUtils {
    private static final Vector2 FORWARD_VECTOR = new Vector2(0, 1);
    private static final Vector2 LATERAL_VECTOR = new Vector2(1, 0);

    public static Vector2 getForwardVelocity(Body body) {
        Vector2 currentRightNormal = body.getWorldVector(FORWARD_VECTOR);
        float v = currentRightNormal.dot(body.getLinearVelocity());
        return currentRightNormal.scl(v);
    }

    public static Vector2 getLateralVelocity(Body body) {
        Vector2 currentRightNormal = body.getWorldVector(LATERAL_VECTOR);
        float v = currentRightNormal.dot(body.getLinearVelocity());
        return currentRightNormal.scl(v);
    }

    public static void applyDrag(Body body, float factor) {
        Vector2 dragForce = body.getLinearVelocity().scl(-factor);
        body.applyForce(dragForce, body.getWorldCenter(), true);
    }

    public static Body createStaticBox(World world, float x, float y, float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x + width / 2, y + height / 2);
        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        body.createFixture(shape, 1);
        return body;
    }

    public static void setCollisionInfo(Body body, int categoryBits, int maskBits) {
        for (Fixture fixture : body.getFixtureList()) {
            Filter filter = fixture.getFilterData();
            filter.categoryBits = (short)categoryBits;
            filter.maskBits = (short)maskBits;
            fixture.setFilterData(filter);
        }
    }

    public static Body createStaticBodyForMapObject(World world, MapObject object) {
        final float u = Constants.UNIT_FOR_PIXEL;
        if (object instanceof RectangleMapObject) {
            Rectangle rect = ((RectangleMapObject)object).getRectangle();
            return createStaticBox(world, u * rect.getX(), u * rect.getY(), u * rect.getWidth(), u * rect.getHeight());
        } else if (object instanceof PolygonMapObject) {
            Polygon polygon = ((PolygonMapObject)object).getPolygon();
            float[] vertices = polygon.getVertices().clone();
            for (int idx = 0; idx < vertices.length; idx += 2) {
                vertices[idx] *= u;
                vertices[idx + 1] *= u;
            }

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(polygon.getX() * u, polygon.getY() * u);
            Body body = world.createBody(bodyDef);

            PolygonShape shape = new PolygonShape();
            shape.set(vertices);

            body.createFixture(shape, 1);
            return body;
        } else if (object instanceof EllipseMapObject) {
            Ellipse ellipse = ((EllipseMapObject)object).getEllipse();
            float radius = ellipse.width * u / 2;
            float x = ellipse.x * u + radius;
            float y = ellipse.y * u + radius;

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(x, y);
            Body body = world.createBody(bodyDef);

            CircleShape shape = new CircleShape();
            shape.setRadius(radius);

            body.createFixture(shape, 1);
            return body;
        }
        throw new RuntimeException("Unsupported MapObject type: " + object);
    }

    public static void setBodyRestitution(Body body, float restitution) {
        for (Fixture fixture : body.getFixtureList()) {
            fixture.setRestitution(restitution);
        }
    }

    /**
     * Returns vertices for a rectangle of size width x height with truncated corners of cornerSize
     */
    static float[] createOctogon(float width, float height, float cornerSize) {
        return createOctogon(width, height, cornerSize, cornerSize);
    }

    static float[] createOctogon(float width, float height, float cornerWidth, float cornerHeight) {
        return new float[]{
                width / 2 - cornerWidth, -height / 2,
                width / 2, -height / 2 + cornerHeight,
                width / 2, height / 2 - cornerHeight,
                width / 2 - cornerWidth, height / 2,
                -width / 2 + cornerWidth, height / 2,
                -width / 2, height / 2 - cornerHeight,
                -width / 2, -height / 2 + cornerHeight,
                -width / 2 + cornerWidth, -height / 2
        };
    }
}