package org.vivecraft.tweaker.asm;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;

public class VivecraftASM_RecipeManager implements ITransformer<MethodNode>
{
    private static final Logger LOGGER = LogManager.getLogger();

    @Nonnull
    public Set<Target> targets()
    {
    	Set<Target> targets = new HashSet<>();
    	targets.add(Target.targetMethod("net/minecraft/world/item/crafting/RecipeManager", "m_5787_", "(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V"));
    	return targets;
    }
    
    public MethodNode transform(MethodNode input, ITransformerVotingContext context)
    {
		LOGGER.info("Vivecraft Modifying " + input.name);
		AbstractInsnNode node = ASMUtil.findFirstInstruction(input, Opcodes.ASTORE, 4);
		if (node == null) {
			LOGGER.warn("Could not inject recipes");
			return input;
		}
		InsnList il = new InsnList();
		il.add(new VarInsnNode(Opcodes.ALOAD, 4));
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/vivecraft/utils/ASMInjections", "injectItems", "(Ljava/util/Map;)V", false));
		input.instructions.insert(node, il);
		return input;
    }

    @Nonnull
    public TransformerVoteResult castVote(ITransformerVotingContext iTransformerVotingContext)
    {
        return TransformerVoteResult.YES;
    }
}
