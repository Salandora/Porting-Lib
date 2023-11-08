package io.github.fabricators_of_create.porting_lib.extensions.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.BlockEntityExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.extensions.LevelExtensions;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Mixin(value = Level.class, priority = 1100) // need to apply after lithium
public abstract class LevelMixin implements LevelAccessor, LevelExtensions {
	// only non-null during transactions. Is set back to null in
	// onFinalCommit on commits, and through snapshot rollbacks on aborts.
	@Unique
	private List<ChangedPosData> port_lib$modifiedStates = null;
	@Unique
	private final ArrayList<BlockEntity> port_lib$freshBlockEntities = new ArrayList<>();
	@Unique
	private final ArrayList<BlockEntity> port_lib$pendingFreshBlockEntities = new ArrayList<>();

	@Unique
	private final SnapshotParticipant<LevelSnapshotData> port_lib$snapshotParticipant = new SnapshotParticipant<>() {

		@Override
		protected LevelSnapshotData createSnapshot() {
			LevelSnapshotData data = new LevelSnapshotData(port_lib$modifiedStates);
			if (port_lib$modifiedStates == null) port_lib$modifiedStates = new LinkedList<>();
			return data;
		}

		@Override
		protected void readSnapshot(LevelSnapshotData snapshot) {
			port_lib$modifiedStates = snapshot.changedStates();
		}

		@Override
		protected void onFinalCommit() {
			super.onFinalCommit();
			List<ChangedPosData> modifications = port_lib$modifiedStates;
			port_lib$modifiedStates = null;
			for (ChangedPosData data : modifications) {
				setBlock(data.pos(), data.state(), data.flags());
			}
		}
	};

	@Shadow
	public abstract BlockState getBlockState(BlockPos blockPos);

	@Shadow
	private boolean tickingBlockEntities;

	@Override
	public SnapshotParticipant<LevelSnapshotData> snapshotParticipant() {
		return port_lib$snapshotParticipant;
	}

	@Inject(method = "getBlockState", at = @At(value = "INVOKE", shift = Shift.BEFORE,
			target = "Lnet/minecraft/world/level/Level;getChunk(II)Lnet/minecraft/world/level/chunk/LevelChunk;"), cancellable = true)
	private void port_lib$getBlockState(BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
		if (port_lib$modifiedStates != null) {
			// iterate in reverse order - latest changes priority
			for (ChangedPosData data : port_lib$modifiedStates) {
				if (data.pos().equals(pos)) {
					BlockState state = data.state();
					if (state == null) {
						new Throwable().printStackTrace();
					} else {
						cir.setReturnValue(state);
					}
					return;
				}
			}
		}
	}

	@Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
			at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/world/level/Level;getChunkAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/chunk/LevelChunk;"), cancellable = true)
	private void port_lib$setBlock(BlockPos pos, BlockState state, int flags, int recursionLeft, CallbackInfoReturnable<Boolean> cir) {
		if (state == null) {
			new Throwable().printStackTrace();
		}
		if (port_lib$modifiedStates != null) {
			port_lib$modifiedStates.add(new ChangedPosData(pos, state, flags));
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "tickBlockEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V", shift = Shift.AFTER))
	public void port_lib$pendingBlockEntities(CallbackInfo ci) {
		if (!this.port_lib$pendingFreshBlockEntities.isEmpty()) {
			this.port_lib$freshBlockEntities.addAll(this.port_lib$pendingFreshBlockEntities);
			this.port_lib$pendingFreshBlockEntities.clear();
		}
	}

	@Inject(method = "tickBlockEntities", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
	public void port_lib$onBlockEntitiesLoad(CallbackInfo ci) {
		if (!this.port_lib$freshBlockEntities.isEmpty()) {
			this.port_lib$freshBlockEntities.forEach(BlockEntityExtensions::onLoad);
			this.port_lib$freshBlockEntities.clear();
		}
	}

	@Unique
	@Override
	public void addFreshBlockEntities(Collection<BlockEntity> beList) {
		if (this.tickingBlockEntities) {
			this.port_lib$pendingFreshBlockEntities.addAll(beList);
		} else {
			this.port_lib$freshBlockEntities.addAll(beList);
		}
	}
}
