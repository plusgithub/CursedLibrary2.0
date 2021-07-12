package com.cursedplanet.cursedlibrary.pathfinder;

import com.github.ysl3000.bukkit.pathfinding.entity.Insentient;
import com.github.ysl3000.bukkit.pathfinding.pathfinding.PathfinderGoal;
import org.bukkit.entity.LivingEntity;

public class GoalFollowEntity implements PathfinderGoal {

	private final LivingEntity entity;
	private final double moveRadius;
	private boolean isAlreadySet;
	private final Insentient pathfinderGoalEntity;
	private final double walkSpeed;


	public GoalFollowEntity(Insentient pathfinderGoalEntity, LivingEntity entity,
							double moveRadius, double walkSpeed) {
		this.entity = entity;
		this.moveRadius = moveRadius;
		this.pathfinderGoalEntity = pathfinderGoalEntity;
		this.walkSpeed = walkSpeed;
	}

	@Override
	public boolean shouldExecute() {
		return this.isAlreadySet = !this.pathfinderGoalEntity.getNavigation().isDoneNavigating();
	}

	/***
	 * Whether the goal should Terminate
	 *
	 * @return true if should terminate
	 */
	@Override
	public boolean shouldTerminate() {
		if (!this.isAlreadySet) {
			return true;
		}
		return this.pathfinderGoalEntity.getBukkitEntity().getLocation()
				.distance(this.entity.getLocation()) > this.moveRadius;
	}

	/***
	 * Runs initially and should be used to setUp goalEnvironment Condition needs to be defined thus
	 * your code in it isn't called
	 */
	@Override
	public void init() {
		if (!this.isAlreadySet) {
			this.pathfinderGoalEntity.getNavigation().moveTo(this.entity, walkSpeed);
		}
	}

	/***
	 * Is called when {@link #shouldExecute()} returns true
	 */
	@Override
	public void execute() {
		this.pathfinderGoalEntity.jump();
	}

	@Override
	public void reset() {

	}
}
