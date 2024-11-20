package org.fortunevale.forceplace;

import net.minecraft.util.math.BlockPos;
import org.fortunevale.forceplace.simplifiedClasses.SimpleBlockPos;

import java.util.Map;

public class Util {
    public static Boolean CheckIfQueued(BlockPos checkingPos)
    {
        SimpleBlockPos simpleCheckingPos = new SimpleBlockPos(checkingPos);

        for (Map.Entry<SimpleBlockPos, Integer> queuedPos : Forceplace.queuedBlocks.entrySet())
        {
            if (queuedPos.getKey().equals(simpleCheckingPos))
                return true;
        }

        return false;
    }
}
