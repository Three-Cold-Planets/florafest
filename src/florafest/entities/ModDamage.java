package florafest.entities;


import arc.audio.Sound;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.Time;
import florafest.content.FloraFx;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.*;

public class ModDamage {
    public static Seq<Unit> list = new Seq<>();

    public static void chain(Position origin, Position targetPos, Team team, IntSeq collided, Sound hitSound, Effect hitEffect, float power, float initialPower, boolean chainAllied, float width, float distanceDamageFalloff, float pierceDamageFactor, int branches, float segmentLength, float arc, int coils, Color color, BulletType lightningType){
        if(power < initialPower/16) return;
        hitSound.at(targetPos.getX(), targetPos.getY(), Mathf.random(0.8f, 1.1f));
        //Scales down width based on percent of power left
        float w = width * power/(initialPower);

        FloraFx.chainLightning.at(targetPos.getX(), targetPos.getY(), 0, color, new FloraFx.VisualLightningHolder() {
            @Override
            public Vec2 start() {
                return new Vec2(origin.getX(), origin.getY());
            }

            @Override
            public Vec2 end() {
                return new Vec2(targetPos.getX(), targetPos.getY());
            }

            @Override
            public float width() {
                return w;
            }

            @Override
            public float segLength(){
                return segmentLength;
            }

            @Override
            public float arc() {
                return arc;
            }

            @Override
            public int coils(){
                return coils;
            }
        });
        hitEffect.at(targetPos.getX(), targetPos.getY(), 0, color);

        float effectiveRange = power/distanceDamageFalloff;

        final float newPower = power * (pierceDamageFactor == 0 ? 1 : pierceDamageFactor);

        boolean derelict = team.id == Team.derelict.id;
        int teamID = team.id;

        Position tPos = targetPos;

        Time.run(15, () -> {
            Seq<Unit> units = Groups.unit.intersect(targetPos.getX() - effectiveRange, targetPos.getY() - effectiveRange, effectiveRange * 2, effectiveRange * 2);
            units.sort(u -> u.dst(tPos));
            Unit targ = units.find(u -> u == targetPos);

            list.clear();

            for (int i = 0; i < Math.min(branches, units.size); i++) {
                Unit unit = units.get(i);
                if(collided.contains(unit.id) || !derelict && unit.team.id == teamID && !chainAllied) continue;
                float dst = unit.dst(targetPos);
                if(dst > effectiveRange) break;
                list.add(unit);
            }
            if(list.size == 0){
                for (int i = 0; i < Math.min(branches, units.size); i++) {
                    //jump to random locations around targetPos
                    float range = newPower / distanceDamageFalloff;
                    Vec2 newPos = new Vec2().trns(Mathf.random(360), Mathf.random(range)).add(targetPos);
                    float newDamage = newPower - distanceDamageFalloff * (targetPos.dst(newPos));

                    chain(targetPos, newPos, team, collided, hitSound, hitEffect, newDamage, initialPower, chainAllied, width, distanceDamageFalloff, pierceDamageFactor, branches, segmentLength, arc, coils, color, lightningType);
                }
                return;
            }
            float numberMultiplier = 1.0f/list.size;

            list.each(u -> {
                float newDamage = power - distanceDamageFalloff * (targetPos.dst(u));
                if(newPower < 0) return;

                //Damaging logic
                if(u.team != team) u.damage(power);
                else u.heal(power);
                collided.add(u.id);
                chain(targetPos, new Vec2(u.x, u.y), team, collided, hitSound, hitEffect, newDamage * numberMultiplier, initialPower, chainAllied, width, distanceDamageFalloff, pierceDamageFactor, branches, segmentLength, arc, coils, color, lightningType);
            });
        });
    }

    //Used when shot from a bullet
    public static void chain(Entityc owner, Position origin, Unit current, Team team, Sound hitSound, Effect hitEffect, float power, float initialPower, boolean chainAllied, float width, float distanceDamageFalloff, float pierceDamageFactor, int branches, float segmentLength, float arc, int coils, Color color, BulletType lightningType) {
        lightningType.create(owner, team, current.x, current.y, 0, power * (owner instanceof Statusc s ? s.damageMultiplier() : 1), 0, 1, 0);
        chain(origin, new Vec2(current.x, current.y), Team.derelict, IntSeq.with(current.id), hitSound, hitEffect, power, initialPower, chainAllied, width, distanceDamageFalloff, pierceDamageFactor, branches, segmentLength, arc, coils, color, lightningType);
    }

}
